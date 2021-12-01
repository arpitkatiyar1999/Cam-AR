package com.example.camar;

import android.app.Activity;
import android.net.Uri;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.concurrent.TimeUnit;

public class FirebaseAPI {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public FirebaseAPI()
    {
        //initialize firebaseauth
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public boolean storeImageToFirebase(Uri result)
    {
        final boolean[] isStored = {false};
        //creating a database reference
        databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getCurrentUser().getUid());
        //creating a storage reference
        storageReference = FirebaseStorage.getInstance().getReference(firebaseAuth.getCurrentUser().getUid());
        //creating a storage reference to a place where we want to store images
        final StorageReference fileRef=storageReference.child(result.getLastPathSegment());
        //pushing file to firebase storage
        fileRef.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //adding image link to database
                        databaseReference.child(result.getLastPathSegment()).setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                isStored[0] =true;
                            }
                        });
                    }
                });
            }
        });
        return isStored[0];
    }

    public PhoneAuthOptions.Builder getPhoneAuthOptionsBuilder(String countryCode, String number, Activity activity,Long timeout,TimeUnit timeUnit) {
        PhoneAuthOptions.Builder builder=PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(countryCode+number)
                .setTimeout(timeout, timeUnit)
                .setActivity(activity);
        return builder;
    }
}
