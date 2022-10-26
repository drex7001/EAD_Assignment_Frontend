package com.example.myapplication.models;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FuelStation {

    private String id;
    private String name;
    private String address;
    List<String> fuelTypes = new ArrayList<>();

    public FuelStation(String id, String name, String address, List<String> fuelTypes) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.fuelTypes = fuelTypes;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getFuelTypes() {
        return fuelTypes;
    }

    public void setFuelTypes(List<String> fuelTypes) {
        this.fuelTypes = fuelTypes;
    }


}
