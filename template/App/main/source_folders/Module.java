package {package_name};

import android.app.Application;
import {package_name}.data.DataModule;
import {package_name}.ui.UiModule;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    includes = {
        UiModule.class,
        DataModule.class
    },
    injects = {
            {app_class_prefix}App.class
    }
)
public final class {app_class_prefix}Module {
  private final {app_class_prefix}App app;

  public {app_class_prefix}Module({app_class_prefix}App app) {
    this.app = app;
  }

  @Provides @Singleton Application provideApplication() {
    return app;
  }
}
