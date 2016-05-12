package bf.cloud.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.airplay.airplayer.AirPlayerActivity;

import bf.cloud.android.playutils.VideoManager;
import bf.cloud.black_board_ui.R;

public class MainActivity extends AppCompatActivity {
    private Button btVod = null;
    private Button btLive = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        TextView versionText = (TextView) findViewById(R.id.Version);

        String version = Version.getFullVersion(this);
        versionText.setText(versionText.getText() + version);
        VideoManager.getInstance(this).startMediaCenter();

    }

    private void init() {
        findViewById(R.id.Airplayer).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AirPlayerActivity.class);
                startActivity(intent);
            }
        });
        btVod = (Button) findViewById(R.id.vod);
        btVod.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, VodDemo.class);
                startActivity(intent);
            }
        });
        btLive = (Button) findViewById(R.id.live);
        btLive.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LiveDemo.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoManager.getInstance(this).stopMediaCenter();
    }
}
