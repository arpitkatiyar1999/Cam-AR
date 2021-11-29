package com.example.camar;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenFragment extends Fragment {
    NavDirections action;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash_screen, container, false);
        //provide delay to screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //get current user
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //check user login
                if (user != null)
                    //Navigate to Gallery
                    action = SplashScreenFragmentDirections.actionSplashScreenFragmentToGalleryFragment();
                else
                    //Navigate to login Screen
                    action = SplashScreenFragmentDirections.actionSplashScreenFragmentToSignupFragment();
                Navigation.findNavController(view).navigate(action);
            }
        }, 1200);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Hide Action Bar
        ((MainActivity) getActivity()).getSupportActionBar().hide();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Show Action Bar
        ((MainActivity) getActivity()).getSupportActionBar().show();
        //Change color of status bar from white to blue
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#006680"));

    }
}