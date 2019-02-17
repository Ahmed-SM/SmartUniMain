package com.aurak.smartuni.smartuni.Calender;

import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.ArrayList;

public class Events {
    static ArrayList<Event> events= new ArrayList<>();

    private Events() {

    }
    public static void Initialize(){
        events = new ArrayList<>();
    }

    public static ArrayList<Event> getEvents() {
        return events;
    }

    public static void setEvents(Event recivedevents) {
        events.add(recivedevents);
    }
}
