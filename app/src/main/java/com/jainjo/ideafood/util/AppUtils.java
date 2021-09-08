package com.jainjo.ideafood.util;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppUtils {
    private static AppUtils instance;
    private RequestQueue requestQueue;
    private static Context ctx;
    private static Map<String,String> bearerTokenHeader;
    private static String username;
    private static String apiToken;

    private AppUtils(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
        bearerTokenHeader = new HashMap<String,String>();
    }

    public static synchronized AppUtils getInstance(Context context) {
        if( instance == null ) { instance = new AppUtils(context); }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if( requestQueue == null ) { requestQueue = Volley.newRequestQueue(ctx); }
        return  requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public Map<String,String> getBearerTokenHeader() {
        bearerTokenHeader.clear();
        if( apiToken != null && !apiToken.isEmpty() )
        { bearerTokenHeader.put("Authorization", "Bearer " + apiToken); }
        return bearerTokenHeader;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    //public String getApiToken() { return apiToken; }
    public void setApiToken(String apiToken) { this.apiToken = apiToken; }

    public void loadPreferences() {
        username = AppPreferences.getUsername();
        apiToken = AppPreferences.getApiToken();
    }

    public static Intent getPickImageChooserIntent() { return getPickImageChooserIntent(ctx); }
    public static Intent getPickImageChooserIntent(Context context) {
        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri(context);

        List<Intent> allIntents = new ArrayList<Intent>();
        PackageManager packageManager = context.getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = null;
        if (allIntents.size() > 0) {
            mainIntent = allIntents.get(allIntents.size() - 1);
        }
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        if( mainIntent != null ) {
            // Create a chooser from the main intent
            Intent chooserIntent = Intent.createChooser(mainIntent, "Selecciona el origen");
            // Add all other intents
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));
            return chooserIntent;
        }

        return null;
    }

    private static Uri getCaptureImageOutputUri(Context context) {
        Uri outputFileUri = null;
        File externalCacheDir = context.getExternalCacheDir();
        if (externalCacheDir != null) {
            outputFileUri = Uri.fromFile(new File(externalCacheDir.getPath(), "producto.png"));
        }
        return outputFileUri;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {
        return rotateImageIfRequired(ctx,img,selectedImage);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {
        ExifInterface ei = new ExifInterface(context.getContentResolver().openInputStream(selectedImage));
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public static Uri getPickImageResultUri(Intent data) {
        return getPickImageResultUri(ctx,data);
    }
    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br />
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public static Uri getPickImageResultUri(Context context, Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri(context) : data.getData();
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static ArrayList findUnAskedPermissions(Context context, ArrayList<String> wanted) {
        ArrayList result = new ArrayList();
        for (String perm : wanted) {
            if (!hasPermission(context,perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    public static boolean hasPermission(Context context, String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private static boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public static void showMessageOKCancel(Context context, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
