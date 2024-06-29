package com.mobility.demand.timeline;

import java.time.LocalDateTime;

public class Point {

    public LocalDateTime datetime;
    public int pos;

    static final int SCALE = 15; //minutes
    static final int MINUTE_ROUND_UP = 8;
    static final int SECOND_ROUND_UP = 30;

    public Point(LocalDateTime dt) {
        int second = dt.getSecond();
        dt = dt.withSecond(0);
        if (second >= SECOND_ROUND_UP) {
            dt = dt.plusMinutes(1);
        }
        int minute = dt.getMinute();
        int m = (minute / SCALE) * SCALE;
        dt = dt.withMinute(m);
        if ((minute - m) >= MINUTE_ROUND_UP) {
            dt = dt.plusMinutes(SCALE);
        }
        datetime = dt;
    }
}
