package com.adsbrain.hch.adsbrainapp;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


public class SelectModeFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match

    private View v;

    private final String TAG = "SelectModeFragment";

    private ImageButton easy;
    private ImageButton normal;
    private ImageButton hard;
    private Button back;
    private int play_game_arg;

    private TextView playgame_textview;

    private FragmentManager manager;

    public SelectModeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_select_mode, container, false);

        easy = (ImageButton) v.findViewById(R.id.select_mode_easy);
        normal = (ImageButton) v.findViewById(R.id.select_mode_normal);
        hard = (ImageButton) v.findViewById(R.id.select_mode_hard);
        back = (Button) v.findViewById(R.id.mode_back);

        playgame_textview = (TextView) v.findViewById(R.id.select_playgame_textview);

        play_game_arg = getArguments().getInt("playgame");
        String play_game_text = "";
        if(play_game_arg == 0){
            play_game_text = "연습게임";
        }else if(play_game_arg == 1){
            play_game_text = "랭크게임";
        }else{
            Log.e(TAG, "play_game_argument Error!!");
        }
        playgame_textview.setText(play_game_text + " 난이도를 선택해 주세요!");

        back.setOnClickListener(this);
        easy.setOnClickListener(this);
        normal.setOnClickListener(this);
        hard.setOnClickListener(this);

        manager = getActivity().getSupportFragmentManager();


        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void onClick(View v) {
        Fragment Game = new PlayGameFragment();
        Bundle bundle = new Bundle();
        switch (v.getId()){
            case R.id.select_mode_easy :
                bundle.putInt("playgame", play_game_arg);
                bundle.putInt("mode", 0);
                Game.setArguments(bundle);
                manager.beginTransaction().replace(R.id.main_container, Game).commit();
                break;
            case R.id.select_mode_normal :
                bundle.putInt("playgame", play_game_arg);
                bundle.putInt("mode", 1);
                Game.setArguments(bundle);
                manager.beginTransaction().replace(R.id.main_container, Game).commit();
                break;
            case R.id.select_mode_hard :
                bundle.putInt("playgame", play_game_arg);
                bundle.putInt("mode", 2);
                Game.setArguments(bundle);
                manager.beginTransaction().replace(R.id.main_container, Game).commit();
                break;
            case R.id.mode_back:
                manager.beginTransaction().replace(R.id.main_container, new MainHomeFragment()).commit();
                break;
            default:
                break;
        }
    }

}
