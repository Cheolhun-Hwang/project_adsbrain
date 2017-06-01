package com.adsbrain.hch.adsbrainapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private Button loginBTN;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBTN = (Button) findViewById(R.id.login_loginBTN);
        loginBTN.setOnClickListener(this);

        mp = MediaPlayer.create(getApplicationContext(), R.raw.bit_rush);
        mp.setLooping(true);
        mp.start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_loginBTN:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                mp.release();
                finish();
                break;
            default:
                break;
        }
    }
}
