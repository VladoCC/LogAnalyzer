package farpost.task;

import farpost.task.analyze.DynamicIntervalAnalyzer;
import farpost.task.exceptions.UnexpectedArgumentException;
import farpost.task.interval.Interval;

import java.util.Scanner;

public class Dynamic {
    public static void main(String[] args) {
        try {
            Args analyzerArgs = new Args(args);
            Scanner input = new Scanner(System.in);
            for (Interval interval: new DynamicIntervalAnalyzer(analyzerArgs).analyze(input)) {
                System.out.println(interval);
            }
        } catch (UnexpectedArgumentException e) {
            e.printStackTrace();
        }
    }
}

