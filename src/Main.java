import java.io.File;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static void main(String[] args){
        Parser parser = null;
        File projectDirectory = new File(System.getProperties().getProperty("user.dir")).getParentFile();
        if(args != null && args.length == 1){
            File fl = new File(projectDirectory  + "/resources/" + args[0] );
            parser = new Parser(fl);
        }
        else if (args != null && args.length == 2) {
            File fl = new File(projectDirectory + "/resources/" + args[0]);
            try {
                parser = new Parser(fl, Integer.parseInt(args[1]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Please provide a cnf file, which should be located within the resource folder of the project.");
            return;
        }


       // SimulatedAnnealing sa = new SimulatedAnnealing(parser.getVariablesList(),parser.getClausesList());
        SimulatedAnnealingSolver saSolver = new SimulatedAnnealingSolver(parser.getVariablesList(),parser.getClausesList());
        double maxTemp = 95;
        double minTemp = 0.003;
        double decayRate = 0.988;
        int iterationsPerCycle = 7000;
      //  for( int j = 0; j < 10; j++){
        System.out.println(projectDirectory);
        File fileLocation = new File(projectDirectory + "/src/Statistics");
            String fileName = maxTemp + "-" + minTemp + " - " + decayRate + " - " + iterationsPerCycle + ".txt";
            File statsFile = new File(fileLocation, fileName);
           System.out.println(statsFile.getAbsolutePath());
            //maxTemp -= 2;
            //minTemp -= 0.0019;
            DataWriter dataWriter = new DataWriter(statsFile.getAbsolutePath());
            for (int i = 0; i < 10; i++) {
                System.out.print("Searching for a solution ...");
                dataWriter.writeText(i + 1 + ". " + saSolver.launchSolver(20, maxTemp, minTemp, decayRate, iterationsPerCycle));
            }
            dataWriter.closeStream();
        }


  //  }
}
