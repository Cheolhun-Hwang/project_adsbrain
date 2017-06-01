package com.adsbrain.hch.adsbrainapp;


import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayGameFragment extends Fragment {
    private View v;
    private VideoView videoView;

    private final String TAG = "PlayGameFragment";

    private String URL;

    private RadioGroup rg;
    private RadioButton q_1;
    private RadioButton q_2;
    private RadioButton q_3;
    private RadioButton q_4;

    private TextView counter;
    private TextView context;
    private TextView q_number;
    private TextView q_cost;
    private TextView game_mode;

    private VideoView game_video;

    private String playgame_ref;

    private LinearLayout show_video_layout;
    private LinearLayout show_question_layout;
    private LinearLayout wait_layout;

    private boolean isRoading;
    private boolean isFinish;
    private short popup_type;

    private Thread timer;
    private Thread RoadDate;
    private Thread Popup;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            if(msg.what == 1001){
                int time = (int)msg.obj;
                Log.d(TAG, "Message is " + time);
                counter.setText(time + "초");
            }



            return true;
        }
    });

    public PlayGameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_play_game, container, false);

        ((MainActivity)getActivity()).Main_BGM_stop();

        isRoading = false;
        isFinish = false;

        rg = (RadioGroup) v.findViewById(R.id.game_question_rg);
        q_1 = (RadioButton) v.findViewById(R.id.game_q_one);
        q_2 = (RadioButton) v.findViewById(R.id.game_q_two);
        q_3 = (RadioButton) v.findViewById(R.id.game_q_three);
        q_4 = (RadioButton) v.findViewById(R.id.game_q_four);

        counter = (TextView) v.findViewById(R.id.game_counter);
        context = (TextView) v.findViewById(R.id.game_context);
        q_number = (TextView) v.findViewById(R.id.game_number);
        q_cost = (TextView) v.findViewById(R.id.game_score);
        game_mode = (TextView) v.findViewById(R.id.game_rank);

        game_video = (VideoView) v.findViewById(R.id.game_video);

        show_video_layout = (LinearLayout) v.findViewById(R.id.game_videoview_layout);
        show_question_layout = (LinearLayout) v.findViewById(R.id.game_question_layout);
        wait_layout = (LinearLayout) v.findViewById(R.id.wait_layout);

        show_question_layout.setVisibility(View.GONE);

        int playgame = getArguments().getInt("playgame");
        int mode = getArguments().getInt("mode");

        if(playgame == 0){
            playgame_ref = "practice";
            Log.d(TAG, "Setting REF : " + playgame_ref);
        }else if(playgame == 1){
            playgame_ref = "rank";
            Log.d(TAG, "Setting REF : " + playgame_ref);
        }else{
            Log.d(TAG, "Setting REF : flase");
        }

        if(mode == 0 ){
            game_mode.setText("Easy");
            game_mode.setTextColor(Color.BLUE);
        }else if(mode == 1){
            game_mode.setText("Normal");
            game_mode.setTextColor(Color.GREEN);
        }else if(mode == 2){
            game_mode.setText("Hard");
            game_mode.setTextColor(Color.RED);
        }else{
            Log.d(TAG, "Setting MODE : flase");
        }

        timer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(isFinish)){
                    try{
                        for(int i = 15 ; i > 0 ; i --){
                            Message msg = handler.obtainMessage();
                            msg.what = 1001;
                            msg.obj = i;
                            handler.sendMessage(msg);


                            Thread.sleep(1000);
                        }
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });


        RoadDate = new Thread(new Runnable() {
            @Override
            public void run() {
                //isRoading = true;

                URL = "https://firebasestorage.googleapis.com/v0/b/tozzim-159012.appspot.com/o/2015%EB%85%84%20%EC%98%A4%EB%9A%9C%EA%B8%B0%20%EC%A7%84%EC%A7%AC%EB%BD%95%20%EA%B4%91%EA%B3%A0%205%EC%B4%88%20B.mp4?alt=media&token=7bb1f406-b7dc-4f0a-8155-56a0ed9ec7f0";

                //game_video.requestFocus();

                game_video.setVideoURI(Uri.parse(URL));

                game_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        try {
                            wait_layout.setVisibility(View.GONE);
                            show_video_layout.setVisibility(View.VISIBLE);

                            Thread.sleep(2000);
                            game_video.seekTo(0);
                            game_video.start();
                        }catch (InstantiationException e){
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

                game_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {

                        try {
                            show_video_layout.setVisibility(View.GONE);
                            show_question_layout.setVisibility(View.VISIBLE);

                            Thread.sleep(2000);
                            timer.start();
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                });


                /*
                if(isRoading){
                }
                */
            }
        });

        Popup = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });


        RoadDate.start();


        return v;
    }

}
