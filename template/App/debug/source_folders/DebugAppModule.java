package {package_name};

import {package_name}.data.DebugDataModule;
import {package_name}.ui.DebugUiModule;
import dagger.Module;

@Module(
        addsTo = {app_class_prefix}Module.class,
        includes = {
                DebugUiModule.class,
                DebugDataModule.class
        },
        overrides = true
)
public final class Debug{app_class_prefix}Module {
}