package com.virex.giftest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.virex.giftest.ui.Html.BaseGifDrawable;
import com.virex.giftest.ui.Html.MyGifDrawable;
import com.virex.giftest.ui.Html.AnimatedImageSpan;
import com.virex.giftest.ui.Html.GlideImageGetter;
import com.virex.giftest.ui.Html.HTMLTextView;

import org.xml.sax.XMLReader;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textview = findViewById(R.id.textview);
        Button btn_glideimagegetter = findViewById(R.id.btn_glideimagegetter);
        Button btn_list = findViewById(R.id.btn_list);
        Button btn_basegifdrawable = findViewById(R.id.btn_basegifdrawable);
        ImageView imageView = findViewById(R.id.imageView);
        HTMLTextView htmltextview = findViewById(R.id.htmltextview);

        try {
            //BaseGifDrawable on ImageView:
            BaseGifDrawable gif = new BaseGifDrawable(textview.getResources().getAssets().open("icons/dance4.gif"));
            gif.setBounds(0,0,100,100);
            gif.setOneShot(false);
            imageView.setImageDrawable(gif);
            gif.setCallback(imageView);
            gif.setVisible(true,true);

            MyGifDrawable gif2 = new MyGifDrawable(textview.getResources().getAssets().open("icons/dance4.gif"),null);
            gif2.setOneShot(false);

            //MyGifDrawable on HTMLTextView:
            SpannableStringBuilder sb = new SpannableStringBuilder();
            sb.append("Text followed by animated gif: ");
            String dummyText = "dummy";
            sb.append(dummyText);
            sb.setSpan(new AnimatedImageSpan(gif2), sb.length() - dummyText.length(), sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            htmltextview.setText(sb);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Update BaseGifDrawable
        btn_basegifdrawable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    BaseGifDrawable gif3 = new BaseGifDrawable(textview.getResources().getAssets().open("icons/dance4.gif"));
                    gif3.setOneShot(false);

                    //BaseGifDrawable on TextView:
                    SpannableStringBuilder sb = new SpannableStringBuilder();
                    sb.append("Text followed by animated gif: ");
                    String dummyText = "dummy";
                    sb.append(dummyText);
                    sb.setSpan(new AnimatedImageSpan(gif3,true), sb.length() - dummyText.length(), sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textview.setText(sb);
                    gif3.setCallback(textview);
                    gif3.setVisible(true,true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        //Update GlideImageGetter
        btn_glideimagegetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String html = " text <img src=\"icons/dance4.gif\" height=1500 weight=1500/> text2 <img src=\"icons/lol.gif\" height=1500 weight=1500/>  text <img src=\"icons/cheerleading.gif\" height=1500 weight=1500/> text2 <img src=\"icons/beach.gif\" height=1500 weight=1500/>";
                //String html = " text <img src=\"icons/dance4.gif\" height=1500 weight=1500/> ";
                SpannableStringBuilder text=(SpannableStringBuilder) HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY, new GlideImageGetter(getResources(), textview), new Html.TagHandler() {
                            @Override
                            public void handleTag(boolean opening, String tag, Editable editable, XMLReader xmlReader) {
                                if (!opening){
                                    ImageSpan[] imageSpans = editable.getSpans(0, editable.length(), ImageSpan.class);
                                    for (ImageSpan imageSpan : imageSpans) {
                                        int start = editable.getSpanStart(imageSpan);
                                        int end = editable.getSpanEnd(imageSpan);
                                        //int flags = editable.getSpanFlags(imageSpan);

                                        Drawable dr = imageSpan.getDrawable();

                                        if (dr instanceof GlideImageGetter.FutureDrawable)
                                            dr = dr.getCurrent();
                                        //dr.setCallback(textview);
                                        dr.setVisible(true,true);

                                        AnimatedImageSpan myImageSpan=new AnimatedImageSpan(dr,true);
                                        editable.setSpan(myImageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        editable.removeSpan(imageSpan);
                                    }
                                }
                            }
                        }
                );

                textview.setText(text);

            }
        });

        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,TestListFragment.class);
                startActivity(i);
            }
        });

    }
}