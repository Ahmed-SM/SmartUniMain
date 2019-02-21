package com.aurak.smartuni.smartuni.Calender;

import android.support.v4.util.Pair;

import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.ArrayList;

public class Events {
    static ArrayList<Event> events= new ArrayList<>();
    static ArrayList<String> id = new ArrayList<>();

    private Events() {

    }


    public static Pair<ArrayList<Event>,ArrayList<String>> getEvents() {
        return new Pair<>(events, id);
    }


    public static void setEvents(Event recivedevents,String recivedId ) {
        events.add(recivedevents);
        id.add(recivedId);
    }

    public static ArrayList<String> getId() {
        return id;
    }

    public static void setId(String recivedid) {
        id.add(recivedid);
    }

    public static void clear() {
        if (events.size() > 0) {
            events.clear();
        }
    }
}
