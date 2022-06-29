package farpost.task.interval;

import java.time.LocalTime;

public class DowntimeInterval extends Interval {
    public DowntimeInterval(LocalTime time) {
        super(time, time);
        setBad(1);
    }
}
