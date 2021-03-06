/*
 * Copyright 2019 This source file is part of the Máni open source project
 *
 * Copyright (c) 2018 - 2019.
 *
 * Licensed under Mozilla Public License 2.0
 *
 * See https://github.com/mani-language/Mani/blob/master/LICENSE.md for license information.
 */

package com.mani.lang.Modules.downloader;

import com.mani.lang.core.Interpreter;
import com.mani.lang.domain.ManiCallable;
import com.mani.lang.domain.ManiFunction;
import com.mani.lang.Modules.Module;
import com.mani.lang.main.Mani;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class downloader implements Module {
    @Override
    public void init(Interpreter interpreter) {
        interpreter.addSTD("download", new ManiCallable() {
            @Override
            public int arity() {
                return -1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                if (!(arguments.size() >= 2)) {
                    Mani.printAndStoreError("Must be atleast 2 arguments in downloader.");
                    return null;
                }
                final String downloadURL = (String) arguments.get(0);
                final String path = (String) arguments.get(1);
                final ManiFunction callback;
                final double contentLength;
                if (arguments.size() == 3) {
                    callback = (ManiFunction) arguments.get(2);
                    contentLength = getContentLength(downloadURL);
                } else {
                    callback = null;
                    contentLength = -1;
                }
                final int bufferSize = (arguments.size() == 4) ? Math.max(1024, Integer.valueOf((Integer) arguments.get(3))) : 16384;
                final boolean calculateProgressEnabled = contentLength > 0 && callback != null;

                if (calculateProgressEnabled) {
                    List<Object> db = new ArrayList<>();
                    db.add((double) 0);
                    db.add((double) 0);
                    db.add(contentLength);
                    callback.call(interpreter, db);
                }

                try (InputStream is = new URL(downloadURL).openStream();
                     OutputStream os = new FileOutputStream(new File(path))) {
                    int downloaded = 0;
                    final byte[] buffer = new byte[bufferSize];
                    int read;

                    while ((read = is.read(buffer, 0, bufferSize)) != -1) {
                        os.write(buffer, 0, read);
                        downloaded += read;
                        if (calculateProgressEnabled) {
                            final int percent = (int) (downloaded / ((double) contentLength) * 100.0);
                            List<Object> db = new ArrayList<>();
                            db.add((double) percent);
                            db.add((double) downloaded);
                            db.add(contentLength);
                            callback.call(interpreter, db);
                        }
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    return (double) 0;
                } finally {
                    if (callback != null) {
                        List<Object> db = new ArrayList<>();
                        db.add(100d);
                        db.add(contentLength);
                        db.add(contentLength);
                        callback.call(interpreter, db);
                    }
                }

                return null;
            }
        });
        interpreter.addSTD("urlExists", new ManiCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                try {
                    URL u = new URL((String) arguments.get(0));
                    HttpURLConnection huc =  (HttpURLConnection)  u.openConnection();
                    HttpURLConnection.setFollowRedirects(false);
                    huc.setRequestMethod("HEAD");
                    huc.connect();
                    return (huc.getResponseCode() == HttpURLConnection.HTTP_OK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    private static int getContentLength(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();
            return connection.getContentLength();
        } catch (IOException ioe) {
            return -1;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
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
