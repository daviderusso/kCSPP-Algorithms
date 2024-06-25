package Solver;

import InstanceUtils.Edge;

import java.util.ArrayList;

public class ThreadReductPreprocess extends Thread {

    int idStart;
    int idEnd;
    boolean[] usedNodes;
    ArrayList<Edge> solution;

    public ThreadReductPreprocess(int idStart, int idEnd, boolean[] usedNodes, ArrayList<Edge> solution) {
        this.idEnd = idEnd;
        this.idStart = idStart;
        this.usedNodes = usedNodes;
        this.solution = solution;
    }

    @Override
    public void run() {
        for (int i = idStart; i < idEnd; i++) {
            synchronized (usedNodes) {
                usedNodes[solution.get(i).idV1] = true;
                usedNodes[solution.get(i).idV2] = true;
            }
        }
    }
}
