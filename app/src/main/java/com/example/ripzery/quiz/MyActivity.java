package com.example.ripzery.quiz;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.sql.SQLException;
import java.util.ArrayList;


public class MyActivity extends FragmentActivity implements SensorEventListener{

    private SensorManager mSensorManager;
    private TabHost tabHost;
    private long lastUpdate;
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private float mAccel; // acceleration apart from gravity
    private GoogleMap mMap;
    private ArrayList<Marker> listMarkers = new ArrayList<Marker>();
    private Marker mMyLocation, mPreviousMarker, mCurrentMarker;
    private boolean isFirstLocation = true, abletoAddMarker = true;
    private GoogleMap.OnMapClickListener mMapClickListener;
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener;
    private DirectionDataSource datasource;
    private ArrayList<Directions> listDirections;
    private ArrayList<LatLng> listLatLngs = new ArrayList<LatLng>();
    private ArrayList<String> listColors = new ArrayList<String>();
    private final ArrayList<String> list = new ArrayList<String>();
    private ToggleButton tbStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        setUpMapIfNeeded();

        tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec(("tabList")).setIndicator("List of Records").setContent(R.id.tabList));
        tabHost.addTab(tabHost.newTabSpec(("tabMap")).setIndicator("Maps").setContent(R.id.tabMap));
        tabHost.setCurrentTab(0);

        final ListView listView = (ListView)findViewById(R.id.list);
        datasource = new DirectionDataSource(this);
        try {
            datasource.open();
            listDirections = datasource.getAllDirections();
            for(Directions direction : listDirections){
                list.add(direction.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final CustomArrayAdapter adapter = new CustomArrayAdapter(this,list);
        listView.setAdapter(adapter);

        Button btnAdd = (Button)findViewById(R.id.btnAdd);
        final EditText etInsert = (EditText)findViewById(R.id.etInsert);
        etInsert.setText("Round"+(list.size()+1));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listMarkers.size()>0){
                    String name = etInsert.getText().toString();
                    list.add(name);
                    etInsert.setText("Round"+(list.size()+1));
                    adapter.notifyDataSetChanged();

                    listMarkers.add(0,mMyLocation);
                    for(Marker marker : listMarkers){
                        listLatLngs.add(marker.getPosition());
                    }
                    datasource.createRecord(name,listLatLngs,listColors);
                }
            }
        });

        mMapClickListener = new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (abletoAddMarker) {
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(latLng.toString());
                    mCurrentMarker = mMap.addMarker(markerOptions);
                    listMarkers.add(mCurrentMarker);
                    abletoAddMarker = false;
                    mSensorManager.registerListener(MyActivity.this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
                }
            }
        };

        tbStatus = (ToggleButton)findViewById(R.id.tbStatus);
        tbStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    mSensorManager.registerListener(MyActivity.this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
                    mMap.setOnMapClickListener(mMapClickListener);
                    //Recording
                }else{
                    mSensorManager.unregisterListener(MyActivity.this);
                    mMap.setOnMapClickListener(null);
                    //Stop record
                }
            }
        });




        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        lastUpdate = System.currentTimeMillis();
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            setAccelerometer(sensorEvent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);

    }

    private void setAccelerometer(SensorEvent event){
        float[] values = event.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];


        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float)Math.sqrt(x * x + y * y + z * z);
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // low-cut filter
        mAccel = Math.abs(mAccel);
        long actualTime = System.currentTimeMillis();



        if(mAccel > 4 && !isFirstLocation){
            if(actualTime - lastUpdate < 3000){
                return;
            }
            lastUpdate = actualTime;
            PolylineOptions line;

            if(mAccel < 6){
                Toast.makeText(this,"Device has shaken slowly", Toast.LENGTH_SHORT).show();
                line = new PolylineOptions().width(7).color(Color.GREEN);
                listColors.add("GREEN");

            }else{
                Toast.makeText(this,"Device has shaken fast", Toast.LENGTH_SHORT).show();
                line = new PolylineOptions().width(7).color(Color.RED);
                listColors.add("RED");
            }
                line.add(mPreviousMarker.getPosition());
                line.add(mCurrentMarker.getPosition());

                mMap.addPolyline(line);
                abletoAddMarker = true;
                mPreviousMarker = mCurrentMarker;
                mSensorManager.unregisterListener(this);
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map. instance
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        mMap.setMyLocationEnabled(true);
                    }
                });

                myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        if(isFirstLocation){
                            mMyLocation = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())));
                            mPreviousMarker = mMyLocation;
                            mMap.setOnMapClickListener(mMapClickListener);
                            isFirstLocation = false;
                        }

                    }
                };

                mMap.setOnMyLocationChangeListener(myLocationChangeListener);
            }
        }
    }

    public void loadDirection(int index){
        mMap.setMyLocationEnabled(false);
        mMap.setOnMyLocationChangeListener(null);
        mSensorManager.unregisterListener(MyActivity.this);
        mMap.setOnMapClickListener(null);
        PolylineOptions polyline;
        mMap.clear();
        tabHost.setCurrentTab(1);
        tbStatus.setClickable(false);
        Directions target = listDirections.get(index);
        ArrayList<LatLng> listTarget = target.getListLatLngs();
        ArrayList<String> listColors = target.getListColors();
        int count = 0;
        for(LatLng latLng : listTarget){
            mMap.addMarker(new MarkerOptions().position(latLng));
            count++;
            if(count > 1){
                if(listColors.get(count-2).equals("RED")){
                    polyline = new PolylineOptions().width(7).color(Color.RED);

                }else{
                    polyline = new PolylineOptions().width(7).color(Color.GREEN);
                }
                polyline.add(listTarget.get(count-2),latLng);
                mMap.addPolyline(polyline);
            }
        }
    }

}
