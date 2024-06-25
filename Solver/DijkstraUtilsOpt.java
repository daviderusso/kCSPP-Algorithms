package Solver;

import InstanceUtils.Edge;
import InstanceUtils.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * Dijkstra utils to compute the Dijkstra algorithm and get the shortest path between two nodes
 */

class VertexDJOpt implements Comparable<VertexDJOpt> {

    public int originalVertex;

    public int minDistance = Integer.MAX_VALUE;
    public int previous = -1;

    public VertexDJOpt(int n) {
        this.originalVertex = n;
    }

    @Override
    public String toString() {
        return this.originalVertex + "";
    }

    public int compareTo(VertexDJOpt other) {
        return Integer.compare(minDistance, other.minDistance);
    }
}

class EdgeDJOpt {

    public int originalArc;
    public final int from;
    public final int to;
    public final int weight;

    public EdgeDJOpt(int idOriArc, int from, int to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
        this.originalArc = idOriArc;
    }

    @Override
    public String toString() {
        return this.originalArc + "";
    }
}

public class DijkstraUtilsOpt {

    public Graph graph;

    private boolean[] usableNodes;
    private boolean[] usableArcs;
    private int[] penaltyWeightPerArc;
    private int[] penaltyWeightPerNode;

    public VertexDJOpt[] vList;
    public int vListSize;
    public EdgeDJOpt[] eList;
    public int eListSize;
    private Random random;

    private int penalization;

    public ArrayList[] forVertexColorUsage;

    public DijkstraUtilsOpt(Graph g) {
        graph = g;
        penalization = g.maxEdgeCost;
        random = new Random();
        vList = new VertexDJOpt[g.nVertices];
        usableNodes = new boolean[g.nVertices];
        penaltyWeightPerNode = new int[g.nVertices];
        for (int i = 0; i < g.nVertices; i++) {
            VertexDJOpt v = new VertexDJOpt(g.vertices.get(i).id);
            vList[i] = v;
            usableNodes[i] = true;
            penaltyWeightPerNode[i] = 0;
        }
        vListSize = g.nVertices;

        eList = new EdgeDJOpt[g.nEdge];
        usableArcs = new boolean[g.nEdge];
        penaltyWeightPerArc = new int[g.nEdge];
        for (int i = 0; i < g.nEdge; i++) {
            EdgeDJOpt e = new EdgeDJOpt(g.edges.get(i).id, g.edges.get(i).idV1, g.edges.get(i).idV2, g.edges.get(i).cost);
            eList[i] = e;
            usableArcs[i] = true;
            penaltyWeightPerArc[i] = 0;
        }
        eListSize = g.nEdge;
    }

    public void changeG(Graph g) {
        vList = new VertexDJOpt[g.nVertices];
        for (int i = 0; i < g.nVertices; i++) {
            VertexDJOpt v = new VertexDJOpt(g.vertices.get(i).id);
            vList[i] = v;
        }
        vListSize = g.nVertices;
        eList = new EdgeDJOpt[g.nEdge];
        for (int i = 0; i < g.nEdge; i++) {
            EdgeDJOpt e = new EdgeDJOpt(g.edges.get(i).id, g.edges.get(i).idV1, g.edges.get(i).idV2, g.edges.get(i).cost);
            eList[i] = e;
        }
        eListSize = g.nEdge;
    }

    public void clearMemory() {
        for (int i = 0; i < this.vListSize; i++) {
            this.vList[i].minDistance = Integer.MAX_VALUE;
            this.vList[i].previous = -1;
            usableNodes[i] = true;
            penaltyWeightPerNode[i] = 0;
        }
        for (int i = 0; i < this.eListSize; i++) {
            usableArcs[i] = true;
            penaltyWeightPerArc[i] = 0;
        }
    }

    // --------------------- STANDARD SHORTEST PATH

