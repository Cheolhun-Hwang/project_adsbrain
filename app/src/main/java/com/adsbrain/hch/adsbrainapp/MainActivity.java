package com.adsbrain.hch.adsbrainapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private FragmentManager manager;
    private MediaPlayer mp;

    public static final String ANONYMOUS = "anonymous";
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    public ProgressDialog progressDialog;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private boolean islogin = false;
    private Long Store_child;

    ///Import Data
    private String who;
    private String nickname;
    private int age;
    private String sexual;

    private Myscore myscore;
    private DatabaseReference rootRef;
    private store_data sd;

    //////LV_UP
    private int nowLV;
    private int nowLV_POINT;
    private ArrayList<Integer> LV_UP = new ArrayList<Integer>();

    public void setSexual(String sexual) {
        this.sexual = sexual;
    }

    public String getSexual() {
        return sexual;
    }

    public void setNowLV(int nowLV) {
        this.nowLV = nowLV;
    }

    public void setNowLV_POINT(int nowLV_POINT) {
        this.nowLV_POINT = nowLV_POINT;
    }

    public void setLV_UP(ArrayList<Integer> LV_UP) {
        this.LV_UP = LV_UP;
    }

    public int getNowLV() {
        return nowLV;
    }

    public int getNowLV_POINT() {
        return nowLV_POINT;
    }

    public ArrayList<Integer> getLV_UP() {
        return LV_UP;
    }

    public Myscore getMyscore() {
        return myscore;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public String getWho() {
        return who;
    }

    public String getNickname() {
        return nickname;
    }

    public int getAge() {
        return age;
    }

    public void setIslogin(boolean islogin) {
        this.islogin = islogin;
    }

    public boolean islogin() {
        return islogin;
    }

    public FragmentManager getManager() {
        return manager;
    }

    public FirebaseAuth getmFirebaseAuth() {
        return mFirebaseAuth;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ////////////////////////////API 23 + 데이터 저장 권한 묻기
        if (shouldAskPermissions()) {
            askPermissions();
        }

        ////////////////////초기화내용/////////////////////
        nowLV = 0;
        nowLV_POINT = 0;
        sexual="";
        setNickname(ANONYMOUS);
        setLV();
        rootRef = FirebaseDatabase.getInstance().getReference();
        myscore = new Myscore();

        ////////////////////BGM
        mp = MediaPlayer.create(getApplicationContext(), R.raw.bit_rush);
        mp.setLooping(true);


        ///////////////////첫 프래그먼트 로그인 프레그먼트로 셋팅
        manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.main_container, new LoginFragment()).commit();

        ////////////////////////구글 로그인 아래

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();


    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    private void setLV(){
        this.LV_UP.add(100);    //+100
        this.LV_UP.add(250);    //+150
        this.LV_UP.add(450);    //+200
        this.LV_UP.add(700);    //+250
        this.LV_UP.add(1000);    //+300
    }

    public void Main_BGM_start(){
        this.mp.start();
    }
    public void Main_BGM_stop(){
        this.mp.pause();
    }
    public void Main_BGM_restart(){
        this.mp.seekTo(0);
        this.mp.start();
    }

    public void store_server(String url, int score, boolean q1, boolean q2, boolean q3, boolean q4, boolean q5){
        sd = new store_data(getWho(), getNickname(), getSexual(), getAge(), getCurrent_Date_time(), url, score, q1, q2, q3, q4, q5);
        Log.d("store_score_for_server", "sexual : " + getSexual());
        try{

            rootRef.child("ACCOUNT").child(getWho()+"_"+getNickname()).child("STORE_SCORE").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Store_child = dataSnapshot.getChildrenCount();
                    if(Store_child == null){
                        Store_child = 0L;
                    }
                    rootRef.child("ACCOUNT").child(getWho()+"_"+getNickname()).child("STORE_SCORE").child("COUNT_"+Store_child).setValue(sd);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




        }catch (Exception e){
            e.printStackTrace();
            Log.e("Sever Store", "sever error, Can't store score. please check!!");
        }
    }

    public String getCurrent_Date_time(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfnow = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String stNow = sdfnow.format(date);
        return  stNow;
    }

    @Override
    protected void onPause() {
        this.Main_BGM_stop();
        this.saveCurrentstate();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        this.mp.release();
        google_logout();
        super.onDestroy();
    }

    @Override
    public void onStop(){
        google_logout();
        super.onStop();
    }

    @Override
    public void onResume(){
        this.Main_BGM_start();
        this.restoreFromeSavedState();


        super.onResume();
    }

    public void google_logout(){
        this.mFirebaseAuth.signOut();
    }

    public void isLevelUP(){
        if(this.nowLV_POINT >= this.LV_UP.get(this.nowLV)){
            this.nowLV ++;
        }
    }

    /////////////////////////////singin
    public void signIn( ) {
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.progressDialog.setTitle("구글 로그인 요청");
        this.progressDialog.setMessage("로그인하는 중입니다.");

        progressDialog.show();

        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                islogin = true;
            }
        });
        t.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed
                Log.e(TAG, "Google Sign In failed.");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            progressDialog.dismiss();
                        } else {
                            setWho(mFirebaseAuth.getCurrentUser().getDisplayName().toString());
                            progressDialog.dismiss();

                            if(getNickname().equals(ANONYMOUS)){
                                manager.beginTransaction().replace(R.id.main_container, new StoryLineFragment()).commit();
                            }else{
                                manager.beginTransaction().replace(R.id.main_container, new MainHomeFragment()).commit();
                            }
                        }
                    }
                });

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    //////////////////////////// save data nickname, age

    protected void saveCurrentstate(){
        try {

            FileOutputStream fos = openFileOutput("adsbraintmp.txt",
                    Context.MODE_PRIVATE);
            String str = nickname + "#" + age  + "#" + nowLV  + "#" + nowLV_POINT  + "#" +
                    myscore.getPE()  + "#" + myscore.getPEC()  + "#" + myscore.getPN()  + "#" +  myscore.getPNC()  + "#" + myscore.getPH()  + "#" + myscore.getRHC() + "#" +
                    myscore.getRE()  + "#" + myscore.getREC()  + "#" + myscore.getRN()  + "#" + myscore.getRNC()  + "#" + myscore.getRH()  + "#" + myscore.getRHC() + "#" + getSexual();
            fos.write(str.getBytes()); // String을 byte배열로 변환후 저장
            fos.close();

        } catch (Exception e) {
            Log.e("File", "에러=" + e);
        }
    }

    protected void restoreFromeSavedState(){
        try {

            FileInputStream fis = openFileInput("adsbraintmp.txt");
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            String str = new String(buffer);
            String[] split = str.split("#");

            nickname = split[0];
            age = Integer.parseInt(split[1]);
            nowLV = Integer.parseInt(split[2]);
            nowLV_POINT = Integer.parseInt(split[3]);
            myscore.setPE(Integer.parseInt(split[4]));
            myscore.setPEC(Integer.parseInt(split[5]));
            myscore.setPN(Integer.parseInt(split[6]));
            myscore.setPNC(Integer.parseInt(split[7]));
            myscore.setPH(Integer.parseInt(split[8]));
            myscore.setPHC(Integer.parseInt(split[9]));
            myscore.setRE(Integer.parseInt(split[10]));
            myscore.setREC(Integer.parseInt(split[11]));
            myscore.setRN(Integer.parseInt(split[12]));
            myscore.setRNC(Integer.parseInt(split[13]));
            myscore.setRH(Integer.parseInt(split[14]));
            myscore.setRHC(Integer.parseInt(split[15]));
            setSexual(split[16]);

            fis.close();

        } catch (Exception e) {
            Log.e("File", "에러=" + e);
        }
    }

    public class Myscore{
        private int PE;
        private int PEC;
        private int PN;
        private int PNC;
        private int PH;
        private int PHC;
        private int RE;
        private int REC;
        private int RN;
        private int RNC;
        private int RH;
        private int RHC;

        public Myscore( ) {
            this.PE = 0;
            this.PEC = 0;
            this.PN = 0;
            this.PNC = 0;
            this.PH = 0;
            this.PHC = 0;
            this.RE = 0;
            this.REC = 0;
            this.RN = 0;
            this.RNC = 0;
            this.RH = 0;
            this.RHC = 0;
        }

        public Myscore(int PE, int PEC, int PN, int PNC, int PH, int PHC, int RE, int REC, int RN, int RNC, int RH, int RHC) {
            this.PE = PE;
            this.PEC = PEC;
            this.PN = PN;
            this.PNC = PNC;
            this.PH = PH;
            this.PHC = PHC;
            this.RE = RE;
            this.REC = REC;
            this.RN = RN;
            this.RNC = RNC;
            this.RH = RH;
            this.RHC = RHC;
        }

        public void setPE(int PE) {
            this.PE = PE;
        }

        public void setPEC(int PEC) {
            this.PEC = PEC;
        }

        public void setPN(int PN) {
            this.PN = PN;
        }

        public void setPNC(int PNC) {
            this.PNC = PNC;
        }

        public void setPH(int PH) {
            this.PH = PH;
        }

        public void setPHC(int PHC) {
            this.PHC = PHC;
        }

        public void setRE(int RE) {
            this.RE = RE;
        }

        public void setREC(int REC) {
            this.REC = REC;
        }

        public void setRN(int RN) {
            this.RN = RN;
        }

        public void setRNC(int RNC) {
            this.RNC = RNC;
        }

        public void setRH(int RH) {
            this.RH = RH;
        }

        public void setRHC(int RHC) {
            this.RHC = RHC;
        }

        public int getPE() {
            return PE;
        }

        public int getPEC() {
            return PEC;
        }

        public int getPN() {
            return PN;
        }

        public int getPNC() {
            return PNC;
        }

        public int getPH() {
            return PH;
        }

        public int getPHC() {
            return PHC;
        }

        public int getRE() {
            return RE;
        }

        public int getREC() {
            return REC;
        }

        public int getRN() {
            return RN;
        }

        public int getRNC() {
            return RNC;
        }

        public int getRH() {
            return RH;
        }

        public int getRHC() {
            return RHC;
        }
    }

}
