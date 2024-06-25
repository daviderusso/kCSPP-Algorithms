package InstanceUtils;

import java.util.ArrayList;

public class Instance {

    public String name;

    public int source;
    public int target;
    public int kColor;

    public Graph graphDirectet;

    public ArrayList<Edge> solution;

    public Instance(Graph g, int source, int target, int kColor) {
        this.graphDirectet = g;
        this.source = source;
        this.target = target;
        this.kColor = kColor;
    }


    public void printInstanceDirected() {
        System.out.println(name);
        System.out.println("From: " + source + " to: " + target + " with " + kColor + " colors");
        graphDirectet.print();
    }
}
