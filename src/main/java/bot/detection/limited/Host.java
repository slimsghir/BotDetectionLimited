package bot.detection.limited;

import java.time.LocalDateTime;

public class Host {

    private String ip;
    private long requestsSincePotentialBot =0;
    private long referrerNullSincePotentialBot=0;
    private  LocalDateTime lastRequestDateTime;
    private double averageRequestTimeSincePotentialBot=0;
    private boolean botConfirmed=false;


    public Host(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "Host{" +
                "ip='" + ip + '\'' +
                ", requestsSincePotentialBot=" + requestsSincePotentialBot +
                ", referrerNullSincePotentialBot=" + referrerNullSincePotentialBot +
                ", lastRequestDateTime=" + lastRequestDateTime +
                ", averageRequestTimeSincePotentialBot=" + averageRequestTimeSincePotentialBot +
                ", botConfirmed=" + botConfirmed +
                '}';
    }

    public String getIp() {
        return ip;
    }

    public long getRequestsSincePotentialBot() {
        return requestsSincePotentialBot;
    }

    public void incrementRequestsSincePotentialBot() {
        this.requestsSincePotentialBot++;
    }

    public LocalDateTime getLastRequestDateTime() {
        return lastRequestDateTime;
    }

    public void setLastRequestDateTime(LocalDateTime lastRequestDateTime) {
        this.lastRequestDateTime = lastRequestDateTime;
    }

    public double getAverageRequestTimeSincePotentialBot() {
        return averageRequestTimeSincePotentialBot;
    }

    public void setAverageRequestTimeSincePotentialBot(double averageRequestTimeSincePotentialBot) {
        this.averageRequestTimeSincePotentialBot = averageRequestTimeSincePotentialBot;
    }

    public long getReferrerNullSincePotentialBot() {
        return referrerNullSincePotentialBot;
    }

    public void incrementReferrerNullSincePotentialBot() {
        this.referrerNullSincePotentialBot++;
    }

    public boolean isBotConfirmed() {
        return botConfirmed;
    }

    public void setBotConfirmed(boolean botConfirmed) {
        this.botConfirmed = botConfirmed;
    }
}
