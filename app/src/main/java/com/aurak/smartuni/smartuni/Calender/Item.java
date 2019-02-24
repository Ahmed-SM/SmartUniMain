package com.aurak.smartuni.smartuni.Calender;

public class Item {
    private   String time;
    private  String desc;
    public String id;



    public Item(String time, String desc, String id) {
        this.time = time;
        this.desc = desc;
        this.id = id;

    }
    public Item(String time, String desc ) {
        this.time = time;
        this.desc = desc;

    }

    public  String getTime() {
        return time;
    }

    public  String getDesc() {
        return desc;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setId(String id) {
        this.id = id;
    }

//public String getId() {
       // return id;
   // }
}
