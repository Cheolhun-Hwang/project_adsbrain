package com.adsbrain.hch.adsbrainapp;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;


public class MenuFragment extends DialogFragment {
    private View v;

    private ImageButton quit_imgbtn;

    private ImageButton story_imgbtn;
    private ImageButton qa_imgbtn;
    private ImageButton store_imgbtn;
    private ImageButton logout_imgbtn;

    private ProgressDialog progressDialog;
    private DatabaseReference ref;


    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        v = inflater.inflate(R.layout.fragment_menu, null);
        ref = FirebaseDatabase.getInstance().getReference();

        story_imgbtn = (ImageButton) v.findViewById(R.id.menu_story_imgbtn);
        qa_imgbtn = (ImageButton) v.findViewById(R.id.menu_qa_imgbtn);
        store_imgbtn = (ImageButton) v.findViewById(R.id.menu_store_data_imgbtn);
        logout_imgbtn = (ImageButton) v.findViewById(R.id.menu_logout_imgbtn);
        quit_imgbtn = (ImageButton) v.findViewById(R.id.menu_close_imgbtn);

        set_eventListener();


        builder.setView(v);
        return builder.create();
    }

    private void set_eventListener(){
        story_imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.main_container, new StoryLineFragment()).commit();
            }
        });

        qa_imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.main_container, new QA()).commit();
            }
        });

        store_imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setTitle("데이터 저장 중");
                progressDialog.setMessage("서버에서 입력받고 있습니다.");

                Thread store = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ref.child("ACCOUNT").child(((MainActivity)getActivity()).getWho()+"_"+((MainActivity)getActivity()).getNickname()).child("STORE_SCORE").
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        ArrayList<store_data> array = new ArrayList<store_data>();
                                        for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                                            Log.d("Road Game", "Data SnapShot : " + Snapshot.getKey());
                                            array.add(Snapshot.getValue(store_data.class));
                                        }

                                        try{
                                            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                                            File file = new File(path, "adsbrain_"+((MainActivity)getActivity()).getCurrent_Date_time()+".csv");
                                            file.createNewFile();

                                            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getPath()), "euc-kr"));

                                            String data = "이름, 닉네임, 성별, 나이, 날짜, 문제URL, 결과, Q1, Q2, Q3, Q4, Q5 \n";

                                            for(int i = 0 ; i< array.size() ; i++){
                                                data = data + array.get(i).getWho() + ", " + array.get(i).getNickname() + ", " + array.get(i).getSexual() + ", " +
                                                        array.get(i).getAge() + ", " + array.get(i).getDate() + ", " +
                                                        array.get(i).getVideo_url() + ", " + array.get(i).getScore() + ", " +
                                                        array.get(i).isQ1() + ", " + array.get(i).isQ2() + ", " + array.get(i).isQ3() + ", "+
                                                        array.get(i).isQ3() + ", " + array.get(i).isQ5() +"\n";
                                                Log.d("Store", data);
                                            }
                                            output.write(data);
                                            output.close();

                                            progressDialog.dismiss();

                                            Toast.makeText(getActivity(), "[다운로드]에 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();

                                            dismiss();
                                        }catch (Exception e){
                                            e.printStackTrace();
                                            Toast.makeText(getActivity(), "권한 설정이 필요합니다.\n설정 > 앱권한", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                });

                store.start();

            }
        });

        logout_imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                ((MainActivity)getActivity()).google_logout();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.main_container, new LoginFragment()).commit();
            }
        });

        quit_imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}
