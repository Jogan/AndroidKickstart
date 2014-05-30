package {package_name}.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import butterknife.InjectView;
import {package_name}.R;
import javax.inject.Inject;

public class DummyFragment extends BaseFragment {

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dummy, container, false);
    }

}