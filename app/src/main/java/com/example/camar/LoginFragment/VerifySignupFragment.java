package com.example.camar.LoginFragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camar.MainActivity;
import com.example.camar.NavGraphDirections;
import com.example.camar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VerifySignupFragment extends Fragment implements OnCompleteListener<AuthResult>{
    private ProgressBar progressBar;
    private EditText o1, o2, o3, o4, o5, o6;
    private TextView numberShow, countryCodeSet,timer;
    private LinearLayout otpNotSend;
    private View view;
    private String countryCode,number,id;
    private FloatingActionButton verify;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_verify_signup, container, false);
        //initialize all variables
        initialize();
        //set the timer to resend otp
        timer();
        countryCodeSet.setText(countryCode);
        numberShow.setText(number);
        verify.setOnClickListener(view -> {
            //get otp entered by user
            String otp = o1.getText().toString().trim() + o2.getText().toString().trim() + o3.getText().toString().trim() + o4.getText().toString().trim() + o5.getText().toString().trim() + o6.getText().toString().trim();
            //check the otp length
            if (otp.length() != 6)
                Toast.makeText(getContext(), "Please enter correct OTP!!!", Toast.LENGTH_SHORT).show();
            else {
                if (id != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    verify.setVisibility(View.INVISIBLE);
                    //verify the given otp is correct or not
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, otp);
                    ((MainActivity)getActivity()).getFirebaseAPI().getFirebaseAuth()
                            .signInWithCredential(credential)
                            .addOnCompleteListener(VerifySignupFragment.this::onComplete);
                }

            }
        });
        moveCursor();
        view.findViewById(R.id.resend_otp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendOtp();
            }
        });
        return view;
    }
    //resend the otp
    private void resendOtp() {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthOptions options = ((MainActivity)getActivity()).getPhoneAuthOptionsBuilder(countryCode,number,60L,TimeUnit.SECONDS)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    //if code send successfully
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(),"OTP Sent Successfully",Toast.LENGTH_SHORT).show();

                    }
                    //on verification of mobile number
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        progressBar.setVisibility(View.GONE);
                    }
                    //if user entered wrong number
                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Incorrect Phone Number Or Country Code", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    //initializing all variables
    private void initialize() {
        numberShow = view.findViewById(R.id.number_show);
        verify = view.findViewById(R.id.verify_otp);
        o1 = view.findViewById(R.id.l1);
        o2 = view.findViewById(R.id.l2);
        o3 = view.findViewById(R.id.l3);
        o4 = view.findViewById(R.id.l4);
        o5 = view.findViewById(R.id.l5);
        o6 = view.findViewById(R.id.l6);
        timer=view.findViewById(R.id.timer);
        otpNotSend=view.findViewById(R.id.otp_not_sent);
        countryCodeSet = view.findViewById(R.id.country_code);
        progressBar = view.findViewById(R.id.progress);
        //get number send from signup fragment
        number = VerifySignupFragmentArgs.fromBundle(getArguments()).getPhoneNumber();
        //get id send from signup fragment
        id = VerifySignupFragmentArgs.fromBundle(getArguments()).getVerificationId();
        //get country code send from signup fragment
        countryCode = VerifySignupFragmentArgs.fromBundle(getArguments()).getCountryCode();
    }
    //move the cursor between 6 otp edit text
    private void moveCursor() {
        o1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //if edit text is not empty
                if (!charSequence.toString().trim().isEmpty())
                    //move cursor to edit text 2
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
                //if edit text is not empty
                if (!charSequence.toString().trim().isEmpty())
                    //move cursor to edit text 3
                    o3.requestFocus();
                    //if edit text is empty
                else if (charSequence.toString().trim().isEmpty())
                    //move cursor to edit text 1
                    o1.requestFocus();
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
                //if edit text is not empty
                if (!charSequence.toString().trim().isEmpty())
                    //move cursor to edit text 4
                    o4.requestFocus();
                    //if edit text is empty
                else if (charSequence.toString().trim().isEmpty())
                    //move cursor to edit text 2
                    o2.requestFocus();
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
                //if edit text is not empty
                if (!charSequence.toString().trim().isEmpty())
                    //move cursor to edit text 5
                    o5.requestFocus();
                    //if edit text is empty
                else if (charSequence.toString().trim().isEmpty())
                    //move cursor to edit text 3
                    o3.requestFocus();
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
                //if edit text is not empty
                if (!charSequence.toString().trim().isEmpty())
                    //move cursor to edit text 6
                    o6.requestFocus();
                    //if edit text is empty
                else if (charSequence.toString().trim().isEmpty())
                    //move cursor to edit text 4
                    o4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        o6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //if edit text is empty
                if (charSequence.toString().trim().isEmpty())
                    //move cursor to edit text 5
                    o5.requestFocus();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        //check whether otp is verified or not
        if (task.isSuccessful()) {
            progressBar.setVisibility(View.VISIBLE);
            verify.setVisibility(View.INVISIBLE);
            //navigate to gallery
            NavDirections action = VerifySignupFragmentDirections.actionVerifySignupFragmentToGalleryFragment();
            Navigation.findNavController(getView()).navigate(action);
            Toast.makeText(getContext(),"Welcome "+number,Toast.LENGTH_SHORT).show();
            hideKeyboard();
        } else {
            //otp not verified
            progressBar.setVisibility(View.INVISIBLE);
            verify.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Invalid OTP!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void timer()
    {
        //convert time to milliseconds
        long duration=TimeUnit.MINUTES.toMillis(1);
        //create a timer
        new CountDownTimer(duration, 1000) {
            @Override
            //on each tick
            public void onTick(long l) {
                //on tick convert millis to seconds
                String time=String.format(Locale.ENGLISH,"%02d : %02d",TimeUnit.MILLISECONDS.toMinutes(l),TimeUnit.MILLISECONDS.toSeconds(l));
                timer.setText(time);
            }
            //when time finishes
            @Override
            public void onFinish() {
                timer.setVisibility(View.GONE);
                otpNotSend.setVisibility(View.VISIBLE);
            }
        }.start();
    }
}