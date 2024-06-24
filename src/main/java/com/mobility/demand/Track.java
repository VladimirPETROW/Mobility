package com.mobility.demand;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class Track {

    String caption;
    Integer stationBegin, stationEnd;
    public LocalDateTime dtBegin, dtEnd;

    public Track(LocalDateTime dtb, LocalDateTime dte) {
        dtBegin = dtb;
        dtEnd = dte;
    }

    public Track(Integer stb, LocalDateTime dtb, Integer ste, LocalDateTime dte) {
        stationBegin = stb;
        dtBegin = dtb;
        stationEnd = ste;
        dtEnd = dte;
    }

    public long getFreeMinutes(HashMap<Integer, HashMap<Integer, Integer>> distances) {
        long freeMinutes = ChronoUnit.MINUTES.between(dtBegin, dtEnd);
        if ((stationBegin != null) && (stationEnd != null) && (stationBegin != stationEnd)) {
            freeMinutes -= distances.get(stationBegin).get(stationEnd);
        }
        return freeMinutes;
    }
}
