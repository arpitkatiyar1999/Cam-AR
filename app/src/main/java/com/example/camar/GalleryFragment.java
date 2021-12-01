package com.example.camar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.camar.Adapters.PhotoAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {
    private NavDirections action;
    private NavController navController;
    private AlertDialog dialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog uploadDialog,loadingDialog;
    private ChildEventListener childEventListener;
    private ArrayList<String> images ;
    private PhotoAdapter adapter;
    private RecyclerView recyclerView;
    private ActivityResultLauncher<String> launcher;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_gallery, container, false);
        FloatingActionButton cameraLaunch=view.findViewById(R.id.camera_launch);
        uploadDialog=new ProgressDialog(getContext());
        uploadDialog.setTitle("Uploading Image");
        uploadDialog.setMessage("Please Wait a Moment");
        uploadDialog.setCancelable(false);
        images=new ArrayList<>();
        navController= Navigation.findNavController(getActivity(),R.id.nav_host_fragment);
        recyclerView=view.findViewById(R.id.recycler_view);
        adapter=new PhotoAdapter(images,getContext());
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference(firebaseAuth.getCurrentUser().getUid());
        cameraLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action=GalleryFragmentDirections.actionGalleryFragmentToCameraFragment();
                navController.navigate(action);
            }
        });
        onDatabaseDataChanges();
        setHasOptionsMenu(true);
        return view;
    }

    private void onDatabaseDataChanges() {
        childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String imageUrl=snapshot.getValue(String.class);
                images.add(imageUrl);
                adapter.notifyDataSetChanged();
                uploadDialog.dismiss();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        launcher=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if(result!=null) {
                    uploadDialog.show();
                    ((MainActivity)getActivity()).getFirebaseAPI().storeImageToFirebase(result);
                }
            }
        });

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
                        images.clear();
                        databaseReference.removeEventListener(childEventListener);

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
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