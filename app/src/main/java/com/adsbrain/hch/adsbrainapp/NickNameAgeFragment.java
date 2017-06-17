package com.adsbrain.hch.adsbrainapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class NickNameAgeFragment extends Fragment {
    private View v;

    private EditText nickname_edittext;
    private EditText age_edittext;
    private Button ok_btn;
    private RadioButton man;
    private RadioButton woman;


    public NickNameAgeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_nick_name_age, container, false);

        nickname_edittext = (EditText) v.findViewById(R.id.nickname_nickname_Edittextview);
        age_edittext = (EditText) v.findViewById(R.id.nickname_age_Edittext);
        ok_btn = (Button) v.findViewById(R.id.nickname_complete_btn);
        man = (RadioButton) v.findViewById(R.id.sexual_man);
        woman = (RadioButton) v.findViewById(R.id.sexual_woman);

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nickname_edittext.length() <= 0 || age_edittext.length() <= 0 || (!(man.isChecked())&&!(woman.isChecked()))){
                    Toast.makeText(getContext(), "모두 작성하셔야 합니다!!", Toast.LENGTH_SHORT).show();
                }else{
                    String Nickname_text = nickname_edittext.getText().toString();
                    String Age_text = age_edittext.getText().toString();
                    Pattern pattern = Pattern.compile("^[0-9]*$");
                    Matcher matcher = pattern.matcher(Age_text);
                    if(!(matcher.matches())){
                        Toast.makeText(getContext(), "나이를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }else{
                        int Age_int = Integer.parseInt(Age_text);
                        if(Age_int >= 100 || Age_int <= 0){
                            Toast.makeText(getContext(), "나이를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }else{
                            ((MainActivity)getActivity()).setNickname(Nickname_text.replaceAll(" ", ""));
                            ((MainActivity)getActivity()).setAge(Age_int);

                            if(man.isChecked()){
                                ((MainActivity)getActivity()).setSexual("man");
                            }else if(woman.isChecked()){
                                ((MainActivity)getActivity()).setSexual("woman");
                            }

                            Toast.makeText(getContext(), "이름 : " + ((MainActivity)getActivity()).getWho() + "\n닉네임 : " + Nickname_text.replaceAll(" ", "") + "\n나이 : " + Age_int+ "\n성별 : " + ((MainActivity)getActivity()).getSexual(), Toast.LENGTH_LONG).show();
                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            manager.beginTransaction().replace(R.id.main_container, new MainHomeFragment()).commit();
                            ((MainActivity)getActivity()).Main_BGM_restart();
                        }
                    }
                }
            }
        });
        return v;
    }

}
