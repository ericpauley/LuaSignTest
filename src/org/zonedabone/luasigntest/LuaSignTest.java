package org.zonedabone.luasigntest;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class LuaSignTest extends JavaPlugin implements Listener {
    
    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getDataFolder().mkdirs();
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        System.out.println(this.getServer().isPrimaryThread());
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Block b = e.getClickedBlock();
        File f = new File(this.getDataFolder(), b.getWorld().getName() + "_" + b.getX() + "_" + b.getY() + "_" + b.getZ() + ".lua");
        if (!f.exists()) {
            e.getPlayer().sendMessage(f.getName());
            return;
        }
        
        String code;
        try {
            code = FileUtils.readFileToString(f);
            ScriptRunner sr = new ScriptRunner(e.getPlayer(), b, code);
            sr.run();
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }
    }
}
