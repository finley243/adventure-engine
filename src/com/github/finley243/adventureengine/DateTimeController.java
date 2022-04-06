package com.github.finley243.adventureengine;

public class DateTimeController {

    public static final int MINUTES_PER_ROUND = 2;
    public static final boolean USE_24_HOUR_FORMAT = false;

    private int minutes;

    public DateTimeController() {}

    public void onNextRound() {
        minutes += MINUTES_PER_ROUND;
        if(minutes >= 1440) {
            minutes = 0;
        }
    }

    public void reset(Data data) {
        int hoursComponent = Integer.parseInt(data.getConfig("startTimeHours"));
        int minutesComponent = Integer.parseInt(data.getConfig("startTimeMinutes"));
        minutes = (hoursComponent * 60) + minutesComponent;
    }

    public void setTime(int minutes) {
        this.minutes = minutes;
    }

    // Requires use of 24-hour format
    public boolean isInRange(int hours1, int minutes1, int hours2, int minutes2) {
        boolean crossZero = hours1 > hours2;
        int totalMinutes1 = (hours1 * 60) + minutes1;
        int totalMinutes2 = (hours2 * 60) + minutes2;
        if(!crossZero && minutes >= totalMinutes1 && minutes <= totalMinutes2) {
            return true;
        } else if(crossZero && (minutes >= totalMinutes1 || minutes <= totalMinutes2)) {
            return true;
        } else {
            return false;
        }
    }

    public int getTotalMinutes() {
        return minutes;
    }

    public int getMinutesComponent() {
        return minutes % 60;
    }

    public int getHoursComponent() {
        int hours = minutes / 60;
        if(USE_24_HOUR_FORMAT) {
            return hours;
        } else if(hours == 0) {
            return 12;
        } else if(hours > 12) {
            return hours - 12;
        } else {
            return hours;
        }
    }

    public boolean isPM() {
        return minutes / 60 >= 12;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        int hoursComponent = getHoursComponent();
        int minutesComponent = getMinutesComponent();
        result.append(hoursComponent).append(":");
        if(minutesComponent == 0) {
            result.append("00");
        } else if(minutesComponent < 10) {
            result.append("0").append(minutesComponent);
        } else {
            result.append(minutesComponent);
        }
        if(!USE_24_HOUR_FORMAT) {
            if(isPM()) {
                result.append(" PM");
            } else {
                result.append(" AM");
            }
        }
        return result.toString();
    }

}
