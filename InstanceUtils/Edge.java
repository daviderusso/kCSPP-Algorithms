package InstanceUtils;

public class Edge {

    public int id;
    public int idV1;
    public int idV2;
    public int cost;
    public int color;


    public Edge(int id, int idV1, int idV2, int cost, int color) {
        this.id = id;
        this.idV1 = idV1;
        this.idV2 = idV2;
        this.cost = cost;
        this.color = color;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "id=" + id +
                ", idV1=" + idV1 +
                ", idV2=" + idV2 +
                ", cost=" + cost +
                ", color=" + color +
                '}';
    }
}