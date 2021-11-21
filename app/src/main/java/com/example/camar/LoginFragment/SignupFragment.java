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
    private String countryCode="+91";
    private String phoneNumber;
    private Button getOtp;
    private ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_signup, container, false);
        editNumber=view.findViewById(R.id.phone_number);
        picker=view.findViewById(R.id.country_code);
        getOtp=view.findViewById(R.id.get_otp);
        progressBar=view.findViewById(R.id.progress);
        picker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCode=picker.getSelectedCountryCodeWithPlus();
            }
        });
        getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber=editNumber.getText().toString();
                if(phoneNumber=="")
                    Toast.makeText(getContext(),"Please Enter A Valid Phone Number",Toast.LENGTH_SHORT).show();
                else
                    sendOTP();
            }
        });
        return view;
    }
    private void sendOTP() {
        progressBar.setVisibility(View.VISIBLE);
        getOtp.setVisibility(View.INVISIBLE);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(countryCode+phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(getActivity())
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        progressBar.setVisibility(View.GONE);
                        getOtp.setVisibility(View.VISIBLE);
                        NavDirections action=SignupFragmentDirections.actionSignupFragmentToVerifySignupFragment(countryCode,phoneNumber,verificationId);
                        Navigation.findNavController(getView()).navigate(action);
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        progressBar.setVisibility(View.GONE);
                        getOtp.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        progressBar.setVisibility(View.GONE);
                        getOtp.setVisibility(View.VISIBLE);

                        Toast.makeText(getContext(), "Incorrect Phone Number Or Country Code", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}