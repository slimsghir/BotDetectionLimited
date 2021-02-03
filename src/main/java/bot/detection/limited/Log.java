package bot.detection.limited;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;

public class Log {

    private String ip;
    private LocalDateTime dateTime;
    private String endpoint;
    private String referer;
    private String agent;

    public Log(Matcher matcher) {
        this.ip = matcher.group(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MMM/yyyy:H:m:s Z", Locale.ENGLISH);
        this.dateTime = LocalDateTime.parse(matcher.group(4),formatter);
        this.endpoint = matcher.group(6);
        this.referer = matcher.group(10);
        this.agent = matcher.group(11).toLowerCase();
    }

    @Override
    public String toString() {
        return "Log{" +
                "ip='" + ip + '\'' +
                ", dateTime=" + dateTime +
                ", referer='" + referer + '\'' +
                ", agent='" + agent + '\'' +
                '}';
    }

    public String getIp() {
        return ip;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getReferer() {
        return referer;
    }

    public String getAgent() {
        return agent;
    }

}