    public void computeShortestPath(int source) {
        PriorityQueue<VertexDJOpt> vertexQueue = new PriorityQueue<>();
        VertexDJOpt u;
        VertexDJOpt v;
        int weight = 0;
        int distanceThroughU = 0;
        vList[source].minDistance = 0;
        vertexQueue.add(vList[source]);
        while (!vertexQueue.isEmpty()) {
            u = vertexQueue.poll();
            for (int eID = 0, nAdj = graph.vertices.get(u.originalVertex).nOutEdge; eID < nAdj; eID++) {
                v = vList[graph.vertices.get(u.originalVertex).exitingEdges.get(eID).idV2];
                if (v != null) {
                    weight = graph.vertices.get(u.originalVertex).exitingEdges.get(eID).cost;
                    distanceThroughU = u.minDistance + weight;
                    if (distanceThroughU < v.minDistance) {
                        vertexQueue.remove(v);
                        v.minDistance = distanceThroughU;
                        v.previous = u.originalVertex;
                        vertexQueue.add(v);
                    }
                    else if (distanceThroughU == v.minDistance) {
                        if (random.nextDouble() <= 0.5) {
                            vertexQueue.remove(v);
                            v.minDistance = distanceThroughU;
                            v.previous = u.originalVertex;
                            vertexQueue.add(v);
                        }
                    }
                }
            }
        }
    }

    public void computeShortestPathWithArcPenalty(int source) {
        PriorityQueue<VertexDJOpt> vertexQueue = new PriorityQueue<>();
        VertexDJOpt u;
        VertexDJOpt v;
        int weight = 0;
        int distanceThroughU = 0;
        vList[source].minDistance = 0;
        vertexQueue.add(vList[source]);
        while (!vertexQueue.isEmpty()) {
            u = vertexQueue.poll();
            for (int eID = 0, nAdj = graph.vertices.get(u.originalVertex).nOutEdge; eID < nAdj; eID++) {
                v = vList[graph.vertices.get(u.originalVertex).exitingEdges.get(eID).idV2];
                if (v != null) {
                    weight = graph.vertices.get(u.originalVertex).exitingEdges.get(eID).cost + penaltyWeightPerArc[graph.vertices.get(u.originalVertex).exitingEdges.get(eID).id];
                    distanceThroughU = u.minDistance + weight;
                    if (distanceThroughU < v.minDistance) {
                        vertexQueue.remove(v);
                        v.minDistance = distanceThroughU;
                        v.previous = u.originalVertex;
                        vertexQueue.add(v);
                    } else if (distanceThroughU == v.minDistance) {
                        if (random.nextDouble() <= 0.5) {
                            vertexQueue.remove(v);
                            v.minDistance = distanceThroughU;
                            v.previous = u.originalVertex;
                            vertexQueue.add(v);
                        }
                    }
                }
            }
        }
    }

    public void computeShortestPathWithForbiddenArc(int source) {
        PriorityQueue<VertexDJOpt> vertexQueue = new PriorityQueue<>();
        VertexDJOpt u;
        VertexDJOpt v;
        int weight = 0;
        int distanceThroughU = 0;
        vList[source].minDistance = 0;
        vertexQueue.add(vList[source]);
        while (!vertexQueue.isEmpty()) {
            u = vertexQueue.poll();
            for (int eID = 0, nAdj = graph.vertices.get(u.originalVertex).nOutEdge; eID < nAdj; eID++) {
                if (usableArcs[graph.vertices.get(u.originalVertex).exitingEdges.get(eID).id]) {
                    v = vList[graph.vertices.get(u.originalVertex).exitingEdges.get(eID).idV2];
                    if (v != null) {
                        weight = graph.vertices.get(u.originalVertex).exitingEdges.get(eID).cost;
                        distanceThroughU = u.minDistance + weight;
                        if (distanceThroughU < v.minDistance) {
                            vertexQueue.remove(v);
                            v.minDistance = distanceThroughU;
                            v.previous = u.originalVertex;
                            vertexQueue.add(v);
                        } else if (distanceThroughU == v.minDistance) {
                            if (random.nextDouble() <= 0.5) {
                                vertexQueue.remove(v);
                                v.minDistance = distanceThroughU;
                                v.previous = u.originalVertex;
                                vertexQueue.add(v);
                            }
                        }
                    }
                }
            }
        }
    }

