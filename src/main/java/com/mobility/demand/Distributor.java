package com.mobility.demand;

import com.mobility.entity.Demand;
import com.mobility.entity.Employee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.mobility.demand.Worker.Gender.FEMALE;
import static com.mobility.demand.Worker.Gender.MALE;

public class Distributor {

    public LocalDate datePlan;

    public List<Support> completed;

    public List<Support> remaining;
    List<Worker> workers;

    public HashMap<Integer, HashMap<Integer, Integer>> distances;

    public static HashSet<String> cancelStatus = new HashSet<>(Arrays.asList(new String[] {
            "Отказ",
            "Отказ по регламенту",
            "Отмена",
            "Отмена заявки по просьбе пассажира",
            "Отмена заявки по неявке пассажира"
    }));

    public Distributor(LocalDate dPlan, List<Demand> dems, List<Employee> emps, HashMap<Integer, HashMap<Integer, Integer>> dists) {
        datePlan = dPlan;
        distances = dists;

        workers = new ArrayList<>(dems.size());
        HashMap<Employee, Worker> empsWorkers = new HashMap<>();
        for (Employee emp : emps) {
            Worker worker = new Worker(emp);
            workers.add(worker);
            empsWorkers.put(emp, worker);
            worker.setBeginEnd(dPlan);
        }

        remaining = new ArrayList<>(dems.size());
        completed = new ArrayList<>(dems.size());

        for (Demand d : dems) {
            if (Distributor.cancelStatus.contains(d.getStatus())) continue;
            Support support = new Support(d);

            List<Employee> demEmps = d.getEmp();
            if (demEmps.isEmpty()) {
                remaining.add(support);
            }
            else {
                completed.add(support);
                for (Employee emp : demEmps) {
                    Worker worker = empsWorkers.get(emp);
                    worker.addWork(support.track);
                }
            }
        }
    }

    LinkedList<Worker> freeWorkers(List<Worker> workers, Track supportTrack, HashMap<Integer, HashMap<Integer, Integer>> distances) {
        LinkedList<Worker> free = new LinkedList<>();
        Iterator<Worker> iterator = workers.iterator();
        while (iterator.hasNext()) {
            Worker worker = iterator.next();
            Map.Entry<LocalDateTime, Track> entry = worker.dtFree.floorEntry(supportTrack.dtBegin);
            if (entry == null) continue;
            Track workerTrack = entry.getValue();
            LocalDateTime workerBegin = workerTrack.dtBegin;
            if ((workerTrack.stationBegin != null) && (!workerTrack.stationBegin.equals(supportTrack.stationBegin))) {
                workerBegin = workerBegin.plusMinutes(distances.get(workerTrack.stationBegin).get(supportTrack.stationBegin));
                if (workerBegin.isAfter(supportTrack.dtBegin)) continue;
            }
            LocalDateTime workerEnd = workerTrack.dtEnd;
            if ((workerTrack.stationEnd != null) && (!workerTrack.stationEnd.equals(supportTrack.stationEnd))) {
                workerEnd = workerEnd.minusMinutes(distances.get(supportTrack.stationEnd).get(workerTrack.stationEnd));
            }
            if (workerEnd.isBefore(supportTrack.dtEnd)) continue;
            // lunch
            boolean lunch = false;
            Map.Entry<LocalDateTime, LocalDateTime> work = worker.dtWork.floorEntry(supportTrack.dtBegin);
            for (Track track : worker.dtFree.subMap(work.getKey(), true, work.getValue(), true).values()) {
                LocalDateTime dtBegin = track.dtBegin;
                LocalDateTime dtEnd = track.dtEnd;
                if (dtEnd.isBefore(supportTrack.dtEnd) || (dtBegin.isAfter(supportTrack.dtBegin))) {
                    if (worker.canLunch(track, distances)) {
                        lunch = true;
                        break;
                    }
                }
                else {
                    if (dtBegin.isBefore(supportTrack.dtBegin)) {
                        Track before = new Track(track.stationBegin, dtBegin, supportTrack.stationBegin, supportTrack.dtBegin);
                        if (worker.canLunch(before, distances)) {
                            lunch = true;
                            break;
                        }
                    }
                    if (dtEnd.isAfter(supportTrack.dtEnd)) {
                        Track after = new Track(supportTrack.stationEnd, supportTrack.dtEnd, track.stationEnd, dtEnd);
                        if (worker.canLunch(after, distances)) {
                            lunch = true;
                            break;
                        }
                    }
                }
            }
            if (lunch) {
                worker.dtReady = workerBegin;
                worker.freeAfter = ChronoUnit.SECONDS.between(supportTrack.dtEnd, workerEnd);
                free.add(worker);
            }
        }
        return free;
    }

    Collection<Area> toAreas(List<Worker> workers) {
        HashMap<String, Area> areasNames = new HashMap<>();
        for (Worker worker : workers) {
            String areaName = worker.employee.getArea();
            Area area = areasNames.get(areaName);
            if (area == null) {
                area = new Area();
                areasNames.put(areaName, area);
            }
            if (worker.gender.equals(MALE)) {
                area.empM.add(worker);
            }
            else {
                area.empF.add(worker);
            }
        }
        return areasNames.values();
    }

