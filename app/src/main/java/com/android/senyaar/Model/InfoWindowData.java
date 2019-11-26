package com.android.senyaar.Model;

public class InfoWindowData {
    private String rider_name;
    private String rider_info;
    private String rating, id, schedule_id,start_lat,start_long,end_long;

    public InfoWindowData(String rider_name, String rider_info, String rating, String id, String schedule_id,String start_lat,String start_long,String end_long,String profile) {
        this.rating = rating;
        this.rider_info = rider_info;
        this.rider_name = rider_name;
        this.id = id;
        this.schedule_id = schedule_id;
        this.start_lat=start_lat;
        this.start_long=start_long;
        this.end_long=end_long;
    }

    public String getRating() {
        return rating;
    }

    public String getRider_info() {
        return rider_info;
    }

    public String getRider_name() {
        return rider_name;
    }

    public String getId() {
        return id;
    }

    public String getSchedule_id() {
        return schedule_id;
    }

    public String getEnd_long() {
        return end_long;
    }

    public String getStart_lat() {
        return start_lat;
    }

    public String getStart_long() {
        return start_long;
    }
}