    public void computeShortestPathWithForbiddenNode(int source) {
        PriorityQueue<VertexDJOpt> vertexQueue = new PriorityQueue<>();
        VertexDJOpt u;
        VertexDJOpt v;
        int weight = 0;
        int distanceThroughU = 0;
        vList[source].minDistance = 0;
        vertexQueue.add(vList[source]);
        while (!vertexQueue.isEmpty()) {
            u = vertexQueue.poll();
            for (int eID = 0, nAdj = graph.vertices.get(u.originalVertex).nOutEdge; eID < nAdj; eID++) {
                v = null;
                if (usableNodes[graph.vertices.get(u.originalVertex).exitingEdges.get(eID).idV2]) {
                    v = vList[graph.vertices.get(u.originalVertex).exitingEdges.get(eID).idV2];
                }
                if (v != null) {
                    weight = graph.vertices.get(u.originalVertex).exitingEdges.get(eID).cost;
                    distanceThroughU = u.minDistance + weight;
                    if (distanceThroughU < v.minDistance) {
                        vertexQueue.remove(v);
                        v.minDistance = distanceThroughU;
                        v.previous = u.originalVertex;
                        vertexQueue.add(v);
                    } else if (distanceThroughU == v.minDistance) {
                        if (random.nextDouble() <= 0.5) {
                            vertexQueue.remove(v);
                            v.minDistance = distanceThroughU;
                            v.previous = u.originalVertex;
                            vertexQueue.add(v);
                        }
                    }
                }
            }
        }
    }

    public void computeShortestPathWithForbiddenNodeAndArc(int source) {
        PriorityQueue<VertexDJOpt> vertexQueue = new PriorityQueue<>();
        VertexDJOpt u;
        VertexDJOpt v;
        int weight = 0;
        int distanceThroughU = 0;
        vList[source].minDistance = 0;
        vertexQueue.add(vList[source]);
        while (!vertexQueue.isEmpty()) {
            u = vertexQueue.poll();
            for (int eID = 0, nAdj = graph.vertices.get(u.originalVertex).nOutEdge; eID < nAdj; eID++) {
                if (usableArcs[graph.vertices.get(u.originalVertex).exitingEdges.get(eID).id]) {
                    v = null;
                    if (usableNodes[graph.vertices.get(u.originalVertex).exitingEdges.get(eID).idV2]) {
                        v = vList[graph.vertices.get(u.originalVertex).exitingEdges.get(eID).idV2];
                    }
                    if (v != null) {
                        weight = graph.vertices.get(u.originalVertex).exitingEdges.get(eID).cost;
                        distanceThroughU = u.minDistance + weight;
                        if (distanceThroughU < v.minDistance) {
                            vertexQueue.remove(v);
                            v.minDistance = distanceThroughU;
                            v.previous = u.originalVertex;
                            vertexQueue.add(v);
                        } else if (distanceThroughU == v.minDistance) {
                            if (random.nextDouble() <= 0.5) {
                                vertexQueue.remove(v);
                                v.minDistance = distanceThroughU;
                                v.previous = u.originalVertex;
                                vertexQueue.add(v);
                            }
                        }
                    }
                }
            }
        }
    }

    // --------------------- INVERSE SHORTEST PATH

    public void computeInverseShortestPath(int source) {
        PriorityQueue<VertexDJOpt> vertexQueue = new PriorityQueue<>();
        VertexDJOpt u;
        VertexDJOpt v;
        int weight = 0;
        int distanceThroughU = 0;
        vList[source].minDistance = 0;
        vertexQueue.add(vList[source]);
        while (!vertexQueue.isEmpty()) {
            u = vertexQueue.poll();
            for (int eID = 0, nAdj = graph.vertices.get(u.originalVertex).nInEdge; eID < nAdj; eID++) {
                v = vList[graph.vertices.get(u.originalVertex).enteringEdges.get(eID).idV1];
                if (v != null) {
                    weight = graph.vertices.get(u.originalVertex).enteringEdges.get(eID).cost;
                    distanceThroughU = u.minDistance + weight;
                    if (distanceThroughU < v.minDistance) {
                        vertexQueue.remove(v);
                        v.minDistance = distanceThroughU;
                        v.previous = u.originalVertex;
                        vertexQueue.add(v);
                    }
                    else if (distanceThroughU == v.minDistance) {
                        if (random.nextDouble() <= 0.5) {
                            vertexQueue.remove(v);
                            v.minDistance = distanceThroughU;
                            v.previous = u.originalVertex;
                            vertexQueue.add(v);
                        }
                    }
                }
            }
        }
    }

