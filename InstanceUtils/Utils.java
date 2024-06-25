
package InstanceUtils;

import java.io.BufferedReader;
import java.io.FileReader;

import static java.lang.Integer.*;

public class Utils {

    private String format = ".txt";

    private String BIG = "Big/";
    private String SMALL = "Small/";
    private String GRID = "Grid/";
    private String RANDOM = "Random/";


    private int[] possibleColorBig = {15, 20};
    private int[] possibleColorSmall = {1, 2};
    private int baseSeed = 27000;

    //GRID INFO
    private int[] row = {100, 100, 250, 250, 500, 500};
    private int[] col = {100, 200, 250, 500, 500, 1000};

    //RANDOM INFO
    private int[] possibleVertices = {75000, 100000, 125000};
    private int[] possibleEdge = {10, 15, 20};

    public Instance readGridDirectedBig(String basePath, int gridSize, int nC, int seed) {
        Instance instance;
        Graph g = new Graph();
        g.nRow = row[gridSize];
        g.nCol = col[gridSize];

        int nEdge = ((2 * (row[gridSize] - 1) * col[gridSize] + 2 * (col[gridSize] - 1) * row[gridSize]));
        int nCol = (nEdge * possibleColorBig[nC]) / 100;

        String dir = "Grid_" + row[gridSize] + "x" + col[gridSize] + "_" + nCol + "/";
        String name = "Grid_" + row[gridSize] + "x" + col[gridSize] + "_" + nCol + "_" + (baseSeed + seed) + format;

        try (BufferedReader br = new BufferedReader(new FileReader(basePath + BIG + GRID + dir + name))) {
            String currentRow;
            String[] firstRow;
            String[] currentPart;

            //Read first row
            currentRow = br.readLine();
            firstRow = currentRow.split(" ");
            int nVertices = Integer.parseInt(firstRow[0]);
            int kColor = Integer.parseInt(firstRow[1]);
            int source = Integer.parseInt(firstRow[2]) - 1;
            int target = Integer.parseInt(firstRow[3]) - 1;

            g.nVertices = nVertices;
            g.nEdge = nEdge;
            g.nColor = nCol;

            //Read Vertices
            int x = 0;
            int y = 0;
            for (int i = 0; i < nVertices; i++) {
                currentRow = br.readLine();
                int nOutEdge = parseInt(currentRow);
                Vertex nd = new Vertex(i, x, y, nOutEdge);
                g.vertices.add(nd);
                x++;
                if (x >= g.nCol) {
                    x = 0;
                    y++;
                }
            }

            int idEdge = 0;
            //Read edge
            g.maxEdgeCost = 0;
            g.meanEdgeCost = 0;
            g.minEdgeCost = Integer.MAX_VALUE;
            g.sumOfAllEdgeCost = 0;
            for (int i = 0; i < g.nVertices; i++) {
                for (int j = 0; j < g.vertices.get(i).nOutEdge; j++) {
                    currentRow = br.readLine();
                    currentPart = currentRow.split(" ");
                    int idV2 = parseInt(currentPart[0]) - 1;
                    int cost = parseInt(currentPart[1]);
                    int color = parseInt(currentPart[2]);
                    Edge edge = new Edge(idEdge, i, idV2, cost, color);
                    idEdge++;
                    g.edges.add(edge);
                    g.vertices.get(i).exitingEdges.add(edge);
                    g.vertices.get(idV2).enteringEdges.add(edge);
                    if (cost > g.maxEdgeCost) {
                        g.maxEdgeCost = cost;
                    }
                    if (cost < g.minEdgeCost) {
                        g.minEdgeCost = cost;
                    }
                    g.sumOfAllEdgeCost += cost;
                }
            }
            g.meanEdgeCost = g.sumOfAllEdgeCost / nEdge;

            //UPDATE Color Occurences and edge instances
            g.update();

            instance = new Instance(g, source, target, kColor);
            instance.name = name;

        } catch (Exception e) {
            e.printStackTrace();
            instance = null;
        }

        return instance;
    }

