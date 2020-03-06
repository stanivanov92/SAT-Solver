import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Parser {
   private HashMap<Integer, HashSet<Integer>> variablesList;
   private int[][] clausesList;
   private Scanner fileReader;
   private File cnfFormula;
   private int numberOfVariables;
   private String strCnfFormula;

    public Parser(File cnfFormula) {
        if(verifyFile(cnfFormula)) {
            this.cnfFormula = cnfFormula;
        }
        else {
            System.out.println("The file can not be recognised");
            return;
        }
        initialiseScanner(this.cnfFormula);
        variablesList = new HashMap<>();
        setStrCnfFormula();
        setClausesList();
    }

    public Parser(File cnfFormula, int numberOfVariables){
        if(verifyFile(cnfFormula)) {
            this.cnfFormula = cnfFormula;
        }
        else {
            return;
        }
        this.numberOfVariables = numberOfVariables;
        initialiseScanner(this.cnfFormula);
        variablesList = new HashMap<>();
        setStrCnfFormula();
        setClausesList();
    }

    private void setStrCnfFormula(){
        StringBuilder cnfStringBuilder = new StringBuilder();
        while (fileReader.hasNext()){
            String currentLine;
            if((currentLine = fileReader.nextLine()).endsWith(" 0"))
            cnfStringBuilder.append(currentLine);
        }
        strCnfFormula = cnfStringBuilder.toString().trim();
    }


    private void setClausesList(){

        //Adjusting the size of the second dimension of the clausesList if number of variables is provided we use this number else we take the number of the characters
        if(numberOfVariables == 0)
        numberOfVariables = strCnfFormula.replace("0","").replace(" ", "").replace("-","").length();

        String[] cnfClauses = strCnfFormula.split(" 0");
        clausesList = new int[cnfClauses.length + 1][numberOfVariables + 2];

        //We start from 1 as 0 is neutral and we can not represent a number with -0
        int i = 1;

        //Decomposing each clause into its variables and storing them into the clauses list.
        //clauseList[i][0] represents the number of variables in clause i.
        //clauseList[i][1 to clausesList[i][0]] represents the variables in the clause i
        //clauseList[i][clauseList[i][0] + 1] represents the number of true literals in the clause
        for(String clause : cnfClauses){
            int j = 2;
           for(String variable : clause.split(" ")) {
               clausesList[i][0]++;
               clausesList[i][j++] = Integer.parseInt(variable);
           }
           clausesList[i][1] = 0;
           ++i;
        }

        for(i = 1; i < clausesList.length; i++){

            for(int j = 2; j <= clausesList[i][0] + 1; j++){

                int variable;
                int clause;

                //If the variable is negated, we add it's absolute value as a key and the the clause it is negated in as negative it it's HashSet values
              if(clausesList[i][j] < 0) {
                  variable = -clausesList[i][j];
                  clause = -i;
              }
              else
                  {
                      variable = clausesList[i][j];
                  clause = i;
              }
               variablesList.putIfAbsent(variable,new HashSet<>());
              variablesList.get(variable).add(clause);
            }
        }
    }

    public HashMap<Integer, HashSet<Integer>> getVariablesList() {
        return variablesList;
    }


    public int[][] getClausesList() {
        return clausesList;
    }

    private boolean verifyFile(File file){
        return file != null && file.exists() && !file.isDirectory();
    }

    private void initialiseScanner(File file){
        try {
            fileReader = new Scanner(file);
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

}
