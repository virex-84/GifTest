package com.virex.giftest.ui.Html;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.style.DynamicDrawableSpan;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AnimatedImageSpan extends DynamicDrawableSpan {

    private boolean isDrawind = true;
    private final Drawable mDrawable;

    public AnimatedImageSpan(Drawable d) {
        super();
        mDrawable = d;
    }

    public AnimatedImageSpan(Drawable d, boolean selfHandler) {
        super();
        mDrawable = d;

        if (!selfHandler) return;

        // Use handler for 'ticks' to proceed to next frame
        final Handler mHandler = new Handler();
        mHandler.post(new Runnable() {
            public void run() {
                Drawable dr = mDrawable;
                if (mDrawable instanceof GlideImageGetter.FutureDrawable) {
                    dr = mDrawable.getCurrent();
                }
                if (dr instanceof MyGifDrawable)
                    ((MyGifDrawable) dr).nextFrame();

                Drawable.Callback c = mDrawable.getCallback();
                if (c != null) {
                    //c.invalidateDrawable(dr);
                    if (c instanceof TextView)
                        if (isDrawind)
                            ((TextView) c).invalidate(dr.getBounds());
                }

                // Set next with a delay depending on the duration for this frame
                if (dr instanceof MyGifDrawable)
                    mHandler.postDelayed(this, ((MyGifDrawable) dr).getFrameDuration());
            }
        });
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        Drawable d = getDrawable();
        //java.lang.NullPointerException at android.text.style.DynamicDrawableSpan.getSize
        if (d==null)
            return 0;

        Rect rect = d.getBounds();

        if (fm != null) {
            fm.ascent = -rect.bottom;
            fm.descent = 0;

            fm.top = fm.ascent;
            fm.bottom = 0;
        }

        return rect.right;
    }

    public void setDrawind(boolean isDrawind){
        this.isDrawind = isDrawind;
    }

    public Drawable getMainDrawable() {
        return mDrawable;
    }

    /*
     * Return current frame from animated drawable. Also acts as replacement for super.getCachedDrawable(),
     * since we can't cache the 'image' of an animated image.
     */
    @Override
    public Drawable getDrawable() {
        Drawable dr = mDrawable;
        if (dr instanceof GlideImageGetter.FutureDrawable){
            dr = mDrawable.getCurrent();
        }
        if (dr instanceof MyGifDrawable){
            dr = ((MyGifDrawable)dr).getDrawable();
        }
        return dr;
    }

    /*
     * Copy-paste of super.draw(...) but use getDrawable() to get the image/frame to draw, in stead of
     * the cached drawable.
     */
    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        Drawable b = getDrawable();
        if (b==null) return;
        canvas.save();

        int transY = bottom - b.getBounds().bottom;
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY -= paint.getFontMetricsInt().descent;
        }

        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }


}