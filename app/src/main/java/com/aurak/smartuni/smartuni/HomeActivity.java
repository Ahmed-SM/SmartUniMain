package com.aurak.smartuni.smartuni;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.aurak.smartuni.smartuni.Calender.Adapter.ListAdapter;
import com.aurak.smartuni.smartuni.Calender.Adapter.RecyclerItemTouchHelperListener;
import com.aurak.smartuni.smartuni.Calender.Adapter.RecyclerTouchHelper;
import com.aurak.smartuni.smartuni.Calender.CalendarActivity;
import com.aurak.smartuni.smartuni.Calender.Events;
import com.aurak.smartuni.smartuni.Calender.Item;
import com.aurak.smartuni.smartuni.Chat.LoginChatActivity;
import com.aurak.smartuni.smartuni.Share.UploadActivity;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.MySSLSocketFactory;
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
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.entity.StringEntity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*Issues log

        - ((ListAdapter) adapter). to access methods

 */
public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RecyclerItemTouchHelperListener, SwipeRefreshLayout.OnRefreshListener {
    BottomNavigationView bottomNavigation;
    FloatingActionButton btn;
    public final String BackEndURL = "https://zlqykmwyml.execute-api.eu-central-1.amazonaws.com/Prod/";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Item> listItems;
    private List<String> listOfId ;
    private List<String> listOfDec;
    static Pair<ArrayList<Event>, ArrayList<String>> events;
    private JSONArray jsonObjects;
    private ArrayList<String> listOfDate;
    private CompactCalendarView calendarView;
    private TextView numberOfEvents;
    private TextView header;
    private AlertDialog dialog;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private StringEntity jsonEntity;
    public AsyncHttpClient client;
    private static boolean clientUpdated = false;


    Button buttonConfirm;
    AutoCompleteTextView input;
    Date dateToAdd;
    boolean doubleClick = false;
    String holdDate;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        String languageToLoad = "en";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());


        recyclerView = findViewById(R.id.recyclerView);
        numberOfEvents = findViewById(R.id.textView5);
        header = findViewById(R.id.textView8);
        header.setSelected(true);
        bottomNavigation = findViewById(R.id.navigationView);
        btn = findViewById(R.id.button2);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        jsonEntity = null;
        client = new AsyncHttpClient(); //import the public server certificate into your default keystore
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());

        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", "Basic Og==");




        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
//        recyclerView2.setHasFixedSize(true);
//        recyclerView3.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView3.setLayoutManager(new LinearLayoutManager(this));


        if (!clientUpdated) {
            attemptFetch();
        }


        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack
                = new RecyclerTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);
//        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView2);
//        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView3);

        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);


        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                if(mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
                attemptFetch();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, CalendarActivity.class));
