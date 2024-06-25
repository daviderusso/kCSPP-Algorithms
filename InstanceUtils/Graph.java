package InstanceUtils;

import java.util.ArrayList;

public class Graph {
    public int nRow;
    public int nCol;

    public int nVertices;
    public ArrayList<Vertex> vertices;
    public int nEdge;
    public ArrayList<Edge> edges;
    public int nColor;

    public int sumOfAllEdgeCost;
    public int meanEdgeCost;
    public int maxEdgeCost;
    public int minEdgeCost;

    public int[] colorOccurences;
    public ArrayList<Edge>[] edgePerColor;

    public Graph() {
        this.vertices = new ArrayList<Vertex>();
        this.edges = new ArrayList<Edge>();
        this.nCol = 0;
        this.nRow = 0;
    }

    public void update() {
        for (int i = 0; i < nVertices; i++) {
            vertices.get(i).nOutEdge = vertices.get(i).exitingEdges.size();
            vertices.get(i).nInEdge = vertices.get(i).enteringEdges.size();
        }

        colorOccurences = new int[nColor];
        edgePerColor = new ArrayList[nColor];
        for (int i = 0; i < nEdge; i++) {
            colorOccurences[edges.get(i).color]++;
            if (edgePerColor[edges.get(i).color] == null) {
                edgePerColor[edges.get(i).color] = new ArrayList<Edge>();
            }
            edgePerColor[edges.get(i).color].add(edges.get(i));
        }

        for (int i = 0; i < edgePerColor.length; i++) {
            if (edgePerColor[i] == null) {
                edgePerColor[i] = new ArrayList<Edge>();
            }
        }
    }

    public Edge getEdge(Vertex from, Vertex to) {
        for (int i = 0; i < this.nEdge; i++) {
            if ((this.edges.get(i).idV1 == from.id) && (this.edges.get(i).idV2 == to.id)) {
                return this.edges.get(i);
            }
        }
        return null;
    }

    public void print() {
        System.out.println();
        System.out.println(this.nVertices + " Nodes: ");
        for (int i = 0; i < this.nVertices; i++) {
            System.out.println(this.vertices.get(i));
        }
        System.out.println();
        System.out.println(this.nEdge + " Arcs: ");
        for (int i = 0; i < this.nEdge; i++) {
            System.out.println(this.edges.get(i));
        }
    }
}
