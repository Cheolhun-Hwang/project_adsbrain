package com.adsbrain.hch.adsbrainapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private final String file_name_one = "adsbraincachetxt.txt";
    private final String file_name_two = "adsbrainpalylogcachecsv.csv";

    private FragmentManager manager;
    private MediaPlayer mp;
    private File cache_file_one;

    public static final String ANONYMOUS = "anonymous";
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    public ProgressDialog progressDialog;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private String mUsername;
    private boolean islogin = false;

    public void setIslogin(boolean islogin) {
        this.islogin = islogin;
    }

    public boolean islogin() {
        return islogin;
    }

    public FragmentManager getManager() {
        return manager;
    }

    public File getCache_file_one() {
        return cache_file_one;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cache_file_one = new File(file_name_one);

        mp = MediaPlayer.create(getApplicationContext(), R.raw.bit_rush);
        mp.setLooping(true);

        manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.main_container, new LoginFragment()).commit();

        //////////
        mUsername = ANONYMOUS;
        ////////////////////////

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
        /////////

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

    public void save_internaldata(String data){
        try{
            FileOutputStream fos = openFileOutput(file_name_one, Context.MODE_PRIVATE);

            PrintWriter out = new PrintWriter(fos);
            out.println(data);
            out.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String show_internaldata(){
        StringBuffer data = new StringBuffer();
        try{
            FileInputStream fis = openFileInput(file_name_one);

            BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));
            String str = buffer.readLine();
            while(str != null){
               data.append(str+"\n");
                str = buffer.readLine();
            }
            buffer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return data.toString();
    }

    @Override
    public void onStop(){
        super.onStop();
        this.mp.release();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient); //(로그인 아이디 매번 다시 선택하기);
        google_logout();
    }

    public void google_logout(){
        this.mFirebaseAuth.signOut();
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
                            mUsername = mFirebaseAuth.getCurrentUser().getDisplayName().toString();
                            progressDialog.dismiss();

                            if(cache_file_one.exists()){
                                manager.beginTransaction().replace(R.id.main_container, new MainHomeFragment()).commit();
                            }else{
                                manager.beginTransaction().replace(R.id.main_container, new StoryLineFragment()).commit();
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

    ////////////////////////////

}