//                AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
//                View mView = getLayoutInflater().inflate(R.layout.event_add, null);
//                buttonConfirm = mView.findViewById(R.id.addbutton);
//                input = mView.findViewById(R.id.input);
//                input.setVisibility(View.VISIBLE);
//                initCalender(mView);
//
//                mBuilder.setView(mView);
//
//                dialog = mBuilder.create();
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//
//                dialog.show();

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
                                //startActivity(new Intent(HomeActivity.this, QrActivity.class));
                                return true;
                            case R.id.navigation_messenger:
                                setTitle("Chat");
                                startActivity(new Intent(HomeActivity.this, LoginChatActivity.class));
                                return true;
                            case R.id.navigation_food:
                                setTitle("Food");
                                return true;
                        }
                        return false;
                    }
                });


    }

    public void displayEvents() {
        mSwipeRefreshLayout.setRefreshing(true);
        listItems = new ArrayList<>();



        events = Events.getEvents();
        numberOfEvents.setText(String.valueOf(events.first.size())+ " Scheduled Dates");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        adapter = new ListAdapter(listItems, this,recyclerView);
        recyclerView.setAdapter(adapter);
        if (events != null) {
            if (events.first.size() > 0 && events.second.size() > 0) {
                int i = 0;
                for (Event item : events.first) {
                    Item listItem = new Item(
                            formatter.format(item.getTimeInMillis()),
                            item.getData().toString(),
                            events.second.get(i)
                    );
                    ((ListAdapter) adapter).restoreItem(listItem);
                    i++;
                }
            }
        }
        Events.clear();
        mSwipeRefreshLayout.setRefreshing(false);

    }


    private void initCalender(View mView) {

        calendarView = mView.findViewById(R.id.compactcalendar);

        calendarView.setUseThreeLetterAbbreviation(true);

        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                final String date = new SimpleDateFormat("yyyy-MM-dd").format(dateClicked);

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
                        if (input.length()> 1) {
                           // SimpleDateFormat date2 = new SimpleDateFormat("yyyy-MM-dd").format(dateToAdd.toString()));
                            Event ev = new Event(Color.GREEN,dateToAdd.getTime(), //autoCompleteTextView.getText()
                                    input.getText());
                            calendarView.addEvent(ev); //toDO IMPROVE THIS MESS
                            attemptToPost(holdDate, input.getText().toString());
                            clientUpdated = false;
                            Events.clear();
                            dialog.dismiss();
                            attemptFetch();
                            ((ListAdapter) adapter).clear();
                            ((ListAdapter) adapter).notifyDataSetChanged();
                            recyclerView.removeAllViews();
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (jsonObjects != null) {
                                        displayEvents();
                                    }
                                }
                            }, 2000);
                       }
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

            startActivity(new Intent(HomeActivity.this, UploadActivity.class));//todo sotorage activity

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void attemptFetch() {

        AndroidHttpClient httpClient = new AndroidHttpClient(BackEndURL);
        httpClient.setMaxRetries(5);
        ParameterMap params = httpClient.newParams()
                .add("Content-Type", "application/json");
        httpClient.get("api/Events", params, new AsyncCallback() {
            public void onComplete(HttpResponse httpResponse) {
                ////Toast.makeText(HomeActivity.this, httpResponse.getBodyAsString(), Toast.LENGTH_SHORT).show();
                JSONArray arr = null;
                try {
                    arr = new JSONArray(new String(httpResponse.getBodyAsString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonObjects = arr;
                fetchDates();
                clientUpdated = false;
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

    }
    private void fetchDates() {
        Events.clear();
        listOfDate = new ArrayList<>();
        listOfDec = new ArrayList<>();
        listOfId = new ArrayList<>();
        for (int i = 0; i< jsonObjects.length(); i++){
            try {
                ////TToast.makeText(HomeActivity.this, jsonObjects.getJSONObject(i).getJSONObject("event").getString("date"), Toast.LENGTH_SHORT).show();
                listOfDate.add(jsonObjects.getJSONObject(i).getJSONObject("event").getString("date"));
                listOfId.add(jsonObjects.getJSONObject(i).getJSONObject("event").getString("id"));
                listOfDec.add(jsonObjects.getJSONObject(i).getJSONObject("event").getString("description"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i< listOfDate.size(); i++) {

            try {
                Date dateToParse=new SimpleDateFormat("yyyy-MM-dd").parse(listOfDate.get(i));
                Event ev = new Event(Color.GREEN, dateToParse.getTime(),listOfDec.get(i));
                Events.setEvents(ev, listOfId.get(i));

                ////TToast.makeText(getApplicationContext(), dateToParse.toString(), Toast.LENGTH_SHORT).show();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // if (jsonObjects != null) {
                displayEvents();
                //}
            }
        }, 1);
    }

    private void attemptToPost(String date, String desciption ) {
        AndroidHttpClient httpClient = new AndroidHttpClient(BackEndURL);
        httpClient.setMaxRetries(5);
        ParameterMap params = httpClient.newParams()
                .add("date",date)
                .add("description", desciption);
        httpClient.addHeader("Accept", "application/json");
        httpClient.addHeader("Content-Type", "application/json");
        httpClient.addHeader("Authorization", "Basic Og==");

        httpClient.post("api/Events", params, new AsyncCallback() {
            public void onComplete(HttpResponse httpResponse) {
                ////TToast.makeText(HomeActivity.this, httpResponse.getBodyAsString(), Toast.LENGTH_SHORT).show();

                clientUpdated = false;
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

    }
    public void attemptToUpdate(String id, String date, String desciption ) {
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("id", id);
            jsonParams.put("date",date);
            jsonParams.put("description", desciption);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        final OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON,jsonParams.toString());
        final Request request = new Request.Builder()
                .url(BackEndURL+"api/Events")
                .put(body)
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
                attemptFetch();
            }
        });
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, int position) {
        AndroidHttpClient httpClient = new AndroidHttpClient(BackEndURL);
        httpClient.setMaxRetries(5);
        httpClient.addHeader("Accept", "application/json");
        httpClient.addHeader("Authorization", "Basic Og==");
        if(viewHolder instanceof  ListAdapter.ViewHolder){
            recyclerView = ((ListAdapter.ViewHolder) viewHolder).recyclerView;
            Item item =((ListAdapter) recyclerView.getAdapter()).getListItems().get(position);
            ((ListAdapter) recyclerView.getAdapter()).deletedEvent(position);
            if ( item.id.equals("No Event") == true) return;
            recyclerView.removeViewAt(position);
            httpClient.delete(String.format("api/Events/%s", item.id), null, new AsyncCallback() {
                @Override
                public void onComplete(HttpResponse httpResponse) {
                    ////TToast.makeText(HomeActivity.this, httpResponse.getBodyAsString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Exception e) {
                    ////TToast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    @Override
    public void onRefresh() {
        attemptFetch();
    }
}
