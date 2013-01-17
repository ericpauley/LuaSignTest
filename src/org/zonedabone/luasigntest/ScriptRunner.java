/*
 Copyright (c) 2011 Kristofer Karlsson <kristofer.karlsson@gmail.com>

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */

package org.zonedabone.luasigntest;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.LuaCaller;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;
import se.krka.kahlua.j2se.J2SEPlatform;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.LuaClosure;

public class ScriptRunner {
    
    private final KahluaConverterManager converterManager = new KahluaConverterManager();
    private final J2SEPlatform platform = new J2SEPlatform();
    private final KahluaTable env = platform.newEnvironment();
    private final KahluaThread thread = new KahluaThread(platform, env);
    private final LuaCaller caller = new LuaCaller(converterManager);
    private final LuaJavaClassExposer exposer = new LuaJavaClassExposer(converterManager, platform, env);
    private Player p;
    private Block b;
    private String code;
    
    public ScriptRunner(Player p, Block b, String code) {
        this.p = p;
        this.b = b;
        this.code = code;
    }
    
    /*
     * public void run() throws IOException { /*KahluaTable javaBase =
     * platform.newTable(); env.rawset("java", javaBase);
     * exposer.exposeLikeJavaRecursively(ArrayList.class, javaBase);
     * env.rawset("test", false); env.rawset("server", p.getServer());
     * env.rawset("player", p); env.rawset("block", b);
     * System.out.println(env.rawget("test")); LuaClosure closure =
     * LuaCompiler.loadstring(code, "", env); caller.protectedCall(thread,
     * closure); System.out.println(env.rawget("test")); }
     */
    
    public void run() throws IOException {
        KahluaTable javaBase = platform.newTable();
        env.rawset("Java", javaBase);
        exposer.exposeLikeJavaRecursively(ArrayList.class, javaBase);
        this.exposeObjectAsTable(p, "player");
        this.exposeObjectAsTable(p.getServer(), "server");
        this.exposeObjectAsTable(b, "block");
        env.rawset("test", false);
        
        LuaClosure closure = LuaCompiler.loadstring(code, "", env);
        caller.protectedCall(thread, closure);
        System.out.println(env.rawget("test"));
    }
    
    public void exposeObjectAsTable(Object object, String name) {
        KahluaTable table = platform.newTable();
        env.rawset(name, table);
        Class<?> clazz = object.getClass();
        for (Method method : clazz.getMethods()) {
            exposer.exposeGlobalObjectFunction(table, object, method);
        }
    }
}