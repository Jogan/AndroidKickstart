package {package_name}.ui;

import android.content.Context;
import {package_name}.ForActivity;
import {package_name}.ui.activities.BaseActivity;
import {package_name}.ui.activities.MainActivity;
import {package_name}.ui.fragments.BaseFragment;
import {package_name}.ui.fragments.DummyFragment;
import {package_name}.ui.UiModule;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        injects = {
                MainActivity.class, BaseFragment.class, DummyFragment.class
        },
        complete = false,
        addsTo = UiModule.class //
)
public class ActivityModule {
    private final BaseActivity activity;

    public ActivityModule(BaseActivity activity) {
        this.activity = activity;
    }

    @Provides @Singleton @ForActivity Context provideActivityContext() {
        return activity;
    }

}