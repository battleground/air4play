package com.airplay.airplayer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bf.cloud.black_board_ui.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentA extends Fragment {

    public FragmentA() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_a, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
