package com.aurak.smartuni.smartuni;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.aurak.smartuni.smartuni.Calender.Adapter.ListAdapter;
import com.aurak.smartuni.smartuni.Calender.Events;
import com.aurak.smartuni.smartuni.Calender.ListItem;
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
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        BottomNavigationView bottomNavigation;
        FloatingActionButton btn;
        private RecyclerView recyclerView;
        private RecyclerView recyclerView2;
        private RecyclerView.Adapter adapter;
        private RecyclerView.Adapter adapter2;
        private List<ListItem> listItems;
        private List<ListItem> listItems2;
        static ArrayList<Event> events;
        private JSONArray jsonObjects;
        private ArrayList<String> listOfDate;
        private CompactCalendarView calendarView;

         //TextView textView3;
         //TextView textView5;
        // TextView buttonBack;
         Button buttonConfirm;
         Date dateToAdd;
         boolean doubleClick = false;
         String holdDate;
         AutoCompleteTextView autoCompleteTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String languageToLoad  = "en";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_home);


        final AsyncHttpClient client = new AsyncHttpClient(); //import the public server certificate into your default keystore
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.addHeader("Accept", "application/json");

        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", "Basic Og==");

        listOfDate = new ArrayList<>();
        attemptFetch(client);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView2 = findViewById(R.id.recyclerView2);
        bottomNavigation = findViewById(R.id.navigationView);

        btn = findViewById(R.id.button2);

        recyclerView.setHasFixedSize(true);
        recyclerView2.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        listItems = new ArrayList<>();
        listItems2 = new ArrayList<>();



        events = Events.getEvents();
        if (events != null && events.size() != 0){
            for (Event item : events) {
                ListItem listItem = new ListItem(
                        item.getTimeInMillis(),
                        item.getData().toString()
                );
                listItems.add(listItem);
            }
            adapter = new ListAdapter(listItems, this);
            recyclerView.setAdapter(adapter);

        }
        else{
            listItems.add(new ListItem(System.currentTimeMillis()+10000000l,"No Events"));
            adapter = new ListAdapter(listItems, this);
            recyclerView.setAdapter(adapter);
            listItems2.add(new ListItem(System.currentTimeMillis(),"No Events"));
            adapter2 = new ListAdapter(listItems2, this);
            recyclerView2.setAdapter(adapter2);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.event_add, null);
                buttonConfirm = mView.findViewById(R.id.addbutton);
                initCalender(mView);

            mBuilder.setView(mView);

            AlertDialog dialog = mBuilder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


            dialog.show();

            }


        });




        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_calender:
                                setTitle("Calender");
                                return true;
                            case R.id.navigation_vrtualnav:
                                setTitle("Navigation");
                                return true;
                            case R.id.navigation_messenger:
                                setTitle("Chat");
                                return true;
                            case R.id.navigation_food:
                                setTitle("Food");
                                return true;
                        }
                        return false;
                    }
                });


    }

    private void initCalender(View mView) {

        calendarView = mView.findViewById(R.id.compactcalendar);

        calendarView.setUseThreeLetterAbbreviation(true);

        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                String date = new SimpleDateFormat("yyyy-MM-dd").format(dateClicked);

                if (doubleClick == true){
                    if (date.compareTo(holdDate) == 0 && calendarView.getEvents(dateClicked).size() > 0) {
                        //Toast.makeText(getApplicationContext(), calendarView.getEvents(dateClicked).get(0).getData().toString(), Toast.LENGTH_SHORT).show();
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
                        SimpleDateFormat date2 = new SimpleDateFormat("yyyy-MM-dd");
                        //if (autoCompleteTextView.length()> 1) {
                            Event ev = new Event(Color.GREEN, dateToAdd.getTime(), //autoCompleteTextView.getText()
                                    "test");
                            calendarView.addEvent(ev);
                            //autoCompleteTextView.setText("");
                            Events.setEvents(ev);
                            startActivity(getIntent());
                       // }
                    }
                });
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

            }


        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_scan) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {


        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void attemptFetch(AsyncHttpClient client) {

        client.get("https://10.0.2.2:5001/api/users/1", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                jsonObjects = response;

//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (jsonObjects != null) {
//                            fetchDates();
//                        }
//                    }
//                }, 2000);

                    if (jsonObjects != null) {
                        fetchDates();

                    }
                }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(getApplicationContext(), response.toString()+"Object", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(HomeActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        } );
    }
    private void fetchDates() {
        for (int i = 0; i< jsonObjects.length(); i++){
            try {
                Toast.makeText(HomeActivity.this, jsonObjects.getJSONObject(i).getJSONObject("event").getString("date"), Toast.LENGTH_SHORT).show();
                listOfDate.add(jsonObjects.getJSONObject(i).getJSONObject("event").getString("date"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        for (String eventDate: listOfDate) {

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

}
