package com.dr.yokohamarally.cores;


import dagger.ObjectGraph;

public final class Injector {

    private static ObjectGraph objectGraph = null;

    public static void init(final Object baseModule) {

        if (objectGraph == null) {
            objectGraph = ObjectGraph.create(baseModule);
        } else {
            objectGraph = objectGraph.plus(baseModule);
        }

        objectGraph.injectStatics();
    }

    public static void init(final Object baseModule, final Object target) {
        init(baseModule);
        inject(target);
    }

    public static void inject(final Object target) {
        objectGraph.inject(target);
    }

    public static <T> T resolve(Class<T> type) {
        return objectGraph.get(type);
    }

}
