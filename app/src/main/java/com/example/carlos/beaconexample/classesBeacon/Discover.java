package com.example.carlos.beaconexample.classesBeacon;

/**
 * Created by Carlos on 08/12/2016.
 */

public class Discover {
    private Integer device_id;
    private Integer beacon_id;
    private Integer major_id;
    private Integer minor_id;
    private String discover_time;

    public Integer getDevice_id() {
        return device_id;
    }

    public void setDevice_id(Integer device_id) {
        this.device_id = device_id;
    }

    public Integer getBeacon_id() {
        return beacon_id;
    }

    public void setBeacon_id(Integer beacon_id) {
        this.beacon_id = beacon_id;
    }

    public Integer getMajor_id() {
        return major_id;
    }

    public void setMajor_id(Integer major_id) {
        this.major_id = major_id;
    }

    public Integer getMinor_id() {
        return minor_id;
    }

    public void setMinor_id(Integer minor_id) {
        this.minor_id = minor_id;
    }

    public String getDiscover_time() {
        return discover_time;
    }

    public void setDiscover_time(String discover_time) {
        this.discover_time = discover_time;
    }
}