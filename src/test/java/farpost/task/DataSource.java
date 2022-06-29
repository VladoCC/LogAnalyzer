package farpost.task;

import java.io.ByteArrayInputStream;
import java.time.LocalTime;
import java.util.Random;
import java.util.Scanner;

public class DataSource {
    StringBuilder builder = new StringBuilder();
    LocalTime time;

    public DataSource() {
        Random random = new Random();
        int hour = random.nextInt(23);
        int minute = random.nextInt(60);
        int second = random.nextInt(60);
        StringBuilder timeBuilder = new StringBuilder();
        appendTimeSegment(timeBuilder, hour);
        timeBuilder.append(":");
        appendTimeSegment(timeBuilder, minute);
        timeBuilder.append(":");
        appendTimeSegment(timeBuilder, second);
        time = LocalTime.parse(timeBuilder.toString());
    }

    private void appendTimeSegment(StringBuilder builder, int number) {
        if (number < 10) {
            builder.append(0);
        }
        builder.append(number);
    }

    public void addTime(int seconds) {
        time = time.plusSeconds(seconds);
    }

    public void addLog(int code, float responseTime) {
        builder.append("192.168.32.181 - - [14/06/2017:");
        // we want to make sure seconds are preserved
        appendTimeSegment(builder, time.getHour());
        builder.append(":");
        appendTimeSegment(builder, time.getMinute());
        builder.append(":");
        appendTimeSegment(builder, time.getSecond());
        builder.append(" +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=e356713 HTTP/1.1\" ");
        builder.append(code);
        builder.append(" 2 ");
        builder.append(responseTime);
        builder.append(" \"-\" \"@list-item-updater\" prio:0\n");
    }

    public Scanner getScanner() {
        return new Scanner(new ByteArrayInputStream(builder.toString().getBytes()));
    }

    public LocalTime getTime() {
        return time;
    }
}
