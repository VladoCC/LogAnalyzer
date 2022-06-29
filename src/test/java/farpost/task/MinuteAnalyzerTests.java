package farpost.task;

import farpost.task.analyze.MinuteIntervalAnalyzer;
import farpost.task.interval.Interval;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinuteAnalyzerTests {
    int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    int getCorrectCode() {
        return getRandomNumber(1, 4) * 100 + getRandomNumber(0, 20);
    }

    @Test
    void codeTest() {
        for (int i = 0; i < 1000; i++) {
            DataSource source = new DataSource();
            for (int j = 0; j < 10; j++) {
                source.addTime(getRandomNumber(5, 10));
                source.addLog(getCorrectCode(), 1f);
            }
            source.addTime(60 - source.getTime().getSecond());
            LocalTime begin = source.getTime();
            for (int j = 0; j < 5; j++) {
                source.addTime(getRandomNumber(5, 10));
                int code = 500 + getRandomNumber(0, 20);
                source.addLog(code, 1f);
            }
            source.addTime(60 - source.getTime().getSecond());
            LocalTime end = source.getTime();
            source.addTime(getRandomNumber(5, 10));
            for (int j = 0; j < 10; j++) {
                source.addTime(getRandomNumber(5, 10));
                source.addLog(getCorrectCode(), 1f);
            }
            Args args = new Args(1f, 50f);
            MinuteIntervalAnalyzer analyzer = new MinuteIntervalAnalyzer(args);
            List<Interval> intervals = analyzer.analyze(source.getScanner());
            assertEquals(1, intervals.size());
            Interval interval = intervals.get(0);
            assertEquals(begin, interval.getBegin());
            assertEquals(end, interval.getEnd());
            assertEquals(0f, interval.getPercent(), 0.0001);
        }
    }

    @Test
    void accessTimeTest() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            DataSource source = new DataSource();
            float limit = random.nextFloat() * 99f + 1f;
            for (int j = 0; j < 10; j++) {
                source.addTime(getRandomNumber(5, 10));
                source.addLog(200,random.nextFloat() * limit);
            }
            source.addTime(60 - source.getTime().getSecond());
            LocalTime begin = source.getTime();
            for (int j = 0; j < 5; j++) {
                source.addTime(getRandomNumber(5, 10));
                source.addLog(200,random.nextFloat() * limit + limit);
            }
            source.addTime(60 - source.getTime().getSecond());
            LocalTime end = source.getTime();
            source.addTime(getRandomNumber(5, 10));
            for (int j = 0; j < 10; j++) {
                source.addTime(getRandomNumber(5, 10));
                source.addLog(200,random.nextFloat() * limit);
            }
            Args args = new Args(1f, limit);
            MinuteIntervalAnalyzer analyzer = new MinuteIntervalAnalyzer(args);
            List<Interval> intervals = analyzer.analyze(source.getScanner());
            assertEquals(1, intervals.size());
            Interval interval = intervals.get(0);
            assertEquals(begin, interval.getBegin());
            assertEquals(end, interval.getEnd());
            assertEquals(0f, interval.getPercent(), 0.0001);
        }
    }

    @Test
    void percentTest() {
        for (int i = 0; i < 1000; i++) {
            int bad = getRandomNumber(1, 49);
            ArrayList<Integer> codes = new ArrayList<>();
            for (int j = 0; j < 50; j++) {
                if (j < bad) {
                    codes.add(500);
                } else {
                    codes.add(200);
                }
            }
            Collections.shuffle(codes);
            DataSource source = new DataSource();
            source.addTime(60 - source.getTime().getSecond());
            for (int code: codes) {
                source.addTime(1);
                source.addLog(code, 1f);
            }
            Args args = new Args(100f, 100f);
            MinuteIntervalAnalyzer analyzer = new MinuteIntervalAnalyzer(args);
            List<Interval> intervals = analyzer.analyze(source.getScanner());
            assertEquals(1, intervals.size());
            Interval interval = intervals.get(0);
            assertEquals(100f - bad * 2f, interval.getPercent(), 0.0001f);
        }
    }

    @Test
    void availabilityTest() {
        for (int i = 0; i < 1000; i++) {
            int availability = getRandomNumber(2, 99);
            int half = availability / 2;
            ArrayList<Integer> codes = new ArrayList<>();
            for (int j = 0; j < 50; j++) {
                if (j < half) {
                    codes.add(500);
                } else {
                    codes.add(200);
                }
            }
            Collections.shuffle(codes);
            DataSource source = new DataSource();
            source.addTime(60 - source.getTime().getSecond());
            for (int code: codes) {
                source.addTime(1);
                source.addLog(code, 1f);
            }
            Args args = new Args(100f - availability, 100f);
            MinuteIntervalAnalyzer analyzer = new MinuteIntervalAnalyzer(args);
            List<Interval> intervals = analyzer.analyze(source.getScanner());
            assertEquals(0, intervals.size());

            codes = new ArrayList<>();
            for (int j = 0; j < 50; j++) {
                if (j < half + 1) {
                    codes.add(500);
                } else {
                    codes.add(200);
                }
            }
            Collections.shuffle(codes);
            source = new DataSource();
            source.addTime(60 - source.getTime().getSecond());
            for (int code: codes) {
                source.addTime(1);
                source.addLog(code, 1f);
            }
            analyzer = new MinuteIntervalAnalyzer(args);
            intervals = analyzer.analyze(source.getScanner());
            assertEquals(1, intervals.size());
        }
    }

    @Test
    void collapseIntervalsTest() {
        for (int i = 0; i < 1000; i++) {
            DataSource source = new DataSource();
            source.addTime(60 - source.getTime().getSecond());
            source.addTime(1);
            source.addLog(500, 1f);
            source.addTime(1);
            source.addLog(500, 1f);
            source.addTime(60);
            source.addLog(200, 1f);
            source.addTime(1);
            source.addLog(200, 1f);
            source.addTime(60);
            source.addLog(500, 1f);
            source.addTime(60);
            source.addLog(200, 1f);
            source.addTime(1);
            source.addLog(200, 1f);
            source.addTime(1);
            source.addLog(200, 1f);
            source.addTime(60);
            source.addLog(500, 1f);
            source.addTime(1);
            source.addLog(500, 1f);
            Args args = new Args(41f, 100f);
            MinuteIntervalAnalyzer analyzer = new MinuteIntervalAnalyzer(args);
            List<Interval> intervals = analyzer.analyze(source.getScanner());
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
            analyzer = new MinuteIntervalAnalyzer(args);
            intervals = analyzer.analyze(source.getScanner());
            assertEquals(1, intervals.size());
            Interval interval = intervals.get(0);
            assertEquals(count, interval.getGood());
            assertEquals(count + 1, interval.getBad());
        }
    }
}
