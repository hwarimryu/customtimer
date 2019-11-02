package com.naver.timer;

public class TimerData {
    private Integer time;
    private Integer id;

    public TimerData(Integer time, Integer id) {
        this.time = time;
        this.id = id;
    }

    public Integer getTime() {
        return time;
    }

    public Integer getId() {
        return id;
    }
}
