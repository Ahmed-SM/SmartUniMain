package com.aurak.smartuni.smartuni.Calender;

public class Item {
    public  long time;
    public  String desc;
    public String id;



    public Item(long time, String desc, String id) {
        this.time = time;
        this.desc = desc;
        this.id = id;

    }
    public Item(long time, String desc ) {
        this.time = time;
        this.desc = desc;

    }

    public  long getTime() {
        return time;
    }

    public  String getDesc() {
        return desc;
    }


    //public String getId() {
       // return id;
   // }
}
