import java.util.Random;
public class SimulatedAnnealingSolver {

    private int[][] listVar;
    private int[][] arrayClauses;
    private int[][] copyOriginalArray;
    private long flips = 0;
    private int iterations = 0;
    private int attempts = 0;
    private int tries = 0;
    private long start;
    private double maxTemp;
    private double temp;
    private double minTemp;
    private double decayRate;
    private final double E = Math.E;
    private Random rnd;
    private int[] listPureClause, listPureVariables;
    private boolean varSelection;

    public SimulatedAnnealingSolver(int[][] listVar, int[][] arrayClauses, int[][] originalArray, int[] listPureVariables, int[] listPureClause) {
        this.listVar = listVar;
        this.arrayClauses = arrayClauses;
        this.copyOriginalArray = originalArray;
        this.listPureVariables = listPureVariables;
        this.listPureClause = listPureClause;

        rnd = new Random();
    }

    public SimulatedAnnealingSolver(int[][] listVar, int[][] arrayClauses){
        this.listVar = listVar;
        this.arrayClauses = arrayClauses;
        rnd = new Random();
    }
    public String launchSolver(int tries, double maxTemp, double minTemp, double decayRate, int iterations, boolean varselection, boolean pureLiteral) {
        this.varSelection = varselection;
        start = System.nanoTime();
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.decayRate = decayRate;
        this.iterations = iterations;
        this.temp = maxTemp;
        this.tries = tries;
        if(pureLiteral)
       return (pureLiteral());
       return noPureLiteral();
    }


    private void clearCurrentAssignment() {
        for (int i = 1; i < arrayClauses.length; i++) {
            arrayClauses[i][1] = 0;
        }
    }

    private String pureLiteral(){
        int reducer = 0;

        for (int i = 0; i < tries; i++) {
            reducer++;
            if(decayRate == 0)
                this.decayRate = 1.0 / (reducer * listVar.length-1);
            clearCurrentAssignment();
            generateRandomAssignments();
            for (int p = 0; p < listPureClause.length; p++) {
                int clause = listPureClause[p];
                arrayClauses[clause][1] = 1;
            }
            temp = this.maxTemp;
            if (startFlipping()) {
                long time = (System.nanoTime() - start) / 1000000;
                return "\nThe time taken was: " + time + "ms\n----------------------------------------------------------------------------------------------------------\n" + getVariableList() + "\n\n---------------------------------" + getListClauses();
            }
        }
        long time = (System.nanoTime() - start) / 1000000;
        System.out.println("\nWe could not find a satisfiable assignment: " + time + "  " + temp + " " + decayRate + "\n----------------------------------------------------------------------------------------------------------------------------------------------");
        return "Solution not found: " + time + "ms\n";
    }



    private String noPureLiteral(){
        int reducer = 0;
        for (int i = 0; i < tries; i++) {
            reducer++;
            if(decayRate == 0)
            this.decayRate = 1.0 / (reducer * listVar.length-1);
            clearCurrentAssignment();
            generateRandomAssignments();
            temp = this.maxTemp;
            if (flipWithoutPure()) {
                long time = (System.nanoTime() - start) / 1000000;
                return "\nThe time taken was: " + time + "ms\n----------------------------------------------------------------------------------------------------------\n" + getVariableList() + "\n\n---------------------------------" + getListClauses();
            }
        }

        long time = (System.nanoTime() - start) / 1000000;
        System.out.println("\nWe could not find a satisfiable assignment: " + time + "\n----------------------------------------------------------------------------------------------------------------------------------------------");
        return "Solution not found: " + time + "ms\n";

    }


    private boolean flipWithoutPure(){
        int randomVar;
        int j = 0;
        while (temp > minTemp) {
            j++;
            for (int k = 1; k <= iterations; k++) {
                attempts++;
                if (!checkSatisfied()) {
                    if (varSelection)
                    randomVar = getRandomVariable(listVar.length-1) + 1;
                    else
                    randomVar = (k % (listVar.length-1))+1;
                        flip(randomVar);
                } else if (checkSatisfied()){
                    System.out.print("\nA solution has been found: \n");
                    System.out.println(getVariableList());
                    System.out.println(getListClauses());
                    return true;
                }
            }
            temp = maxTemp * Math.pow(Math.E, -j * decayRate);
        }
        return false;
    }

    private boolean startFlipping() {
        int randomVar;
        int j = 0;

        while (temp > minTemp) {
            j++;
            for (int k = 1; k <= iterations; k++) {
                attempts++;
                if (!checkSatisfied()) {
                    if (varSelection)
                   randomVar = getRandomVariable(listVar.length-1) + 1;
                    else
                    randomVar = (k % (listVar.length-1))+1;

                    flip(randomVar);

                } else if (checkSatisfied()){
                    printSatisfyingAssignment();
                    return true;
                }
            }
            temp = maxTemp * Math.pow(Math.E, -j * decayRate);
        }
        return false;
    }



    private void flip(int variable) {
        if (successfulFlip(variable)) {
            ++flips;
            listVar[variable][1] = (listVar[variable][1] + 1)%2;
            updateClause(variable);
        }
    }