    public void computeInverseShortestPathWithArcPenalty(int source) {
        PriorityQueue<VertexDJOpt> vertexQueue = new PriorityQueue<>();
        VertexDJOpt u;
        VertexDJOpt v;
        int weight = 0;
        int distanceThroughU = 0;
        vList[source].minDistance = 0;
        vertexQueue.add(vList[source]);
        while (!vertexQueue.isEmpty()) {
            u = vertexQueue.poll();
            for (int eID = 0, nAdj = graph.vertices.get(u.originalVertex).nInEdge; eID < nAdj; eID++) {
                v = vList[graph.vertices.get(u.originalVertex).enteringEdges.get(eID).idV1];
                if (v != null) {
                    weight = graph.vertices.get(u.originalVertex).enteringEdges.get(eID).cost + penaltyWeightPerArc[graph.vertices.get(u.originalVertex).enteringEdges.get(eID).id];
                    distanceThroughU = u.minDistance + weight;
                    if (distanceThroughU < v.minDistance) {
                        vertexQueue.remove(v);
                        v.minDistance = distanceThroughU;
                        v.previous = u.originalVertex;
                        vertexQueue.add(v);
                    } else if (distanceThroughU == v.minDistance) {
                        if (random.nextDouble() <= 0.5) {
                            vertexQueue.remove(v);
                            v.minDistance = distanceThroughU;
                            v.previous = u.originalVertex;
                            vertexQueue.add(v);
                        }
                    }
                }
            }
        }
    }

    public void computeInverseShortestPathWithForbiddenArc(int source) {
        PriorityQueue<VertexDJOpt> vertexQueue = new PriorityQueue<>();
        VertexDJOpt u;
        VertexDJOpt v;
        int weight = 0;
        int distanceThroughU = 0;
        vList[source].minDistance = 0;
        vertexQueue.add(vList[source]);
        while (!vertexQueue.isEmpty()) {
            u = vertexQueue.poll();
            for (int eID = 0, nAdj = graph.vertices.get(u.originalVertex).nInEdge; eID < nAdj; eID++) {
                if (usableArcs[graph.vertices.get(u.originalVertex).enteringEdges.get(eID).id]) {
                    v = vList[graph.vertices.get(u.originalVertex).enteringEdges.get(eID).idV1];
                    if (v != null) {
                        weight = graph.vertices.get(u.originalVertex).enteringEdges.get(eID).cost;
                        distanceThroughU = u.minDistance + weight;
                        if (distanceThroughU < v.minDistance) {
                            vertexQueue.remove(v);
                            v.minDistance = distanceThroughU;
                            v.previous = u.originalVertex;
                            vertexQueue.add(v);
                        } else if (distanceThroughU == v.minDistance) {
                            if (random.nextDouble() <= 0.5) {
                                vertexQueue.remove(v);
                                v.minDistance = distanceThroughU;
                                v.previous = u.originalVertex;
                                vertexQueue.add(v);
                            }
                        }
                    }
                }
            }
        }
    }

    public void computeInverseShortestPathWithForbiddenNode(int source) {
        PriorityQueue<VertexDJOpt> vertexQueue = new PriorityQueue<>();
        VertexDJOpt u;
        VertexDJOpt v;
        int weight = 0;
        int distanceThroughU = 0;
        vList[source].minDistance = 0;
        vertexQueue.add(vList[source]);
        while (!vertexQueue.isEmpty()) {
            u = vertexQueue.poll();
            for (int eID = 0, nAdj = graph.vertices.get(u.originalVertex).nInEdge; eID < nAdj; eID++) {
                v = null;
                if (usableNodes[graph.vertices.get(u.originalVertex).enteringEdges.get(eID).idV1]) {
                    v = vList[graph.vertices.get(u.originalVertex).enteringEdges.get(eID).idV1];
                }
                if (v != null) {
                    weight = graph.vertices.get(u.originalVertex).enteringEdges.get(eID).cost;
                    distanceThroughU = u.minDistance + weight;
                    if (distanceThroughU < v.minDistance) {
                        vertexQueue.remove(v);
                        v.minDistance = distanceThroughU;
                        v.previous = u.originalVertex;
                        vertexQueue.add(v);
                    } else if (distanceThroughU == v.minDistance) {
                        if (random.nextDouble() <= 0.5) {
                            vertexQueue.remove(v);
                            v.minDistance = distanceThroughU;
                            v.previous = u.originalVertex;
                            vertexQueue.add(v);
                        }
                    }
                }
            }
        }
    }

