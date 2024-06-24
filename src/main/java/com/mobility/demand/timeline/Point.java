package com.mobility.demand.timeline;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Point {

    public LocalDateTime datetime;
    public int pos;

    Point(LocalDateTime dt) {
        LocalDateTime ceiling = dt.truncatedTo(ChronoUnit.MINUTES);
        if (ceiling.isBefore(dt)) {
            ceiling = ceiling.plusMinutes(1);
        }
        datetime = ceiling;
    }
}
