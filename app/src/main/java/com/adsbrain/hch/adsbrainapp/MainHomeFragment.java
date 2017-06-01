package com.adsbrain.hch.adsbrainapp;


import android.media.Image;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class MainHomeFragment extends Fragment{
    private View v;

    private final String TAG = "MainHomeFragment";

    private Button main_menu;
    private ImageButton main_sound_imgbtn;
    private boolean isSoundOK;

    private ImageButton main_practise;
    private ImageButton main_rank;

    private TextView name;
    private TextView rank_text;
    private TextView addcost_text;
    private TextView latestcost_text;

    private FragmentManager manager;

    public MainHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //메인 프레그먼트 사운드 셋팅_메소드 메인 통신
        ((MainActivity)getActivity()).Main_BGM_start();
        isSoundOK = true;

        v = inflater.inflate(R.layout.fragment_main_home_layout, container, false);

        init_btn(v);


        manager = getActivity().getSupportFragmentManager();

        return v;
    }

    private void init_btn(View v){
        main_menu = (Button) v.findViewById(R.id.main_menu_btn);
        main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        main_sound_imgbtn = (ImageButton) v.findViewById(R.id.main_sound_imgbtn);
        main_sound_imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSoundOK){
                    ((MainActivity)getActivity()).Main_BGM_stop();
                    main_sound_imgbtn.setImageResource(R.drawable.ic_volume_off_black_24dp);
                    isSoundOK = false;
                }else{
                    ((MainActivity)getActivity()).Main_BGM_restart();
                    main_sound_imgbtn.setImageResource(R.drawable.ic_volume_up_black_24dp);
                    isSoundOK = true;
                }
            }
        });
        main_practise = (ImageButton) v.findViewById(R.id.main_practice_game_btn);
        main_practise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectfragment_practice = new SelectModeFragment();
                Bundle bundle_pracitec = new Bundle();
                bundle_pracitec.putInt("playgame", 0);
                selectfragment_practice.setArguments(bundle_pracitec);
                manager.beginTransaction().replace(R.id.main_container, selectfragment_practice).commit();
            }
        });

        main_rank = (ImageButton) v.findViewById(R.id.main_rank_game_btn);
        main_rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectfragment_rank = new SelectModeFragment();
                Bundle bundle_rank = new Bundle();
                bundle_rank.putInt("playgame", 1);
                selectfragment_rank.setArguments(bundle_rank);
                manager.beginTransaction().replace(R.id.main_container, selectfragment_rank).commit();
            }
        });
    }
}
