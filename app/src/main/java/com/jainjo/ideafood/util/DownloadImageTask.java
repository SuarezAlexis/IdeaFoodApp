package com.jainjo.ideafood.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView imageView;

    public DownloadImageTask(ImageView imageView) { this.imageView = imageView; }

    @Override
    protected Bitmap doInBackground(String... urls) {
        Bitmap bm = null;
        try {
            InputStream in = new URL(urls[0]).openStream();
            bm = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return bm;
    }

    @Override
    protected void onPostExecute(Bitmap result) { imageView.setImageBitmap(result); }
}
