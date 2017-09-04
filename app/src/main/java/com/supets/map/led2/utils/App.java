//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.supets.map.led2.utils;

import android.app.Application;
import android.widget.Toast;

public class App {
    public static final Application INSTANCE;

    public App() {
    }

    public static void toast(String msg) {
        Toast.makeText(INSTANCE, msg, 0).show();
    }

    public static void toast(int msgId) {
        Toast.makeText(INSTANCE, msgId, 0).show();
    }

    public static void longToast(String msg) {
        Toast.makeText(INSTANCE, msg, 1).show();
    }

    static {
        Application app = null;

        try {
            app = (Application)Class.forName("android.app.AppGlobals").getMethod("getInitialApplication", new Class[0]).invoke((Object)null, new Object[0]);
            if(app == null) {
                throw new IllegalStateException("Static initialization of Applications must be on main thread.");
            }
        } catch (Exception var8) {
            var8.printStackTrace();

            try {
                app = (Application)Class.forName("android.app.ActivityThread").getMethod("currentApplication", new Class[0]).invoke((Object)null, new Object[0]);
            } catch (Exception var7) {
                var7.printStackTrace();
            }
        } finally {
            INSTANCE = app;
        }

    }
}
