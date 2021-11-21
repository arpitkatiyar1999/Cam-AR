package com.example.camar;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;

import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class GalleryFragment extends Fragment {
    private NavDirections action;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private NavController navController;
    private AlertDialog dialog;
    private ActivityResultLauncher<String> launcher;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_gallery, container, false);
        FloatingActionButton cameraLaunch=view.findViewById(R.id.camera_launch);
        navController= Navigation.findNavController(getActivity(),R.id.nav_host_fragment);
        cameraLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action=GalleryFragmentDirections.actionGalleryFragmentToCameraFragment();
                navController.navigate(action);
            }
        });
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        launcher=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                final StorageReference reference=firebaseStorage.getReference();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.gallery_option_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.about_app:
                action=NavGraphDirections.actionGlobalAboutAppFragment();
                navController.navigate(action);
                break;
            case R.id.delete_account:
                deleteAccount();
                break;
            case R.id.logout:
                logout();
                break;
            case R.id.from_storage:
                selectFromStorage();
                break;

        }
        return true;
    }

    private void logout() {
        dialog=new AlertDialog.Builder(getContext())
                .setTitle("Logout App")
                .setIcon(R.drawable.ic_warning)
                .setMessage("Do You Want To Logout From App")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getContext(),"Logged Out Successfully",Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(getView()).navigate(R.id.action_global_signupFragment);

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private void deleteAccount() {
        dialog=new AlertDialog.Builder(getContext())
                .setTitle("Delete Account")
                .setIcon(R.drawable.ic_warning)
                .setMessage("Your All Data Will Be Lost")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(),"User account deleted",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        Navigation.findNavController(getView()).navigate(R.id.action_global_signupFragment);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                     dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }
    private void selectFromStorage()
    {
        launcher.launch("image/*");
    }


}