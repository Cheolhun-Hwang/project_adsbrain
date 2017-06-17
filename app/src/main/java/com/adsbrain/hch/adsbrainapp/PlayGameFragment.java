package com.adsbrain.hch.adsbrainapp;


import android.app.DownloadManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayGameFragment extends Fragment {
    private View v;

    private final String TAG = "PlayGameFragment";

    private String URL;
    private int num_view;

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

    private TextView explain_mode_textview;
    private TextView explain_textview_01;
    private TextView explain_textview_02;
    private TextView explain_textview_03;
    private TextView explain_textview_04;


    private VideoView game_video;

    private String playgame_ref;

    private LinearLayout show_video_layout;
    private LinearLayout show_question_layout;
    private LinearLayout wait_layout;

    private boolean isRoading;
    private boolean isCheckAnswer;
    private int playgame;
    private int mode;
    private int type_num;

    private Thread timer;
    private Thread RoadDate;
    ArrayList<Integer> Store_Answer_array;

    private Game_Question_Info_Class gqic;

    private int result;
    private boolean[] anwer_check;

    private Long Question_num;
    DatabaseReference rootRef;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            if(msg.what == 1001){
                int time = (int)msg.obj;
                Log.d(TAG, "Message is " + time);
                counter.setText(time + "초");
            }else if(msg.what ==1002){
                game_video.setVideoURI(Uri.parse(URL));
            }else if(msg.what == 1004){
                show_video_layout.setVisibility(View.GONE);
                show_question_layout.setVisibility(View.VISIBLE);
            }else if(msg.what == 2001){
                int q_n = (int) msg.obj;

                Log.d("Check Data Question", "Question num : " + q_n);

                context.setText(gqic.getList_q().get(q_n).getContext().toString());
                q_1.setText(gqic.getList_q().get(q_n).getQ_one().toString());
                q_2.setText(gqic.getList_q().get(q_n).getQ_two().toString());
                q_3.setText(gqic.getList_q().get(q_n).getQ_three().toString());
                q_4.setText(gqic.getList_q().get(q_n).getQ_four().toString());
                q_number.setText("문제 " + (q_n+1));
                q_cost.setText("문제 배점 : " + gqic.getList_q().get(q_n).getScore() + "점");
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
        rootRef = FirebaseDatabase.getInstance().getReference();
        isRoading = false;
        isCheckAnswer = false;

        Store_Answer_array = new ArrayList<Integer>();

        gqic = null;
        num_view = 0;

        playgame = getArguments().getInt("playgame");
        mode = getArguments().getInt("mode");

        rg = (RadioGroup) v.findViewById(R.id.game_question_rg);
        q_1 = (RadioButton) v.findViewById(R.id.game_q_one);
        q_2 = (RadioButton) v.findViewById(R.id.game_q_two);
        q_3 = (RadioButton) v.findViewById(R.id.game_q_three);
        q_4 = (RadioButton) v.findViewById(R.id.game_q_four);

        radiobutton_event();

        counter = (TextView) v.findViewById(R.id.game_counter);
        context = (TextView) v.findViewById(R.id.game_context);
        q_number = (TextView) v.findViewById(R.id.game_number);
        q_cost = (TextView) v.findViewById(R.id.game_score);
        game_mode = (TextView) v.findViewById(R.id.game_rank);

        game_video = (VideoView) v.findViewById(R.id.game_video);
        game_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer player) {
                //game_video.pause();
                wait_layout.setVisibility(View.GONE);
            }
        });
        game_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer Player) {

                try {
                    Thread.sleep(2000);
                    Message msg = handler.obtainMessage();
                    msg.what = 1004;
                    handler.sendMessage(msg);
                    timer.start();

                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });
        show_video_layout = (LinearLayout) v.findViewById(R.id.game_videoview_layout);
        show_question_layout = (LinearLayout) v.findViewById(R.id.game_question_layout);
        wait_layout = (LinearLayout) v.findViewById(R.id.wait_layout);

        explain_mode_textview = (TextView) v.findViewById(R.id.wait_game_mode_textview);
        explain_textview_01 = (TextView) v.findViewById(R.id.wait_mode_rule_textview1);
        explain_textview_02 = (TextView) v.findViewById(R.id.wait_mode_rule_textview2);
        explain_textview_03 = (TextView) v.findViewById(R.id.wait_mode_rule_textview3);
        explain_textview_04 = (TextView) v.findViewById(R.id.wait_mode_rule_textview4);

        show_question_layout.setVisibility(View.GONE);

        URL = "";

        set_mode_proof();


        timer = new Thread(new Runnable() {
            @Override
            public void run() {

                for(int j = 0 ; j < gqic.getList_q().size() ; j++){
                    Message msg = handler.obtainMessage();
                    msg.what = 2001;
                    msg.obj = j;
                    handler.sendMessage(msg);

                    try{
                        for(int i = 15 ; i >= 0 ; i --){
                            if(i == 0){
                                if( !(q_1.isChecked()) && !(q_2.isChecked()) && !(q_3.isChecked()) && !(q_4.isChecked())){
                                    int size = Store_Answer_array.size();
                                    Store_Answer_array.add(0);
                                }
                                break;
                            }
                            if(isCheckAnswer == true){
                                isCheckAnswer = false;
                                break;
                            }
                            Message msg2 = handler.obtainMessage();
                            msg2.what = 1001;
                            msg2.obj = i;
                            handler.sendMessage(msg2);

                            Thread.sleep(1000);
                        }
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }

                }
                RoadDate.interrupt();
                finish_Quiz();
            }
        });


        RoadDate = new Thread(new Runnable() {
            @Override
            public void run() {

                while (!(isRoading)){
                    try{
                        Log.d("Road Game", "Sleep");
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    if(URL.length() > 7 ){
                        isRoading = true;
                        break;
                    }
                }

                Message msg = handler.obtainMessage();
                msg.what = 1002;
                handler.sendMessage(msg);

            }
        });


        RoadDate.start();


        return v;
    }

    public void finish_Quiz(){
        result = 0;
        int LV_point;
        int add_num = 1;
        anwer_check = new boolean[5];
        for(int j = 0;j < gqic.getList_q().size() ; j++){
            Log.d("Score", "Store : " + Store_Answer_array.get(j));
            Log.d("Score", "Answer : " + gqic.getList_q().get(j).getAnswer());
            if(Store_Answer_array.get(j) == gqic.getList_q().get(j).getAnswer()){
                result += gqic.getList_q().get(j).getScore();
                Log.d("Score", "result : Ture / " + result);
                anwer_check[j] = true;
            }else{
                Log.d("Score", "result : False " );
                anwer_check[j] = false;
            }
        }
        Log.d("Score", "Total : result : " + result);

        if(playgame == 0){
            LV_point = ((MainActivity)getActivity()).getNowLV_POINT();
            ((MainActivity)getActivity()).setNowLV_POINT((LV_point+1));
            MainActivity.Myscore myscore = ((MainActivity)getActivity()).getMyscore();
            if(mode == 0){
                int count = myscore.getPEC();
                int add_result = myscore.getPE();
                myscore.setPE(add_result + result);
                myscore.setPEC(count+1);
            }else if(mode == 1){
                int count = myscore.getPNC();
                int add_result = myscore.getPN();
                myscore.setPN(add_result + result);
                myscore.setPNC(count+1);
            }else if(mode == 2){
                int count = myscore.getPHC();
                int add_result = myscore.getPH();
                myscore.setPH(add_result + result);
                myscore.setPHC(count+1);
            }
        }else if(playgame == 1){
            LV_point = ((MainActivity)getActivity()).getNowLV_POINT();
            MainActivity.Myscore myscore = ((MainActivity)getActivity()).getMyscore();
            if(mode == 0){
                int count = myscore.getREC();
                int add_result = myscore.getRE();
                add_num =(int) (result*0.33);
                if(add_num < 1){
                    add_num = 1;
                }
                ((MainActivity)getActivity()).setNowLV_POINT((LV_point+add_num));
                myscore.setRE(add_result + result );
                myscore.setREC(count+1);
            }else if(mode == 1){
                int count = myscore.getRNC();
                int add_result = myscore.getRN();
                add_num = (int)(result*(0.66));
                if(add_num < 1){
                    add_num = 1;
                }
                ((MainActivity)getActivity()).setNowLV_POINT((LV_point+add_num));
                myscore.setRN(add_result + result);
                myscore.setRNC(count+1);
            }else if(mode == 2){
                int count = myscore.getRHC();
                int add_result = myscore.getRH();
                add_num = (int) (result*1.00);
                if(add_num < 1){
                    add_num = 1;
                }
                ((MainActivity)getActivity()).setNowLV_POINT((LV_point+add_num));
                myscore.setRH(add_result + result);
                myscore.setRHC(count+1);
            }
        }

        Thread store = new Thread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity)getActivity()).store_server(URL, result, anwer_check[0], anwer_check[1],
                                                            anwer_check[2], anwer_check[3], anwer_check[4]);
            }
        });

        store.start();
        FragmentManager manager = getActivity().getSupportFragmentManager();
        ScoreFragment score = new ScoreFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("score", result);
        bundle.putInt("Add", add_num);
        score.setArguments(bundle);
        score.show(manager, "ScoreFragments");


    }

    private void radiobutton_event(){
        q_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Store_Answer_array.add(1);
                isCheckAnswer = true;
                rg.clearCheck();
            }
        });
        q_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Store_Answer_array.add(2);
                isCheckAnswer = true;
                rg.clearCheck();
            }
        });
        q_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Store_Answer_array.add(3);
                isCheckAnswer = true;
                rg.clearCheck();
            }
        });
        q_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Store_Answer_array.add(4);
                isCheckAnswer = true;
                rg.clearCheck();
            }
        });
    }

    public void wait_sec_time(int time){
        try{
            Thread.sleep(time*1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void set_mode_proof(){
        if(playgame == 0){
            playgame_ref = "Practice";
            game_mode.setText(playgame_ref);
            Log.d(TAG, "Setting REF : " + playgame_ref);

            if(mode == 0 ){
                explain_mode_textview.setText("Easy");
                explain_mode_textview.setTextColor(Color.BLUE);
                explain_textview_01.setText("1. 5초 이내의 광고 영상 한편을 시청하시게 됩니다.");
                explain_textview_02.setText("2. 영상 종료 이후 곧바로 문제를 푸시게 됩니다.");
                explain_textview_03.setText("3. 총 5문제를 푸시며, 제한 시간은 15초 입니다.");
                explain_textview_04.setText("4. 모든 문제를 푸시면 결과가 저장됩니다." +
                        "\n     '메뉴 > 성장과정 저장'을 통해 모든 결과를 보실 수 있습니다.");

            }else if(mode == 1){
                explain_mode_textview.setText("Normal");
                explain_mode_textview.setTextColor(Color.GREEN);
                explain_textview_01.setText("1. 15초 이내의 광고 영상 한편을 시청하시게 됩니다.");
                explain_textview_02.setText("2. 영상 종료 이후 곧바로 문제를 푸시게 됩니다.");
                explain_textview_03.setText("3. 총 5문제를 푸시며, 제한 시간은 15초 입니다.");
                explain_textview_04.setText("4. 모든 문제를 푸시면 결과가 저장됩니다." +
                        "\n     '메뉴 > 성장과정 저장'을 통해 모든 결과를 보실 수 있습니다.");

            }else if(mode == 2){
                explain_mode_textview.setText("Hard");
                explain_mode_textview.setTextColor(Color.RED);
                explain_textview_01.setText("1. 15초 이상의 광고 영상 한편을 시청하시게 됩니다.");
                explain_textview_02.setText("2. 영상 종료 이후 곧바로 문제를 푸시게 됩니다.");
                explain_textview_03.setText("3. 총 5문제를 푸시며, 제한 시간은 15초 입니다.");
                explain_textview_04.setText("4. 모든 문제를 푸시면 결과가 저장됩니다." +
                        "\n     '메뉴 > 성장과정 저장'을 통해 모든 결과를 보실 수 있습니다.");

            }else{
                Log.d(TAG, "Setting MODE : flase");
            }
            Road_question(mode);
        }else if(playgame == 1){
            playgame_ref = "Rank";
            game_mode.setText(playgame_ref);
            Log.d(TAG, "Setting REF : " + playgame_ref);

            if(mode == 0 ){
                explain_mode_textview.setText("Easy");
                explain_mode_textview.setTextColor(Color.BLUE);
                explain_textview_01.setText("1. 5초 이내의 광고 영상 한편을 시청하시게 됩니다.");
                explain_textview_02.setText("2. 영상 종료 이후 곧바로 문제를 푸시게 됩니다.");
                explain_textview_03.setText("3. 총 5문제를 푸시며, 제한 시간은 15초 입니다.");
                explain_textview_04.setText("4. 모든 문제를 푸시면 결과가 저장됩니다." +
                        "\n     '메뉴 > 성장과정 저장'을 통해 모든 결과를 보실 수 있습니다.");

            }else if(mode == 1){
                explain_mode_textview.setText("Normal");
                explain_mode_textview.setTextColor(Color.GREEN);
                explain_textview_01.setText("1. 15초 이내의 광고 영상 한편을 시청하시게 됩니다.");
                explain_textview_02.setText("2. 영상 종료 이후 곧바로 문제를 푸시게 됩니다.");
                explain_textview_03.setText("3. 총 5문제를 푸시며, 제한 시간은 15초 입니다.");
                explain_textview_04.setText("4. 모든 문제를 푸시면 결과가 저장됩니다." +
                        "\n     '메뉴 > 성장과정 저장'을 통해 모든 결과를 보실 수 있습니다.");

            }else if(mode == 2){
                explain_mode_textview.setText("Hard");
                explain_mode_textview.setTextColor(Color.RED);
                explain_textview_01.setText("1. 15초 이상의 광고 영상 한편을 시청하시게 됩니다.");
                explain_textview_02.setText("2. 영상 종료 이후 곧바로 문제를 푸시게 됩니다.");
                explain_textview_03.setText("3. 총 5문제를 푸시며, 제한 시간은 15초 입니다.");
                explain_textview_04.setText("4. 모든 문제를 푸시면 결과가 저장됩니다." +
                        "\n     '메뉴 > 성장과정 저장'을 통해 모든 결과를 보실 수 있습니다.");

            }else{
                Log.d(TAG, "Setting MODE : flase");
            }

            Road_question(mode);

        }else{
            Log.d(TAG, "Setting REF : flase");
        }
    }

    public void Road_question(int mode){
        Thread t;
        Log.d("Road Game", "Road_question");
        switch (mode){
            case 0 :
                Log.d("Road Game", "Road_question mode : " + mode);

                t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        rootRef.child("GAME").child("UNDER5").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Question_num = dataSnapshot.getChildrenCount();
                                Random random = new Random(System.currentTimeMillis());
                                type_num = random.nextInt(Integer.parseInt(""+Question_num));
                                Log.d("TYPE_NUM", "num : " + (type_num+1));
                                rootRef.child("GAME").child("UNDER5").child("TYPE_"+(type_num+1)).
                                        addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Log.d("Road Game", "Data SnapShot");
                                                for (DataSnapshot Snapshot: dataSnapshot.child("game").getChildren()) {
                                                    Log.d("Road Game", "Data SnapShot : " + Snapshot.getKey());
                                                    gqic = Snapshot.getValue(Game_Question_Info_Class.class);
                                                    URL = gqic.getMovie_URL();
                                                    Log.d("Road Game", "Data SnapShot : " + URL);
                                                }

                                                if(dataSnapshot.child("show").child("num").getValue(Integer.class) == null){
                                                    num_view = 0;
                                                }else{
                                                    num_view = dataSnapshot.child("show").child("num").getValue(Integer.class);
                                                }
                                                rootRef.child("GAME").child("UNDER5").child("TYPE_"+(type_num+1)).child("show").child("num").setValue(num_view + 1);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                });
                t.start();
                break;
            case 1 :
                Log.d("Road Game", "Road_question mode : " + mode);

                t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        rootRef.child("GAME").child("UNDER15").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Question_num = dataSnapshot.getChildrenCount();

                                Random random = new Random(System.currentTimeMillis());
                                type_num = random.nextInt(Integer.parseInt(""+Question_num));
                                Log.d("TYPE_NUM", "num : " + (type_num+1));
                                rootRef.child("GAME").child("UNDER15").child("TYPE_"+(type_num+1)).
                                        addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Log.d("Road Game", "Data SnapShot");
                                                for (DataSnapshot Snapshot: dataSnapshot.child("game").getChildren()) {
                                                    Log.d("Road Game", "Data SnapShot : " + Snapshot.getKey());
                                                    gqic = Snapshot.getValue(Game_Question_Info_Class.class);
                                                    URL = gqic.getMovie_URL();
                                                    Log.d("Road Game", "Data SnapShot : " + URL);
                                                }

                                                if(dataSnapshot.child("show").child("num").getValue(Integer.class) == null){
                                                    num_view = 0;
                                                }else{
                                                    num_view = dataSnapshot.child("show").child("num").getValue(Integer.class);
                                                }
                                                rootRef.child("GAME").child("UNDER15").child("TYPE_"+(type_num+1)).child("show").child("num").setValue(num_view + 1);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                t.start();
                break;
            case 2 :
                Log.d("Road Game", "Road_question mode : " + mode);

                t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        rootRef.child("GAME").child("UPPER60").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Question_num = dataSnapshot.getChildrenCount();
                                Random random = new Random(System.currentTimeMillis());
                                type_num = random.nextInt(Integer.parseInt(""+Question_num));
                                Log.d("TYPE_NUM", "num : " + (type_num+1));
                                rootRef.child("GAME").child("UPPER60").child("TYPE_"+(type_num+1)).
                                        addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Log.d("Road Game", "Data SnapShot");
                                                for (DataSnapshot Snapshot: dataSnapshot.child("game").getChildren()) {
                                                    Log.d("Road Game", "Data SnapShot : " + Snapshot.getKey());
                                                    gqic = Snapshot.getValue(Game_Question_Info_Class.class);
                                                    URL = gqic.getMovie_URL();
                                                    Log.d("Road Game", "Data SnapShot : " + URL);
                                                }

                                                if(dataSnapshot.child("show").child("num").getValue(Integer.class) == null){
                                                    num_view = 0;
                                                }else{
                                                    num_view = dataSnapshot.child("show").child("num").getValue(Integer.class);
                                                }
                                                rootRef.child("GAME").child("UPPER60").child("TYPE_"+(type_num+1)).child("show").child("num").setValue(num_view + 1);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                                           }
                });
                t.start();
                break;
            default:
                break;
        }

    }

}
