package com.virex.giftest.ui.Html;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.text.Html;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.virex.giftest.R;

import java.io.IOException;
import java.util.HashMap;

public class GlideImageGetter implements Html.ImageGetter{

    private final Resources resources;
    private final TextView textView;
    HashMap<String, String> icons;
    int width=-1;

    public GlideImageGetter(Resources resources, TextView tv_text, int width) {
        this.resources = resources;
        this.textView = tv_text;
        this.width = width;
        new GlideImageGetter(resources, tv_text);
    }

    public GlideImageGetter(Resources resources, TextView target) {
        super();
        this.resources = resources;
        this.textView = target;
    }

    @Override
    public Drawable getDrawable(String source) {
        if (source==null) return null;

        final FutureDrawable result = new FutureDrawable(resources);

        Drawable empty = ContextCompat.getDrawable(textView.getContext(), R.drawable.ic_empty_image);
        if (this.width>0){
            empty = new ScaleDrawable(empty, 0, width, width).getDrawable();
            empty.setBounds(0, 0, this.width, this.width);
            result.setBounds(0, 0, this.width, this.width);
        } else
            result.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

        result.setDrawable(empty);

        final String imgsource;

        imgsource=source;

        /*
        if (!result.isLocalImage)
            if (VideoUtils.isVideoFast(source))
                result.isVideo=true;
        */
        result.isLocalImage=true;

        if (result.isLocalImage) {
            String filename = imgsource.replace("file:///android_asset/", "");

            BaseGifDrawable gif;
            try {

                gif = new MyGifDrawable(textView.getResources().getAssets().open(filename), new MyGifDrawable.UpdateListener() {
                    @Override
                    public void update(Drawable drawable) {
                        //textView.invalidate();
                        //textView.invalidate(drawable.getBounds());
                    }
                });

                gif.setOneShot(false);
                gif.setCallback(textView);
                gif.setVisible(true,true);

                result.isGif = true;
                result.setDrawable(gif);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    public class FutureDrawable extends Drawable  {
        private Drawable drawable;
        public boolean isGif=false;
        public boolean isVideo=false;
        public boolean isLocalImage=false;
        Bitmap play;

        private Bitmap getBitmap(Drawable vectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
            return bitmap;
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        FutureDrawable(Resources res) {
            play = getBitmap(res.getDrawable(R.drawable.ic_play));
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            if(drawable != null) {
                drawable.draw(canvas);
                if (!isLocalImage && (isGif || isVideo)) {
                    //рисуем значок поверх изображения

                    //если гифка запущена - не рисуем
                    //if (this.drawable instanceof GifDrawable && ((GifDrawable) this.drawable).isRunning())
                        //return;

                    //т.к. размер картинки может быть не пропорциональным
                    //берем размер например высоты
                    int width = getBounds().width() / 4;
                    int height = width;

                    if (width == 0 | height == 0) {
                        width = play.getWidth();
                        height = play.getHeight();
                    }

                    play = Bitmap.createScaledBitmap(play, width, height, false);

                    int x = (getBounds().width() - width) / 2;
                    int y = (getBounds().height() - height) / 2;
                    canvas.drawBitmap(play, x, y, new Paint(Paint.FILTER_BITMAP_FLAG));
                }
            }
        }


        @NonNull
        @Override
        public Drawable getCurrent() {
            return drawable;
        }

        @Override
        public void setAlpha(int i) {
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
        }

        @Override
        public int getOpacity() {
            return PixelFormat.UNKNOWN;
        }

        public void setDrawable(Drawable drawable){
            this.drawable=drawable;
        }

    }

}
