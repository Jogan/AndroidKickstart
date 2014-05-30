package {package_name}.data.api;

import dagger.Module;
import {package_name}.data.api.dummyapi.DummyApiModule;


@Module(
        includes = {
                DummyApiModule.class
        },
        complete = false,
        library = true)
public final class ApiModule {

}