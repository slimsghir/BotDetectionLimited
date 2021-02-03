package bot.detection.limited;

import java.time.Duration;
import java.util.AbstractMap;
import java.util.ArrayList;

public class Detection {

    private final int maxNumberRequests=1500;
    private final int minAverageRequestTime=2;
    private ArrayList<Host> potentialBots = new ArrayList<>();


    public void runDetection(Log log, long i){

        Host potentialBot = this.potentialBots.stream()
                .filter(randomHost -> log.getIp().equals(randomHost.getIp()))
                .findAny()
                .orElse(null);

        //If the host is not marked as a potential bot before AND we detected that is a potential bot
        if(potentialBot == null && runBasicDetection(log)){
            potentialBot = new Host(log.getIp());
            potentialBot.incrementReferrerNullSincePotentialBot();
            potentialBot.incrementRequestsSincePotentialBot();
            potentialBot.setLastRequestDateTime(log.getDateTime());
            potentialBots.add(potentialBot);
        }
        //If the host is banned because it is a bot
        else if(potentialBot != null && potentialBot.isBotConfirmed()) {
            System.out.println("THIS HOST IS BANNED -> " + log);
            System.out.println(potentialBot);
        }
        //If the host is marked as a potential bot
        else if(potentialBot != null){
            int index= potentialBots.indexOf(potentialBot);
            potentialBot.incrementRequestsSincePotentialBot();
            //If we detected that is a bot, we will not consider it in the future
            if(runDetectionOnPotentialBot(log, potentialBot))
                potentialBot.setBotConfirmed(true);
            potentialBots.add(index, potentialBot);
        }
    }

    public boolean runBasicDetection(Log log){
        String message="";
        boolean potentialBotDetected = false;

        if(hasRobotTxtRequest(log, null)) {
            message += "hasRobotTxtRequest ";
            potentialBotDetected = true;
        }
        if(hasBotInUserAgent(log, null)) {
            message += "hasBotInUserAgent ";
            potentialBotDetected = true;
        }
        if(hasNullReferrer(log)) {
            message += "hasNullReferrer ";
            potentialBotDetected = true;
        }
        if(potentialBotDetected)
            message = "POTENTIAL BOT DETECTED - SAVING THE HOST FOR CHECKING [ REASONS: " + message + " ] -> " + log;
        else
            message = "NOT A BOT -> " + log;
        System.out.println(message);

        return potentialBotDetected;
    }

    public boolean runDetectionOnPotentialBot(Log log, Host potentialBot){
        String message="";
        boolean botDetected = false;

        if(hasHighNumberRequests(potentialBot)) {
            message += "hasHighNumberRequest ";
            botDetected = true;
        }
        if(hasMoreThanTenTimesNullReferrer(log,potentialBot)) {
            message += "hasMoreThanTenTimesReferrerNull ";
            botDetected = true;
        }

        AbstractMap.SimpleEntry<Double,Boolean> averageRequestTime = hasLowAverageRequestTime(log,potentialBot);
        if(averageRequestTime.getValue()) {
            message += "hasLowAverageRequestTime = " + String.format("%.2f", averageRequestTime.getKey()) + " ";
            botDetected = true;
        }

        if(botDetected)
            message = "BOT DETECTED - WE WILL NOT CONSIDER IT IN THE FUTURE [ REASONS: " + message + " ] -> " + log;
        else
            message = "STILL POTENTIAL BOT -> " + log;

        System.out.println(message);
        System.out.println(potentialBot);
        return botDetected;
    }


    private boolean hasRobotTxtRequest(Log log, Host host){
        if (log.getEndpoint().contains("/robots.txt")) {
            return true;
        }
        return false;
    }

    private boolean hasBotInUserAgent(Log log, Host host){
        if (log.getAgent().contains("bot")) {
            return true;
        }
        return false;
    }

    private boolean hasNullReferrer(Log log){
        if(log.getReferer().compareTo("-") == 0)
            return true;
        return false;
    }

    private boolean hasHighNumberRequests(Host potentialBot){
        if(potentialBot.getRequestsSincePotentialBot() > maxNumberRequests) {
            return true;
        }
        return false;
    }


    private boolean hasMoreThanTenTimesNullReferrer(Log log, Host potentialBot){
        if (log.getReferer().compareTo("-")==0) {
            potentialBot.incrementReferrerNullSincePotentialBot();
        }
        if(potentialBot.getReferrerNullSincePotentialBot()>10)
            return true;
        return false;
    }

    private AbstractMap.SimpleEntry<Double,Boolean> hasLowAverageRequestTime(Log log, Host potentialBot){

        //We assume that we consider the average if the number of requests exceeds 4
        if(potentialBot.getRequestsSincePotentialBot()>=5) {
            double average = calculateAverageRequestTime(log,potentialBot);
            potentialBot.setAverageRequestTimeSincePotentialBot(average);
            potentialBot.setLastRequestDateTime(log.getDateTime());
            return new AbstractMap.SimpleEntry<>(average, average <= minAverageRequestTime);
        }
        else if(potentialBot.getRequestsSincePotentialBot()<5 && potentialBot.getRequestsSincePotentialBot()>1) {
            double average = calculateAverageRequestTime(log,potentialBot);
            potentialBot.setAverageRequestTimeSincePotentialBot(average);
            potentialBot.setLastRequestDateTime(log.getDateTime());
            return new AbstractMap.SimpleEntry<>(average, false);
        }
        //First request : Average=0
        else {
            potentialBot.setLastRequestDateTime(log.getDateTime());
            potentialBot.setAverageRequestTimeSincePotentialBot(0);
            return new AbstractMap.SimpleEntry<>((double) 0, false);
        }

    }

    private double calculateAverageRequestTime(Log log, Host potentialBot){
        double lastDuration = Math.abs(Duration.between(potentialBot.getLastRequestDateTime(), log.getDateTime()).toSeconds());
        if(potentialBot.getRequestsSincePotentialBot()>2)
            return (potentialBot.getAverageRequestTimeSincePotentialBot() * (potentialBot.getRequestsSincePotentialBot()-2) + lastDuration) / (potentialBot.getRequestsSincePotentialBot()-1);
        return lastDuration;
    }

}
