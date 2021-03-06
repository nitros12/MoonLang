/*
 * Copyright 2019 This source file is part of the Máni open source project
 *
 * Copyright (c) 2018 - 2019.
 *
 * Licensed under Mozilla Public License 2.0
 *
 * See https://github.com/mani-language/Mani/blob/master/LICENSE.md for license information.
 */

package com.mani.lang.Modules.files;

import com.mani.lang.core.Interpreter;
import com.mani.lang.domain.ManiCallable;
import com.mani.lang.domain.ManiCallableInternal;
import com.mani.lang.Modules.Module;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class files implements Module {

    @Override
    public void init(Interpreter interpreter) {
        interpreter.addSTD("fopen", new files_open());
        interpreter.addSTD("fwrite", new files_write());
        interpreter.addSTD("fread", new files_read());
        interpreter.addSTD("fgetPath", new files_getPath());
    }

    @Override
    public boolean hasExtensions() {
        return true;
    }

    @Override
    public Object extensions() {
        HashMap<String, HashMap<String, ManiCallableInternal>> db = new HashMap<>();
        HashMap<String, ManiCallableInternal> locals = new HashMap<>();

        locals.put("exists", new ManiCallableInternal() {
           @Override
           public Object call(Interpreter interpreter, List<Object> arguments) {
               return ((File) workWith).exists();
           }
        });

        locals.put("canWrite", new ManiCallableInternal() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return ((File) workWith).canWrite();
            }
        });

        locals.put("canRead", new ManiCallableInternal() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return ((File) workWith).canRead();
            }
        });

        locals.put("canExecute", new ManiCallableInternal() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return ((File) workWith).canExecute();
            }
        });

        db.put("file", locals);
        return db;
    }
}
