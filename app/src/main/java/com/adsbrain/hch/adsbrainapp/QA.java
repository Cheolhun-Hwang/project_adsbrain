package com.adsbrain.hch.adsbrainapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by qewqs on 2017-04-08.
 */

public class QA extends Fragment {
    View v;

    public static final String ANONYMOUS = "anonymous";
    String username="";
    private FirebaseAuth finfo;
    private String Username;

    Button qa_send_btn;
    Button qa_cancel_btn;
    EditText qa_title;
    EditText qa_contents;
    TextView checkTextLenth;

    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD_1 = "QA";
    public String MESSAGES_CHILD_2 = "Date";
    DatabaseReference mFirebaseDatabaseReference;
    public boolean isOverText;


    FragmentManager manager = getFragmentManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.qa_layout, container, false);

        isOverText = false;

        finfo = ((MainActivity)getActivity()).getmFirebaseAuth();

        checkTextLenth = (TextView) v.findViewById(R.id.checkTextLenth);
        qa_send_btn = (Button)v.findViewById(R.id.qa_send_btn);
        qa_cancel_btn = (Button)v.findViewById(R.id.qa_cancel_btn);
        qa_title = (EditText)v.findViewById(R.id.qa_title);
        qa_contents = (EditText)v.findViewById(R.id.qa_contents);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(1000);
        qa_contents.setFilters(FilterArray);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        qa_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Username = finfo.getCurrentUser().getDisplayName().toString();

                if(isOverText){
                    Toast.makeText(getActivity(), "본문 내용의 최대 길이를 확인해 주세요.", Toast.LENGTH_LONG).show();
                }else{
                    String stNow = ((MainActivity)getActivity()).getCurrent_Date_time();

                    MESSAGES_CHILD_2 = stNow;

                    qa_send_format QAMessage = new qa_send_format(Username, stNow,
                            qa_title.getText().toString(), qa_contents.getText().toString());
                    mFirebaseDatabaseReference.child(MESSAGES_CHILD_1).child(MESSAGES_CHILD_2).push().setValue(QAMessage);

                    Toast.makeText(getActivity(), "성공적으로 전송되었습니다.", Toast.LENGTH_SHORT).show();

                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.main_container, new MainHomeFragment()).commit();
                }

            }
        });

        qa_contents.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 1000){
                    checkTextLenth.setText("최대 길이 1000자 / " + s.length());
                    checkTextLenth.setTextColor(Color.RED);
                    isOverText = true;
                }else{
                    checkTextLenth.setText("최대 길이 1000자 / " + s.length());
                    checkTextLenth.setTextColor(Color.BLACK);
                    isOverText = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        qa_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.main_container, new MainHomeFragment()).commit();
            }
        });


        return v;


    }

    private class qa_send_format {
        private String who;
        private String when;
        private String title;
        private String contents;

        qa_send_format(){
            this.who = "";
            this.when = "";
            this.title = "";
            this.contents = "";
        }

        qa_send_format(String who, String when, String title, String contents){
            this.who = who;
            this.when = when;
            this.title = title;
            this.contents = contents;
        }

        public void setWho(String who){
            this.who = who;
        }
        public void setWhen(String when){
            this.when = when;
        }
        public void setTitle(String title){
            this.title = title;
        }
        public void setContents(String contents){
            this.contents = contents;
        }

        public String getWho(){
            return this.who;
        }
        public String getWhen(){
            return  this.when;
        }
        public String getTitle(){
            return  this.title;
        }
        public String getContents(){
            return  this.contents;
        }
    }
}