    public void computeInverseShortestPathWithForbiddenNodeAndArc(int source) {
        PriorityQueue<VertexDJOpt> vertexQueue = new PriorityQueue<>();
        VertexDJOpt u;
        VertexDJOpt v;
        int weight = 0;
        int distanceThroughU = 0;
        vList[source].minDistance = 0;
        vertexQueue.add(vList[source]);
        while (!vertexQueue.isEmpty()) {
            u = vertexQueue.poll();
            for (int eID = 0, nAdj = graph.vertices.get(u.originalVertex).nInEdge; eID < nAdj; eID++) {
                if (usableArcs[graph.vertices.get(u.originalVertex).enteringEdges.get(eID).id]) {
                    v = null;
                    if (usableNodes[graph.vertices.get(u.originalVertex).enteringEdges.get(eID).idV1]) {
                        v = vList[graph.vertices.get(u.originalVertex).enteringEdges.get(eID).idV1];
                    }
                    if (v != null) {
                        weight = graph.vertices.get(u.originalVertex).enteringEdges.get(eID).cost;
                        distanceThroughU = u.minDistance + weight;
                        if (distanceThroughU < v.minDistance) {
                            vertexQueue.remove(v);
                            v.minDistance = distanceThroughU;
                            v.previous = u.originalVertex;
                            vertexQueue.add(v);
                        } else if (distanceThroughU == v.minDistance) {
                            if (random.nextDouble() <= 0.5) {
                                vertexQueue.remove(v);
                                v.minDistance = distanceThroughU;
                                v.previous = u.originalVertex;
                                vertexQueue.add(v);
                            }
                        }
                    }
                }
            }
        }
    }

    // --------------------- COLORED SHORTEST PATH

    public void computeColouredShortestPath(int source) {
        PriorityQueue<VertexDJOpt> vertexQueue = new PriorityQueue<>();
        VertexDJOpt u;
        VertexDJOpt v;
        int weight = 0;
        int distanceThroughU = 0;
        vList[source].minDistance = 0;
        vertexQueue.add(vList[source]);

        while (!vertexQueue.isEmpty()) {
            u = vertexQueue.poll();
            for (int eID = 0, nAdj = graph.vertices.get(u.originalVertex).nOutEdge; eID < nAdj; eID++) {
                v = vList[graph.vertices.get(u.originalVertex).exitingEdges.get(eID).idV2];
                if (v != null) {
                    weight = graph.vertices.get(u.originalVertex).exitingEdges.get(eID).cost;
                    distanceThroughU = u.minDistance + weight;
                    if (distanceThroughU < v.minDistance) {
                        if (!forVertexColorUsage[u.originalVertex].contains(graph.edges.get(graph.vertices.get(u.originalVertex).exitingEdges.get(eID).id).color)) {
                            distanceThroughU += this.penalization;
                            if (distanceThroughU < v.minDistance) {
                                vertexQueue.remove(v);
                                v.minDistance = distanceThroughU;
                                v.previous = u.originalVertex;
                                forVertexColorUsage[v.originalVertex].clear();
                                forVertexColorUsage[v.originalVertex].addAll(forVertexColorUsage[u.originalVertex]);
                                forVertexColorUsage[v.originalVertex].add(graph.edges.get(graph.vertices.get(u.originalVertex).exitingEdges.get(eID).id).color);
                                vertexQueue.add(v);
                            }
                        } else {
                            vertexQueue.remove(v);
                            v.minDistance = distanceThroughU;
                            v.previous = u.originalVertex;
                            forVertexColorUsage[v.originalVertex].clear();
                            forVertexColorUsage[v.originalVertex].addAll(forVertexColorUsage[u.originalVertex]);
                            vertexQueue.add(v);
                        }
                    }
                }
            }
        }
    }

