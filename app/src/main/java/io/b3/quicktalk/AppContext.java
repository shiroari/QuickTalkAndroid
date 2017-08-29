package io.b3.quicktalk;

import android.content.Context;

import dagger.ObjectGraph;

/**
 * @author shiroari
 * @since 6.08.2016
 */
public class AppContext {

    private static Context CONTEXT;

    private static ObjectGraph DI;

    public static void initContext(Context context) {
        CONTEXT = context;
    }

    public static Context context() {
        return CONTEXT;
    }

    public static void addModule(AppModule module) {
        DI = ObjectGraph.create(module);
    }

    public static void inject(Object target) {
        DI.inject(target);
    }

}
