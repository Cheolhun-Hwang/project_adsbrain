package com.adsbrain.hch.adsbrainapp;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ViewFlipper;


/**
 * A simple {@link Fragment} subclass.
 */
public class StoryLineFragment extends Fragment {

    private View v;
    private ViewFlipper flipper;
    private Button skip_btn;
    private Thread timer_page_turn;
    private Thread timer_change_Activity;
    private MediaPlayer mp;


    public StoryLineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_story_line, container, false);

        flipper = (ViewFlipper) v.findViewById(R.id.story_line_viewflipper);
        skip_btn = (Button) v.findViewById(R.id.story_line_skip_btn);
        skip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turn_main_activity();
            }
        });


        flipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left));

        ((MainActivity)getActivity()).Main_BGM_stop();
        mp = MediaPlayer.create(getContext(), R.raw.story_line_bgm);
        mp.setLooping(true);

        timer_page_turn = new Thread(new Runnable() {
            @Override
            public void run() {
                flipper.setFlipInterval(6500);
                flipper.startFlipping();
            }
        });

        timer_change_Activity = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(45500);
                    turn_main_activity();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });

        timer_page_turn.start();
        timer_change_Activity.start();

        return v;
    }

    @Override
    public void onStart(){
        super.onStart();

        mp.start();
    }

    private void turn_main_activity(){
        mp.stop();
        timer_change_Activity.interrupt();
        timer_page_turn.interrupt();
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.main_container, new MainHomeFragment()).commit();
    }
}