    public void computeColouredShortestPathWithForbiddenNode(int source) {
        PriorityQueue<VertexDJOpt> vertexQueue = new PriorityQueue<>();
        VertexDJOpt u;
        VertexDJOpt v;
        int weight = 0;
        int distanceThroughU = 0;
        vList[source].minDistance = 0;
        vertexQueue.add(vList[source]);

        while (!vertexQueue.isEmpty()) {
            u = vertexQueue.poll();
            for (int eID = 0, nAdj = graph.vertices.get(u.originalVertex).nOutEdge; eID < nAdj; eID++) {
                v = null;
                if (usableNodes[graph.vertices.get(u.originalVertex).enteringEdges.get(eID).idV2]) {
                    v = vList[graph.vertices.get(u.originalVertex).exitingEdges.get(eID).idV2];
                }
                if (v != null) {
                    weight = graph.vertices.get(u.originalVertex).exitingEdges.get(eID).cost;
                    distanceThroughU = u.minDistance + weight;
                    if (distanceThroughU < v.minDistance) {
                        if (!forVertexColorUsage[u.originalVertex].contains(graph.edges.get(graph.vertices.get(u.originalVertex).exitingEdges.get(eID).id).color)) {
                            distanceThroughU += this.penalization;
                            if (distanceThroughU < v.minDistance) {
                                vertexQueue.remove(v);
                                v.minDistance = distanceThroughU;
                                v.previous = u.originalVertex;
                                forVertexColorUsage[v.originalVertex].clear();
                                forVertexColorUsage[v.originalVertex].addAll(forVertexColorUsage[u.originalVertex]);
                                forVertexColorUsage[v.originalVertex].add(graph.edges.get(graph.vertices.get(u.originalVertex).exitingEdges.get(eID).id).color);
                                vertexQueue.add(v);
                            }
                        } else if (distanceThroughU < v.minDistance) {
                            vertexQueue.remove(v);
                            v.minDistance = distanceThroughU;
                            v.previous = u.originalVertex;
                            forVertexColorUsage[v.originalVertex].clear();
                            forVertexColorUsage[v.originalVertex].addAll(forVertexColorUsage[u.originalVertex]);
                            vertexQueue.add(v);
                        }
                    }
                }
            }
        }
    }

    public void computeShortestPathColorCost1(int source) {
        PriorityQueue<VertexDJOpt> vertexQueue = new PriorityQueue<>();
        VertexDJOpt u;
        VertexDJOpt v;
        int weight = 0;
        vList[source].minDistance = 0;
        vertexQueue.add(vList[source]);

        while (!vertexQueue.isEmpty()) {
            u = vertexQueue.poll();
            for (int eID = 0, nAdj = graph.vertices.get(u.originalVertex).nOutEdge; eID < nAdj; eID++) {
                v = vList[graph.vertices.get(u.originalVertex).exitingEdges.get(eID).idV2];
                if (v != null) {
                    weight = u.minDistance;
                    if (!forVertexColorUsage[u.originalVertex].contains(graph.vertices.get(u.originalVertex).exitingEdges.get(eID).color)) {
                        weight++;
                        if (weight < v.minDistance) {
                            vertexQueue.remove(v);
                            v.minDistance = weight;
                            v.previous = u.originalVertex;
                            forVertexColorUsage[v.originalVertex].clear();
                            forVertexColorUsage[v.originalVertex].addAll(forVertexColorUsage[u.originalVertex]);
                            forVertexColorUsage[v.originalVertex].add(graph.vertices.get(u.originalVertex).exitingEdges.get(eID).color);
                            vertexQueue.add(v);
                        }
                    } else if (weight < v.minDistance) {
                        vertexQueue.remove(v);
                        v.minDistance = weight;
                        v.previous = u.originalVertex;
                        forVertexColorUsage[v.originalVertex].clear();
                        forVertexColorUsage[v.originalVertex].addAll(forVertexColorUsage[u.originalVertex]);
                        vertexQueue.add(v);
                    }
                }
            }
        }
    }

