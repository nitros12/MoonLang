package com.mani.lang.Modules.system;

import com.mani.lang.Modules.Module;
import com.mani.lang.core.Interpreter;
import com.mani.lang.domain.ManiCallable;
import com.mani.lang.main.Mani;

import java.util.List;

public class system implements Module {
    @Override
    public void init(Interpreter interpreter) {
        interpreter.addSTD("online", new ManiCallable() {
            @Override
            public int arity() { return 1; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                if (!(arguments.get(0) instanceof Boolean)) {
                    System.err.println("Argument must be true or false!");
                    return null;
                }
                Mani.hasInternet = (Boolean) arguments.get(0);
                return null;
            }
        });

        interpreter.addSTD("compiled", new ManiCallable() {
            @Override
            public int arity() { return 1; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                if (!(arguments.get(0) instanceof Boolean)) {
                    System.err.println("Argument must be true or false!");
                    return null;
                }
                Mani.compiledMode = (Boolean) arguments.get(0);
                return null;
            }
        });

//        interpreter.addSTD("stdlib_path", new ManiCallable() {
//            @Override
//            public int arity() {
//                return 1;
//            }
//
//            @Override
//            public Object call(Interpreter interpreter, List<Object> arguments) {
//                if (!(arguments.get(0) instanceof String)) {
//                    System.err.println("Argument must be a string!");
//                    return null;
//                }
//                Mani.stdlib_path = (String) arguments.get(0);
//                return true;
//            }
//        });

        interpreter.addSTD("hadError", new ManiCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return Mani.hadRuntimeError;
            }
        });
    }

    @Override
    public boolean hasExtensions() {
        return false;
    }

    @Override
    public Object extensions() {
        return null;
    }
}
