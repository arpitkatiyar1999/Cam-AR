package com.example.camar;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class CameraFragment extends Fragment {
    private ArFragment arFragment;
    private Button capturePhoto;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_camera, container, false);
        capturePhoto=view.findViewById(R.id.capture_photo);
        ((MainActivity)getActivity()).getSupportActionBar().hide();
        Dexter.withContext(getContext()).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                ArCamera();
                capturePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dexter.withContext(getContext()).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                captureImage();
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                Intent intent=new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri=Uri.fromParts("package",((MainActivity)getActivity()).getPackageName(),null);
                                intent.setData(uri);
                                startActivity(intent);
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        });
                    }
                });
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent=new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri=Uri.fromParts("package",((MainActivity)getActivity()).getPackageName(),null);
                intent.setData(uri);
                startActivity(intent);

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
        return view;
    }

    private void captureImage() {

    }


    private void ArCamera()
    {
            arFragment=(ArFragment) getChildFragmentManager().findFragmentById(R.id.arFragment);
            arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
                    Anchor anchor = hitResult.createAnchor();
                    ModelRenderable
                            .builder()
                            .setSource(getContext(), Uri.parse("TocoToucan.sfb"))
                            .build()
                            .thenAccept(modelRenderable -> addModelToScene(anchor, modelRenderable))
                            .exceptionally(throwable -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setMessage("Something is not right" + throwable.getMessage()).show();
                                return null;
                            });


            });
    }
    private void addModelToScene(Anchor anchor, ModelRenderable modelRenderable) {
        AnchorNode node=new AnchorNode(anchor);
        TransformableNode transformableNode=new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(node);
        transformableNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(node);
        transformableNode.select();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity)getActivity()).getSupportActionBar().show();
    }
}