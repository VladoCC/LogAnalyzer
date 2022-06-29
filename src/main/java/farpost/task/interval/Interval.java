package farpost.task.interval;

import java.time.LocalTime;

public class Interval {
    private LocalTime begin, end;
    private int good = 0, bad = 0;
    /**
     * Amount of good logs between this and the next interval
     */
    private int between = 0;

    public Interval(LocalTime begin, LocalTime end) {
        this.begin = begin;
        this.end = end;
    }

    public LocalTime getBegin() {
        return begin;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setBegin(LocalTime begin) {
        this.begin = begin;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    public int getGood() {
        return good;
    }

    public int getBad() {
        return bad;
    }

    public void setGood(int good) {
        this.good = good;
    }

    public void setBad(int bad) {
        this.bad = bad;
    }

    public void addGood(int good) {
        this.good += good;
    }

    public void addBad(int bad) {
        this.bad += bad;
    }

    public int getBetween() {
        return between;
    }

    public void setBetween(int between) {
        this.between = between;
    }

    public float getPercent() {
        return good * 100f / (good + bad);
    }

    @Override
    public String toString() {
        return begin + " " + end + ' ' + getPercent();
    }
}
