package com.airplay.airplayer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.airplay.airplayer.FragmentHandlerAdapter.TabInfo;

import bf.cloud.black_board_ui.R;

public class Fragment2Activity extends AppCompatActivity {

    private FragmentHandlerAdapter Fragments = new FragmentHandlerAdapter(getSupportFragmentManager(), this);


    private boolean home = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Fragments.addTab(new TabInfo(FragmentA.class, "home", null))
                .addTab(new TabInfo(FragmentB.class, "founder", null));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Fragments.show(Fragments.getTabInfo(home ? 1 : 0), R.id.FrameLayout);
                home = !home;

            }
        });


        Fragments.show(Fragments.getTabInfo(home ? 1 : 0), R.id.FrameLayout);
    }

}
