package farpost.task;

import farpost.task.analyze.MinuteIntervalAnalyzer;
import farpost.task.exceptions.UnexpectedArgumentException;
import farpost.task.interval.Interval;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            Args analyzerArgs = new Args(args);
            Scanner input = new Scanner(System.in);
            for (Interval interval: new MinuteIntervalAnalyzer(analyzerArgs).analyze(input)) {
                System.out.println(interval);
            }
        } catch (UnexpectedArgumentException e) {
            System.out.println(e.getMessage());
            System.out.println();
            printUsage();
        }
    }

    private static void printUsage() {
        System.out.println("Usage");
        System.out.println("Logs are received through system input stream");
        System.out.println();
        System.out.println("Parameters:");
        System.out.println("    -u [float] - minimal allowed availability level. Value: (0, 100]");
        System.out.println("    -t [float] - maximal allowed response time. Value: (0, +inf)");
    }
}

