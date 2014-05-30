package {package_name}.ui;

import android.app.Activity;
import android.view.ViewGroup;
import {package_name}.{app_class_prefix}App;

import static butterknife.ButterKnife.findById;

/** An indirection which allows controlling the root container used for each activity. */
public interface AppContainer {
  /** The root {@link android.view.ViewGroup} into which the activity should place its contents. */
  ViewGroup get(Activity activity, {app_class_prefix}App app);

  /** An {@link {package_name}.ui.AppContainer} which returns the normal activity content view. */
  AppContainer DEFAULT = new AppContainer() {
    @Override public ViewGroup get(Activity activity, {app_class_prefix}App app) {
      return findById(activity, android.R.id.content);
    }
  };
}
