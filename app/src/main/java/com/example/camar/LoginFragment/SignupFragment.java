package com.example.camar.LoginFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.camar.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class SignupFragment extends Fragment {
    private EditText editNumber;
    private CountryCodePicker picker;
    private String countryCode;
    private String phoneNumber;
    private FloatingActionButton getOtp;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        editNumber = view.findViewById(R.id.phone_number);
        picker = view.findViewById(R.id.country_code);
        getOtp = view.findViewById(R.id.get_otp);
        progressBar = view.findViewById(R.id.progress);
        //get selected country cde
        countryCode = picker.getSelectedCountryCodeWithPlus();
        //get the changed country code
        picker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCode = picker.getSelectedCountryCodeWithPlus();
            }
        });

        getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check the number is given by user or not
                phoneNumber = editNumber.getText().toString().trim();
                if (phoneNumber.matches(""))
                    Toast.makeText(getContext(), "Please Enter A Mobile Number", Toast.LENGTH_SHORT).show();
                else
                    //send otp to given number
                    sendOTP();
            }
        });
        return view;
    }

    private void sendOTP() {
        progressBar.setVisibility(View.VISIBLE);
        getOtp.setVisibility(View.INVISIBLE);
        //get Firebase authentication  instance
        FirebaseAuth auth = FirebaseAuth.getInstance();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(countryCode + phoneNumber)                      //Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS)                    // Timeout and unit
                .setActivity(getActivity())
                //OnVerificationStateChangedCallbacks
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    //if code sent successfully
                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        progressBar.setVisibility(View.GONE);
                        getOtp.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Otp Sent Successfully", Toast.LENGTH_SHORT).show();
                        //send verification id and phone number to VerifySignup Fragment
                        NavDirections action = SignupFragmentDirections.actionSignupFragmentToVerifySignupFragment(countryCode, phoneNumber, verificationId);
                        Navigation.findNavController(getView()).navigate(action);
                    }

                    //if number verification is completed
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        progressBar.setVisibility(View.GONE);
                        getOtp.setVisibility(View.VISIBLE);
                    }

                    //if number verification failed
                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        progressBar.setVisibility(View.GONE);
                        getOtp.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Incorrect Phone Number Or Country Code", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        //Request firebase to verify user's phone number and send otp
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}