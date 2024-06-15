package com.mobility.demand;

import com.mobility.entity.Demand;

import java.time.LocalDateTime;

public class Support {

    public Demand demand;

    public Track track;

    //Employee must be in place before demand begin
    static final int EMP_BEFORE = 15; //minutes

    public Support(Demand d) {
        demand = d;
        LocalDateTime dtBegin = LocalDateTime.of(d.getDatePlan(), d.getTimePlan()).minusMinutes(EMP_BEFORE);
        LocalDateTime dtEnd = dtBegin.plusSeconds(d.getDuration().toSecondOfDay());
        track = new Track(d.getStBegin().getId(), dtBegin, d.getStEnd().getId(), dtEnd);
    }

    public void addWorker(Worker w) {
        demand.getEmp().add(w.employee);
        w.addWork(track);
    }
}
