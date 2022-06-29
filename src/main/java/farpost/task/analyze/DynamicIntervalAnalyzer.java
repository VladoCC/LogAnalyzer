package farpost.task.analyze;

import farpost.task.Args;
import farpost.task.exceptions.UnexpectedInputException;
import farpost.task.interval.DowntimeInterval;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicIntervalAnalyzer extends Analyzer<DowntimeInterval> {

    public DynamicIntervalAnalyzer(Args args) {
        super(args);
    }

    @Override
    public List<DowntimeInterval> analyze(Scanner input) {
        return collapseIntervals(extractLogs(input));
    }

    private List<DowntimeInterval> extractLogs(Scanner input) {
        ArrayList<DowntimeInterval> list = new ArrayList<>();
        DowntimeInterval downtime = null;
        int counter = 0;

        while (input.hasNextLine()) {
            Matcher matcher = pattern.matcher(input.nextLine());
            if (matcher.matches()) {
                LocalTime time = LocalTime.parse(matcher.group(5));
                String code = matcher.group(11);
                float responseTime = Float.parseFloat(matcher.group(13));
                if (isFail(code, responseTime)) {
                    DowntimeInterval updated = addLog(downtime, counter, time);
                    if (updated != downtime) {
                        downtime = updated;
                        list.add(downtime);
                    }
                    counter = 0;
                } else {
                    counter++;
                }
            } else {
                throw new UnexpectedInputException();
            }
        }
        return list;
    }

    private DowntimeInterval addLog(DowntimeInterval downtime, int counter, LocalTime time) {
        if (downtime != null) {
            int good = downtime.getGood() + counter;
            float level = good * 100f / (good + downtime.getBad() + 1);
            if (level < args.getAvailability()) {
                downtime.setBad(downtime.getBad() + 1);
                downtime.addGood(counter);
                downtime.setEnd(time);
            } else {
                downtime.setBetween(counter);
                downtime = new DowntimeInterval(time);
            }
        } else {
            downtime = new DowntimeInterval(time);
        }
        return downtime;
    }
}
