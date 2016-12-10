package com.example.carlos.beaconexample.classesBeacon;

/**
 * Created by Carlos on 02/12/2016.
 */

public class Beacon {

    private Integer id;
    private Integer major_id;
    private Integer minor_id;
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
