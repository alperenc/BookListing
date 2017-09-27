package com.alperencan.booklisting.android.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alperencan.booklisting.android.R;
import com.alperencan.booklisting.android.adapter.VolumeAdapter;
import com.alperencan.booklisting.android.model.Volume;

import java.util.ArrayList;

public class BookListingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_listing);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        ArrayList<Volume> volumes= new ArrayList<Volume>();
        volumes.add(new Volume("Android in The Attic", new String[]{"Nicholas Allan"}, "http://books.google.com/books/content?id=MoXpe6H2B5gC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"));
        volumes.add(new Volume("Voice Application Development for Android", new String[]{"Michael F. McTear", "Zoraida Callejas"}, "http://books.google.com/books/content?id=V-gtAgAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"));

        VolumeAdapter volumeAdapter = new VolumeAdapter(volumes);
        recyclerView.setAdapter(volumeAdapter);
    }
}
