package com.aurak.smartuni.smartuni.Share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.Button;

import com.aurak.smartuni.smartuni.R;
import com.aurak.smartuni.smartuni.Share.Adapters.FullSizeAdapter;

public class FullViewActivity extends Activity {


    ViewPager viewPager;
    Button button;
    String[] images;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_view);

        if (savedInstanceState == null){
            Intent i = getIntent();
            images = i.getStringArrayExtra("IMAGES");
            position = i.getIntExtra("POSITION",0);
        }
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        button = findViewById(R.id.saveButton);

        FullSizeAdapter fullSizeAdapter = new FullSizeAdapter(this, images);
        viewPager.setAdapter(fullSizeAdapter);
        viewPager.setCurrentItem(position, true);
    }
}