    void distribute(BiFunction<Support, List<Worker>, List<Worker>> func) {
        Iterator<Support> iterator = remaining.iterator();
        while (iterator.hasNext()) {
            Support support = iterator.next();
            List<Worker> freeWorkers = freeWorkers(workers, support.track, distances);
            if (freeWorkers.isEmpty()) continue;
            List<Worker> demWorkers = func.apply(support, freeWorkers);
            if (demWorkers != null) {
                for (Worker worker : demWorkers) {
                    support.addWorker(worker);
                }
                iterator.remove();
                completed.add(support);
            }
        }
    }

    public void distribute() {
        Collections.sort(remaining, (s1, s2) -> s1.track.dtBegin.compareTo(s2.track.dtBegin));
        distribute(this::distributeSingleArea);
        distribute(this::distributeFreeWorkers);
    }

    List<Worker> distributeSingleArea(Support support, List<Worker> freeWorkers) {
        //Distribute single area
        Collection<Area> freeAreas = toAreas(freeWorkers);

        int countM = support.demand.getEmpM();
        int countF = support.demand.getEmpF();

        if (countF == 0) {
            //M only
            TreeMap<Integer, Area> priorityAreas = new TreeMap<>();
            for (Area area : freeAreas) {
                priorityAreas.put(area.empM.size(), area);
            }

            //Single area
            Map.Entry<Integer, Area> entry = priorityAreas.ceilingEntry(countM);
            if (entry != null) {
                Area demArea = entry.getValue();
                Collections.sort(demArea.empM, (w1, w2) -> {
                    //int cmp = w1.dtReady.compareTo(w2.dtReady);
                    int cmp = Long.compare(w1.freeAfter, w2.freeAfter);
                    if (cmp == 0) {
                        //cmp = Long.compare(w1.freeAfter, w2.freeAfter);
                        cmp = w1.dtReady.compareTo(w2.dtReady);
                    }
                    return cmp;
                });
                List<Worker> demWorkers = new ArrayList<>(countM);
                for (int i = 0; i < countM; i++) {
                    Worker worker = demArea.empM.poll();
                    demWorkers.add(worker);
                }
                return demWorkers;
            }
        }
        else {
            //M and F
            freeAreas = freeAreas.stream()
                    .filter(area -> ((area.empM.size() >= countM) && ((area.empM.size() + area.empF.size()) >= (countM + countF))))
                    .collect(Collectors.toList());

            if (!freeAreas.isEmpty()) {
                TreeMap<Integer, Area> priorityAreas = new TreeMap<>();
                for (Area area : freeAreas) {
                    priorityAreas.put(area.empF.size(), area);
                }

                //Single area
                Map.Entry<Integer, Area> entry = priorityAreas.ceilingEntry(countF);
                if (entry == null) {
                    entry = priorityAreas.lastEntry();
                }
                Area demArea = entry.getValue();
                Collections.sort(demArea.empM, (w1, w2) -> {
                    //int cmp = w1.dtReady.compareTo(w2.dtReady);
                    int cmp = Long.compare(w1.freeAfter, w2.freeAfter);
                    if (cmp == 0) {
                        //cmp = Long.compare(w1.freeAfter, w2.freeAfter);
                        cmp = w1.dtReady.compareTo(w2.dtReady);
                    }
                    return cmp;
                });
                Collections.sort(demArea.empF, (w1, w2) -> {
                    //int cmp = w1.dtReady.compareTo(w2.dtReady);
                    int cmp = Long.compare(w1.freeAfter, w2.freeAfter);
                    if (cmp == 0) {
                        //cmp = Long.compare(w1.freeAfter, w2.freeAfter);
                        cmp = w1.dtReady.compareTo(w2.dtReady);
                    }
                    return cmp;
                });
                List<Worker> demWorkers = new ArrayList<>(countM + countF);
                for (int i = 0; i < countM; i++) {
                    Worker worker = demArea.empM.poll();
                    demWorkers.add(worker);
                }
                int count = countF;
                while ((count > 0) && (demArea.empF.size() > 0)) {
                    Worker worker = demArea.empF.poll();
                    demWorkers.add(worker);
                    count--;
                }
                for (int i = 0; i < count; i++) {
                    Worker worker = demArea.empM.poll();
                    demWorkers.add(worker);
                }
                return demWorkers;
            }
        }
        return null;
    }

    List<Worker> distributeFreeWorkers(Support support, List<Worker> freeWorkers) {
        //Distribute free workers

        int countM = support.demand.getEmpM();
        int countF = support.demand.getEmpF();

        Collections.sort(freeWorkers, (w1, w2) -> {
            //int cmp = w1.dtReady.compareTo(w2.dtReady);
            int cmp = Long.compare(w1.freeAfter, w2.freeAfter);
            if (cmp == 0) {
                //cmp = Long.compare(w1.freeAfter, w2.freeAfter);
                cmp = w1.dtReady.compareTo(w2.dtReady);
            }
            return cmp;
        });
        List<Worker> demWorkers = new ArrayList<>(countM + countF);
        for (Worker worker : freeWorkers) {
            if (worker.gender.equals(MALE)) {
                if (countM > 0) {
                    demWorkers.add(worker);
                    countM--;
                }
            }
            else {
                if (countF > 0) {
                    demWorkers.add(worker);
                    countF--;
                }
            }
            if ((countM == 0) && (countF == 0)) {
                return demWorkers;
            }
        }
        return null;
    }

}
