package com.android.senyaar.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import id.zelory.compressor.Compressor;

/**
 * Created by ATTech_Android_1 on 01/01/2018.
 */

/**
 * Class to compress image when user upload image while editing profile.
 */
public class AppHelper {

    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] CompressImage(String path, Context context) {
        File imgFile = new File(path);
        Bitmap compressedImageBitmap;
        if (imgFile.exists()) {
            try {
                compressedImageBitmap = new Compressor(context).compressToBitmap(imgFile);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                return byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
