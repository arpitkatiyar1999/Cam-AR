package com.example.camar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthOptions;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    //creating only a single instance of firebase api
    private FirebaseAPI firebaseAPI;

    public static boolean getIsPhotoClicked() {
        return isPhotoClicked;
    }

    public static void setIsPhotoClicked(boolean isPhotoClicked) {
        MainActivity.isPhotoClicked = isPhotoClicked;
    }

    public static boolean isPhotoClicked=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAPI=new FirebaseAPI();
    }
    //returning an instance of firebaseAPI
    public FirebaseAPI getFirebaseAPI()
    {
        return firebaseAPI;
    }
    //returning an instance of PhoneAuthOptions.Builder
    public PhoneAuthOptions.Builder getPhoneAuthOptionsBuilder(String countryCode, String number, Long timeout, TimeUnit timeUnit)
    {
        return firebaseAPI.getPhoneAuthOptionsBuilder(countryCode,number,this,timeout,timeUnit);
    }
    //returning a instance of firebase user
    public FirebaseUser getFireBaseUser()
    {
        return firebaseAPI.getFirebaseUser();
    }
}