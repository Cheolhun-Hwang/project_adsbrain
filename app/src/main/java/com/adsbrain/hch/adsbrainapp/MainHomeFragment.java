package com.adsbrain.hch.adsbrainapp;


import android.icu.text.DecimalFormat;
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
    int LV;
    private Button main_menu;
    private ImageButton main_sound_imgbtn;
    private boolean isSoundOK;

    private ImageButton main_practise;
    private ImageButton main_rank;

    private TextView name;
    private TextView PTS;
    private TextView RE;
    private TextView RN;
    private TextView RH;

    private TextView main_LV;
    private TextView main_LV_point;
    private MainActivity.Myscore myscore;

    private FragmentManager manager;

    public MainHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isSoundOK = true;

        myscore = ((MainActivity)getActivity()).getMyscore();

        v = inflater.inflate(R.layout.fragment_main_home_layout, container, false);
        name = (TextView) v.findViewById(R.id.main_home_nickname);
        PTS = (TextView) v.findViewById(R.id.main_galley_practice_addcost);
        RE = (TextView) v.findViewById(R.id.main_galley_rank_easy_avg);
        RN = (TextView) v.findViewById(R.id.main_galley_rank_normal_avg);
        RH = (TextView) v.findViewById(R.id.main_galley_rank_hard_avg);

        main_LV = (TextView) v.findViewById(R.id.main_LV_textview);
        main_LV_point = (TextView) v.findViewById(R.id.main_LV_UP_point_textview);

        init_btn(v);
        LV = ((MainActivity)getActivity()).getNowLV();
        main_LV_point.setText(((MainActivity)getActivity()).getNowLV_POINT() + " / " + ((MainActivity)getActivity()).getLV_UP().get(LV));
        name.setText(((MainActivity)getActivity()).getNickname());
        PTS.setText((myscore.getPE() + myscore.getPN() + myscore.getPH()) + "점");

        if(!(myscore.getREC() <= 0 )){
            RE.setText(String.format("%.2f", ((float)myscore.getRE() / (float)myscore.getREC())) + "점");
        }
        if(!(myscore.getRNC() <= 0 )){
            RN.setText(String.format("%.2f", ((float)myscore.getRN() / (float)myscore.getRNC())) + "점");
        }
        if(!(myscore.getRHC() <= 0 )){
            RH.setText(String.format("%.2f", ((float)myscore.getRH() / (float)myscore.getRHC())) + "점");
        }




        manager = getActivity().getSupportFragmentManager();

        return v;
    }

    private void init_btn(View v){
        main_menu = (Button) v.findViewById(R.id.main_menu_btn);
        main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                MenuFragment menuFragment = new MenuFragment();
                menuFragment.show(manager, "MenuFragments");
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


    @Override
    public void onResume() {
        name.setText(((MainActivity)getActivity()).getNickname());
        ((MainActivity)getActivity()).isLevelUP();
        main_LV.setText(LV+"");
        super.onResume();
    }
}
