package com.naver.timer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TimerActivity extends Activity {

    private static final int MESSAGE_TIMER_START = 100;
    private static final int MESSAGE_TIMER_REPEAT = 101;
    private static final int MESSAGE_TIMER_STOP = 102;
    private static final int MESSAGE_TIMER_PAUSE=103;
    //알림 소리or진동
    Vibrator vib;
    Uri notification;
    Ringtone ringtone;
    TimerAdapter timerAdapter = null;
    String timerName;
    TimerTask tt;
    TextView newTimerText, tvTimerName;
    Button btnSubmit;
    ImageButton btnBack, btnAdd, btnStop, btnPause, btnStart, btnSound;
    ListView cTimerList;
    LinearLayout newTimerLayout;
    EditText repeatText;
    int pause = 0;
    static int counter = 0;
    int COUNT = 0, index = 0, repeat = 2, SOUND = 1;// 0;
    //SOUND==0이면 진동(1번만)
    //SOUND==1이면 소리
    //SOUND==2이면 매번 소리
    //SOUND==3이면 매번 진동
    RestartHandler timerHandler = null;
    ArrayList<TimerData> counters = null;

    //db연동
    TimerDBHelper timerDBHelper;
    SQLiteDatabase sqlDB;

    SwipeDismissListViewTouchListener touchListener;

    public TimerActivity() {
    }

    void selectTimers(String tName) {
        timerAdapter.clear();
        counters.clear();
        Cursor cursor = null;
        try {
            cursor = sqlDB.query("myTimers", null, "tName=" + "?", new String[]{tName}, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    TimerData data = new TimerData(cursor.getInt(cursor.getColumnIndex("tNumber")), cursor.getInt(cursor.getColumnIndex("id")));
                    timerAdapter.addTime(cursor.getString(cursor.getColumnIndex("tString")));

                    counters.add(data);
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

    Boolean insertNewTimer(String timerName, String time) {
        ContentValues contentValues = new ContentValues();

        if (time.length() > 5) {
            Toast.makeText(getApplicationContext(), "mm:ss 또는 ss만 가능합니다.", Toast.LENGTH_SHORT).show();
            return Boolean.FALSE;

        } else if (time.length() <= 2) {
            //초만 있는 경우
            COUNT = Integer.parseInt(time);
        } else {
            //분:초 인경우
            String[] tempTime = null;
            tempTime = time.split(":");
            COUNT += 60 * Integer.parseInt(tempTime[0]);
            COUNT += Integer.parseInt(tempTime[1]);
        }

        timerAdapter.addTime(time);

        contentValues.put("tName", timerName);
        contentValues.put("tNumber", COUNT);
        contentValues.put("tString", time);

        sqlDB.insert("myTimers", null, contentValues);

        selectTimers(this.timerName);
        return Boolean.TRUE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customtimer);

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnStart = (ImageButton) findViewById(R.id.startBtn);
        btnSound = (ImageButton) findViewById(R.id.soundBtn);
        btnStop = (ImageButton) findViewById(R.id.stopBtn);
        btnAdd = (ImageButton) findViewById(R.id.addBtn);
        btnPause = (ImageButton) findViewById(R.id.pauseBtn);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        repeatText = (EditText) findViewById(R.id.repeatText);
        tvTimerName = (TextView) findViewById(R.id.timerName);
        newTimerLayout = (LinearLayout) findViewById(R.id.newTimer);
        newTimerText = (TextView) findViewById(R.id.newTimerText);
        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);

        timerAdapter = new TimerAdapter(this);
        timerAdapter.addTime("5");

        counters = new ArrayList<TimerData>();

        Intent intent = getIntent();
        this.timerName = intent.getStringExtra("timerName");

        tvTimerName.setText(this.timerName);

        timerDBHelper = new TimerDBHelper(this);

        sqlDB = timerDBHelper.getWritableDatabase();//쓰기

        selectTimers(this.timerName);

        cTimerList = (ListView) findViewById(R.id.timers);
        cTimerList.setAdapter(timerAdapter);


        touchListener =
                new SwipeDismissListViewTouchListener(
                        cTimerList,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {//삭제
                                for (int position : reverseSortedPositions) {
                                    String id = counters.get(position).getId().toString();
                                    sqlDB.delete("myTimers", "id=?", new String[]{id});
                                    timerAdapter.remove(position);
                                    counters.remove(position);
                                }
                                timerAdapter.notifyDataSetChanged();
                            }
                        });

        cTimerList.setOnTouchListener(touchListener);
        cTimerList.setOnScrollListener(touchListener.makeScrollListener());

        /*타이머 추가*/
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("btnAdd Clicked", "새 타이머 추가1");
                newTimerLayout.setVisibility(View.VISIBLE);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = newTimerText.getText().toString();
                Log.i("btnSubmit :", time);
                COUNT = 0;
                if (insertNewTimer(tvTimerName.getText().toString(), time)) {
                    newTimerLayout.setVisibility(View.INVISIBLE);
                    newTimerText.setText("");
                } else {
                    newTimerText.setText("");
                }

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerAdapter.clear();

                Intent MainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(MainIntent);
                finish();
            }
        });


        final Timer timer = new Timer();
        timerHandler = new RestartHandler();

        /*타이머 소리 설정 변경*/
        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SOUND==0이면 진동(1번만)
                //SOUND==1이면 소리
                //SOUND==2이면 매번 소리
                //SOUND==3이면 매번 진동
               if(SOUND==0){
                   btnSound.setImageResource(R.drawable.vibrateloop);
                   SOUND=3;
               }else if(SOUND==1){
                   btnSound.setImageResource(R.drawable.soundloop);
                   SOUND=2;
               }else if(SOUND==2){
                   btnSound.setImageResource(R.drawable.vibrate);
                   SOUND=0;
               }else{//SOUND==3
                   btnSound.setImageResource(R.drawable.sound);
                   SOUND=1;
               }
            }
        });
        /*타이머 리스트 실행*/
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 0;
                repeat = Integer.parseInt(repeatText.getText().toString());
                repeat--;
                runOnetime(timer);
            }
        });

        /*일시정지*/
        btnPause.setOnClickListener(new View.OnClickListener() {
            //0이면 pause
            //1이면 restart
            @Override
            public void onClick(View v) {
                if (pause == 0) {//일시정지
                    timerHandler.sendEmptyMessage(MESSAGE_TIMER_PAUSE);
                    Log.e("마지막 실행 시간", "tt태스크를 cancel합니다.");
                    timerAdapter.changeColor(index, Color.BLUE);
                    tt.cancel();
                    pause = 1;
                } else {
                    timerHandler.sendEmptyMessage(MESSAGE_TIMER_START);
                    Log.e("마지막 실행 시간", "tt태스크를 restart합니다.");
//                    timerAdapter.changeColor(index, Color.RED);
                    tt = new TimerTask() {//정지했던 상태에서 시작-> 숫자, 색깔 바꾸는거
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    timerAdapter.changeColor(index, Color.RED);
                                    TextView temp = (TextView) timerAdapter.getItem(index);

                                    Log.e("1번 태스크 카운터", String.valueOf(counter));
                                    counter--;

                                    int mm = counter / 60;
                                    int ss = counter % 60;

                                    timerAdapter.times.set(index, Integer.toString(mm) + " : " + Integer.toString(ss));
                                    timerAdapter.notifyDataSetChanged();
                                    if (counter == 0) {
                                        timerAdapter.changeColor(index, Color.BLACK);
                                        index++;
                                        if(SOUND==3){//진동모드
                                            vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                            vib.vibrate(500);
                                        }else if (SOUND == 2) {
                                            ringtone.play();
                                        }
                                        if (counters.size() > (index)) {//마지막 타이머 아닌 경우
                                            timerAdapter.changeColor(index, Color.RED);
                                            counter = counters.get(index).getTime().intValue();
                                            timerAdapter.notifyDataSetChanged();
                                            if(SOUND==3){//진동모드
                                                vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                                vib.vibrate(500);
                                            }else if (SOUND == 2) {
                                                ringtone.play();
                                            }
                                        } else if (repeat == 0) {//전체 반복 없는 경우-> 완전끝
                                            index = 0;
                                            timerAdapter.resetTimes();
                                            timerHandler.sendEmptyMessage(MESSAGE_TIMER_STOP);
                                            if(SOUND==3||SOUND==0){//진동모드
                                                vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                                vib.vibrate(500);
                                            }else if (SOUND == 2||SOUND==1) {
                                                ringtone.play();
                                            }
                                            cancel();
                                        } else {//마지막 타이머 끝나고, 전체 반복 있는 경우
                                            index = 0;
                                            repeat--;
                                            timerAdapter.resetTimes();
                                            counter = counters.get(index).getTime().intValue();
                                        }
                                    }
                                }
                            });
                        }
                    };

                    timer.schedule(tt, 1000, 1000);
                    tt.run();

                    pause = 0;
                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerHandler.sendEmptyMessage(MESSAGE_TIMER_STOP);
                tt.cancel();
                timerAdapter.changeColor(index, Color.BLACK);
                index = 0;
                timerAdapter.resetTimes();
            }
        });
    }


    public void runOnetime(Timer timer) {
        timerAdapter.resetTimes();

        counter = counters.get(index).getTime().intValue();//ok

        tt = new TimerTask() {//숫자, 색깔 바꿈
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timerAdapter.changeColor(index, Color.RED);
                        TextView temp = (TextView) timerAdapter.getItem(index);
                        Log.e("1번 태스크 카운터", String.valueOf(counter));
                        counter--;
                        int mm = counter / 60;
                        int ss = counter % 60;

                        timerAdapter.times.set(index, Integer.toString(mm) + " : " + Integer.toString(ss));
                        timerAdapter.notifyDataSetChanged();
                        if (counter == 0) {
                            timerAdapter.changeColor(index, Color.BLACK);
                            index++;
                            if(SOUND==3){//진동모드
                                vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                vib.vibrate(500);
                           }else if (SOUND == 2) {
                                ringtone.play();
                           }
                            if (counters.size() > (index)) {//마지막 타이머 아닌 경우
                                timerAdapter.changeColor(index, Color.RED);
                                counter = counters.get(index).getTime().intValue();
                                timerAdapter.notifyDataSetChanged();
                                if(SOUND==3){//진동모드
                                    vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                    vib.vibrate(500);
                                }else if (SOUND == 2) {
                                    ringtone.play();
                                }
                            } else if (repeat == 0) {//전체 반복 없는 경우-> 완전끝
                                index = 0;
                                timerAdapter.resetTimes();
                                timerHandler.sendEmptyMessage(MESSAGE_TIMER_STOP);
                                if(SOUND==3||SOUND==0){//진동모드
                                    vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                    vib.vibrate(500);
                                }else if (SOUND == 2||SOUND==1) {
                                    ringtone.play();
                                }
                                cancel();
                            } else {//마지막 타이머 끝나고, 전체 반복 있는 경우
                                index = 0;
                                repeat--;
                                timerAdapter.resetTimes();
                                counter = counters.get(index).getTime().intValue();
                            }
                        }
                    }
                });
            }
        };
        timer.schedule(tt, 1000, 1000);
    }

    private class RestartHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TIMER_START:
                    //타이머 초기화 기능
                    Log.d("TimerHandler", "Timer Start.");
                    //타이머 시작을 여러번 눌렀을 때 여러 스레드가 실행되는 것을 방지하기 위해.
                    this.removeMessages(MESSAGE_TIMER_REPEAT);
                    this.sendEmptyMessage(MESSAGE_TIMER_REPEAT);
                    break;
                case MESSAGE_TIMER_REPEAT:
                    //실제 타이머 줄어드는 부분
                    this.sendEmptyMessageDelayed(MESSAGE_TIMER_REPEAT, 1000);
                    break;
                case MESSAGE_TIMER_STOP:
                    Log.d("TimerHandler", "Timer End.");
                    this.removeMessages(MESSAGE_TIMER_REPEAT);//MESSAGE_TIMER_REPEAT 메세지를 삭제하면 타이머 종료
                    break;
                case MESSAGE_TIMER_PAUSE:
                    Log.d("TimerHandler","Timer Pause");
                    this.removeMessages(MESSAGE_TIMER_REPEAT);//MESSAGE_TIMER_REPEAT 메세지를 삭제하면 타이머 종료
                    break;

            }
        }
    }

}