    public Instance readRandomDirectedBig(String basePath, int nN, int nE, int nC, int seed) {
        Instance instance;
        Graph g = new Graph();

        int nEdge = (possibleVertices[nN] * possibleEdge[nE]);
        int nCol = (nEdge * possibleColorBig[nC]) / 100;

        String dir = "Random_" + possibleVertices[nN] + "x" + nEdge + "_" + nCol + "/";
        String name;

        boolean foundRight = false;
        int actualSeed = 0;
        int found = 0;
        do {
            name = "Random_" + possibleVertices[nN] + "x" + nEdge + "_" + nCol + "_" + (baseSeed + actualSeed) + format;
            try (BufferedReader br = new BufferedReader(new FileReader(basePath + BIG + RANDOM + dir + name))) {
                if (found == seed) {
                    foundRight = true;
                } else {
                    found++;
                    actualSeed++;
                }
            } catch (Exception e) {
                actualSeed++;
            }
        } while (!foundRight);

        name = "Random_" + possibleVertices[nN] + "x" + nEdge + "_" + nCol + "_" + (baseSeed + actualSeed) + format;

        try (BufferedReader br = new BufferedReader(new FileReader(basePath + BIG + RANDOM + dir + name))) {
            String currentRow;
            String[] firstRow;
            String[] currentPart;

            //Read first row
            currentRow = br.readLine();
            firstRow = currentRow.split(" ");
            int nVertices = Integer.parseInt(firstRow[0]);
            int kColor = Integer.parseInt(firstRow[1]);
            int source = Integer.parseInt(firstRow[2]) - 1;
            int target = Integer.parseInt(firstRow[3]) - 1;

            g.nVertices = nVertices;
            g.nEdge = nEdge;
            g.nColor = nCol;


            //Read vertices
            for (int i = 0; i < nVertices; i++) {
                currentRow = br.readLine();
                int nOut = parseInt(currentRow);
                Vertex nd = new Vertex(i, -1, -1, nOut);
                g.vertices.add(nd);
            }

            //Read edge
            int idEdge = 0;
            g.maxEdgeCost = 0;
            g.meanEdgeCost = 0;
            g.minEdgeCost = Integer.MAX_VALUE;
            g.sumOfAllEdgeCost = 0;
            for (int i = 0; i < nVertices; i++) {
                for (int j = 0; j < g.vertices.get(i).nOutEdge; j++) {
                    currentRow = br.readLine();
                    currentPart = currentRow.split(" ");
                    int idV2 = parseInt(currentPart[0]) - 1;
                    int cost = parseInt(currentPart[1]);
                    int color = parseInt(currentPart[2]);
                    Edge edge = new Edge(idEdge, i, idV2, cost, color);
                    idEdge++;
                    g.edges.add(edge);
                    g.vertices.get(i).exitingEdges.add(edge);
                    g.vertices.get(idV2).enteringEdges.add(edge);
                    if (cost > g.maxEdgeCost) {
                        g.maxEdgeCost = cost;
                    }
                    if (cost < g.minEdgeCost) {
                        g.minEdgeCost = cost;
                    }
                    g.sumOfAllEdgeCost += cost;
                }
            }
            g.meanEdgeCost = g.sumOfAllEdgeCost / nEdge;

            //UPDATE Color Occurences and edge instances
            g.update();

            instance = new Instance(g, source, target, kColor);
            instance.name = name;
        } catch (Exception e) {
            e.printStackTrace();
            instance = null;
        }

        return instance;
    }

    public Instance readGridDirectedSmall(String basePath, int gridSize, int nC, int seed) {
        Instance instance;
        Graph g = new Graph();
        g.nRow = row[gridSize];
        g.nCol = col[gridSize];


        int nEdge = ((2 * (row[gridSize] - 1) * col[gridSize] + 2 * (col[gridSize] - 1) * row[gridSize]));
        int nCol = (nEdge * possibleColorSmall[nC]) / 100;

        String dir = "Grid_" + row[gridSize] + "x" + col[gridSize] + "_" + nCol + "/";
        String name = "Grid_" + row[gridSize] + "x" + col[gridSize] + "_" + nCol + "_" + (baseSeed + seed) + format;

        try (BufferedReader br = new BufferedReader(new FileReader(basePath + SMALL + GRID + dir + name))) {
//        try (BufferedReader br = new BufferedReader(new FileReader(prova))) {
//            System.out.println(name);
            String currentRow;
            String[] firstRow;
            String[] currentPart;

            //Read first row
            currentRow = br.readLine();
            firstRow = currentRow.split(" ");
            int nVertices = Integer.parseInt(firstRow[0]);
            int kColor = Integer.parseInt(firstRow[1]);
            int source = Integer.parseInt(firstRow[2]) - 1;
            int target = Integer.parseInt(firstRow[3]) - 1;

            g.nVertices = nVertices;
            g.nEdge = nEdge;
            g.nColor = nCol;

            //Read Vertices
            int x = 0;
            int y = 0;
            for (int i = 0; i < nVertices; i++) {
                currentRow = br.readLine();
                int nOutEdge = parseInt(currentRow);
                Vertex nd = new Vertex(i, x, y, nOutEdge);
                g.vertices.add(nd);
                x++;
                if (x >= g.nCol) {
                    x = 0;
                    y++;
                }
            }

            int idEdge = 0;
            //Read edge
            g.maxEdgeCost = 0;
            g.meanEdgeCost = 0;
            g.minEdgeCost = Integer.MAX_VALUE;
            g.sumOfAllEdgeCost = 0;
            for (int i = 0; i < g.nVertices; i++) {
                for (int j = 0; j < g.vertices.get(i).nOutEdge; j++) {
                    currentRow = br.readLine();
                    currentPart = currentRow.split(" ");
                    int idV2 = parseInt(currentPart[0]) - 1;
                    int cost = parseInt(currentPart[1]);
                    int color = parseInt(currentPart[2]);
                    Edge edge = new Edge(idEdge, i, idV2, cost, color);
                    idEdge++;
                    g.edges.add(edge);
                    g.vertices.get(i).exitingEdges.add(edge);
                    g.vertices.get(idV2).enteringEdges.add(edge);
                    if (cost > g.maxEdgeCost) {
                        g.maxEdgeCost = cost;
                    }
                    if (cost < g.minEdgeCost) {
                        g.minEdgeCost = cost;
                    }
                    g.sumOfAllEdgeCost += cost;
                }
            }
            g.meanEdgeCost = g.sumOfAllEdgeCost / nEdge;

            //UPDATE Color Occurences and edge instances
            g.update();

            instance = new Instance(g, source, target, kColor);
            instance.name = name;

        } catch (Exception e) {
            e.printStackTrace();
            instance = null;
        }

        return instance;
    }

