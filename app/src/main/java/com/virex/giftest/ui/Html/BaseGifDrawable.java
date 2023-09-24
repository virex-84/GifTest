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

        /*
        фикс ушами: для гифок с одним кадром - добавляем еще один,
        и используем только кадр 1 а не 0 (см MyGifDrawable где поправка на использование 1 кадра как начального)
        т.к. setBounds для текущего AnimationDrawable применяется позже добавления первого кадра
        и масштабирование первого кадра не срабатывает
         */
        int realcount=decoder.getFrameCount();
        int count=realcount;
        if (realcount==1)
            count=2;

        // Iterate through the gif frames, add each as animation frame
        for (int i = 0; i < count; i++) {
            Bitmap bitmap = null;
            if (realcount==1)
                bitmap = decoder.getFrame(0);
            else
                bitmap = decoder.getFrame(i);
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
