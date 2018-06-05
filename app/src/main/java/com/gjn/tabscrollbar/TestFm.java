package com.gjn.tabscrollbar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by gjn on 2018/6/5.
 */

public class TestFm extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.testfm, container, false);

        TextView test = view.findViewById(R.id.tv_test_fm);

        String str = "123";

        Bundle bundle = getArguments();

        if (bundle != null) {
            str = bundle.getString("title");
            Log.e("-s-", " str =" + str);
        }

        test.setText(str);

        return view;
    }
}
