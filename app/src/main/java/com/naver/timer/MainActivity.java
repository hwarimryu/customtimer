package com.naver.timer;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static int counter = 0;
    ArrayList<String> customTimers;
    ImageButton addBtn;//+버튼
    Button submitBtn;
    ListView cTimerList;
    ArrayAdapter<String> tAdapter;//cTimerList에 적용시킴
    LinearLayout linearLayout;
    EditText newTimerNameText;
    TimerDBHelper timerDBHelper;
    //db연동
    SQLiteDatabase sqlDB;
    SwipeDismissListViewTouchListener touchListener;

    //select * from myTimerNames
    void selectTimerName() {
        Cursor cursor = null;
        try {
            cursor = sqlDB.query("myTimerNames", null, null, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    customTimers.add(cursor.getString(cursor.getColumnIndex("tName")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addBtn = (ImageButton) findViewById(R.id.btnAddTimer);
        submitBtn = (Button) findViewById(R.id.btnSubmitName);
        linearLayout = (LinearLayout) findViewById(R.id.newTimerName);
        newTimerNameText = (EditText) findViewById(R.id.newTimerNameText);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.VISIBLE);
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = newTimerNameText.getText().toString();
                ContentValues contentValues = new ContentValues();

                contentValues.put("tName", string);

                sqlDB.insert("myTimerNames", null, contentValues);
                linearLayout.setVisibility(View.INVISIBLE);
                customTimers.add(string);
                tAdapter.notifyDataSetChanged();
            }
        });
        customTimers = new ArrayList<String>();

        timerDBHelper = new TimerDBHelper(this);
        sqlDB = timerDBHelper.getWritableDatabase();

        selectTimerName();

        tAdapter = new ArrayAdapter<String>(this, R.layout.single_row, R.id.label, customTimers);
        
        cTimerList = (ListView) findViewById(R.id.timers);

        cTimerList.setAdapter(tAdapter);


        touchListener =
                new SwipeDismissListViewTouchListener(
                        cTimerList,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    String string =customTimers.get(position);
                                    sqlDB.delete("myTimerNames","tName=?",new String[]{string});
                                    tAdapter.remove(tAdapter.getItem(position));
                                }
                                tAdapter.notifyDataSetChanged();
                            }
                        });

        cTimerList.setOnTouchListener(touchListener);
        cTimerList.setOnScrollListener(touchListener.makeScrollListener());


        cTimerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //cTimerList에서 한 item(커스텀타이머) 터치하면 그 타이머 열림.
                Intent cTimerIntent = new Intent(getApplicationContext(), TimerActivity.class);
                ListView listView = (ListView) parent;
                String timerName = (String) listView.getItemAtPosition(position);
                cTimerIntent.putExtra("timerName", timerName);//선택된 cTimer의 이름 넘겨줌
                startActivity(cTimerIntent);
                finish();
            }
        });
    }

}
