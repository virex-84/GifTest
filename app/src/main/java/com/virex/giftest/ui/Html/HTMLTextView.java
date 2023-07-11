package com.virex.giftest.ui.Html;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spanned;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.Vector;

public class HTMLTextView extends AppCompatTextView implements Drawable.Callback, MyGifDrawable.UpdateListener {
    private Vector<Drawable> drawables;

    public HTMLTextView(@NonNull Context context) {
        super(context);
    }

    public HTMLTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HTMLTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (drawables==null) {
            drawables = new Vector<Drawable>();
        }
        clearDrawables();
        initDrawables();
    }

    protected void initDrawables(){
        CharSequence text = getText();
        //вставляем все изображения
        if (text instanceof Spanned) {
            Spanned spanned = (Spanned) text;
            /*
            ImageSpan[] spans = spanned.getSpans(0, spanned.length() - 1, ImageSpan.class);
            for (ImageSpan imageSpan : spans) {
                Drawable drawable = imageSpan.getDrawable();
                drawables.add(drawable);

                //if (drawable instanceof AnimatedGifDrawable){
                    drawable.setCallback(HTMLTextView.this);
                    drawable.setVisible(true,true);
                //}
            }
             */

            AnimatedImageSpan[] spans2 = spanned.getSpans(0, spanned.length() - 1, AnimatedImageSpan.class);
            for (AnimatedImageSpan imageSpan : spans2) {
                Drawable drawable = imageSpan.getMainDrawable();

                if (drawable instanceof GlideImageGetter.FutureDrawable){
                    drawable = drawable.getCurrent();
                }

                drawables.add(drawable);
            }
        }
        startDrawables();
    }

    protected void startDrawables(){
        for (Drawable drawable : drawables){
            if (drawable instanceof BaseGifDrawable){
                drawable.setCallback(HTMLTextView.this);
                drawable.setVisible(true,true);
                ((BaseGifDrawable)drawable).start();
                if (drawable instanceof MyGifDrawable)
                    ((MyGifDrawable)drawable).setListener(HTMLTextView.this);
            }
        }
    }

    protected void stopDrawables(){
        for (Drawable drawable : drawables){
            if (drawable instanceof BaseGifDrawable) {
                drawable.setCallback(null);
                if (drawable instanceof MyGifDrawable) {
                    MyGifDrawable dr = ((MyGifDrawable) drawable);
                    dr.setListener(null);
                    dr.unscheduleSelf(dr);
                    dr.stop();
                }
            }
        }
    }

    protected void clearDrawables(){
        stopDrawables();
        drawables.clear();
    }


    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if (who != null && what != null) {
           postDelayed(what, when);
        }
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        if (who != null && what != null) {
            removeCallbacks(what);
        }
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        if (drawable instanceof BaseGifDrawable) {
            Drawable dr = ((MyGifDrawable)drawable).getDrawable();
            Bitmap b = ((BitmapDrawable)dr).getBitmap();
            b = b.copy(Bitmap.Config.ARGB_8888, true);
            drawable.draw(new Canvas(b));
        } else
            drawable.draw(new Canvas());

        super.invalidateDrawable(drawable);
    }

    @Override
    public void update(Drawable drawable) {
        //this.invalidate();
        this.postInvalidate(); //подпинываем анимацию в recuclerview
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initDrawables();
    }

    @Override
    protected void onDetachedFromWindow() {
        clearDrawables();
        super.onDetachedFromWindow();
    }


}
