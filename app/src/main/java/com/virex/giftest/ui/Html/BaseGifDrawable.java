package com.virex.giftest.ui.Html;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;

import java.io.InputStream;

/**
 * Базовый класс: загрузка фреймов
 */
public class BaseGifDrawable extends AnimationDrawable {

    public BaseGifDrawable(InputStream source) {
        GifDecoder decoder = new GifDecoder();
        decoder.read(source);

        // Iterate through the gif frames, add each as animation frame
        for (int i = 0; i < decoder.getFrameCount(); i++) {
            Bitmap bitmap = decoder.getFrame(i);
            BitmapDrawable drawable = new BitmapDrawable(bitmap);
            int width = bitmap.getWidth() * 3;
            int height = bitmap.getHeight() * 3;

            // Explicitly set the bounds in order for the frames to display
            drawable.setBounds(0, 0, width, height);

            addFrame(drawable, decoder.getDelay(i));

            if (i == 0) {
                // Also set the bounds for this container drawable
                setBounds(0, 0, width, height);
            }
        }
    }

}
