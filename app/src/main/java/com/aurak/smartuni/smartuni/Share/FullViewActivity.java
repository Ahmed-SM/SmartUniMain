package com.aurak.smartuni.smartuni.Share;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aurak.smartuni.smartuni.R;
import com.aurak.smartuni.smartuni.Share.Adapters.FullSizeAdapter;

import java.io.File;

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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                haveStoragePermission();
            }
        });
    }

    public void downloadFile(String uRl) {

        File direct = new File(Environment.getExternalStorageDirectory()
                + "/Downloads");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle(images[position].substring(56))
                .setDescription(" ")
                .setDestinationInExternalPublicDir("/Downloads", images[position].substring(56));

        mgr.enqueue(request);

        // Open Download Manager to view File progress
        Toast.makeText(this, "Downloading...",Toast.LENGTH_LONG).show();
        startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));

    }
    public  boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error","You have permission");
                downloadFile(images[position]);
                return true;
            } else {

                Log.e("Permission error","You have asked for permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error","You already have the permission");
            return true;
        }
    }

}
