package com.example.camar.LoginFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerifySignupFragment extends Fragment {
    ProgressBar progressBar;
    EditText o1,o2,o3,o4,o5,o6;
    TextView numberShow,countryCodeSet;
    View view;
    Button verify;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_verify_signup, container, false);
        initialize();
        String number = VerifySignupFragmentArgs.fromBundle(getArguments()).getPhoneNumber();
        String id=VerifySignupFragmentArgs.fromBundle(getArguments()).getVerificationId();
        String countryCode=VerifySignupFragmentArgs.fromBundle(getArguments()).getCountryCode();
        countryCodeSet.setText(countryCode);
        numberShow.setText(number);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp=o1.getText().toString().trim()+o2.getText().toString().trim()+o3.getText().toString().trim()+o4.getText().toString().trim()+o5.getText().toString().trim()+o6.getText().toString().trim();
                if(otp.length()!=6)
                    Toast.makeText(getContext(),"Please enter correct OTP!!!",Toast.LENGTH_SHORT).show();
                else
                {
                    if(id!=null)
                    {
                        progressBar.setVisibility(View.VISIBLE);
                        verify.setVisibility(View.INVISIBLE);
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, otp);
                        FirebaseAuth
                                .getInstance()
                                .signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    progressBar.setVisibility(View.VISIBLE);
                                    verify.setVisibility(View.INVISIBLE);
                                    NavDirections action=VerifySignupFragmentDirections.actionVerifySignupFragmentToGalleryFragment();
                                    Navigation.findNavController(getView()).navigate(action);
                                }
                                else
                                {   progressBar.setVisibility(View.INVISIBLE);
                                    verify.setVisibility(View.VISIBLE);
                                    Toast.makeText(getContext(),"Invalid OTP!!!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
            }
        });
        moveCursor();
        return view;
    }

    private void initialize() {
        numberShow = view.findViewById(R.id.number_show);
        verify = view.findViewById(R.id.verify_otp);
        o1=view.findViewById(R.id.l1);
        o2=view.findViewById(R.id.l2);
        o3=view.findViewById(R.id.l3);
        o4=view.findViewById(R.id.l4);
        o5=view.findViewById(R.id.l5);
        o6=view.findViewById(R.id.l6);
        countryCodeSet=view.findViewById(R.id.country_code);
        progressBar=view.findViewById(R.id.progress);
    }
    private void moveCursor()
    {
        o1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty())
                    o2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        o2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty())
                    o3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        o3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty())
                    o4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        o4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty())
                    o5.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        o5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty())
                    o6.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


}