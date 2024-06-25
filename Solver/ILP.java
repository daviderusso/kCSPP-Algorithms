//package Solver;
//
//import InstanceUtils.Edge;
//import InstanceUtils.Instance;
//import ilog.concert.*;
//import ilog.cplex.IloCplex;
//import ilog.cplex.IloCplexModeler;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//
//public class ILP {
//
//    private IloCplex model;
//    private Instance instance;
//    private Solver solver;
//
//    public ILP(Instance instance, boolean showModel, Solver solver) throws IloException {
//        this.instance = instance;
//        this.model = new IloCplex();
//        if (!showModel) {
//            model.setOut(null);
//        }
//        this.solver = solver;
//    }
//
//
//    public ArrayList<Edge> solveOriginalModel() throws IloException {
//        //Variables
//        IloIntVar[] edgeVariables = model.boolVarArray(instance.graphDirectet.nEdge);
//        for (int i = 0; i < instance.graphDirectet.nEdge; i++) {
//            edgeVariables[i].setName("x_" + instance.graphDirectet.edges.get(i).idV1 + "_" + instance.graphDirectet.edges.get(i).idV2);
//        }
//
//        IloIntVar[] colorVariables = model.boolVarArray(instance.graphDirectet.nColor);
//        for (int i = 0; i < instance.graphDirectet.nColor; i++) {
//            colorVariables[i].setName("y_" + i);
//        }
//
//        //Objective Function
//        IloLinearIntExpr objectiveFunction = model.linearIntExpr();
//        for (int i = 0; i < instance.graphDirectet.nEdge; i++) {
//            objectiveFunction.addTerm(edgeVariables[i], instance.graphDirectet.edges.get(i).cost);
//        }
//        model.addMinimize(objectiveFunction);
//
//        //Flow Balance
//        for (int i = 0; i < instance.graphDirectet.nVertices; i++) {
//            IloLinearIntExpr flow = model.linearIntExpr();
//            for (int j = 0; j < instance.graphDirectet.vertices.get(i).nInEdge; j++) {
//                flow.addTerm(edgeVariables[instance.graphDirectet.vertices.get(i).enteringEdges.get(j).id], 1);
//            }
//            for (int j = 0; j < instance.graphDirectet.vertices.get(i).nOutEdge; j++) {
//                flow.addTerm(edgeVariables[instance.graphDirectet.vertices.get(i).exitingEdges.get(j).id], -1);
//            }
//            int b_i = 0;
//            if (i == instance.source) {
//                b_i = -1;
//            } else if (i == instance.target) {
//                b_i = 1;
//            }
//            model.addEq(flow, b_i);
//        }
//
//        //Color Edge Connection
//        for (int i = 0; i < instance.graphDirectet.nEdge; i++) {
//            model.addLe(edgeVariables[i], colorVariables[instance.graphDirectet.edges.get(i).color]);
//        }
//
//        //Max Color
//        IloLinearIntExpr color = model.linearIntExpr();
//        for (int i = 0; i < instance.graphDirectet.nColor; i++) {
//            color.addTerm(colorVariables[i], 1);
//        }
//        model.addLe(color, instance.kColor);
//
////        model.exportModel("ModelExact.lp");
//        model.setParam(IloCplex.Param.TimeLimit, 900);
//        //**********************************************************************
//        ArrayList<Edge> solution = null;
//        if (model.solve()) {
////            System.out.println(model.getObjValue());
//            solution = new ArrayList<>();
//            for (int i = 0; i < edgeVariables.length; i++) {
//                if (model.getValue(edgeVariables[i]) > 0.8) {
//                    solution.add(instance.graphDirectet.edges.get(i));
//                }
//            }
//        } else {
//            System.err.println("Errore: " + model.getStatus());
//        }
//        model.end();
//
//        return solution;
//    }
//
//    //-----------------------------------------------------------------------
//
//    //ILP With graph reduction
//    public ArrayList<Edge> solverReducedILP() throws IloException, InterruptedException {
//        ArrayList<Edge> incumbentSolution = new ArrayList<>();
//
//        Heuristic solveH = new Heuristic(instance);
//        ArrayList<Edge> solution = solveH.solveColorConstrainedDijkstraAlgorithm();
//        int bestFF = 0;
//        boolean[] usableNodes = new boolean[instance.graphDirectet.nVertices];
//        Arrays.fill(usableNodes, true);
//        if (solver.checkAdmissibility(solution, false)) {
//            bestFF = solver.evaluateSolution(solution)[1];
//            usableNodes = this.solver.reduceGraphByCostMultiThread(solution, bestFF);
//        } else {
//            solution.clear();
//            System.out.println("Solution not found by heuristic");
//        }
//
//        //call ilp without selected edges
//        IloIntVar[] edgeVariables = model.boolVarArray(instance.graphDirectet.nEdge);
//        for (int i = 0; i < instance.graphDirectet.nEdge; i++) {
//            if (usableNodes[instance.graphDirectet.edges.get(i).idV1] && usableNodes[instance.graphDirectet.edges.get(i).idV2]) {
//                edgeVariables[i].setName("x_" + instance.graphDirectet.edges.get(i).idV1 + "_" + instance.graphDirectet.edges.get(i).idV2);
//            } else {
//                edgeVariables[i] = null;
//            }
//        }
//
//        IloIntVar[] colorVariables = model.boolVarArray(instance.graphDirectet.nColor);
//        for (int i = 0; i < instance.graphDirectet.nColor; i++) {
//            colorVariables[i].setName("y_" + i);
//        }
//
//        //Objective Function
//        IloLinearIntExpr objectiveFunction = model.linearIntExpr();
//        for (int i = 0; i < instance.graphDirectet.nEdge; i++) {
//            if (edgeVariables[i] != null) {
//                objectiveFunction.addTerm(edgeVariables[i], instance.graphDirectet.edges.get(i).cost);
//            }
//        }
//        model.addMinimize(objectiveFunction);
//
//        //Flow Balance
//        for (int i = 0; i < instance.graphDirectet.nVertices; i++) {
//            IloLinearIntExpr flow = model.linearIntExpr();
//            boolean added = false;
//            for (int j = 0; j < instance.graphDirectet.vertices.get(i).nInEdge; j++) {
//                if (edgeVariables[instance.graphDirectet.vertices.get(i).enteringEdges.get(j).id] != null) {
//                    flow.addTerm(edgeVariables[instance.graphDirectet.vertices.get(i).enteringEdges.get(j).id], 1);
//                    added = true;
//                }
//            }
//            for (int j = 0; j < instance.graphDirectet.vertices.get(i).nOutEdge; j++) {
//                if (edgeVariables[instance.graphDirectet.vertices.get(i).exitingEdges.get(j).id] != null) {
//                    flow.addTerm(edgeVariables[instance.graphDirectet.vertices.get(i).exitingEdges.get(j).id], -1);
//                    added = true;
//                }
//            }
//            int b_i = 0;
//            if (i == instance.source) {
//                b_i = -1;
//            } else if (i == instance.target) {
//                b_i = 1;
//            }
//            if (added) {
//                model.addEq(flow, b_i);
//            }
//        }
//
//        //Color Edge Connection
//        for (int i = 0; i < instance.graphDirectet.nEdge; i++) {
//            if (edgeVariables[i] != null) {
//                model.addLe(edgeVariables[i], colorVariables[instance.graphDirectet.edges.get(i).color]);
//            }
//        }
//
//        //Max Color
//        IloLinearIntExpr color = model.linearIntExpr();
//        for (int i = 0; i < instance.graphDirectet.nColor; i++) {
//            color.addTerm(colorVariables[i], 1);
//        }
//        model.addLe(color, instance.kColor);
//
//        // PASS INITIAL SOLUTION
//        if (!solution.isEmpty()) {
//            double[] valueOfVar = new double[instance.graphDirectet.nEdge];
//            Arrays.fill(valueOfVar, 0);
//            for (int i = 0, l = solution.size(); i < l; i++) {
//                valueOfVar[solution.get(i).id] = 1.0;
//            }
//
//            ArrayList<IloNumVar> startVarList = new ArrayList<>();
//            ArrayList<Double> startValList = new ArrayList<>();
//            for (int i = 0; i < instance.graphDirectet.nEdge; ++i) {
//                if (usableNodes[instance.graphDirectet.edges.get(i).idV1] && usableNodes[instance.graphDirectet.edges.get(i).idV2]) {
//                    startVarList.add(edgeVariables[i]);
//                    startValList.add(valueOfVar[i]);
//                }
//            }
//
//            IloNumVar[] startVar = new IloNumVar[startVarList.size()];
//            double[] startVal = new double[startValList.size()];
//            for (int i = 0, idx = 0, l = startVarList.size(); i < l; ++i) {
//                startVar[idx] = startVarList.get(i);
//                startVal[idx] = startValList.get(i);
//                ++idx;
//            }
//            model.addMIPStart(startVar, startVal);
//            startVar = null;
//            startVal = null;
//        }
//
////        model.exportModel("ModelExactRed.lp");
//        model.setParam(IloCplex.Param.TimeLimit, 900);
//        //**********************************************************************
//        if (model.solve()) {
//            for (int i = 0; i < edgeVariables.length; i++) {
//                if (edgeVariables[i] != null) {
//                    if (model.getValue(edgeVariables[i]) > 0.9) {
//                        incumbentSolution.add(instance.graphDirectet.edges.get(i));
//                    }
//                }
//            }
//        } else {
//            System.err.println("Errore: " + model.getStatus());
//            incumbentSolution = solution;
//        }
//        model.end();
//
//        return incumbentSolution;
//    }
//
//}
