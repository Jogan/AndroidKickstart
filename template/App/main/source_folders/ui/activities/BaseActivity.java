package {package_name}.ui.activities;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import {package_name}.{app_class_prefix}App;
import {package_name}.ui.ActivityModule;
import {package_name}.ui.AppContainer;
import dagger.ObjectGraph;
import hugo.weaving.DebugLog;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

public class BaseActivity extends Activity {
    @Inject AppContainer appContainer;

    private ViewGroup container;
    private ObjectGraph activityGraph;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildActivityGraphAndInject();
    }

    @DebugLog
    private void buildActivityGraphAndInject() {
        // Create the activity graph by .plus-ing our modules onto the application graph.
        {app_class_prefix}App app = {app_class_prefix}App.get(this);
        activityGraph = app.getApplicationGraph().plus(getModules().toArray());

        // Inject ourselves so subclasses will have dependencies fulfilled when this method returns.
        activityGraph.inject(this);

        container = appContainer.get(this, app);
    }

    /** Inject the given object into the activity graph. */
    public void inject(Object o) {
        activityGraph.inject(o);
    }

    /**
     * A list of modules to use for the individual activity graph. Subclasses can override this
     * method to provide additional modules provided they call and include the modules returned by
     * calling {@code super.getModules()}.
     */
    protected List<Object> getModules() {
        return Arrays.<Object>asList(new ActivityModule(this));
    }

    @Override protected void onResume() {
        super.onResume();
    }

    @Override protected void onPause() {
        super.onPause();
    }

    @Override protected void onDestroy() {
        // Eagerly clear the reference to the activity graph to allow it to be garbage collected as
        // soon as possible.
        activityGraph = null;

        super.onDestroy();
    }

    protected void inflateLayout(int layoutResID) {
        getLayoutInflater().inflate(layoutResID, container);
        // Inject Views
        ButterKnife.inject(this);
    }

    public static BaseActivity get(Fragment fragment) {
        return (BaseActivity) fragment.getActivity();
    }
}