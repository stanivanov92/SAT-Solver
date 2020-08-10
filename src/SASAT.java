
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;


public class SASAT {

    private static DataWriter dataWriter;
    private static long timer;
    private static double maxTemp;
    private static double minTemp;
    private static double decayRate;
    private static long timeOut;
    private static int iterationsPerCycle;
    private static int max_tries;
    private static int availThreads;
    private static int threadsUsage;
    private static String fileName;
    private static boolean varSelection;
    private static boolean concurrency;
    private static boolean pureLiteral;
    private static String statsLocation, statsFileName;
    private static Parser parser;


    public static void main(String[] args) {

        //Parse the input, verifies it and assign the data to the appropriate variables
        parseInputData(args);

        File projectDirectory = new File(System.getProperties().getProperty("user.dir")).getParentFile();
        File fl = new File(projectDirectory + "/resources/" + fileName);
        parser = new Parser(fl);

        setStatisticFileName(projectDirectory);

        timer = System.nanoTime();


        if (timeOut > 0) {
            timeOut();
        }

        if (concurrency) {
            setConcurrency();
        }

        System.out.println("File name: " + statsFileName + "\nMax temperature is: " + maxTemp + "\nMin temperature: " + minTemp + "\nDecay rate: " + decayRate + "\nIterations: " + iterationsPerCycle + "\n" +
                "Max_Tries: " + max_tries + "\nConcurrency: " + concurrency + "\nAvailable threads: " + availThreads + "\nPure literal: " + pureLiteral + "\nThreads utilisation: " + threadsUsage + "\nTimeout: " + (timeOut > 0 ? timeOut + " ms" : "Off"));

        SimulatedAnnealingSolver saSolver;

        if (!pureLiteral) {
            saSolver = new SimulatedAnnealingSolver(parser.getVariablesList(), parser.getClausesList());
         }

        else {
            int[]  pl = getCopyOfArray(parser.getListPureVariables());
            int[]  pc = getCopyOfArray(parser.getListPureClause());
            saSolver = new SimulatedAnnealingSolver(parser.getVariablesList(), parser.getClausesList(), parser.getOriginal(), pl, pc);
            System.out.println("Pure literals: " + parser.getListPureVariables());
            System.out.println("Pre-Satisfied clauses: " + parser.getListPureClause()
                    + "\n--------------------------------------------------------------------");
      }

        System.out.print("Searching for a solution ...\n");
        printText("Variables: " + (parser.getVariablesList().length - 1) + "\nClauses: " + (parser.getClausesList().length - 1) + "\nFile name: " + statsFileName + "\nMax temperature is: " + maxTemp + "\nMin temperature: " + minTemp + "\nDecay rate: " + decayRate + "\nIterations: " + iterationsPerCycle + "\n" +
                "Max_Tries: " + max_tries + "\nConcurrency: " + concurrency + "\nAvailable threads: " + availThreads + "\nPure literal: " + pureLiteral +
                "\nTimeout: " + (timeOut > 0 ? timeOut + " ms" : "Off") + "\n" + " " + saSolver.launchSolver(max_tries, maxTemp, minTemp, decayRate, iterationsPerCycle, varSelection,pureLiteral));


    }





    public static synchronized void printText(String txt) {
        long time = (System.nanoTime() - timer) / 1000000;
        dataWriter.writeText(txt);
        dataWriter.closeStream();
        System.out.println("It took : " + time + " ms");
        System.out.println("The statistics are recorded in : " + statsLocation);
        Toolkit.getDefaultToolkit().beep();
        System.exit(1);
    }


    private static void timeOut(){
        long finalTimeOut = timeOut;
        final long currentTime = timer;
        new Thread(() -> {
            while (((System.nanoTime() - currentTime) / 1000000) < finalTimeOut) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("TIME OUT!  " + finalTimeOut + " ms");
            printText("The system Timed out after: " + finalTimeOut);
        }
        ).start();
    }



    private static void parseInputData(String args[]){
        FrontEndParser frontEnd = new FrontEndParser(args);
        frontEnd.parseInput();
        maxTemp = frontEnd.getMaxTemp();
        minTemp = frontEnd.getMinTemp();
        decayRate = frontEnd.getDecayRate();
        timeOut = frontEnd.getTimeOut();
        iterationsPerCycle = frontEnd.getIterationsPerCycle();
        max_tries = frontEnd.getMax_tries();
        availThreads = Runtime.getRuntime().availableProcessors();
        threadsUsage = 1;
        fileName = frontEnd.getFileName();
        varSelection = frontEnd.isIterativeSelection();
        concurrency = frontEnd.isConcurrency();
        pureLiteral = frontEnd.isPureLiteral();
    }


