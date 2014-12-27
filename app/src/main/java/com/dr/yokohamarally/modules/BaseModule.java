package com.dr.yokohamarally.modules;

import dagger.Module;

@Module(
        includes = {
                AndroidModule.class,
                YokohamarallyModule.class
        }
)

public class BaseModule {
}
