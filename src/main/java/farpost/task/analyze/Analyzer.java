package farpost.task.analyze;

import farpost.task.Args;
import farpost.task.interval.Interval;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public abstract class Analyzer<T extends Interval> {
    protected final Args args;
    protected final static Pattern pattern = Pattern.compile("((\\d{2,3}.){3}(\\d{2,3})) - - \\[(\\d{2}/\\d{2}/\\d{4}):(\\d{2}:\\d{2}:\\d{2}) (\\+\\d{4})] \"([a-zA-Z]+) /([-a-zA-Z0-9@:%_+.~#?&/=]*) ([A-Z]+/[\\d](.\\d){0,1})\" (\\d{3}) (\\d) (\\d+.\\d+) (\"\\S\") (\"@[a-zA-Z0-9-]+\") (prio:(\\d+))");

    public Analyzer(Args args) {
        this.args = args;
    }

    abstract List<T> analyze(Scanner input);

    /**
     * Combines intervals if their shared percent of fails is too high
     * (availability lower than expected value).
     * @param intervals - intervals of logs (function modifies contents of this list)
     * @return the same list with intervals combined when possible
     */
    protected List<T> collapseIntervals(List<T> intervals) {
        int batch = intervals.size();
        int good = 0;
        int bad = 0;
        for (T interval: intervals) {
            bad += interval.getBad();
            good += interval.getGood() + interval.getBetween();
        }
        int curGood = good;
        int startGood = good;
        int curBad = bad;
        int startBad = bad;
        while (batch > 1) {
            boolean found = false;
            for (int i = 0; i <= intervals.size() - batch; i++) {
                if (i != 0) {
                    T last = intervals.get(i - 1);
                    curBad -= last.getBad();
                    curGood -= last.getGood() + last.getBetween();
                    T next = intervals.get(i + batch - 1);
                    curBad += next.getBad();
                    curGood += next.getGood();
                    curGood += intervals.get(i + batch - 2).getBetween();
                }

                // combining intervals won't be effective if interval on the corner has no fails
                if (intervals.get(i).getBad() == 0 || intervals.get(i + batch - 1).getBad() == 0) {
                    continue;
                }

                float level = curGood * 100f / (curGood + curBad);
                if (level < args.getAvailability()) {
                    T collapsed = intervals.get(i);
                    collapsed.setBad(curBad);
                    collapsed.setGood(curGood);
                    T last = intervals.get(i + batch - 1);
                    collapsed.setBetween(last.getBetween());
                    collapsed.setEnd(last.getEnd());
                    for (int j = 1; j < batch; j++) {
                        intervals.remove(i + 1);
                    }

                    found = true;
                    break;
                }
            }
            if (!found) {
                batch--;
                for (int i = batch; i < intervals.size(); i++) {
                    T interval = intervals.get(i);
                    curBad -= interval.getBad();
                    curGood -= interval.getGood() + interval.getBetween();
                }
                startGood -= intervals.get(batch).getGood() + intervals.get(batch - 1).getBetween();
                startBad -= intervals.get(batch).getBad();
                curGood = startGood;
                curBad = startBad;
            } else {
                batch = intervals.size();
                curGood = good;
                curBad = bad;
            }
        }
        return intervals;
    }

    protected boolean isFail(String code, float responseTime) {
        return code.charAt(0) == '5' || responseTime > args.getResponseTime();
    }
}
