package com.mobility.demand;

import com.mobility.entity.Employee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.TreeMap;

public class Worker {

    public Employee employee;

    public TreeMap<LocalDateTime, Track> dtFree;

    LocalDateTime dtReady;
    Gender gender;

    enum Gender {
        MALE,
        FEMALE
    }

    public Worker(Employee emp) {
        employee = emp;
        switch (emp.getGender()) {
            case 'М':
                gender = Gender.MALE;
                break;
            case 'Ж':
                gender = Gender.FEMALE;
                break;
            default:
                throw new RuntimeException(String.format("Unknown gender '%s'", emp.getGender()));
        }
        dtFree = new TreeMap<>();
    }

    public void setBeginEnd(LocalDate dPlan) {
        LocalTime tBegin = employee.getTimeBegin();
        LocalTime tEnd = employee.getTimeEnd();
        LocalDateTime dtBegin = LocalDateTime.of(dPlan, tBegin);
        LocalDateTime dtEnd = LocalDateTime.of(dPlan, tEnd);
        if (tEnd.isBefore(tBegin)) {
            addFree(new Track(dtBegin.minusDays(1), dtEnd));
            addFree(new Track(dtBegin, dtEnd.plusDays(1)));
        }
        else {
            addFree(new Track(dtBegin, dtEnd));
        }
    }

    public void addFree(Track free) {
        Integer stationBegin = free.stationBegin;
        LocalDateTime dtBegin = free.dtBegin;
        Integer stationEnd = free.stationEnd;
        LocalDateTime dtEnd = free.dtEnd;
        Map.Entry<LocalDateTime, Track> floorEntry = dtFree.floorEntry(free.dtBegin);
        Map.Entry<LocalDateTime, Track> ceilingEntry = dtFree.ceilingEntry(free.dtBegin);
        if (floorEntry != null) {
            Track floor = floorEntry.getValue();
            if (floor.dtEnd.equals(free.dtBegin) && floor.stationEnd.equals(free.stationBegin)) {
                dtFree.remove(floor.dtBegin);
                dtBegin = floor.dtBegin;
                stationBegin = floor.stationBegin;
            }
        }
        if (ceilingEntry != null) {
            Track ceiling = ceilingEntry.getValue();
            if (ceiling.dtBegin.equals(free.dtEnd) && ceiling.stationBegin.equals(free.stationEnd)) {
                dtFree.remove(ceiling.dtBegin);
                dtEnd = ceiling.dtEnd;
                stationEnd = ceiling.stationEnd;
            }
        }
        free = new Track(stationBegin, dtBegin, stationEnd, dtEnd);
        dtFree.put(free.dtBegin, free);
    }

    public void addWork(Track work) {
        Track free = dtFree.floorEntry(work.dtBegin).getValue();
        dtFree.remove(free.dtBegin);
        if (free.dtBegin.isBefore(work.dtBegin)) {
            Track before = new Track(free.stationBegin, free.dtBegin, work.stationBegin, work.dtBegin);
            dtFree.put(before.dtBegin, before);
        }
        if (free.dtEnd.isAfter(work.dtEnd)) {
            Track after = new Track(work.stationEnd, work.dtEnd, free.stationEnd, free.dtEnd);
            dtFree.put(after.dtBegin, after);
        }
    }
}
