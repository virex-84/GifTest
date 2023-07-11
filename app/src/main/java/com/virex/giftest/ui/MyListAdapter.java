package com.virex.giftest.ui;

import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.virex.giftest.R;
import com.virex.giftest.ui.Html.AnimatedImageSpan;
import com.virex.giftest.ui.Html.BaseGifDrawable;
import com.virex.giftest.ui.Html.GlideImageGetter;
import com.virex.giftest.ui.Html.HTMLTextView;
import com.virex.giftest.ui.Html.MyGifDrawable;

import org.xml.sax.XMLReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<String> items=new ArrayList<>();

    private final ClickListener clickListener;
    private final LinkMovementMethod linkMovementMethod;

    public interface ClickListener {
        void onLinkClick(String link);
        void onImageClick(Drawable drawable, String filename, boolean isLongClick);
    }

    public MyListAdapter(ClickListener clickListener){

        this.clickListener = clickListener;

        //отрабатываем нажатие на ссылки
        this.linkMovementMethod=new LinkMovementMethod(){
            final long longClickDelay = ViewConfiguration.getLongPressTimeout();
            long startTime;
            boolean isLong=false;

            @Override
            public boolean canSelectArbitrarily () {
                return true;
            }

            @Override
            public void initialize(TextView widget, Spannable text) {
                Selection.setSelection(text, text.length());
            }

            @Override
            public void onTakeFocus(TextView view, Spannable text, int dir) {
                if ((dir & (View.FOCUS_FORWARD | View.FOCUS_DOWN)) != 0) {
                    if (view.getLayout() == null) {
                        // This shouldn't be null, but do something sensible if it is.
                        Selection.setSelection(text, text.length());
                    }
                } else {
                    Selection.setSelection(text, text.length());
                }
            }

            @Override
            public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startTime = System.currentTimeMillis();
                    isLong=false;
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - startTime >= longClickDelay)
                        isLong=true;

                    if (isLong){
                        AnimatedImageSpan[] imageSpans = buffer.getSpans(0, buffer.length(), AnimatedImageSpan.class);
                        for (AnimatedImageSpan imageSpan : imageSpans) {
                            imageSpan.setDrawind(false);
                        }

                        widget.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                setTextIsSelectable(widget,true);

                                widget.postDelayed(new Runnable(){
                                    @Override
                                    public void run() {

                                        float x = event.getX();
                                        float y = event.getY();

                                        Log.i("Gesture1", "START, x: " + x + ", y: " + y);

                                        x = event.getRawX() - widget.getTop();
                                        y = event.getRawY() - widget.getBottom();

                                        Log.i("Gesture11", "START, x: " + x + ", y: " + y);

                                        x -= widget.getX() / 2f;
                                        y -= widget.getY() /2f;

                                        longClickView(widget,x,y);
                                    }
                                },200);

                                widget.setCursorVisible(true);
                                Selection.setSelection(buffer,
                                        buffer.getSpanStart(buffer),
                                        buffer.getSpanEnd(buffer));
                            }
                        },200);

                        return super.onTouchEvent(widget, buffer, event);
                    }
                }

                if (event.getAction() != MotionEvent.ACTION_UP)
                    return super.onTouchEvent(widget, buffer, event);

                int action = event.getAction();

                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    x -= widget.getTotalPaddingLeft();
                    y -= widget.getTotalPaddingTop();

                    x += widget.getScrollX();
                    y += widget.getScrollY();

                    Layout layout = widget.getLayout();
                    int line = layout.getLineForVertical(y);
                    int off = layout.getOffsetForHorizontal(line, x);

                    URLSpan[] spanLink = buffer.getSpans(off, off, URLSpan.class);
                    if (spanLink.length != 0) {
                        //находим ссылку
                        String link = spanLink[0].getURL();
                        if (MyListAdapter.this.clickListener != null) {
                            MyListAdapter.this.clickListener.onLinkClick(link);
                            return true;
                        }
                    }

                    ImageSpan[] spanImage = buffer.getSpans(off, off, ImageSpan.class);
                    if (spanImage.length != 0) {
                        if (MyListAdapter.this.clickListener != null) {
                            MyListAdapter.this.clickListener.onImageClick(spanImage[0].getDrawable(), spanImage[0].getSource(), isLong);
                            return true;
                        }
                    }
                }

                return super.onTouchEvent(widget, buffer, event);
                //return false;
            }
        };
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View viewItem = inflater.inflate(R.layout.text_item, parent, false);
        return new ItemHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final String html = items.get(position);
        ItemHolder itemHolder=((ItemHolder)holder);

        //TextView
        SpannableStringBuilder txt=(SpannableStringBuilder) HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY, new GlideImageGetter(itemHolder.tv_textview.getResources(), itemHolder.tv_textview), new Html.TagHandler() {
                    @Override
                    public void handleTag(boolean opening, String tag, Editable editable, XMLReader xmlReader) {
                        if (!opening){
                            ImageSpan[] imageSpans = editable.getSpans(0, editable.length(), ImageSpan.class);
                            for (ImageSpan imageSpan : imageSpans) {
                                int start = editable.getSpanStart(imageSpan);
                                int end = editable.getSpanEnd(imageSpan);
                                //int flags = editable.getSpanFlags(imageSpan);

                                imageSpan.getDrawable().setCallback(itemHolder.tv_textview);

                                AnimatedImageSpan myImageSpan=new AnimatedImageSpan(imageSpan.getDrawable(),true);
                                editable.setSpan(myImageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                editable.removeSpan(imageSpan);
                            }
                        }
                    }
                }
        );
        itemHolder.tv_textview.setText(txt);
        //itemHolder.tv_textview.setTextIsSelectable(true); !!! обновление прекращается
        itemHolder.tv_textview.setMovementMethod(this.linkMovementMethod);
        //tv_text.clearFocus();
        //tv_text.setSelected(false);


        itemHolder.tv_textview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //setTextIsSelectable((TextView)v, true);
                //longClickView(v,10,150);
                Log.i("LONG CLICK","on");
                return false;
            }
        });

        itemHolder.tv_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setTextIsSelectable((TextView)v, false);
                Log.i("CLICK","on");
            }
        });

        itemHolder.tv_textview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //Toast.makeText(v.getContext(), "Got the focus", Toast.LENGTH_LONG).show();
                    Log.i("FOCUS","on");
                } else {
                    //Toast.makeText(v.getContext(), "Lost the focus", Toast.LENGTH_LONG).show();
                    //setTextIsSelectable((TextView)v, false);
                    Log.i("FOCUS","off");
                }
            }
        });



        /*
        сильно тормозит при скроллинге
        //HTMLTextView
        SpannableStringBuilder htmltxt=(SpannableStringBuilder) HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY, new GlideImageGetter(itemHolder.tv_htmltext.getResources(), itemHolder.tv_htmltext), new Html.TagHandler() {
                    @Override
                    public void handleTag(boolean opening, String tag, Editable editable, XMLReader xmlReader) {
                        if (!opening){
                            ImageSpan[] imageSpans = editable.getSpans(0, editable.length(), ImageSpan.class);
                            for (ImageSpan imageSpan : imageSpans) {
                                int start = editable.getSpanStart(imageSpan);
                                int end = editable.getSpanEnd(imageSpan);
                                //int flags = editable.getSpanFlags(imageSpan);

                                AnimatedImageSpan myImageSpan=new AnimatedImageSpan(imageSpan.getDrawable());
                                editable.setSpan(myImageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                editable.removeSpan(imageSpan);
                            }
                        }
                    }
                }
        );
        itemHolder.tv_htmltext.setText(htmltxt);

         */

        //базовая картинка
        try {
            BaseGifDrawable gif = new BaseGifDrawable(itemHolder.iv_test.getResources().getAssets().open("icons/dance4.gif"));
            gif.setOneShot(false);
            //gif.setBounds(0,0,100,100);
            itemHolder.iv_test.setImageDrawable(gif);
            gif.setCallback(itemHolder.iv_test);
            gif.setVisible(true,true);
            itemHolder.iv_test.setMinimumHeight(100);
            itemHolder.iv_test.setMinimumWidth(100);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void submitList(List<String> data){
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        TextView tv_textview;
        HTMLTextView tv_htmltext;
        ImageView iv_test;
        View main;

        ItemHolder(@NonNull View itemView) {
            super(itemView);
            main = itemView;
            tv_textview = itemView.findViewById(R.id.tv_textview);
            tv_htmltext = itemView.findViewById(R.id.tv_htmltext);
            iv_test = itemView.findViewById(R.id.iv_test);
        }
    }

    public static void longClickView(View v, float x, float y2) {

        final int viewWidth = v.getWidth();
        final int viewHeight = v.getHeight();

        //float x = viewWidth / 2.0f;
        float y = viewHeight / 2.0f;

        Log.i("Gesture2", "START, x: " + x + ", y: " + y);

        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();

        MotionEvent event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, 0);
        v.onTouchEvent(event);

        eventTime = SystemClock.uptimeMillis();
        final int touchSlop = ViewConfiguration.get(v.getContext()).getScaledTouchSlop();
        event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_MOVE,x + touchSlop / 2, y + touchSlop / 2, 0);
        v.onTouchEvent(event);

        v.postDelayed(() -> {
            long eventTime2 = SystemClock.uptimeMillis();
            MotionEvent event2 = MotionEvent.obtain(downTime, eventTime2, MotionEvent.ACTION_UP, x, y, 0);
            v.onTouchEvent(event2);
        }, (long) (ViewConfiguration.getLongPressTimeout() * 1.5f));
    }

    public void setTextIsSelectable(TextView textView, boolean selectable) {
        textView.setTextIsSelectable(selectable);
        textView.setFocusable(selectable);
        textView.setLongClickable(selectable);

        if (!selectable) {
            CharSequence text = textView.getText();
            //вставляем все изображения
            if (text instanceof Spanned) {
                Spanned spanned = (Spanned) text;
                AnimatedImageSpan[] spans2 = spanned.getSpans(0, spanned.length() - 1, AnimatedImageSpan.class);
                for (AnimatedImageSpan imageSpan : spans2) {
                    Drawable drawable = imageSpan.getMainDrawable();

                    if (drawable instanceof GlideImageGetter.FutureDrawable){
                        drawable = drawable.getCurrent();
                        drawable.setCallback(textView);
                        drawable.setVisible(true,true);
                        ((BaseGifDrawable)drawable).start();
                        //if (drawable instanceof MyGifDrawable)
                            //((MyGifDrawable)drawable).setListener(textView);
                    }
                }
            }
        }

    }

}
