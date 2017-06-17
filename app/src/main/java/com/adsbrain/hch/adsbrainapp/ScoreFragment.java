package com.adsbrain.hch.adsbrainapp;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScoreFragment extends DialogFragment {
    private View v;
    private int score;
    private int add_ch;

    private TextView score_add_ch;
    private TextView express_result_textview;
    private TextView score_result_textview;
    private Button back;

    public ScoreFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d =  super.onCreateDialog(savedInstanceState);
        d.setCanceledOnTouchOutside(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.fragment_score, null);
        d.setContentView(v);



        score_result_textview = (TextView) v.findViewById(R.id.score_result_textview);
        express_result_textview = (TextView) v.findViewById(R.id.score_state_score);
        score_add_ch = (TextView) v.findViewById(R.id.score_add_score);
        back = (Button) v.findViewById(R.id.score_back_main_btn);

        score = (int) getArguments().getInt("score");
        add_ch = (int) getArguments().getInt("Add");

        score_add_ch.setText("+"+add_ch+" 경험치");

        score_result_textview.setText(score + " / 10");

        if(score >= 8){
            express_result_textview.setText("Excellent");
            express_result_textview.setTextColor(Color.GREEN);
        }else if (score >= 5){
            express_result_textview.setText("Good");
            express_result_textview.setTextColor(Color.BLUE);
        }else{
            express_result_textview.setText("Bad");
            express_result_textview.setTextColor(Color.RED);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.main_container, new MainHomeFragment()).commit();
            }
        });

        return d;
    }

}
