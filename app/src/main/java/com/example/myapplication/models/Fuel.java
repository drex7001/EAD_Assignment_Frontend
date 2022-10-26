package com.example.myapplication.models;

import java.util.List;
import java.util.Map;

public class Fuel {

    private String id;
    private String name;
    private String type;
    private String status;
    private double amount;
    private String arrivalTime;
    private String finishTime;
    private String stationID;
    private Map<String, String> vehicleCountList;

    public Fuel(String id, String name, String type, String status, double amount, String arrivalTime, String finishTime, String stationID, Map<String, String> vehicleCountList) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.amount = amount;
        this.arrivalTime = arrivalTime;
        this.finishTime = finishTime;
        this.stationID = stationID;
        this.vehicleCountList = vehicleCountList;
    }

    public Map<String, String> getVehicleCountList() {
        return vehicleCountList;
    }

    public void setVehicleCountList(Map<String, String> vehicleCountList) {
        this.vehicleCountList = vehicleCountList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getStationID() {
        return stationID;
    }

    public void setStationID(String stationID) {
        this.stationID = stationID;
    }

}
