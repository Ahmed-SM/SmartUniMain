package com.aurak.smartuni.smartuni.Calender;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aurak.smartuni.smartuni.R;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.aurak.smartuni.smartuni.Notifcation.Notif.CHANNEL_1_ID;

public class CalendarActivity extends AppCompatActivity {

    CompactCalendarView calendarView;
    AutoCompleteTextView autoCompleteTextView;
    TextView textView5;
    Button buttonConfirm;
    Date dateToAdd;
    boolean doubleClick = false;
    String holdDate;
    private NotificationManagerCompat notificationManagerCompat;
    JSONArray jsonObjects;
    ArrayList<String> listOfDate;
    public final String BackEndURL = "https://zlqykmwyml.execute-api.eu-central-1.amazonaws.com/Prod/";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidHttpClient httpClient = new AndroidHttpClient(BackEndURL);
        httpClient.setMaxRetries(5);
        httpClient.addHeader("Accept", "application/json");
        httpClient.addHeader("Content-Type", "application/json");
        httpClient.addHeader("Authorization", "Basic Og==");



        listOfDate = new ArrayList<>();
        attemptFetch(httpClient);


        String languageToLoad  = "en";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_calendar);
        calendarView = findViewById(R.id.compactcalendar_view);

        textView5 = findViewById(R.id.textView5);

        buttonConfirm = findViewById(R.id.buttonConfirm);
        autoCompleteTextView = findViewById(R.id.Description);


        textView5.setText("<  " + new SimpleDateFormat("yyyy-MM",Locale.getDefault()).format(new Date()) + "  >");
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        notificationManagerCompat = NotificationManagerCompat.from(this);

        calendarView.setUseThreeLetterAbbreviation(true);




        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                String date = new SimpleDateFormat("yyyy-MM-dd").format(dateClicked);

                if (doubleClick == true){
                    if (date.compareTo(holdDate) == 0 && calendarView.getEvents(dateClicked).size() > 0) {
                        Toast.makeText(CalendarActivity.this, calendarView.getEvents(dateClicked).get(0).getData().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                else if (doubleClick == false && date != holdDate){
                    doubleClick = true;
                }
                holdDate = new SimpleDateFormat("yyyy-MM-dd").format(dateClicked);
                dateToAdd=dateClicked;
                buttonConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (autoCompleteTextView.length()> 1) {
                            Event ev = new Event(Color.GREEN, dateToAdd.getTime(), autoCompleteTextView.getText().toString());
                            calendarView.addEvent(ev);
                            Events.setEvents(ev," ");
                        }
                            Activity activity = CalendarActivity.this;
                            View view = activity.getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        postEvent();
                        finish();
                    }
                });
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                textView5.setText("<  " +new SimpleDateFormat("yyyy-MM").format(firstDayOfNewMonth) + "  >");
            }
        });


    }//onCreate

    private void postEvent() {
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("date", holdDate);
            jsonParams.put("description", autoCompleteTextView.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        final OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON,jsonParams.toString());
        final Request request = new Request.Builder()
                .url(BackEndURL+"api/Events")
                .post(body)
                .build();

       client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // Handle the error
                }

            }
        });
    }

    private void fetchDates() {
       for (int i = 0; i< jsonObjects.length(); i++){
           try {
                //Toast.makeText(getApplicationContext(), jsonObjects.getJSONObject(i).getJSONObject("event").getString("date"), Toast.LENGTH_SHORT).show();
               listOfDate.add(jsonObjects.getJSONObject(i).getJSONObject("event").getString("date"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        for (String eventDate: listOfDate) {
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

            try {
                Date dateToParse=new SimpleDateFormat("yyyy-MM-dd").parse(eventDate);
                Event ev = new Event(Color.GREEN, dateToParse.getTime(), eventDate);
                Events.setEvents(ev, " ");

                ////TToast.makeText(getApplicationContext(), dateToParse.toString(), Toast.LENGTH_SHORT).show();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void attemptFetch(AndroidHttpClient client) {

        ParameterMap params = client.newParams()
                .add("Content-Type", "application/json");

        client.get("api/Events", params, new AsyncCallback() {
            public void onComplete(HttpResponse httpResponse) {
                ////TToast.makeText(getApplicationContext(), httpResponse.getBodyAsString(), Toast.LENGTH_SHORT).show();
                JSONArray arr = null;
                try {
                    arr = new JSONArray(new String(httpResponse.getBodyAsString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonObjects = arr;

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                            fetchDates();
                    }
                }, 1);
            }
            @Override
            public void onError(Exception e) {
                ////T Toast.makeText(CalendarActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendOnChannel1(View v){
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("hi")
                .setContentText("hello")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .build();

        notificationManagerCompat.notify(1,notification);
    }
}
