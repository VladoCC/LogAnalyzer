package farpost.task;

import farpost.task.analyze.DynamicIntervalAnalyzer;
import farpost.task.analyze.MinuteIntervalAnalyzer;
import farpost.task.interval.DowntimeInterval;
import farpost.task.interval.Interval;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamicAnalyzerTests {
    int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    @Test
    void percentTest() {
        for (int i = 0; i < 1000; i++) {
            int bad = getRandomNumber(2, 48);
            ArrayList<Integer> codes = new ArrayList<>();
            for (int j = 0; j < 48; j++) {
                if (j < bad - 2) {
                    codes.add(500);
                } else {
                    codes.add(200);
                }
            }
            Collections.shuffle(codes);
            // have to make sure that data starts and ends with fail
            // otherwise interval won't contain all logs
            codes.add(500);
            codes.add(0, 500);
            DataSource source = new DataSource();
            for (int code: codes) {
                source.addTime(getRandomNumber(1, 100));
                source.addLog(code, 1f);
            }
            Args args = new Args(100f, 100f);
            DynamicIntervalAnalyzer analyzer = new DynamicIntervalAnalyzer(args);
            List<DowntimeInterval> intervals = analyzer.analyze(source.getScanner());
            assertEquals(1, intervals.size());
            Interval interval = intervals.get(0);
            assertEquals(100f - bad * 2f, interval.getPercent(), 0.0001f);
        }
    }

    @Test
    void collapseIntervalsTest() {
        for (int i = 0; i < 1000; i++) {
            DataSource source = new DataSource();
            for (int j = 0; j < getRandomNumber(1, 10); j++) {
                source.addTime(getRandomNumber(1, 100));
                source.addLog(200, 1f);
            }
            source.addTime(getRandomNumber(1, 100));
            source.addLog(500, 1f);
            source.addTime(getRandomNumber(1, 100));
            source.addLog(500, 1f);
            source.addTime(getRandomNumber(1, 100));
            source.addLog(200, 1f);
            source.addTime(getRandomNumber(1, 100));
            source.addLog(200, 1f);
            source.addTime(getRandomNumber(1, 100));
            source.addLog(500, 1f);
            source.addTime(getRandomNumber(1, 100));
            source.addLog(200, 1f);
            source.addTime(getRandomNumber(1, 100));
            source.addLog(200, 1f);
            source.addTime(getRandomNumber(1, 100));
            source.addLog(200, 1f);
            source.addTime(getRandomNumber(1, 100));
            source.addLog(500, 1f);
            source.addTime(getRandomNumber(1, 100));
            source.addLog(500, 1f);
            for (int j = 0; j < getRandomNumber(1, 10); j++) {
                source.addTime(getRandomNumber(1, 100));
                source.addLog(200, 1f);
            }
            Args args = new Args(41f, 100f);
            DynamicIntervalAnalyzer analyzer = new DynamicIntervalAnalyzer(args);
            List<DowntimeInterval> intervals = analyzer.analyze(source.getScanner());
            assertEquals(2, intervals.size());
            assertEquals(3, intervals.get(0).getBad());
            assertEquals(2, intervals.get(0).getGood());
            assertEquals(2, intervals.get(1).getBad());
            assertEquals(0, intervals.get(1).getGood());

            source = new DataSource();
            source.addTime(60 - source.getTime().getSecond());
            source.addLog(500, 1f);
            int count = getRandomNumber(10, 20);
            for (int j = 0; j < count; j++) {
                source.addTime(60 - source.getTime().getSecond());
                source.addLog(200, 1f);
                source.addTime(60 - source.getTime().getSecond());
                source.addLog(500, 1f);
            }
            args = new Args(50f, 100f);
            analyzer = new DynamicIntervalAnalyzer(args);
            intervals = analyzer.analyze(source.getScanner());
            assertEquals(1, intervals.size());
            Interval interval = intervals.get(0);
            assertEquals(count, interval.getGood());
            assertEquals(count + 1, interval.getBad());
        }
    }
}
