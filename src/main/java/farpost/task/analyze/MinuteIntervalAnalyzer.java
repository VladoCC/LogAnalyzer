package farpost.task.analyze;

import farpost.task.Args;
import farpost.task.interval.Interval;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class MinuteIntervalAnalyzer extends Analyzer<Interval> {

    public MinuteIntervalAnalyzer(Args args) {
        super(args);
    }

    @Override
    public List<Interval> analyze(Scanner input) {
        return collapseIntervals(extractLogs(input))
                .stream()
                .filter((i) -> i.getPercent() < args.getAvailability())
                .collect(Collectors.toList());
    }

    private List<Interval> extractLogs(Scanner input) {
        ArrayList<Interval> list = new ArrayList<>();
        while (input.hasNextLine()) {
            Matcher matcher = pattern.matcher(input.nextLine());
            if (matcher.matches()) {
                LocalTime time = LocalTime.parse(matcher.group(5));
                String code = matcher.group(11);
                float responseTime = Float.parseFloat(matcher.group(13));
                if (list.isEmpty()) {
                    list.add(createInterval(time));
                }
                Interval current = list.get(list.size() - 1);
                if (time.isAfter(current.getEnd())) {
                    list.add(createInterval(time));
                    current = list.get(list.size() - 1);
                }
                if (isFail(code, responseTime)) {
                    current.addBad(1);
                } else {
                    current.addGood(1);
                }
            }
        }
        return list;
    }

    private Interval createInterval(LocalTime time) {
        LocalTime startTime = time.minusSeconds(time.getSecond());
        LocalTime endTime = startTime.plusMinutes(1);
        return new Interval(startTime, endTime);
    }
}
