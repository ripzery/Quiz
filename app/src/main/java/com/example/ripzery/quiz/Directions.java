package com.example.ripzery.quiz;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by visit on 9/26/14 AD.
 */
public class Directions {
    private String name = "";
    private String latLngs = "";
    private String colors = "";
    private ArrayList<LatLng> listLatLngs = new ArrayList<LatLng>();
    private ArrayList<String> listColors = new ArrayList<String>();

    public void setName(String name){
        this.name = name;
    }

    public void setLatLngs(String latLngs){
        this.latLngs = latLngs;
    }

    public void setColors(String colors){
        this.colors = colors;
    }

    public String getName(){
        return name;
    }

    public String getLatLngs(){
        return latLngs;
    }

    public String getColors(){
        return colors;
    }

    public ArrayList<LatLng> getListLatLngs(){
        String []latandlng = latLngs.split("!");
        for(String ll : latandlng){
            LatLng latLng = new LatLng(Double.parseDouble(ll.split(",")[0]),Double.parseDouble(ll.split(",")[1]));
            listLatLngs.add(latLng);
        }
        return listLatLngs;
    }

    public ArrayList<String> getListColors(){
        String []allcolors = colors.split("!");
        for(String color : allcolors){
            listColors.add(color);
        }
        return listColors;
    }
}
