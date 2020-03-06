import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
//UF50-965,967 is satisfiable
public class SimulatedAnnealingSolver {

    private HashMap<Integer, HashSet<Integer>> hashMapVariables;
    private int[][] arrayClauses;
    private boolean[] lstVariablesTruthValues;
    private long flips = 0;
    private int iterations = 0;
    private long start;
    private double maxTemp;
    private double temp;
    private double minTemp;
    private double decayRate;
    private final double E = Math.E;

    public SimulatedAnnealingSolver(HashMap<Integer, HashSet<Integer>> hashMapVariables, int[][] arrayClauses){
        this.hashMapVariables = hashMapVariables;
        this.arrayClauses = arrayClauses;
       // printClausesPerVariable();
        lstVariablesTruthValues = new boolean[hashMapVariables.size() + 1];
    }

    public String launchSolver(int tries, double maxTemp, double minTemp, double decayRate, int iterations){
        flips = 0;
        start = System.nanoTime();
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.decayRate = decayRate;
        this.iterations = iterations;
        temp = maxTemp;
        generateRandomAssignments();
        for(int i = 0; i < tries; i++){
            clearCurrentAssignment();
            generateRandomAssignments();
            temp = maxTemp;
            if(startFlipping()) {
                long time = (System.nanoTime() - start) / 10000000;
                System.out.println("It took: " + time + "\n------------------------------------------------------------------------------------------------");
                return "Solution was found after: " + flips + " flips," + " taking " + time + "ms\n";
            }
        }

        long time = (System.nanoTime() - start) / 10000000;
        System.out.println("We could not find a satisfiable assignment: " + time + "  " + temp + " " + decayRate + "\n------------------------------------------------------------------------------------------------");
        return "Solution not found: " + time + "ms\n" ;
    }

    private void clearCurrentAssignment(){
        for(int i = 1; i < arrayClauses.length;i++){
            arrayClauses[i][1] = 0;
        }
    }


    private boolean startFlipping(){
        int randomVar = 0;
       while (temp > minTemp){
           int intIt = iterations;
           for (int k = 1; k <= intIt; k++) {
               if (!checkSatisfied()) {
                   randomVar = getRandomVariable(hashMapVariables.size()) + 1;
                       flip(randomVar);
               }
               else {
                   System.out.println("We have successfully found a solution after: " + flips);
                   return true;
               }
           }
           intIt *= 0.999;
           temp = temp * decayRate;
       }
       return false;
    }


    private void flip(int variable){
        if(successfulFlip(variable)){
            ++flips;
            lstVariablesTruthValues[variable] = !lstVariablesTruthValues[variable];
            updateClause(variable);
       }
    }

    private void generateRandomAssignments(){
        for(int var : hashMapVariables.keySet()) {
            //If the variables has been assigned true, increase the true literal values in every clause that has this variable
          if((lstVariablesTruthValues[var] = getRandomVariable(2) > 0 ? true : false) == true) {
              for (int clause : hashMapVariables.get(var))
                  if (clause > 0) {
                      ++arrayClauses[clause][1];
                  }
          }

          else {
              for(int clause : hashMapVariables.get(var)) {
                  if(clause < 0)
                  ++arrayClauses[-clause][1];
              }
          }

        }

    }

    private int getRandomVariable(int max){
        Random rnd = new Random();
        return rnd.nextInt(max);
    }

    private void updateClause(int variable){
        if(lstVariablesTruthValues[variable]) {
            for (int clause : hashMapVariables.get(variable)) {

                if (clause > 0) {
                    ++arrayClauses[clause][1];
                } else {
                    --arrayClauses[-clause][1];
                }

            }
        }
        else {
            for (int clause : hashMapVariables.get(variable)) {
                if (clause > 0) {
                    --arrayClauses[clause][1];
                } else {
                    ++arrayClauses[-clause][1];
                }
            }
        }
    }

    public int computeSigma(int variable) {
        int sigma = 0;

        if (lstVariablesTruthValues[variable] == false) {
            for (int clause : hashMapVariables.get(variable)) {
                sigma = clause > 0 ? arrayClauses[clause][1] == 0 ? sigma + 1 : sigma : sigma;
                sigma = clause < 0 ? arrayClauses[-clause][1] == 1 ? sigma - 1 : sigma : sigma;
            }
        }
        else {
            for (int clause : hashMapVariables.get(variable)) {
                sigma = clause < 0 ? arrayClauses[-clause][1] == 0 ? sigma + 1 : sigma : sigma;
                sigma = clause > 0 ? arrayClauses[clause][1] == 1 ? sigma + 1 : sigma : sigma;
            }
        }
        return sigma;
    }

    private int getNumberOfSatisfiedClause(){
        int trueClauses = 0;
        for(int i = 1; i < arrayClauses.length; i++){
            trueClauses = arrayClauses[i][1] > 0 ? trueClauses + 1 : trueClauses;
        }
        return trueClauses;
    }

    private boolean successfulFlip(int var){
        int sigma = computeSigma(var);
        if(sigma > 0) {
            return true;
        }
        else {
            double chance = Math.pow(E, sigma / temp);
            return chance * 100 > getRandomVariable(101);
        }
    }

    private boolean checkSatisfied(){
        return getNumberOfSatisfiedClause() == arrayClauses.length - 1;
    }

    private void printClausesPerVariable() {
        int pos = 0;
        int neg = 0;
        for (int var : hashMapVariables.keySet()) {

            for (int clause : hashMapVariables.get(var)) {
                if (pos == 0 || neg == 0) {
                    if (clause > 0)
                        pos++;
                    else
                        neg++;

                } else {
                    pos = 0;
                    neg = 0;
                    break;
                }
            }
            if((pos > 0 || neg > 0) & !(pos > 0 && neg > 0))
                System.out.println("Pure literal, variable: " + var);
        }
    }
}
