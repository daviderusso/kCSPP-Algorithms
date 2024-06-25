package Solver;

public class ThreadReduct extends Thread {

    int idStart;
    int idEnd;
    boolean[] usedNodes;
    int bestFF;
    DijkstraUtilsOpt du;
    int[] spValueTroughNode;
    boolean[] availableVert;
    boolean inverse;
    int[] removed;
    int id;

    public ThreadReduct(int bestFF, int idStart, int idEnd, DijkstraUtilsOpt dijkstraUtilsOpt, boolean[] usedNodes, int[] spValueTroughNode, boolean[] availableVert, boolean inverse, int[] removed, int id) {
        this.idStart = idStart;
        this.idEnd = idEnd;
        this.bestFF = bestFF;
        this.du = dijkstraUtilsOpt;
        this.usedNodes = usedNodes;
        this.spValueTroughNode = spValueTroughNode;
        this.availableVert = availableVert;
        this.inverse = inverse;
        this.removed = removed;
        this.id = id;
    }

    @Override
    public void run() {
        int calc;
        if (!inverse) {
            for (int i = idStart; i < idEnd; i++) {
                if (!usedNodes[i]) {
                    calc = (int) du.getLengthShortestPathTo(i);
                    spValueTroughNode[i] = calc;
                } else {
                    spValueTroughNode[i] = bestFF;
                }
            }
        } else {
            int remov = 0;
            for (int i = idStart; i < idEnd; i++) {
                if (!usedNodes[i]) {
                    calc = spValueTroughNode[i] + (int) du.getLengthInverseShortestPathTo(i);
                    if (calc > bestFF) {
                        availableVert[i] = false;
                        remov++;
                    }
                }
            }
            removed[id] = remov;
        }
    }

}
