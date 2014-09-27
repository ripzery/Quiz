package com.example.ripzery.quiz;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by visit on 9/26/14 AD.
 */
public class Directions {
    private String name = "";
    private String latLngs = "";
    private String colors = "";

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

        try {
            ArrayList<LatLng> listLatLngs = new ArrayList<LatLng>();
            JSONArray jsonArray = new JSONArray(latLngs);
            for(int i=0 ; i<jsonArray.length();i++){
                JSONObject object = (JSONObject)jsonArray.get(i);
                LatLng latLng = new LatLng(object.getDouble("lat"),object.getDouble("lng"));
                listLatLngs.add(latLng);
            }
            return listLatLngs;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getListColors(){
        try {
            ArrayList<String> listColors = new ArrayList<String>();
            JSONArray jsonArray = new JSONArray(colors);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject object = (JSONObject)jsonArray.get(i);
                listColors.add(object.getString("color"));
            }
            return  listColors;

        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}
