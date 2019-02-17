package com.aurak.smartuni.smartuni.Calender;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
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

import com.aurak.smartuni.smartuni.HomeActivity;
import com.aurak.smartuni.smartuni.R;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

import static com.aurak.smartuni.smartuni.Notifcation.Notif.CHANNEL_1_ID;

public class CalendarActivity extends AppCompatActivity {

    CompactCalendarView calendarView;
    AutoCompleteTextView autoCompleteTextView;
    TextView textView3;
    TextView textView5;
    TextView buttonBack;
    Button buttonConfirm;
    Date dateToAdd;
    boolean doubleClick = false;
    String holdDate;
    private NotificationManagerCompat notificationManagerCompat;
    JSONArray jsonObjects;
    ArrayList<String> listOfDate;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        final AsyncHttpClient client = new AsyncHttpClient(); //import the public server certificate into your default keystore
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.addHeader("Accept", "application/json");

        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", "Basic Og==");

        listOfDate = new ArrayList<>();
        attemptFetch(client);


        String languageToLoad  = "en";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_calendar);
        calendarView = findViewById(R.id.compactcalendar_view);
        textView3 = findViewById(R.id.textView3);
        textView5 = findViewById(R.id.textView5);
        buttonBack = findViewById(R.id.buttonBack);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        autoCompleteTextView = findViewById(R.id.Description);

        textView5.setText(new SimpleDateFormat("yyyy-MM",Locale.getDefault()).format(new Date()));
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        textView3.setText(date);
        notificationManagerCompat = NotificationManagerCompat.from(this);

        calendarView.setUseThreeLetterAbbreviation(true);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (jsonObjects != null) {
                    fetchDates();
                }
            }
        }, 2000);


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


                textView3.setText(date);
                dateToAdd=dateClicked;
                buttonConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SimpleDateFormat date2 = new SimpleDateFormat("yyyy-MM-dd");
                        if (autoCompleteTextView.length()> 1) {
                            Event ev = new Event(Color.GREEN, dateToAdd.getTime(), autoCompleteTextView.getText());
                            calendarView.addEvent(ev);
                            autoCompleteTextView.setText("");
                            Events.setEvents(ev);
                        }
                            Activity activity = CalendarActivity.this;
                            View view = activity.getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                });
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                textView5.setText(new SimpleDateFormat("yyyy-MM").format(firstDayOfNewMonth));
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOnChannel1(v);
                Intent intent = new Intent(CalendarActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

    }//onCreate

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
                Events.setEvents(ev);

                Toast.makeText(getApplicationContext(), dateToParse.toString(), Toast.LENGTH_SHORT).show();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void attemptFetch(AsyncHttpClient client) {

        client.get("https://10.0.2.2:5001/api/users/1", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                jsonObjects = response;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(getApplicationContext(), response.toString()+"Object", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(CalendarActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        } );
    }//attemptFetch

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
