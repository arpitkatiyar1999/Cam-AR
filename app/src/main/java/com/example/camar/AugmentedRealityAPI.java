package com.example.camar;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.PixelCopy;

import androidx.appcompat.app.AlertDialog;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.IOException;
import java.io.OutputStream;

public class AugmentedRealityAPI {
    private final ArFragment arFragment;
    private final Context context;
    private Bitmap bitmap;
    private Uri uri;

    AugmentedRealityAPI(ArFragment arFragment, Context context) {
        this.context = context;
        this.arFragment = arFragment;
    }

    public void placeObject() {
        //on tapping the place place the 3-D Image
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            Anchor anchor = hitResult.createAnchor();
            ModelRenderable
                    .builder()
                    .setSource(context, Uri.parse("TocoToucan.sfb"))
                    .build()
                    .thenAccept(modelRenderable -> addModelToScene(anchor, modelRenderable))
                    .exceptionally(throwable -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Something is not right" + throwable.getMessage()).show();
                        return null;
                    });


        });
    }

    //adding the model to scene
    private void addModelToScene(Anchor anchor, ModelRenderable modelRenderable) {
        AnchorNode node = new AnchorNode(anchor);
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(node);
        transformableNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(node);
        transformableNode.select();
    }

    public Uri saveBitmapToStorage(Bitmap.CompressFormat compressFormat, String mimeType, String displayName) {
        ArSceneView view = arFragment.getArSceneView();
        //capturing bitmap
        bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        PixelCopy.request(view, bitmap, copyResult -> {
            if (copyResult == PixelCopy.SUCCESS) {
                try {
                    //saving bitmap to DCIM
                    uri = saveBitmap(compressFormat, mimeType, displayName);
                } catch (Exception e) {
                    Log.e("From SaveBitmapToStorage",e.getMessage());
                }
            }
        }, new Handler());
        return uri;
    }

    private Uri saveBitmap(Bitmap.CompressFormat format, String mimeType, String displayName) throws IOException {
        final ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);

        final ContentResolver resolver = context.getContentResolver();
        Uri uri = null;

        try {
            final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            uri = resolver.insert(contentUri, values);

            if (uri == null)
                throw new IOException("Failed to create new MediaStore record.");

            try (final OutputStream stream = resolver.openOutputStream(uri)) {
                if (stream == null)
                    throw new IOException("Failed to open output stream.");

                if (!bitmap.compress(format, 95, stream))
                    throw new IOException("Failed to save bitmap.");
            }

            return uri;
        } catch (IOException e) {

            if (uri != null) {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null);
            }

            throw e;
        }
    }
}
