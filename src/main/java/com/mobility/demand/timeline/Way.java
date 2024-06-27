package com.mobility.demand.timeline;

import com.mobility.demand.Track;

public class Way {

    public String caption;
    public long id;
    Track track;
    Kind kind;
    public int duration;

    public Way(Kind k, int d) {
        kind = k;
        duration = d;
    }

    public Way(String c, Track t, Kind k) {
        caption = c;
        track = t;
        kind = k;
    }

    public Way(String c, long i, Track t, Kind k) {
        caption = c;
        id = i;
        track = t;
        kind = k;
    }

    public enum Kind {
        EMPTY,
        FREE,
        TASK
    }

    public boolean isEmpty() {
        return kind == Kind.EMPTY;
    }

    public boolean isFree() {
        return kind == Kind.FREE;
    }

    public boolean isTask() {
        return kind == Kind.TASK;
    }
}