    public void computeShortestPathColorCost1WithForbiddenNode(int source) {
        PriorityQueue<VertexDJOpt> vertexQueue = new PriorityQueue<>();
        VertexDJOpt u;
        VertexDJOpt v;
        int weight = 0;
        vList[source].minDistance = 0;
        vertexQueue.add(vList[source]);

        while (!vertexQueue.isEmpty()) {
            u = vertexQueue.poll();
            for (int eID = 0, nAdj = graph.vertices.get(u.originalVertex).nOutEdge; eID < nAdj; eID++) {
                v = null;
                if (usableNodes[graph.vertices.get(u.originalVertex).enteringEdges.get(eID).idV2]) {
                    v = vList[graph.vertices.get(u.originalVertex).exitingEdges.get(eID).idV2];
                }
                if (v != null) {
                    weight = u.minDistance;
                    if (!forVertexColorUsage[u.originalVertex].contains(graph.vertices.get(u.originalVertex).exitingEdges.get(eID).color)) {
                        weight++;
                        if (weight < v.minDistance) {
                            vertexQueue.remove(v);
                            v.minDistance = weight;
                            v.previous = u.originalVertex;
                            forVertexColorUsage[v.originalVertex].clear();
                            forVertexColorUsage[v.originalVertex].addAll(forVertexColorUsage[u.originalVertex]);
                            forVertexColorUsage[v.originalVertex].add(graph.vertices.get(u.originalVertex).exitingEdges.get(eID).color);
                            vertexQueue.add(v);
                        }
                    } else if (weight < v.minDistance) {
                        vertexQueue.remove(v);
                        v.minDistance = weight;
                        v.previous = u.originalVertex;
                        forVertexColorUsage[v.originalVertex].clear();
                        forVertexColorUsage[v.originalVertex].addAll(forVertexColorUsage[u.originalVertex]);
                        vertexQueue.add(v);
                    }
                }
            }
        }
    }

    public void computeInverseShortestPathColorCost1(int source) {
        PriorityQueue<VertexDJOpt> vertexQueue = new PriorityQueue<>();
        VertexDJOpt u;
        VertexDJOpt v;
        int weight = 0;
        vList[source].minDistance = 0;
        vertexQueue.add(vList[source]);

        while (!vertexQueue.isEmpty()) {
            u = vertexQueue.poll();
            for (int eID = 0, nAdj = graph.vertices.get(u.originalVertex).nInEdge; eID < nAdj; eID++) {
                v = vList[graph.vertices.get(u.originalVertex).enteringEdges.get(eID).idV1];
                if (v != null) {
                    weight = u.minDistance;
                    if (!forVertexColorUsage[u.originalVertex].contains(graph.vertices.get(u.originalVertex).enteringEdges.get(eID).color)) {
                        weight++;
                        if (weight < v.minDistance) {
                            vertexQueue.remove(v);
                            v.minDistance = weight;
                            v.previous = u.originalVertex;
                            forVertexColorUsage[v.originalVertex].clear();
                            forVertexColorUsage[v.originalVertex].addAll(forVertexColorUsage[u.originalVertex]);
                            forVertexColorUsage[v.originalVertex].add(graph.vertices.get(u.originalVertex).enteringEdges.get(eID).color);
                            vertexQueue.add(v);
                        }
                    } else if (weight < v.minDistance) {
                        vertexQueue.remove(v);
                        v.minDistance = weight;
                        v.previous = u.originalVertex;
                        forVertexColorUsage[v.originalVertex].clear();
                        forVertexColorUsage[v.originalVertex].addAll(forVertexColorUsage[u.originalVertex]);
                        vertexQueue.add(v);
                    }
                }
            }
        }
    }

    public void colorUsageInit() {
        forVertexColorUsage = new ArrayList[graph.nVertices];
        for (int i = 0; i < graph.nVertices; i++) {
            forVertexColorUsage[i] = new ArrayList();
        }
    }

    public void colorUsageReset() {
        for (int i = 0; i < graph.nVertices; i++) {
            forVertexColorUsage[i].clear();
        }
    }

    public void updateColorPenalty(int penalization) {
        this.penalization = penalization;
    }

    // --------------------- GET SHORTEST PATH

    public ArrayList<Edge> getShortestPathTo(int nodeID) {
        ArrayList<Edge> shortestPath = new ArrayList<>();

        int currID = nodeID;
        ArrayList<VertexDJOpt> sp = new ArrayList<VertexDJOpt>();
        sp.add(vList[nodeID]);
        while (vList[currID].previous != -1) {
            currID = vList[currID].previous;
            sp.add(vList[currID]);
        }
        Collections.reverse(sp);

        for (int i = 0, len = sp.size() - 1; i < len; i++) {
            shortestPath.add(getArchGivenVertices(sp.get(i), sp.get(i + 1)));
        }
        return shortestPath;
    }

