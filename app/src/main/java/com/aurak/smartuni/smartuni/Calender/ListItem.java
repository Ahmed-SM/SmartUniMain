package com.aurak.smartuni.smartuni.Calender;

public class ListItem {
    public  Long time;
    public  String desc;



    public ListItem(Long time, String desc) {
        this.time = time;
        this.desc = desc;

    }

    public  Long getTime() {
        return time;
    }

    public  String getDesc() {
        return desc;
    }

}
