import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

public class Parser {
    private int[][] variablesList;
    private int[][] clausesList;
    private int[][] copyOriginalArray;
    private Scanner fileReader;
    private File cnfFormula;
    private int numberOfVariables, numberOfClauses;
    private String strCnfFormula;
    private HashSet<Integer> listPureClause, listPureVariables;

    public Parser(File cnfFormula) {
        if (verifyFile(cnfFormula)) {
            this.cnfFormula = cnfFormula;
        }

        else {
            System.out.println("The file can not be recognised");
            System.out.println(Help.commandFile() + "\n use 'java SASA -help'  for more information");
            System.exit(1);
        }

        listPureClause = new HashSet<>();
        listPureVariables = new HashSet<>();
        initialiseScanner(this.cnfFormula);
        setStrCnfFormula();
        setClausesList();
        copyOriginalArray();
        checkPureLiteral();
    }

    private void setStrCnfFormula() {
        StringBuilder cnfStringBuilder = new StringBuilder();
        while (fileReader.hasNext()) {
            String currentLine;
            currentLine = fileReader.nextLine();
            currentLine = currentLine.trim();
            if (currentLine.endsWith(" 0"))
                cnfStringBuilder.append(currentLine);
            else if (currentLine.startsWith("p")) {
                currentLine = currentLine.replace("  ", " ");
                String[] varNum = currentLine.split(" ");
                try {
                    numberOfClauses = Integer.parseInt(varNum[varNum.length - 1]);
                    numberOfVariables = Integer.parseInt(varNum[varNum.length - 2]);
                    System.out.println("\nNumber of variables: " + numberOfVariables);
                    System.out.println("Number of clauses: " + numberOfClauses);
                } catch (Exception e) {
                    System.out.println("Warning: Reading of the number of variables is unsuccessful");
                    e.printStackTrace();
                }
            }
            /*
             * else if((currentLine = fileReader.nextLine()).endsWith(" 0 ")){ currentLine =
             * currentLine.strip(); cnfStringBuilder.append(currentLine); }
             * 
             */
        }
        strCnfFormula = cnfStringBuilder.toString().trim();

    }

    private void setClausesList() {
        // Adjusting the size of the second dimension of the clausesList if number of
        // variables is provided we use this number else we take the number of the
        // characters
        if (numberOfClauses == 0)
            numberOfClauses = strCnfFormula.replace("0", "").replace(" ", "").replace("-", "").length();
        String[] cnfClauses = strCnfFormula.split(" 0");
        clausesList = new int[numberOfClauses + 1][numberOfVariables + 2];

        // We start from 1 as 0 is neutral and we can not represent a number with -0
        int i = 1;

        // Decomposing each clause into its variables and storing them into the clauses
        // list.
        // clauseList[i][0] represents the number of variables in clause i.
        // clauseList[i][1 to clausesList[i][0]] represents the variables in the clause
        // i
        // clauseList[i][clauseList[i][0] + 1] represents the number of true literals in
        // the clause
        for (String clause : cnfClauses) {
            int j = 2;
            for (String variable : clause.split(" ")) {
                clausesList[i][0]++;
                clausesList[i][j++] = Integer.parseInt(variable);
            }
            clausesList[i][1] = 0;
            ++i;
        }
        populateVariableList();

    }

    private void populateVariableList() {
        variablesList = new int[numberOfVariables + 1][numberOfClauses + 2];
        for (int i = 1; i < clausesList.length; i++) {
            for (int j = 2; j <= clausesList[i][0] + 1; j++) {
                int variable;
                int clause;
                // If the variable is negated, we add it's absolute value as a key and the the
                // clause it is negated in as negative it it's HashSet values
                if (clausesList[i][j] < 0) {
                    variable = -clausesList[i][j];
                    clause = -i;
                } else {
                    variable = clausesList[i][j];
                    clause = i;
                }
                variablesList[variable][0]++;
                variablesList[variable][variablesList[variable][0] + 1] = clause;
            }
        }
    }

    public int[][] getVariablesList() {
        return variablesList;
    }

    public int[][] getClausesList() {
        return clausesList;
    }

    private boolean verifyFile(File file) {
        return file != null && file.exists() && !file.isDirectory();
    }

    private void initialiseScanner(File file) {
        try {
            fileReader = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void printClause() {
        for (int i = 1; i < clausesList.length; i++) {
            System.out.print("Clause " + i + ": (");
            for (int k = 2; k < clausesList[i][0] + 2; k++) {
                System.out.print(clausesList[i][k] + " ");
            }
            System.out.println(")");
        }
    }

    private void printVariableList() {
        for (int i = 1; i < variablesList.length; i++) {
            System.out.print("Variable " + i + ": (");
            for (int k = 2; k < variablesList[i][0] + 2; k++) {
                System.out.print(variablesList[i][k] + " ");
            }
            System.out.println(")");
        }
    }

    private void checkPureLiteral() {
        for (int i = 1; i < variablesList.length; i++) {
            boolean pos = false;
            boolean neg = false;
            for (int j = 2; j < variablesList[i][0] + 2; j++) {
                if (variablesList[i][j] > 0) {
                    pos = true;
                } else if (variablesList[i][j] < 0) {
                    neg = true;
                }
            }
            if ((!pos || !neg) && !(!pos && !neg)) {
                listPureVariables.add(variablesList[i][2] > 0 ? i : -i);

                printPureLit(i);

            }

        }
    }

    private void printPureLit(int var) {
        HashSet<Integer> setForRemove = new HashSet<>();
        for (int i = 2; i < variablesList[var][0] + 2; i++) {
            int clause = variablesList[var][i];
            setForRemove.add(clause);
        }

        removeClausesFromVar(setForRemove);
    }

    private void removeClausesFromVar(HashSet<Integer> clauses) {
        for (int clause : clauses) {
            if (clause > 0) {
                listPureClause.add(clause);
                for (int i = 2; i < clausesList[clause][0] + 2; i++) {
                    removeClausefromVar(clausesList[clause][i], clause);
                }

            } else {
                listPureClause.add(-clause);
                for (int i = 2; i < clausesList[-clause][0] + 2; i++) {
                    removeClausefromVar(clausesList[-clause][i], -clause);
                }
            }
        }

    }

    private void removeClausefromVar(int var, int clause) {
        boolean reduce = false;
        if (var < 0) {
            var = -var;
            clause = -clause;
        }
        for (int i = 2; i < variablesList[var][0] + 2; i++) {
            if (variablesList[var][i] == clause) {
                variablesList[var][i] = 0;
                reduce = true;
            } else if (reduce)
                variablesList[var][i - 1] = variablesList[var][i];

        }
        variablesList[var][0]--;
    }

    private void copyOriginalArray() {
        copyOriginalArray = new int[variablesList.length][clausesList.length + 2];
        for (int m = 1; m < variablesList.length; m++) {
            for (int j = 0; j < variablesList[m][0] + 2; j++) {
                copyOriginalArray[m][j] = variablesList[m][j];
            }
        }
    }

    public int[][] getOriginal() {
        return copyOriginalArray;
    }

    public HashSet<Integer> getListPureClause() {
        return listPureClause;
    }

    public HashSet<Integer> getListPureVariables() {
        return listPureVariables;
    }
}
