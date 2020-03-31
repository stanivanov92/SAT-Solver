import java.awt.*;
import java.io.File;


public class Main {

    public static void main(String[] args){
        double maxTemp = 100;
        double minTemp = 0.001;
        double decayRate = 0.97;
        int iterationsPerCycle = 13000;
        int availThreads = Runtime.getRuntime().availableProcessors();
        String fileName = null;
        boolean conc = false;
        for(String param : args){

            switch (param.substring(0,5)){
                case "-help":
                    System.out.println("Help page");
                    break;
                case "-file":
                    fileName = param.substring(5);
                    break;
                case "-minT":
                    minTemp =Double.valueOf(param.substring(5));
                    break;
                case "-maxT":
                    maxTemp =Integer.valueOf(param.substring(5));
                case "-decR":
                    decayRate =Double.valueOf(param.substring(5));
                    break;
                case "-conc":
                    conc = true;
                    break;
                case "-iter":
                    iterationsPerCycle =Integer.valueOf(param.substring(5));
                    break;
                default:
                    System.out.println(param + " Is not recognised");



            }
        }
        System.out.println("File name: " + fileName + "\nMin temperature: " + minTemp + "\nMax temperature is: " + maxTemp + "\nIterations: " + iterationsPerCycle + "\nDecay rate: " + decayRate + "\nConcurency: " + conc + "\nAvailable threads: " + availThreads);
        Parser parser = null;
        File projectDirectory = new File(System.getProperties().getProperty("user.dir")).getParentFile();

         if (fileName != null) {
             File fl = new File(projectDirectory + "/resources/" + fileName);
             parser = new Parser(fl);
             /*
         } else if (args != null && args.length == 2) {
             File fl = new File(projectDirectory + "/resources/" + args[0]);
             try {
                 parser = new Parser(fl, Integer.parseInt(args[1]));
             } catch (Exception e) {
                 e.printStackTrace();
             }

              */
         } else {
             System.out.println("Please provide a cnf file, which should be located within the resource folder of the project.");
             return;
         }


         // SimulatedAnnealing sa = new SimulatedAnnealing(parser.getVariablesList(),parser.getClausesList());
         SimulatedAnnealingSolver saSolver = new SimulatedAnnealingSolver(parser.getVariablesList(), parser.getClausesList());
         //  for( int j = 0; j < 10; j++){
         String arrayPathLiterals[] = fileName.split("/");
         String strFileName = arrayPathLiterals[arrayPathLiterals.length - 1];
         File fileLocation = new File(projectDirectory + "/src/Statistics");
         String fName = maxTemp + "-" + minTemp + " - " + decayRate + " - " + iterationsPerCycle + "-" + strFileName + ".txt";
         File statsFile = new File(fileLocation, fName);

         DataWriter dataWriter = new DataWriter(statsFile.getAbsolutePath());
         for (int i = 0; i < 5; i++) {
             System.out.print("Searching for a solution ...");
             dataWriter.writeText(i + 1 + ". " + saSolver.launchSolver(25, maxTemp, minTemp, decayRate, iterationsPerCycle));
             Toolkit.getDefaultToolkit().beep();
         }
         dataWriter.closeStream();
     }
   }


  //  }
