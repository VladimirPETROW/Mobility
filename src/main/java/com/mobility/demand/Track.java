package com.mobility.demand;

import java.time.LocalDateTime;

public class Track {

    Integer stationBegin, stationEnd;
    LocalDateTime dtBegin, dtEnd;

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

}
