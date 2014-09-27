package com.example.ripzery.quiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by visit on 9/26/14 AD.
 */
public class CustomArrayAdapter extends BaseAdapter {

    private List<String> list;
    private MyActivity myActivity;
    private LayoutInflater inflater;
    private CustomArrayAdapter customArrayAdapter;
    private DirectionDataSource datasource;

    public CustomArrayAdapter(MyActivity context,List<String> objects){
        myActivity = context;
        list = objects;
        customArrayAdapter = this;

        datasource = new DirectionDataSource(myActivity);
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        inflater = (LayoutInflater)myActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.list_item, null);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myActivity.loadDirection(i);
            }
        });

        TextView tvItem = (TextView)view.findViewById(R.id.tvItem);
        Button btnDelete = (Button)view.findViewById(R.id.btnDelete);

        tvItem.setText(list.get(i));
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                datasource.deleteDirection(list.get(i));
                list.remove(i);
                customArrayAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }
}
