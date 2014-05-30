package {package_name}.ui.activities;

import android.os.Bundle;
import android.view.ViewGroup;
import {package_name}.R;
import {package_name}.data.prefs.BooleanPreference;
import {package_name}.data.prefs.FirstRun;
import {package_name}.ui.AppContainer;
import {package_name}.ui.fragments.DummyFragment;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {
    @Inject
    AppContainer appContainer;

    @Inject
    @FirstRun
    BooleanPreference firstRun; // do something on first run of app...may not be needed but helpful nonetheless

    private ViewGroup container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateLayout(R.layout.activity_main);

        getFragmentManager().beginTransaction()
                .replace(R.id.content, new DummyFragment())
                .commit();

        if (!firstRun.get()) {
            //do something
            firstRun.set(true);
        }

    }
}
