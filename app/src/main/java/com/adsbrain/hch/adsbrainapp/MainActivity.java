package com.adsbrain.hch.adsbrainapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {
    private final String file_name = "adsbraincachetxt.txt";

    private FragmentManager manager;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File cache_file= new File(file_name);

        mp = MediaPlayer.create(getApplicationContext(), R.raw.bit_rush);
        mp.setLooping(true);

        if(cache_file.exists()){
            manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.main_container, new MainHomeFragment()).commit();
        }else{
            manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.main_container, new StoryLineFragment()).commit();
        }



    }

    public void Main_BGM_start(){
        this.mp.start();
    }
    public void Main_BGM_stop(){
        this.mp.pause();
    }
    public void Main_BGM_restart(){
        this.mp.seekTo(0);
        this.mp.start();
    }

    public void save_internaldata(String data){
        try{
            FileOutputStream fos = openFileOutput(file_name, Context.MODE_PRIVATE);

            PrintWriter out = new PrintWriter(fos);
            out.println(data);
            out.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String show_internaldata(){
        StringBuffer data = new StringBuffer();
        try{
            FileInputStream fis = openFileInput(file_name);

            BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));
            String str = buffer.readLine();
            while(str != null){
               data.append(str+"\n");
                str = buffer.readLine();
            }
            buffer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return data.toString();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        this.mp.release();
    }
}