    public Instance readRandomDirectedSmall(String basePath, int nN, int nE, int nC, int seed) {
        Instance instance;
        Graph g = new Graph();

        int nEdge = (possibleVertices[nN] * possibleEdge[nE]);
        int nCol = (nEdge * possibleColorSmall[nC]) / 100;

        String s = nCol + "";
        if (s.length() < 5) {
            s = "0" + s;
        }

        String dir = "Random_" + possibleVertices[nN] + "x" + nEdge + "_" + s + "/";

        String name;

        boolean foundRight = false;
        int actualSeed = 0;
        int found = 0;
        do {
            name = "Random_" + possibleVertices[nN] + "x" + nEdge + "_" + s + "_" + (baseSeed + actualSeed) + format;
            try (BufferedReader br = new BufferedReader(new FileReader(basePath + SMALL + RANDOM + dir + name))) {
                if (found == seed) {
                    foundRight = true;
                } else {
                    found++;
                    actualSeed++;
                }
            } catch (Exception e) {
                actualSeed++;
            }
        } while (!foundRight);

        name = "Random_" + possibleVertices[nN] + "x" + nEdge + "_" + s + "_" + (baseSeed + actualSeed) + format;

        try (BufferedReader br = new BufferedReader(new FileReader(basePath + SMALL + RANDOM + dir + name))) {
            String currentRow;
            String[] firstRow;
            String[] currentPart;

            //Read first row
            currentRow = br.readLine();
            firstRow = currentRow.split(" ");
            int nVertices = Integer.parseInt(firstRow[0]);
            int kColor = Integer.parseInt(firstRow[1]);
            int source = Integer.parseInt(firstRow[2]) - 1;
            int target = Integer.parseInt(firstRow[3]) - 1;

            g.nVertices = nVertices;
            g.nEdge = nEdge;
            g.nColor = nCol;


            //Read vertices
            for (int i = 0; i < nVertices; i++) {
                currentRow = br.readLine();
                int nOut = parseInt(currentRow);
                Vertex nd = new Vertex(i, -1, -1, nOut);
                g.vertices.add(nd);
            }

            //Read edge
            int idEdge = 0;
            g.maxEdgeCost = 0;
            g.meanEdgeCost = 0;
            g.minEdgeCost = Integer.MAX_VALUE;
            g.sumOfAllEdgeCost = 0;
            for (int i = 0; i < nVertices; i++) {
                for (int j = 0; j < g.vertices.get(i).nOutEdge; j++) {
                    currentRow = br.readLine();
                    currentPart = currentRow.split(" ");
                    int idV2 = parseInt(currentPart[0]) - 1;
                    int cost = parseInt(currentPart[1]);
                    int color = parseInt(currentPart[2]);
                    Edge edge = new Edge(idEdge, i, idV2, cost, color);
                    idEdge++;
                    g.edges.add(edge);
                    g.vertices.get(i).exitingEdges.add(edge);
                    g.vertices.get(idV2).enteringEdges.add(edge);
                    if (cost > g.maxEdgeCost) {
                        g.maxEdgeCost = cost;
                    }
                    if (cost < g.minEdgeCost) {
                        g.minEdgeCost = cost;
                    }
                    g.sumOfAllEdgeCost += cost;
                }
            }
            g.meanEdgeCost = g.sumOfAllEdgeCost / nEdge;

            //UPDATE Color Occurences and edge instances
            g.update();

            instance = new Instance(g, source, target, kColor);
            instance.name = name;
        } catch (Exception e) {
            e.printStackTrace();
            instance = null;
        }

        return instance;
    }

}
