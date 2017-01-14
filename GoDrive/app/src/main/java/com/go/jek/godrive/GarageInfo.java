package com.go.jek.godrive;

/**
 * Created by kumardev on 1/15/2017.
 */

public class GarageInfo {
    private int id;
    private int idGarage;
    private String Name;
    private String Address;
    private String City;
    private String ContactNo;
    private double latitude;
    private double longitude;
    private String status;

    public GarageInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdGarage() {
        return idGarage;
    }

    public void setIdGarage(int idGarage) {
        this.idGarage = idGarage;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getContactNo() {
        return ContactNo;
    }

    public void setContactNo(String contactNo) {
        ContactNo = contactNo;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
