package com.example.pron;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Paresh N. Mayani
 */
public class entryAdapter extends BaseAdapter
{
    public ArrayList<HashMap> list;
    Activity activity;

    public entryAdapter(Activity activity, ArrayList<HashMap> list) {
        super();
        this.activity = activity;
        this.list = list;
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
        return 0;
    }

    private class ViewHolder {
        TextView txtDay;
        TextView txtTemp;
        ImageView imageIcon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater =  activity.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_row, null);
            holder = new ViewHolder();
            holder.txtDay = (TextView) convertView.findViewById(R.id.row_day);
            holder.txtTemp = (TextView) convertView.findViewById(R.id.row_temp);
            holder.imageIcon = (ImageView) convertView.findViewById(R.id.row_image);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        HashMap map = list.get(position);
        holder.txtDay.setText(map.get("day").toString());
        holder.txtTemp.setText(map.get("temp").toString());
        holder.imageIcon.setImageResource(Integer.parseInt(map.get("image").toString()));

        return convertView;
    }

}