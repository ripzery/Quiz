package com.example.ripzery.quiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by visit on 9/26/14 AD.
 */
public class DirectionDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allcolumns = {MySQLiteHelper.NAME, MySQLiteHelper.LATLNG, MySQLiteHelper.COLOR};

    public DirectionDataSource(Context context){
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public void createRecord(String name,ArrayList<LatLng> latLngs, ArrayList<String> colors) throws JSONException {

        JSONArray jsonColor = new JSONArray();
        JSONArray jsonLatLng = new JSONArray();

        for(int i=0 ; i<latLngs.size() ; i++){
            JSONObject objectLatLng = new JSONObject();
            objectLatLng.put("lat", String.valueOf(latLngs.get(i).latitude));
            objectLatLng.put("lng", String.valueOf(latLngs.get(i).longitude));
            jsonLatLng.put(objectLatLng);
            if(i<colors.size()){
                JSONObject objectColor = new JSONObject();
                objectColor.put("color",colors.get(i));
                jsonColor.put(objectColor);
            }
        }

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.NAME,name);
        values.put(MySQLiteHelper.LATLNG,jsonLatLng.toString());
        values.put(MySQLiteHelper.COLOR,jsonColor.toString());
        database.insert(MySQLiteHelper.TABLE_NAME,null,values);
//        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME,allcolumns,MySQLiteHelper.NAME + " = '" + name + "'", null,null,null,null);
//        cursor.moveToFirst();
//        Directions newDirection = cursorToDirection(cursor);
//        cursor.close();
//        return newDirection;
    }

    public ArrayList<Directions> getAllDirections(){
        ArrayList<Directions> directions = new ArrayList<Directions>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, allcolumns, null,null,null,null,null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            Directions direction = cursorToDirection(cursor);
            directions.add(direction);
            cursor.moveToNext();
        }
        Log.d("Cursor",""+directions.size());
        cursor.close();
        return directions;
    }

    public void deleteDirection(String recordName){
        database.delete(MySQLiteHelper.TABLE_NAME, MySQLiteHelper.NAME + " = '" + recordName + "'", null);
    }

    private Directions cursorToDirection(Cursor cursor){
        Directions directions = new Directions();
        directions.setName(cursor.getString(0));
        directions.setLatLngs(cursor.getString(1));
        directions.setColors(cursor.getString(2));
        return directions;
    }

}
