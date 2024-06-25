package Solver;

import InstanceUtils.Utils;
import InstanceUtils.Edge;
import InstanceUtils.Vertex;
import InstanceUtils.Instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Solver {

    public Instance instance;

    public DijkstraUtilsOpt dijkstraUtils;

    public Solver(Instance instance) {
        this.instance = instance;
//        this.dijkstraUtils = new DijkstraUtils(instance.graphDirectet);
        this.dijkstraUtils = new DijkstraUtilsOpt(instance.graphDirectet);
    }

    public boolean checkAdmissibility(ArrayList<Edge> solution, boolean print) {
        if (solution == null || solution.size() <= 0) {
            if (print) {
                System.out.println("ERROR: no solution");
            }
            return false;
        }

        //Check Color
        boolean[] usedColor = new boolean[instance.graphDirectet.nColor];
        int countColor = 0;
        for (int i = 0, l = solution.size(); i < l; i++) {
            usedColor[solution.get(i).color] = true;
        }
        for (int i = 0; i < usedColor.length; i++) {
            if (usedColor[i]) {
                countColor++;
            }
        }
        if (countColor > instance.kColor) {
            if (print) {
                System.err.println("ERROR: invalid n of color: " + (countColor - instance.kColor) + " more");
            }
            return false;
        }

        //Check subtour
        int[] nOfIncident = new int[instance.graphDirectet.nVertices];
        for (int i = 0, l = solution.size(); i < l; i++) {
            nOfIncident[solution.get(i).idV1]++;
            nOfIncident[solution.get(i).idV2]++;
        }
        for (int i = 0; i < nOfIncident.length; i++) {
            if (nOfIncident[i] > 2) {
                System.out.println("ERROR node:" + i + " have " + nOfIncident[i] + " incident arcs");
            }
            if (i == instance.source) {
                if (nOfIncident[i] > 1) {
                    System.out.println("ERROR source node:" + i + " have " + nOfIncident[i] + " incident arcs");
                }
            } else if (i == instance.target) {
                if (nOfIncident[i] > 1) {
                    System.out.println("ERROR target node:" + i + " have " + nOfIncident[i] + " incident arcs");
                }
            }
        }

        //Check Path
        solution = this.reorderSolution(solution);
        if (solution.get(0).idV1 != instance.source || solution.get(solution.size() - 1).idV2 != instance.target) {
            if (print) {
                System.err.println("ERROR: invalid source or dest");
            }
            return false;
        }
        for (int i = 0, l = solution.size(); i < l - 1; i++) {
            if (solution.get(i).idV2 != solution.get(i + 1).idV1) {
                if (print) {
                    System.err.println("ERROR: invalid path");
                }
                return false;
            }
        }
        return true;
    }

    public int[] evaluateSolution(ArrayList<Edge> solution) {
        int[] evaluation = new int[2]; //0- nColor -- 1- fitness value
        if (solution != null && solution.size() > 0) {
            boolean[] usedColor = new boolean[instance.graphDirectet.nColor];

            for (int i = 0, l = solution.size(); i < l; i++) {
                usedColor[solution.get(i).color] = true;
                evaluation[1] += solution.get(i).cost;
            }

            for (int i = 0; i < usedColor.length; i++) {
                if (usedColor[i]) {
//                    System.out.print(i + " ");
                    evaluation[0]++;
                }
            }
//            System.out.println();
        }
        return evaluation;
    }

    public final ArrayList<Edge> reorderSolution(ArrayList<Edge> solution) {
        boolean[] consideredEdge = new boolean[solution.size()];
        int toConsider = consideredEdge.length;
        ArrayList<Integer> edgeOrder = new ArrayList<>();
        int currVertex = instance.source;
        while (currVertex != instance.target && toConsider > 0) {
            for (int i = 0, l = solution.size(); i < l; i++) {
                if (!consideredEdge[i]) {
                    if (solution.get(i).idV1 == currVertex) {
                        currVertex = solution.get(i).idV2;
                        consideredEdge[i] = true;
                        edgeOrder.add(i);
                        toConsider--;
                    }
                }
            }
        }

        for (int i = 0; i < consideredEdge.length; i++) {
            if (!consideredEdge[i]) {
                System.out.println("Somethig not considered while reording");
                //TO recompute sp on available edges (to avoid selection of extra edges)
                boolean[] arcAvailables = new boolean[instance.graphDirectet.nEdge];
                Arrays.fill(arcAvailables, false);
                for (int j = 0, l = solution.size(); j < l; j++) {
                    arcAvailables[solution.get(j).id] = true;
                }
                ArrayList<Edge> newSol = new ArrayList<Edge>();
                dijkstraUtils.updateArcAvailable(arcAvailables);
                dijkstraUtils.computeShortestPathWithForbiddenArc(instance.source);
                newSol = dijkstraUtils.getShortestPathTo(instance.target);
                dijkstraUtils.clearMemory();
//                System.out.println(solution);
//                System.out.println(newSol);
                return newSol;
            }
        }

        ArrayList<Edge> newSol = new ArrayList<Edge>();

        for (int i = 0, l = edgeOrder.size(); i < l; i++) {
            newSol.add(solution.get(edgeOrder.get(i)));
        }
        return newSol;
    }

    public boolean[] reduceGraphByCostMultiThread(ArrayList<Edge> solution, int bestFF) throws InterruptedException {
        double startT = System.currentTimeMillis();

        boolean[] availableVert = new boolean[instance.graphDirectet.nVertices];
        Arrays.fill(availableVert, true);
        boolean[] usedNodes = new boolean[instance.graphDirectet.nVertices];
        Arrays.fill(usedNodes, false);
        int[] spValueTroughNode = new int[instance.graphDirectet.nVertices];

        int nThread = Runtime.getRuntime().availableProcessors();

        ThreadReductPreprocess[] threadsPreprocess = new ThreadReductPreprocess[nThread];
        int splitN = solution.size() / nThread;
        int startID = 0;
        int endID = splitN;

        for (int i = 0; i < nThread - 1; i++) {
            threadsPreprocess[i] = new ThreadReductPreprocess(startID, endID, usedNodes, solution);
            threadsPreprocess[i].start();
            startID = endID;
            endID = startID + splitN;
        }
        endID = solution.size();
        threadsPreprocess[nThread - 1] = new ThreadReductPreprocess(startID, endID, usedNodes, solution);
        threadsPreprocess[nThread - 1].start();
        for (int i = 0; i < nThread; i++) {
            while (threadsPreprocess[i].isAlive()) {
                threadsPreprocess[i].join(100);
            }
        }

        ThreadReduct[] threads = new ThreadReduct[nThread];
        int[] removed = new int[nThread];
        Arrays.fill(removed, 0);
        splitN = instance.graphDirectet.nVertices / nThread;
        startID = 0;
        endID = splitN;

        this.dijkstraUtils.clearMemory();
        this.dijkstraUtils.computeShortestPath(instance.source);

        for (int i = 0; i < nThread - 1; i++) {
            threads[i] = new ThreadReduct(bestFF, startID, endID, dijkstraUtils, usedNodes, spValueTroughNode, null, false, removed, i);
            threads[i].start();
            startID = endID;
            endID = startID + splitN;
        }
        endID = instance.graphDirectet.nVertices;
        threads[nThread - 1] = new ThreadReduct(bestFF, startID, endID, dijkstraUtils, usedNodes, spValueTroughNode, null, false, removed, nThread - 1);
        threads[nThread - 1].start();

        for (int i = 0; i < nThread; i++) {
            while (threads[i].isAlive()) {
                threads[i].join(100);
            }
        }

        this.dijkstraUtils.clearMemory();
        this.dijkstraUtils.computeInverseShortestPath(instance.target);

        startID = 0;
        endID = splitN;
        for (int i = 0; i < nThread - 1; i++) {
            threads[i] = new ThreadReduct(bestFF, startID, endID, dijkstraUtils, usedNodes, spValueTroughNode, availableVert, true, removed, i);
            threads[i].start();
            startID = endID;
            endID = startID + splitN;
        }
        endID = instance.graphDirectet.nVertices;
        threads[nThread - 1] = new ThreadReduct(bestFF, startID, endID, dijkstraUtils, usedNodes, spValueTroughNode, availableVert, true, removed, nThread - 1);
        threads[nThread - 1].start();

        int totRemoved = 0;
        for (int i = 0; i < nThread; i++) {
            while (threads[i].isAlive()) {
                threads[i].join(100);
            }
            totRemoved += removed[i];
        }

        double endT = System.currentTimeMillis();

        int countRemoved = 0;
        int countRemaining = 0;
        for (int i = 0; i < spValueTroughNode.length; i++) {
            if (!availableVert[i]) {
                countRemoved++;
            } else {
                countRemaining++;
            }
        }
//        System.out.println("removed " + totRemoved);
//        System.out.println("removed " + countRemoved);
//        System.out.println("remaining " + countRemaining);
//        System.out.println(((countRemoved * 1.0) * 100.00) / (instance.graphDirectet.nVertices * 1.0));
//        System.out.println(((countRemoved * 1.0) * 100.00) / (instance.graphDirectet.nVertices * 1.0) + "\t" + (endT-startT)/1000.0);

        return availableVert;
    }

}
