package com.mobility.demand;

import com.mobility.entity.Demand;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Support {

    public Demand demand;

    public Track track;

    //Employee must be in place before demand begin
    public static final int EMP_BEFORE = 15; //minutes

    public Support(Demand d) {
        demand = d;
        LocalDateTime dtBegin = LocalDateTime.of(d.getDatePlan(), d.getTimePlan());
        LocalDateTime dtEnd = dtBegin.plusSeconds(d.getDuration().toSecondOfDay());
        dtBegin = dtBegin.minusMinutes(EMP_BEFORE);
        track = new Track(d.getStBegin().getId(), dtBegin, d.getStEnd().getId(), dtEnd);
    }

    public void addWorker(Worker w) {
        demand.getEmp().add(w.employee);
        w.addWork(track);
    }

    public String getCaption() {
        return String.format("%d %s  [линия_%s] -> %s  [линия_%s] Мужчин %s, Женщин %s",
                demand.getId(),
                demand.getStBegin().getName(), demand.getStBegin().getLine().getName(),
                demand.getStEnd().getName(), demand.getStEnd().getLine().getName(),
                ((demand.getEmpM() > 0) ? demand.getEmpM() : "нет"), ((demand.getEmpF() > 0) ? demand.getEmpF() : "нет"));
    }

}
