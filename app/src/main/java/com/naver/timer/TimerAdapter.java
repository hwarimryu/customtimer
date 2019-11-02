package com.naver.timer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
//import android.view.Hol;

import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.time.format.TextStyle;
import java.util.ArrayList;

public class TimerAdapter extends BaseAdapter {
    LayoutInflater inflater;
    public ViewGroup parentView = null;
    public View timersView = null;
    public static ArrayList<String> preTimes = new ArrayList<String>();
    public static ArrayList<String> times = new ArrayList<String>();
    public static ArrayList<TextView> timers = new ArrayList<TextView>();
//    public static ArrayList<TimerViewHolder> timers = null;


    public TimerAdapter(Context context) {
//        timers = new ArrayList<TextView>();
//        timers = new ArrayList<TimerViewHolder>();

    }
//    public class timerViewHolder{
//        public TextView timer;
//    }

    public void resetTimes() {
        for (int i = 0; i < preTimes.size(); i++)
            times.set(i, preTimes.get(i));
        this.notifyDataSetChanged();
    }



    public void remove(int position) {
        timers.remove(position);
        times.remove(position);
        preTimes.remove(position);
    }

    public void addTime(String time) {

        times.add(time);
        preTimes.add(time);
//        TimerViewHolder newtime= new TimerViewHolder();
//        timers.add(newtime);
        this.notifyDataSetChanged();
    }

    public void changeColor(int position, int color) {
        Context context = parentView.getContext();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        parentView.getChildAt(position);
        timersView = inflater.inflate(R.layout.single_row, parentView, false);

        TextView time = (TextView) parentView.getChildAt(position).findViewById(R.id.label);

//        parentView.get.getChildAt(position);
        time.setTextColor(color);
        System.out.println("==========" + time.getText());
        this.notifyDataSetChanged();

//        System.out.println("=================="+parentView.getChildAt(position).findViewById(R.id.label));

    }
    public void changeNum(int position, String str) {
        Context context = parentView.getContext();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        parentView.getChildAt(position);
        timersView = inflater.inflate(R.layout.single_row, parentView, false);


        TextView time = (TextView) parentView.getChildAt(position).findViewById(R.id.label);
time.setText(str);
        times.set(position,str);

//        parentView.get.getChildAt(position);
//        time.setText(?);
//        System.out.println("==========" + time.getText());
        this.notifyDataSetChanged();

//        System.out.println("=================="+parentView.getChildAt(position).findViewById(R.id.label));

    }
//    public void chageColor(int position, int color) {
//        timers.get(position).getTimer().setTextColor(color);
//    }

    @Override
    public int getCount() {
        return times.size();
    }

    public void clear() {
        times.clear();
        timers.clear();
        preTimes.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
//        System.out.println("==========" + timers.get(position));
//        System.out.println("==========" + timers.get(position).getTimer());

        return timers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

//        System.out.println(parent);
        timersView = convertView;
        parentView = parent;

//        View timerView = (View) getItem(position).getTimer();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            TextView textView =
//            TextView textView=inflater.inflate(R.layout.single_row,parent,false);
            convertView = inflater.inflate(R.layout.single_row, parent, false);


        }


        TextView time = (TextView) convertView.findViewById(R.id.label);
//
        String timeString = times.get(position);
        time.setText(timeString);
//        TimerViewHolder timerViewHolder = new TimerViewHolder(time);
//        timers.add(timerViewHolder);
//        TextView time = (TextView) convertView.findViewById(R.id.label);
        timers.add(time);
//        String timeString = times.get(position);
//        timers.add(time);
//        TextView time2=(TextView)getItem(position);
//        time2.setText(timeString);

        return convertView;
    }
}
