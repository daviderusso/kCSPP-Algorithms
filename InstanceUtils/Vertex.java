package InstanceUtils;

import java.util.ArrayList;

public class Vertex {

    public int x;
    public int y;
    public int id;

    public int nInEdge;
    public int nOutEdge;
    public ArrayList<Edge> exitingEdges;
    public ArrayList<Edge> enteringEdges;

    public Vertex(int id, int x, int y) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.exitingEdges = new ArrayList<Edge>();
        this.enteringEdges = new ArrayList<Edge>();
    }

    public Vertex(int id, int x, int y, int nOutEdge) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.nOutEdge = nOutEdge;
        this.nInEdge = nOutEdge;
        this.exitingEdges = new ArrayList<Edge>();
        this.enteringEdges = new ArrayList<Edge>();
    }

    public Vertex(int id, int x, int y, int nInEdge, int nOutEdge) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.nInEdge = nInEdge;
        this.nOutEdge = nOutEdge;
        this.exitingEdges = new ArrayList<Edge>();
        this.enteringEdges = new ArrayList<Edge>();
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "x=" + x +
                ", y=" + y +
                ", id=" + id +
                ", nInEdge=" + nInEdge +
                ", nOutEdge=" + nOutEdge +
                ", exitingEdges=" + exitingEdges +
                ", enteringEdges=" + enteringEdges +
                '}';
    }
}
