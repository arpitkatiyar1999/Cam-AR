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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.camar.Adapters.PhotoAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {
    private NavDirections action;
    private NavController navController;
    private AlertDialog dialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
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
        navController= Navigation.findNavController(getActivity(),R.id.nav_host_fragment);
        recyclerView=view.findViewById(R.id.recycler_view);
        images=new ArrayList<>();
        adapter=new PhotoAdapter(images,getContext());
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference(firebaseAuth.getCurrentUser().getUid());
        storageReference=FirebaseStorage.getInstance().getReference(firebaseAuth.getCurrentUser().getUid());
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
                    ((MainActivity)getActivity()).getFirebaseAPI().storeImageToFirebase(result);
//                    final StorageReference fileRef=storageReference.child(result.getLastPathSegment());
//                    fileRef.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    databaseReference.child(result.getLastPathSegment()).setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void unused) {
//                                            progressDialog.dismiss();
//                                            Toast.makeText(getContext(),"Image Uploaded Successfully",Toast.LENGTH_SHORT).show();
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            progressDialog.dismiss();
//                                            Toast.makeText(getContext(),"Error Occurred while uploading image to database",Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    progressDialog.dismiss();
//                                    Toast.makeText(getContext(),"Error Occurred while uploading image to storage",Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                    });
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

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                }).create();
        databaseReference.removeEventListener(childEventListener);
        dialog.show();
    }
    private void selectFromStorage()
    {
        launcher.launch("image/*");
    }


}