    public ArrayList<Edge> getInverseShortestPathTo(int nodeID) {
        ArrayList<Edge> shortestPath = new ArrayList<>();

        int currID = nodeID;
        ArrayList<VertexDJOpt> sp = new ArrayList<VertexDJOpt>();
        sp.add(vList[nodeID]);
        while (vList[currID].previous != -1) {
            currID = vList[currID].previous;
            sp.add(vList[currID]);
        }

        for (int i = 0, len = sp.size() - 1; i < len; i++) {
            shortestPath.add(getArchGivenVertices(sp.get(i), sp.get(i + 1)));
        }
        return shortestPath;
    }

    public double getLengthShortestPathTo(int nodeID) {
        double spLength = 0.0;

        int currID = nodeID;
        ArrayList<VertexDJOpt> sp = new ArrayList<VertexDJOpt>();
        sp.add(vList[nodeID]);
        while (vList[currID].previous != -1) {
            currID = vList[currID].previous;
            sp.add(vList[currID]);
        }
        Collections.reverse(sp);

        for (int i = 0, len = sp.size() - 1; i < len; i++) {
            spLength += getArchGivenVertices(sp.get(i), sp.get(i + 1)).cost;
        }
        return spLength;
    }

    public double getLengthInverseShortestPathTo(int nodeID) {
        double spLength = 0.0;

        int currID = nodeID;
        ArrayList<VertexDJOpt> sp = new ArrayList<VertexDJOpt>();
        sp.add(vList[nodeID]);
        while (vList[currID].previous != -1) {
            currID = vList[currID].previous;
            sp.add(vList[currID]);
        }

        for (int i = 0, len = sp.size() - 1; i < len; i++) {
            spLength += getArchGivenVertices(sp.get(i), sp.get(i + 1)).cost;
        }
        return spLength;
    }

    public ArrayList<Integer> getColorsShortestPathTo(int nodeID) {
        ArrayList<Integer> colors = new ArrayList<>();

        int currID = nodeID;
        ArrayList<VertexDJOpt> sp = new ArrayList<VertexDJOpt>();
        sp.add(vList[nodeID]);
        while (vList[currID].previous != -1) {
            currID = vList[currID].previous;
            sp.add(vList[currID]);
        }
        Collections.reverse(sp);

        for (int i = 0, len = sp.size() - 1; i < len; i++) {
            colors.add(getArchGivenVertices(sp.get(i), sp.get(i + 1)).color);
        }
        return colors;
    }

    public ArrayList<Integer> getColorsInverseShortestPathTo(int nodeID) {
        ArrayList<Integer> colors = new ArrayList<>();

        int currID = nodeID;
        ArrayList<VertexDJOpt> sp = new ArrayList<VertexDJOpt>();
        sp.add(vList[nodeID]);
        while (vList[currID].previous != -1) {
            currID = vList[currID].previous;
            sp.add(vList[currID]);
        }

        for (int i = 0, len = sp.size() - 1; i < len; i++) {
            colors.add(getArchGivenVertices(sp.get(i), sp.get(i + 1)).color);
        }
        return colors;
    }

    private VertexDJOpt getVertexGivenNode(int nID) {
        for (int i = 0; i < vListSize; i++) {
            if (nID == vList[i].originalVertex) {
                return vList[i];
            }
        }
        return null;
    }

    private Edge getArchGivenVertices(VertexDJOpt v1, VertexDJOpt v2) {
        for (int i = 0, l = graph.vertices.get(v1.originalVertex).nOutEdge; i < l; i++) {
            if (graph.vertices.get(v1.originalVertex).exitingEdges.get(i).idV2 == v2.originalVertex) {
                return graph.vertices.get(v1.originalVertex).exitingEdges.get(i);
            }
        }
        return null;
    }

    // --------------------- UPDATE PENALTIES AND PROHIBITION

    public void updateAvailable(boolean[] availableNodes, boolean[] availableArcs) {
        this.usableArcs = availableArcs;
        this.usableNodes = availableNodes;
    }

    public void updateArcAvailable(boolean[] availableArcs) {
        this.usableArcs = availableArcs;
    }

    public void updateNodeArcPenalty(int[] arcPenalty, int[] nodePenalty) {
        this.penaltyWeightPerArc = arcPenalty;
        this.penaltyWeightPerNode = nodePenalty;
    }

    public void updateArcPenalty(int[] arcPenalty) {
        this.penaltyWeightPerArc = arcPenalty;
    }

    public void updateAvailableNodes(boolean[] availableNodes) {
        this.usableNodes = availableNodes;
    }
}
