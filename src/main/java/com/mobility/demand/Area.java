package com.mobility.demand;

import java.util.LinkedList;

public class Area {

    public LinkedList<Worker> empM;
    public LinkedList<Worker> empF;

    public Area() {
        empM = new LinkedList<>();
        empF = new LinkedList<>();
    }

    public Area(LinkedList<Worker> eM, LinkedList<Worker> eF) {
        empM = eM;
        empF = eF;
    }

}
