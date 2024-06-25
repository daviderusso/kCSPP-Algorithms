package Solver;

import InstanceUtils.Instance;
import InstanceUtils.Edge;

import java.util.ArrayList;

public class Heuristic extends Solver {

    int[] penalties;

    public Heuristic(Instance instance) {
        super(instance);
        this.penalties = new int[9];
        this.penalties[0] = 0;
        this.penalties[1] = this.instance.graphDirectet.minEdgeCost / 4;
        this.penalties[2] = this.instance.graphDirectet.minEdgeCost / 2;
        this.penalties[3] = this.instance.graphDirectet.minEdgeCost;
        this.penalties[4] = this.instance.graphDirectet.minEdgeCost * 2;
        this.penalties[5] = this.instance.graphDirectet.meanEdgeCost / 4;
        this.penalties[6] = this.instance.graphDirectet.meanEdgeCost / 2;
        this.penalties[7] = this.instance.graphDirectet.meanEdgeCost;
        this.penalties[8] = this.instance.graphDirectet.maxEdgeCost;
    }

    public ArrayList<Edge> solveColorConstrainedDijkstraAlgorithm() {
        ArrayList<Edge> solution = null;

        boolean foundSol = false;

        this.dijkstraUtils.colorUsageInit();
        for (int i = 0; i < penalties.length - 1; i++) {
            this.dijkstraUtils.updateColorPenalty(penalties[i]);
            this.dijkstraUtils.computeColouredShortestPath(instance.source);
            solution = this.dijkstraUtils.getShortestPathTo(instance.target);
            if (this.checkAdmissibility(solution, false)) {
                foundSol = true;
                break;
            }
            this.dijkstraUtils.clearMemory();
            this.dijkstraUtils.colorUsageReset();
        }

        if (!foundSol) {
            //compute first an admissible solution
            this.dijkstraUtils.colorUsageReset();
            this.dijkstraUtils.clearMemory();
            this.dijkstraUtils.computeShortestPathColorCost1(instance.source);
            solution = this.dijkstraUtils.getShortestPathTo(instance.target);
        }

        boolean[] colors = new boolean[this.instance.graphDirectet.nColor];
        for (int i = 0, l = solution.size(); i < l; i++) {
            colors[solution.get(i).color] = true;
        }
        this.dijkstraUtils.clearMemory();
        boolean[] availableArcs = new boolean[instance.graphDirectet.nEdge];
        for (int i = 0, l = instance.graphDirectet.nEdge; i < l; i++) {
            if (colors[instance.graphDirectet.edges.get(i).color]) {
                availableArcs[i] = true;
            }
        }
        this.dijkstraUtils.updateArcAvailable(availableArcs);
        this.dijkstraUtils.computeShortestPathWithForbiddenArc(instance.source);
        ArrayList<Edge> newSol = this.dijkstraUtils.getShortestPathTo(instance.target);

        int[] evalOld = this.evaluateSolution(solution);
        int[] evalNew = this.evaluateSolution(newSol);
        if (evalNew[1] < evalOld[1]) {
//            System.out.println("New:" + evalNew[1] + " - " + evalNew[0]);
            return newSol;
        } else {
//            System.out.println("Old:" + evalOld[1] + " - " + evalOld[0]);
            return solution;
        }
    }

}
