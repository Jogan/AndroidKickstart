
package {package_name}.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import butterknife.ButterKnife;
import {package_name}.ForActivity;
import {package_name}.ui.activities.BaseActivity;
import javax.inject.Inject;

public class BaseFragment extends Fragment {
    @Inject @ForActivity Context activityContext;

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BaseActivity.get(this).inject(this);
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public void onStart() {
        super.onStart();
    }

    @Override public void onPause() {
        super.onPause();
    }

    @Override public void onViewCreated(View view, Bundle inState) {
        super.onViewCreated(view, inState);
        ButterKnife.inject(this, view);
    }

    @Override public void onDestroyView() {
        ButterKnife.reset(this);
        super.onDestroyView();
    }
}