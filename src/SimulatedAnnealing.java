import java.util.*;

public class SimulatedAnnealing {

    private HashMap<Integer, HashSet<Integer>> listVariables;
    private int[][] listClauses;
    private boolean[] lstVariablesTrueValue;
    private long flips = 0;
    private long time;

    public SimulatedAnnealing(HashMap<Integer, HashSet<Integer>> listVariables, int[][] listClauses) {
        this.listClauses = listClauses;
        this.listVariables = listVariables;
        lstVariablesTrueValue = new boolean[listVariables.size() + 1];
        generateRandomAssignment();
        // printVariablesTruthValue();
        // printListClause();

    }

    public void setTime(long nanoTime) {
        time = nanoTime;
    }

    public void performflipOn(int var) {
        ++flips;
        System.out.println("On variable: " + var + " sigma is: " + computeSigma(var));
        if (lstVariablesTrueValue[var] == true) {
            for (int clause : listVariables.get(var)) {
                if (clause < 0) {
                    int indexTruthValues = listClauses[-clause][0] + 1;
                    listClauses[-clause][indexTruthValues]++;
                } else {
                    int indexTruthValues = listClauses[clause][0] + 1;
                    listClauses[clause][indexTruthValues]--;
                }
            }
        } else {
            for (int clause : listVariables.get(var)) {
                if (clause < 0) {
                    int indexTruthValues = listClauses[-clause][0] + 1;
                    listClauses[-clause][indexTruthValues]--;
                } else {
                    int indexTruthValues = listClauses[clause][0] + 1;
                    listClauses[clause][indexTruthValues]++;
                }
            }
        }
        lstVariablesTrueValue[var] = !lstVariablesTrueValue[var];
        if (numberOfTrueClause() == listClauses.length - 1) {

            System.out.println("We have found a result after " + flips + " flips");
            long timeTaken = System.nanoTime() - time;
            System.out.println("The time taken is: " + timeTaken);
            printVariablesTruthValue();

            System.exit(1);
        }
    }

    public void performFlip() {
        ++flips;
        int rndNumber = getRandomVariable(lstVariablesTrueValue.length - 1) + 1;
        if (lstVariablesTrueValue[rndNumber] == true) {
            for (int clause : listVariables.get(rndNumber)) {
                if (clause < 0) {
                    int indexTruthValues = listClauses[-clause][0] + 1;
                    listClauses[-clause][indexTruthValues]++;
                } else {
                    int indexTruthValues = listClauses[clause][0] + 1;
                    listClauses[clause][indexTruthValues]--;
                }
            }
        } else {
            for (int clause : listVariables.get(rndNumber)) {
                if (clause < 0) {
                    int indexTruthValues = listClauses[-clause][0] + 1;
                    listClauses[-clause][indexTruthValues]--;
                } else {
                    int indexTruthValues = listClauses[clause][0] + 1;
                    listClauses[clause][indexTruthValues]++;
                }
            }
        }
        lstVariablesTrueValue[rndNumber] = !lstVariablesTrueValue[rndNumber];

        if (checkSatisfiability()) {
            System.out.println("We have found a result after " + flips + " flips");
            printVariablesTruthValue();
            long timeTaken = System.nanoTime() - time;
            System.out.println("The time taken is: " + timeTaken / 1000000);
            System.exit(1);
        }
    }

    private boolean checkSatisfiability() {
        return numberOfTrueClause() == listClauses.length - 1;
    }

    private int numberOfTrueClause() {
        int trueClause = 0;
        for (int i = 1; i < listClauses.length; ++i) {
            int indexTruthValues = listClauses[i][0] + 1;
            if (listClauses[i][indexTruthValues] > 0)
                trueClause++;

        }
        // System.out.println("The number of true clauses is: " + trueClause);

        return trueClause;
    }

    private void clausesTrueLiterals() {
        for (int i = 1; i < listClauses.length; ++i) {
            int indexTruthValues = listClauses[i][0] + 1;
            System.out.println("Clause " + i + " has: " + listClauses[i][indexTruthValues]);
        }
    }

    private void printVariablesTruthValue() {
        for (int i = 1; i < lstVariablesTrueValue.length; ++i) {
            System.out.println("Variable: " + i + " is " + lstVariablesTrueValue[i]);
        }
    }

    public void printListVariables() {
        System.out.println(listVariables);
    }

    public void printListClause() {
        for (int i = 1; i < listClauses.length; ++i) {
            System.out.println("Clause: " + i + " has " + listClauses[i][listClauses[i][0] + 1] + " true literals");
            for (int j = 1; j <= listClauses[i][0]; ++j) {
                System.out.print(listClauses[i][j] + ", ");
            }
            System.out.println("\n");
        }
    }

    private int getRandomVariable(int max) {
        Random rnd = new Random();
        return rnd.nextInt(max);
    }

    private void generateRandomAssignment() {
        for (int variable : listVariables.keySet()) {

            if (getRandomVariable(2) > 0) {
                System.out.println("Variable: " + variable + " has been assigned to true");
                lstVariablesTrueValue[variable] = true;
                for (int clause : listVariables.get(variable)) {
                    if (clause > 0) {
                        listClauses[clause][listClauses[clause][0] + 1]++;
                    }
                }
            } else {
                System.out.println("Variable: " + variable + " has been assigned to false");
                lstVariablesTrueValue[variable] = false;
                for (int clause : listVariables.get(variable)) {
                    if (clause < 0) {
                        listClauses[-clause][listClauses[-clause][0] + 1]++;
                    }
                }
            }
        }
    }

    private int computeSigma(int variable) {
        int sigma = 0;
        if (lstVariablesTrueValue[variable] == true) {
            for (int clause : listVariables.get(variable)) {
                if (clause < 0) {
                    int indexTruthValues = listClauses[-clause][0] + 1;
                    if (listClauses[-clause][indexTruthValues] == 0)
                        ++sigma;
                } else {
                    int indexTruthValues = listClauses[clause][0] + 1;
                    if (listClauses[clause][indexTruthValues] == 1)
                        --sigma;
                }
            }
        } else {
            for (int clause : listVariables.get(variable)) {
                if (clause < 0) {
                    int indexTruthValues = listClauses[-clause][0] + 1;
                    if (listClauses[-clause][indexTruthValues] == 1)
                        --sigma;
                } else {
                    int indexTruthValues = listClauses[clause][0] + 1;
                    if (listClauses[clause][indexTruthValues] == 0)
                        ++sigma;
                }
            }
        }
        return sigma;
    }

}
