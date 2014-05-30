package {package_name}.data;

import android.content.SharedPreferences;
import {package_name}.data.prefs.BooleanPreference;
import {package_name}.data.prefs.FirstRun;
import {package_name}.data.prefs.StringPreference;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(complete = false, library = true)
public class UserPreferencesModule {

    //Could be used for API access
    /*@Provides @Singleton @SomeKey StringPreference provideApiKeyPreference(
            SharedPreferences preferences) {
        return new StringPreference(preferences, "some_api_key");
    }*/

    @Provides @Singleton @FirstRun BooleanPreference provideFirstRun(
            SharedPreferences sharedPreferences) {
        return new BooleanPreference(sharedPreferences, "first_run", false);
    }
}