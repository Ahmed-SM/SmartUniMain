package com.aurak.smartuni.smartuni.Share;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.aurak.smartuni.smartuni.R;
import com.aurak.smartuni.smartuni.Share.Adapters.GalleryImageAdapter;
import com.aurak.smartuni.smartuni.Share.Interfaces.IRecyclerViewClickListerner;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 0;
    private static final int RESULT_LOAD_IMAGE = 1;
    public AsyncHttpClient client;
    private String picturePath;
    private File file;
    private RequestParams params;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private JSONArray arr;
    private String[] list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        setTitle("Share Center");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }

        recyclerView = findViewById(R.id.filerecyclerview);
        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        attemptFetch();

        Random random = new Random();

        final String[] images = new String[10];


        for (int i=0; i<images.length; i++)
            images[i] = "https://picsum.photos/600?image="+random.nextInt(1000+1);





        client = new AsyncHttpClient(); //import the public server certificate into your default keystore
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());



        Toolbar toolbar = findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ////Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                ////.setAction("Action", null).show();
                Intent i = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        //haveStoragePermission();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    file = new File(picturePath);
                    final OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", file.getName(),
                                    RequestBody.create(MediaType.parse("multipart/form-data"),
                                            new File(picturePath)))
                            .build();

                    Request request = new Request.Builder()
                            .url("https://zlqykmwyml.execute-api.eu-central-1.amazonaws.com/real/api/S3Bucket/PostFile")
                            .post(requestBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                        }
                    });
                }
        }
    }



    private void attemptFetch() {

        AndroidHttpClient httpClient = new AndroidHttpClient("https://zlqykmwyml.execute-api.eu-central-1.amazonaws.com/Prod/");
        httpClient.setMaxRetries(5);
        ParameterMap params = httpClient.newParams()
                .add("Content-Type", "application/json");
        httpClient.get("api/S3Bucket/GetFiles", params, new AsyncCallback() {
            public void onComplete(HttpResponse httpResponse) {
                ////TToast.makeText(UploadActivity.this, httpResponse.getBodyAsString(), Toast.LENGTH_SHORT).show();

                try {
                    arr = new JSONArray(new String(httpResponse.getBodyAsString()));
                    list = new String[arr.length()];
                    if (arr != null) {
                        int len = arr.length();
                        for (int i=0;i<len;i++){
                            try {
                                list[i] = (arr.get(i).toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final IRecyclerViewClickListerner listerner = new IRecyclerViewClickListerner() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent i =  new Intent(getApplicationContext(), FullViewActivity.class);
                        i.putExtra("IMAGES", list);
                        i.putExtra("POSITION", position);
                        startActivity(i);
                    }
                };
                GalleryImageAdapter galleryImageAdapter = new GalleryImageAdapter(getApplicationContext(),list,listerner);
                recyclerView.setAdapter(galleryImageAdapter);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }


}