    private static void setStatisticFileName(File projectDirectory){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        String[] arrayPathLiterals = fileName.split("/");
        String strFileName = arrayPathLiterals[arrayPathLiterals.length - 1];
        statsFileName = strFileName;
        File fileLocation = new File(projectDirectory + "/src/Statistics");
        String statsName = strFileName + "-" +  dtf.format(now) + ".txt";
        statsName = statsName.replace(" ", "-");
        statsName = statsName.replace("/", "-");
        File statsFile = new File(fileLocation, statsName);
        dataWriter = new DataWriter(statsFile.getAbsolutePath());
        statsLocation = statsFile.getAbsolutePath();
    }




    private static void setConcurrency(){
        threadsUsage = availThreads / 2;
        Thread[] threads = new Thread[threadsUsage - 1];
        final boolean finalPureLiteral = pureLiteral;
        for (int i = 0; i < threadsUsage - 1; i++) {
            int[][] newVar = new int[parser.getVariablesList().length][parser.getClausesList().length];
            int[][] newCl = new int[parser.getClausesList().length][parser.getVariablesList().length];
            for (int m = 1; m < parser.getVariablesList().length; m++) {
                for (int j = 0; j < parser.getVariablesList()[m][0] + 2; j++) {
                    newVar[m][j] = parser.getVariablesList()[m][j];
                }
            }
            for (int m = 1; m < parser.getClausesList().length; m++) {
                for (int j = 0; j < parser.getClausesList()[m][0] + 2; j++) {
                    newCl[m][j] = parser.getClausesList()[m][j];
                }
            }
            int[] pl1 = new int[parser.getListPureVariables().size()];
            int[] pc1 = new int[parser.getListPureClause().size()];


            threads[i] = new Thread(() -> {
                if (finalPureLiteral) {
                    SimulatedAnnealingSolver saSolverP2 = new SimulatedAnnealingSolver(newVar, newCl, parser.getOriginal(), pl1, pc1);
                    printText("Variables: " + (parser.getVariablesList().length - 1) + "\nClauses: " + (parser.getClausesList().length - 1) + "\nFile name: " + statsFileName + "\nMax temperature is: " + maxTemp + "\nMin temperature: " + minTemp + "\nDecay rate: " + decayRate + "\nIterations: " + iterationsPerCycle + "\n" +
                            "Max_Tries: " + max_tries + "\nConcurrency: " + concurrency + "\nAvailable threads: " + availThreads + "\nPure literal: " + pureLiteral +
                            "\nTimeout: " + (timeOut > 0 ? timeOut + " ms" : "Off") + "\n" + " " + saSolverP2.launchSolver(max_tries, maxTemp, minTemp, decayRate, iterationsPerCycle, varSelection,pureLiteral));
                } else {
                    SimulatedAnnealingSolver saSolver2 = new SimulatedAnnealingSolver(newVar, newCl);
                    printText("Variables: " + (parser.getVariablesList().length - 1) + "\nClauses: " + (parser.getClausesList().length - 1) + "\nFile name: " + statsFileName + "\nMax temperature is: " + maxTemp + "\nMin temperature: " + minTemp + "\nDecay rate: " + decayRate + "\nIterations: " + iterationsPerCycle + "\n" +
                            "Max_Tries: " + max_tries + "\nConcurrency: " + concurrency + "\nAvailable threads: " + availThreads + "\nPure literal: " + pureLiteral +
                            "\nTimeout: " + (timeOut > 0 ? timeOut + " ms" : "Off") + "\n" + " " + saSolver2.launchSolver(max_tries, maxTemp, minTemp, decayRate, iterationsPerCycle, varSelection,pureLiteral));
                }
            });
        }
        for (int a = 0; a < threadsUsage - 1; a++) {
            System.out.println("Engine " + a + " starts");
            threads[a].start();
        }
    }

    private static int[] getCopyOfArray(HashSet<Integer> originalArray ){
        int[] arrayCopy = new int[originalArray.size()];
        int j = 0;
        for (int pure : originalArray) {
            arrayCopy[j] = pure;
            j++;
        }
        return arrayCopy;
    }

}

