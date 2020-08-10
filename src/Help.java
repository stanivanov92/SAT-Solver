public class Help {

    public static String helpPage(){
        return
                "\n\n-------------------------------------------------------- HELP PAGE ---------------------------------------------------------\n" +
                        "----------------------------------------------------------------------------------------------------------------------------\n\n" +
                        "Commands available:\n" +
                        "\n1. " + commandFile() + "\n\n2. " + commandDecayRate()+
                "\n\n3. " + commandMaxTemp() + "\n\n4. " + commandMinTemp() + "\n\n5. " + commandConcurrency() + "\n\n6. " + commandPureLiteral() +"\n\n7. " + commandIterations() + "\n\n8. " + commandMaxTries() +
                    "\n\n9." + commandVarS() + "\n\n10." + commandTimeOut() +   "\n\n-------------------------------------------------------------------------------------------" +
                        "-----------------------------------\n\n";
    }


    public static String commandFile(){
        return "-file  \n" +
                "      This command is used to provide a cnf file containing a boolean formula in CNF - DIMACS format\n" +
                "      The file must be placed within the resource directory of the project.\n" +
                "      To refer to a file in the resource, type the path within the resource directory and the name of the file\n" +
                "      For example, if within resource folder there is a folder CNF-Clause100 containing \n" +
                "      a target file SAT50x100.cnf it can be used with the following command: -file/CNF-Clause100/SAT50x100.cnf";

    }

    public static String commandDecayRate(){
        return "-decR  \n" +
                "      This command is used to set the value for the decay rate. The decay rate determines the speed with which\n" +
                "      the temperature will cool down starting from Maximum temperature and cooling down to the Minimum temperature.\n" +
                "      The value for the decay rate must be between 0 and 1 exclusively. If this command is not used pre-execution,\n" +
                "      the decay rate will be calculated depending on the number of variables and it will be dynamically reduced during execution.\n" +
                "      For example to set the decay rate to 0.05 use the following command: -minT0.05";

    }

    public static String commandMaxTemp(){
        return "-maxT\n" +
                "      This command is used to set the value for the Maximum temperature. The maximum temperature\n" +
                "      will be used to every-time a cycle is finished by assigning the current temperature to the maximum temperature.\n" +
                "      The value for the maximum temperature must be a positive number, often set between 0 and 1,\n" +
                "      depending on minimum temperature and decay rate.\n" +
                "      If the command is not used a default value of 0.3 will be assigned.\n" +
                "      For example to set the Maximum temperature to 0.5 use the following command: -maxT0.5";
    }

    public static String commandMinTemp(){
        return "-minT\n" +
                "      This command is used to set the value for the Minimum temperature. The minimum temperature\n" +
                "      is used to determine the end of a cycle. Once the current temperature is equal\n" +
                "      to the minimum or lower, the cycle has finished.The value for the minimum temperature must be a positive number,\n" +
                "      often set between 0 and 1, depending on maximum temperature and decay rate.\n" +
                "      If the command is not used a default value of 0.01 will be assigned.\n" +
                "      For example to set the Minimum temperature to 0.03 use the following command: -minT0.03";
    }

    public static String commandConcurrency(){
        return "-conc\n"+
        "      This command is used to set the execution of the program to run on multiple threads if more than one thread is available";
    }

    public static String commandPureLiteral(){
        return "-pure\n" +
                "      This command is used to activate the pure literal heuristic.\n" +
                "      By default the heuristic is turned off";
    }

    public static String commandIterations(){
        return "-iter\n" +
                "      This command is used to set the value for the number of iterations. The number of iterations\n" +
                "      determines how many attempts to flip a variable will be made before the current temperature is reduced.\n" +
                "      The higher the number is, the more thorough space exploration will be undertaken.\n" +
                "      The input is required to be a positive integer and the default value is 5,000.\n" +
                "      For example to set the iteration value to 3000 use the following command: -iter3000";
    }

    public static String commandMaxTries(){
        return "-trie\n" +
                "      This command is used to set the value for the maximum number of tries. The number of tries\n" +
                "      determines how many times maximum (if solution is not found before that) the temperature will be rest,\n" +
                "      starting with a fresh randomly generated. The command takes as an input a positive integer, with\n" +
                "      a default value set to 10. For exmaple to set the maximum tries to 15, use the following commande:\n" +
                "      -trie15";
    }

    public static String commandVarS() {
        return "-varS  \n" +
                "      sThis command is used to set the way a variable is selected. It can either be a randomised or\n" +
                "      iterative. For randomised type the following command: -varS1  while for iterative -varS0.\n";
    }

    public static String commandTimeOut() {
        return "-time  \n" +
                "      This command is used to set timeout for the application. If the application elapse time reaches the \n" +
                "      timeout set it terminates. The input should be in milliseconds. For example -time600000 sets the time out on 1 minutes.";

    }

}