    private void generateRandomAssignments() {
        for (int i = 1; i < listVar.length; i++) {
            //If the variables has been assigned true, increase the true literal values in every clause that has this variable
            listVar[i][1] = getRandomVariable(2);
            if (listVar[i][1] == 1) {
                for (int k = 2; k < listVar[i][0] + 2; k++) {
                    int clause = listVar[i][k];
                    if (clause > 0)
                        ++arrayClauses[clause][1];
                }
            }
            else {
                for (int k = 2; k < listVar[i][0] + 2; k++) {
                    int clause = listVar[i][k];
                    if (clause < 0)
                        ++arrayClauses[-clause][1];
                }
            }
        }
    }

    public String getListClauses() {
        StringBuilder listClause = new StringBuilder();
        listClause.append("\n");
        for (int i = 1; i < arrayClauses.length; i++) {
            listClause.append("Clause " + i + " has (");
            for (int k = 2; k <= arrayClauses[i][0] + 1; k++) {
               listClause.append(arrayClauses[i][k] + " ");
            }
            listClause.append(" Number of satisfying literals: " + arrayClauses[i][1] + ")\n");
        }
        return listClause.toString();
    }

    private int getRandomVariable(int max) {
        return rnd.nextInt(max);
    }

    private void updateClause(int variable) {
        if (listVar[variable][1] == 1) {
            for (int i = 2; i < listVar[variable][0]+2;i++) {
                int clause = listVar[variable][i];
                if (clause > 0) {
                    ++arrayClauses[clause][1];
                } else {
                    if(arrayClauses[-clause][1] > 0)
                    --arrayClauses[-clause][1];
                }
            }
        } else {
            for (int i = 2; i < listVar[variable][0]+2;i++) {
                int clause = listVar[variable][i];
                if (clause > 0) {
                    if(arrayClauses[clause][1] > 0)
                    --arrayClauses[clause][1];
                } else {
                    ++arrayClauses[-clause][1];
                }
            }
        }
    }

    public int computeSigma(int variable) {
        int sigma = 0;

        if (listVar[variable][1] == 0) {
            for (int i = 2; i < listVar[variable][0]+2;i++) {
                int clause = listVar[variable][i];
                sigma = clause > 0 ? arrayClauses[clause][1] == 0 ? sigma + 1 : sigma : sigma;
                sigma = clause < 0 ? arrayClauses[-clause][1] == 1 ? sigma - 1 : sigma : sigma;
            }
        }
        else
            {
            for (int i = 2; i < listVar[variable][0]+2;i++) {
                int clause = listVar[variable][i];
                sigma = clause < 0 ? arrayClauses[-clause][1] == 0 ? sigma + 1 : sigma : sigma;
                sigma = clause > 0 ? arrayClauses[clause][1] == 1 ? sigma - 1 : sigma : sigma;
            }
        }
        return sigma;
    }

    private int getNumberOfSatisfiedClause() {
        int trueClauses = 0;
        for (int i = 1; i < arrayClauses.length; i++) {
            trueClauses = arrayClauses[i][1] > 0 ? trueClauses + 1 : trueClauses;
        }
        return trueClauses;
    }

    private boolean successfulFlip(int var) {
        int sigma = computeSigma(var);
        if (sigma > 0)
            return true;
        double chance = 1.0 / (1.0 + Math.pow(E, -sigma / temp));
        return chance * 100 > getRandomVariable(100);
    }

    private boolean checkSatisfied() {
        return getNumberOfSatisfiedClause() == arrayClauses.length - 1;
    }

    private String getVariableList(){
        StringBuilder listVariables = new StringBuilder();
        for(int i = 1; i < listVar.length; i++){
           listVariables.append("Variable " + i + " is: " + listVar[i][1]+"\n");
        }
        return listVariables.toString();
    }


    private void printSatisfyingAssignment(){
        System.out.println("\nWe have successfully found a solution after: " + flips + " flips with ratio: " + (double) flips / attempts + " and attempts: " + attempts + " at temp: " + temp);

        for(int i = 1; i < listVar.length; i++){
            copyOriginalArray[i][1] = listVar[i][1];
        }
        listVar = copyOriginalArray;
        for(int var : listPureVariables){
            if(var < 0){
                listVar[-var][1] = 0;
            }
            else listVar[var][1] = 1;
        }
        clearCurrentAssignment();
        for(int i = 1; i < listVar.length; i++) {

            if (listVar[i][1] == 1) {
                for (int n = 2; n < listVar[i][0] + 2; n++) {
                    int clause = listVar[i][n];
                    if (clause > 0)
                        ++arrayClauses[clause][1];
                }
            }
            else {
                for (int n = 2; n < listVar[i][0] + 2; n++) {
                    int clause = listVar[i][n];
                    if (clause < 0)
                        ++arrayClauses[-clause][1];
                }
            }
        }

        System.out.print("\nA solution has been found: \n");
        System.out.println(getVariableList());
        System.out.print(getListClauses());
    }

}
