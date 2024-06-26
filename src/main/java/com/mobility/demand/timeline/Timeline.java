package com.mobility.demand.timeline;

import com.mobility.demand.Distributor;
import com.mobility.demand.Support;
import com.mobility.demand.Track;
import com.mobility.demand.Worker;
import com.mobility.entity.Demand;
import com.mobility.entity.Employee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Timeline {

    public LocalDate datePlan;

    public ArrayList<Point> points;
    HashMap<LocalDateTime, Point> datetimesPoints;

    public ArrayList<ArrayList<Way>> remaining;
    public ArrayList<Map.Entry<Employee, ArrayList<ArrayList<Way>>>> completed;

    public Timeline(LocalDate dPlan, List<Demand> dems, List<Employee> emps, HashMap<Integer, HashMap<Integer, Integer>> dists) {
        datePlan = dPlan;

        HashSet<LocalDateTime> datetimes = new HashSet<>();
        ArrayList<TreeMap<LocalDateTime, Way>> remainingTasks = new ArrayList<>();
        HashMap<Worker, ArrayList<TreeMap<LocalDateTime, Way>>> completedTasks = new HashMap<>();

        HashMap<Employee, Worker> workers = new HashMap<>();
        for (Employee emp : emps) {
            Worker worker = new Worker(emp);
            workers.put(emp, worker);
            worker.setBeginEnd(dPlan);
            for (Track track : worker.dtFree.values()) {
                datetimes.add(track.dtBegin);
                datetimes.add(track.dtEnd);
            }
            ArrayList<TreeMap<LocalDateTime, Way>> tasks = new ArrayList<>();
            completedTasks.put(worker, tasks);
        }

        for (Demand d : dems) {
            if (Distributor.cancelStatus.contains(d.getStatus())) continue;
            Support support = new Support(d);
            datetimes.add(support.track.dtBegin);
            datetimes.add(support.track.dtEnd);

            List<Employee> demEmps = d.getEmp();
            if (demEmps.isEmpty()) {
                addTask(new Way(support.getCaption(), support.demand.getId(), support.track, Way.Kind.TASK), remainingTasks);
            }
            else {
                for (Employee emp : demEmps) {
                    Worker worker = workers.get(emp);
                    ArrayList<TreeMap<LocalDateTime, Way>> tasks = completedTasks.get(worker);
                    addTask(new Way(support.getCaption(), support.demand.getId(), support.track, Way.Kind.TASK), tasks);
                    worker.addWork(support.track);
                }
            }
        }

        for (Map.Entry<Worker, ArrayList<TreeMap<LocalDateTime, Way>>> entry : completedTasks.entrySet()) {
            Worker worker = entry.getKey();
            ArrayList<TreeMap<LocalDateTime, Way>> tasks = entry.getValue();
            for (Track track : worker.dtFree.values()) {
                String caption = worker.canLunch(track, dists) ? "Обед возможен" : "";
                if ((track.stationBegin != null) && (track.stationEnd != null)) {
                    if (track.getMinutes() >= Point.SCALE) {
                        long dist = track.getMoveMinutes(dists);
                        if (dist < Point.SCALE) {
                            dist = Point.SCALE;
                        }
                        LocalDateTime dtMoveEnd = track.dtBegin.plusMinutes(dist);
                        datetimes.add(dtMoveEnd);
                        Track move = new Track(track.stationBegin, track.dtBegin, track.stationEnd, dtMoveEnd);
                        addTask(new Way("", move, Way.Kind.MOVE), tasks);
                        if (dtMoveEnd.isBefore(track.dtEnd)) {
                            Track free = new Track(track.stationEnd, dtMoveEnd, track.stationEnd, track.dtEnd);
                            addTask(new Way(caption, free, Way.Kind.FREE), tasks);
                        }
                    }
                    else {
                        addTask(new Way("", track, Way.Kind.MOVE), tasks);
                    }
                } else {
                    addTask(new Way(caption, track, Way.Kind.FREE), tasks);
                }
            }
        }

        HashMap<LocalDateTime, Point> dts = new HashMap<>();
        TreeMap<LocalDateTime, Point> timemap = new TreeMap<>();
        for (LocalDateTime datetime : datetimes) {
            Point point = new Point(datetime);
            Point p = timemap.get(point.datetime);
            if (p == null) {
                timemap.put(point.datetime, point);
            }
            else {
                point = p;
            }
            dts.put(datetime, point);
        }
        ArrayList<Point> ps = new ArrayList<>(timemap.size());
        int max = -1;
        Iterator<Point> iterator = timemap.values().iterator();
        LocalDateTime dt = timemap.firstKey().withMinute(0);
        while (iterator.hasNext()) {
            Point point = iterator.next();
            while (dt.isBefore(point.datetime)) {
                Point p = new Point(dt);
                p.pos = ++max;
                ps.add(p);
                dt = dt.plusMinutes(Point.SCALE);
            }
            point.pos = ++max;
            ps.add(point);
            dt = point.datetime.plusMinutes(Point.SCALE);
        }
        points = ps;
        datetimesPoints = dts;

        remaining = tasksToIntervals(remainingTasks, max);

        completed = new ArrayList<>(completedTasks.size());
        HashMap<Employee, ArrayList<ArrayList<Way>>> empsCompleted = new HashMap<>();
        for (Map.Entry<Worker, ArrayList<TreeMap<LocalDateTime, Way>>> entry : completedTasks.entrySet()) {
            Worker worker = entry.getKey();
            ArrayList<TreeMap<LocalDateTime, Way>> tasks = entry.getValue();
            ArrayList<ArrayList<Way>> intervals = tasksToIntervals(tasks, max);
            empsCompleted.put(worker.employee, intervals);
        }
        for (Employee emp : emps) {
            ArrayList<ArrayList<Way>> intervals = empsCompleted.get(emp);
            Map.Entry<Employee, ArrayList<ArrayList<Way>>> work = new AbstractMap.SimpleEntry<>(emp, intervals);
            completed.add(work);
        }
    }

    void addTask(Way way, ArrayList<TreeMap<LocalDateTime, Way>> tasks) {
        int row = 0;
        for (;;) {
            if (row == tasks.size()) {
                TreeMap<LocalDateTime, Way> series = new TreeMap<>();
                tasks.add(series);
                series.put(way.track.dtBegin, way);
                break;
            }
            TreeMap<LocalDateTime, Way> series = tasks.get(row);
            Map.Entry<LocalDateTime, Way> entry = series.floorEntry(way.track.dtBegin);
            if (entry == null) {
                entry = series.firstEntry();
                if (!entry.getValue().track.dtBegin.isBefore(way.track.dtEnd)) {
                    series.put(way.track.dtBegin, way);
                    break;
                }
            }
            else {
                if (!entry.getValue().track.dtEnd.isAfter(way.track.dtBegin)) {
                    entry = series.higherEntry(entry.getKey());
                    if (entry == null) {
                        series.put(way.track.dtBegin, way);
                        break;
                    }
                    else if (!entry.getValue().track.dtBegin.isBefore(way.track.dtEnd)) {
                        series.put(way.track.dtBegin, way);
                        break;
                    }
                }
            }
            row++;
        }
    }

    ArrayList<ArrayList<Way>> tasksToIntervals(ArrayList<TreeMap<LocalDateTime, Way>> tasks, int max) {
        ArrayList<ArrayList<Way>> intervals = new ArrayList<>(tasks.size());
        for (TreeMap<LocalDateTime, Way> series : tasks) {
            int pos = 0;
            ArrayList<Way> row = new ArrayList<>(series.size());
            intervals.add(row);
            for (Way task : series.values()) {
                int begin = datetimesPoints.get(task.track.dtBegin).pos;
                if (begin > pos) {
                    Way interval = new Way(Way.Kind.EMPTY, begin - pos);
                    row.add(interval);
                }
                int end = datetimesPoints.get(task.track.dtEnd).pos;
                if (end > begin) {
                    task.duration = end - begin;
                    row.add(task);
                }
                pos = end;
            }
            if (pos < max) {
                Way interval = new Way(Way.Kind.EMPTY, max - pos);
                row.add(interval);
            }
        }
        return intervals;
    }
}
