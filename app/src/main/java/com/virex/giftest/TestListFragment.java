package com.virex.giftest;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.virex.giftest.ui.MyListAdapter;

import java.util.ArrayList;

public class TestListFragment extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_layout);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        //recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemAnimator(null);
        //ecyclerView.getRecycledViewPool().clear();

        MyListAdapter myListAdapter = new MyListAdapter(new MyListAdapter.ClickListener() {
            @Override
            public void onLinkClick(String link) {

            }

            @Override
            public void onImageClick(Drawable drawable, String filename, boolean isLongClick) {

            }
        });

        recyclerView.setAdapter(myListAdapter);

        ArrayList<String> items=new ArrayList<>();
        items.add(" text <img src=\"icons/dance4.gif\" height=1500 weight=1500/> text2 <img src=\"icons/lol.gif\" height=1500 weight=1500/>  text <img src=\"icons/cheerleading.gif\" height=1500 weight=1500/> text2 <img src=\"icons/beach.gif\" height=1500 weight=1500/>");
        items.add(" text <img src=\"icons/dance4.gif\" height=1500 weight=1500/> text2 <img src=\"icons/lol.gif\" height=1500 weight=1500/>  text <img src=\"icons/cheerleading.gif\" height=1500 weight=1500/> text2 <img src=\"icons/beach.gif\" height=1500 weight=1500/>");
        items.add(" text <img src=\"icons/dance4.gif\" height=1500 weight=1500/> text2 <img src=\"icons/lol.gif\" height=1500 weight=1500/>  text <img src=\"icons/cheerleading.gif\" height=1500 weight=1500/> text2 <img src=\"icons/beach.gif\" height=1500 weight=1500/>");
        items.add(" text <img src=\"icons/dance4.gif\" height=1500 weight=1500/> text2 <img src=\"icons/lol.gif\" height=1500 weight=1500/>  text <img src=\"icons/cheerleading.gif\" height=1500 weight=1500/> text2 <img src=\"icons/beach.gif\" height=1500 weight=1500/>");
        items.add(" text <img src=\"icons/dance4.gif\" height=1500 weight=1500/> text2 <img src=\"icons/lol.gif\" height=1500 weight=1500/>  text <img src=\"icons/cheerleading.gif\" height=1500 weight=1500/> text2 <img src=\"icons/beach.gif\" height=1500 weight=1500/>");
        items.add(" text <img src=\"icons/dance4.gif\" height=1500 weight=1500/> text2 <img src=\"icons/lol.gif\" height=1500 weight=1500/>  text <img src=\"icons/cheerleading.gif\" height=1500 weight=1500/> text2 <img src=\"icons/beach.gif\" height=1500 weight=1500/>");
        myListAdapter.submitList(items);
    }

    /*
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_layout,container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        //recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemAnimator(null);

        MyListAdapter myListAdapter = new MyListAdapter();
        recyclerView.setAdapter(myListAdapter);

        ArrayList<String> items=new ArrayList<>();
        items.add(" text <img src=\"icons/dance4.gif\" height=1500 weight=1500/> text2 <img src=\"icons/lol.gif\" height=1500 weight=1500/>  text <img src=\"icons/cheerleading.gif\" height=1500 weight=1500/> text2 <img src=\"icons/beach.gif\" height=1500 weight=1500/>");
        myListAdapter.submitList(items);

        return view;
    }

     */
}
