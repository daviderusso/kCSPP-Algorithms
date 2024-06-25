import InstanceUtils.Instance;
import InstanceUtils.Edge;
import InstanceUtils.Utils;
//import Solver.ILP;
import Solver.Heuristic;
//import ilog.concert.IloException;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class Main {

    public static void mainGrid(String[] args) throws InterruptedException {
        String BASE_DIR = "/mnt/DATA/Ricerca/5 - Paper/3 - k-ColorShortestPath/Code/kColorShortestPath/Istanze/";

        Utils utils = new Utils();
        Instance instance;

        boolean big = true;
        for (int seed = 0; seed <= 9; seed++) { //0-9
            System.out.println(seed);
            //GRID----------------------------
            for (int gridSize = 0; gridSize <= 5; gridSize++) { //0-5
                for (int nC = 0; nC <= 1; nC++) {//0-1
                    if (big) {
                        instance = utils.readGridDirectedBig(BASE_DIR, gridSize, nC, seed);
                    } else {
                        instance = utils.readGridDirectedSmall(BASE_DIR, gridSize, nC, seed);
                    }
                    System.out.println(instance.name + "\t" + instance.graphDirectet.nRow + "\t" + instance.graphDirectet.nCol + "\t" + instance.graphDirectet.nVertices + "\t" +
                            instance.graphDirectet.nEdge + "\t" + instance.graphDirectet.nColor + "\t" + instance.kColor + "\t" + seed);


                    ArrayList<Edge> solution;
                    int eval[] = null;
                    double startT = 0;
                    double endT = 0;
                    boolean admiss = false;

                    //ILP
//                    Solver solver = new Solver(instance);
//                    ILP ilp = new ILP(instance, true, solver);
//                    startT = System.currentTimeMillis();

//                    SOLVE ORIGINAL FORMULATION AS IS
//                    solution = ilp.solveOriginalModel();

//                    SOLVE FORMULATION WITH HEURISTIC INIT AND GRAPH REDUCTION
//                    solution = ilp.solverGReduction();
//
//                    endT = System.currentTimeMillis();
//                    eval = solver.evaluateSolution(solution);
//                    admiss = solver.checkAdmissibility(solution, true);

                    //HEURISTIC
                    Heuristic heuristic = new Heuristic(instance);
                    startT = System.currentTimeMillis();

                    solution = heuristic.solveColorConstrainedDijkstraAlgorithm();

                    endT = System.currentTimeMillis();
                    eval = heuristic.evaluateSolution(solution);
                    admiss = heuristic.checkAdmissibility(solution, true);


                    System.out.println("Color: " + eval[0]);
                    System.out.println("FF: " + eval[1]);
                    System.out.println("Time: " + ((endT - startT) / 1000.0));
                    System.out.println("Admiss: " + admiss);
                    System.out.println("---------------------------------------------");
                }
            }
            System.out.println();
        }
    }

    public static void mainRandom(String[] args) throws InterruptedException {
        String BASE_DIR = "/mnt/DATA/Ricerca/5 - Paper/3 - k-ColorShortestPath/Code/kColorShortestPath/Istanze/";

        Utils utils = new Utils();
        Instance instance;

        boolean big = true;
        for (int seed = 0; seed <= 9; seed++) { //0-9
            System.out.println(seed);
            //RANDOM--------------------------
            for (int nN = 0; nN <= 2; nN++) { //0-2
                for (int nE = 0; nE <= 2; nE++) { //0-2
                    for (int nC = 0; nC <= 1; nC++) {//0-1
                        if (big) {
                            instance = utils.readRandomDirectedBig(BASE_DIR, nN, nE, nC, seed);
                        } else {
                            instance = utils.readRandomDirectedSmall(BASE_DIR, nN, nE, nC, seed);
                        }
                        System.out.println(instance.name + "\t" + instance.graphDirectet.nVertices + "\t" +
                                instance.graphDirectet.nEdge + "\t" + instance.graphDirectet.nColor + "\t" + instance.kColor + "\t" + seed);


                        ArrayList<Edge> solution;
                        int eval[] = null;
                        double startT = 0;
                        double endT = 0;
                        boolean admiss = false;

                        //ILP
//                    Solver solver = new Solver(instance);
//                    ILP ilp = new ILP(instance, true, solver);
//                    startT = System.currentTimeMillis();

                    //SOLVE ORIGINAL FORMULATION AS IS
//                    solution = ilp.solveOriginalModel();

                    //SOLVE FORMULATION WITH HEURISTIC INIT AND GRAPH REDUCTION
//                    solution = ilp.solverGReduction();
//
//                    endT = System.currentTimeMillis();
//                    eval = solver.evaluateSolution(solution);
//                    admiss = solver.checkAdmissibility(solution, true);

                        //HEURISTIC
                        Heuristic heuristic = new Heuristic(instance);
                        startT = System.currentTimeMillis();

                        solution = heuristic.solveColorConstrainedDijkstraAlgorithm();

                        endT = System.currentTimeMillis();
                        eval = heuristic.evaluateSolution(solution);
                        admiss = heuristic.checkAdmissibility(solution, true);


                        System.out.println("Color: " + eval[0]);
                        System.out.println("FF: " + eval[1]);
                        System.out.println("Time: " + ((endT - startT) / 1000.0));
                        System.out.println("Admiss: " + admiss);
                        System.out.println("---------------------------------------------");
                    }
                }
            }
            System.out.println();
        }
    }
}
