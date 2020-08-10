public class FrontEndParser {

    private double maxTemp = 0.3;
    private double minTemp = 0.001;
    private double decayRate = 0.00;
    private long timeOut = 0;
    private int iterationsPerCycle = 5000;
    private int max_tries = 10;
    private String fileName = null;
    private boolean varSelection = false;
    private boolean concurrency = false;
    private boolean pureLiteral = false;
    private String params[];

    public FrontEndParser(String params[]) {
        this.params = params;
    }

    public void parseInput() {
        for (String param : params) {
            try {
                String parameter = param.substring(0, 5);
                switch (parameter) {
                    case "-help":
                        System.out.println(Help.helpPage());
                        System.exit(0);
                    case "-file":
                        fileName = param.substring(5);
                        break;
                    case "-minT":
                        minTemp = Double.valueOf(param.substring(5));
                        if (minTemp < 0)
                            Double.valueOf("s");
                        break;
                    case "-maxT":
                        maxTemp = Double.valueOf(param.substring(5));
                        if (maxTemp < 0)
                            Double.valueOf("s");
                        break;
                    case "-time":
                        timeOut = Long.valueOf(param.substring(5));
                        if (timeOut <= 0)
                            Double.valueOf("S");
                        break;
                    case "-decR":
                        decayRate = Double.valueOf(param.substring(5));
                        if (decayRate <= 0 || decayRate >= 1)
                            Double.valueOf("s");
                        break;
                    case "-conc":
                        concurrency = true;
                        break;
                    case "-iter":
                        iterationsPerCycle = Integer.valueOf(param.substring(5));
                        if (iterationsPerCycle <= 0)
                            Double.valueOf("s");
                        break;
                    case "-pure":
                        pureLiteral = true;
                        break;
                    case "-trie":
                        max_tries = Integer.valueOf(param.substring(5));
                        if (max_tries <= 0)
                            Double.valueOf("s");
                        break;

                    case "-varS":
                        int type = Integer.valueOf(param.substring(5));
                        if (type < 0 || type > 1)
                            Double.valueOf("s");
                        else {
                            if (type == 1) {
                                varSelection = true;
                            } else if (type == 0) {
                                varSelection = false;
                            }
                        }
                        break;
                    default:
                        System.out.println(param + " Is not recognised");
                        return;
                }

            } catch (IndexOutOfBoundsException e1) {
                System.out.println("Parameter " + param + " is not recognised");
                break;

            } catch (NumberFormatException e2) {
                System.out.println(param.substring(5) + " is not a valid argument for " + param.substring(0, 5));
                switch (param.substring(0, 5)) {
                    case "-minT":
                        System.out.println(Help.commandMinTemp());
                        break;
                    case "-maxT":
                        System.out.println(Help.commandMaxTemp());
                        break;
                    case "-decR":
                        System.out.println(Help.commandDecayRate());
                        break;
                    case "-iter":
                        System.out.println(Help.commandIterations());
                        break;
                    case "-trie":
                        System.out.println(Help.commandMaxTries());
                        break;
                    case "-time":
                        System.out.println(Help.commandTimeOut());
                        break;
                    case "-varS":
                        System.out.println(Help.commandVarS());
                }

                return;
            }

        }

        verifyInput();

    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public double getDecayRate() {
        return decayRate;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public int getIterationsPerCycle() {
        return iterationsPerCycle;
    }

    public int getMax_tries() {
        return max_tries;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isIterativeSelection() {
        return varSelection;
    }

    public boolean isConcurrency() {
        return concurrency;
    }

    public boolean isPureLiteral() {
        return pureLiteral;
    }

    private void verifyInput() {
        if (fileName == null) {
            System.out.println("To run the system, please provide a file in cnf form.\n"
                    + "For more information please run the command:java SASAT -help");
            System.exit(1);
        }
        if (maxTemp <= minTemp) {
            System.out.println("Max temperature can not have smaller or equal value to minimum temperature ");
            System.exit(1);
        }
    }

}
