package com.example.android.moviesapp.utilities;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {

    private static Bitmap posterBitmap;

    public static String savePosterToInternalStorage(String posterAddress, String mdb_id, Context context){
        Picasso.with(context).load(posterAddress).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                posterBitmap = bitmap;
            }
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {}
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        });
        if(posterBitmap != null) {
            return saveToInternalStorageHelper(posterBitmap, context, mdb_id);
        }
        return null;
    }

    private static String saveToInternalStorageHelper(Bitmap bitmapImage, Context context, String mdb_id){
        context = context.getApplicationContext();
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File posterPath=new File(directory, "poster_" + mdb_id + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(posterPath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public static Bitmap getPosterFromStorage(String path, String mdb_id) {
        Bitmap bitmap = null;
        if(path == null) return bitmap;
        try {
            File file =new File(path, "poster_" + mdb_id + ".jpg");
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
