package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.load.SaveData;

import java.util.ArrayList;
import java.util.List;

public class DateTimeController {

    public static final int MINUTES_PER_ROUND = 2;
    public static final boolean USE_24_HOUR_FORMAT = false;
    public static final int MINUTES_PER_DAY = 1440;

    private int minutes;
    private int day;
    private int month;
    private int year;
    private int weekday;

    public DateTimeController() {}

    public void onNextRound() {
        minutes += MINUTES_PER_ROUND;
        if (minutes >= MINUTES_PER_DAY) {
            minutes = 0;
            day += 1;
            weekday += 1;
            if (weekday > 7) {
                weekday = 1;
            }
            if (day > daysInMonth()) {
                day = 1;
                month += 1;
                if (month > 12) {
                    month = 1;
                    year += 1;
                }
            }
        }
    }

    public void reset(Data data) {
        int hoursComponent = Integer.parseInt(data.getConfig("startTimeHours"));
        int minutesComponent = Integer.parseInt(data.getConfig("startTimeMinutes"));
        int startDateYear = Integer.parseInt(data.getConfig("startDateYear"));
        int startDateMonth = Integer.parseInt(data.getConfig("startDateMonth"));
        int startDateDay = Integer.parseInt(data.getConfig("startDateDay"));
        int startDateWeekday = DateTimeController.getWeekdayIndex(data.getConfig("startDateWeekday"));
        this.minutes = (hoursComponent * 60) + minutesComponent;
        this.year = startDateYear;
        this.month = startDateMonth;
        this.day = startDateDay;
        this.weekday = startDateWeekday;
    }

    // Requires use of 24-hour format
    public boolean isInRange(int hours1, int minutes1, int hours2, int minutes2) {
        boolean crossZero = hours1 > hours2;
        int totalMinutes1 = (hours1 * 60) + minutes1;
        int totalMinutes2 = (hours2 * 60) + minutes2;
        if (!crossZero && minutes >= totalMinutes1 && minutes <= totalMinutes2) {
            return true;
        } else return crossZero && (minutes >= totalMinutes1 || minutes <= totalMinutes2);
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getMinutesComponent() {
        return minutes % 60;
    }

    public int getHoursComponent() {
        int hours = minutes / 60;
        if (USE_24_HOUR_FORMAT) {
            return hours;
        } else if (hours == 0) {
            return 12;
        } else if (hours > 12) {
            return hours - 12;
        } else {
            return hours;
        }
    }

    public boolean isPM() {
        return minutes / 60 >= 12;
    }

    public int daysInMonth() {
        return switch (month) {
            case 0, 2, 4, 6, 7, 9, 11 -> 31;
            case 3, 5, 8, 10 -> 30;
            case 1 -> 28;
            default -> -1;
        };
    }

    public String getWeekday() {
        return switch (weekday) {
            case 1 -> "monday";
            case 2 -> "tuesday";
            case 3 -> "wednesday";
            case 4 -> "thursday";
            case 5 -> "friday";
            case 6 -> "saturday";
            case 7 -> "sunday";
            default -> null;
        };
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        int hoursComponent = getHoursComponent();
        int minutesComponent = getMinutesComponent();
        result.append(hoursComponent).append(":");
        if (minutesComponent == 0) {
            result.append("00");
        } else if (minutesComponent < 10) {
            result.append("0").append(minutesComponent);
        } else {
            result.append(minutesComponent);
        }
        if (!USE_24_HOUR_FORMAT) {
            if (isPM()) {
                result.append(" PM");
            } else {
                result.append(" AM");
            }
        }
        result.append(" - ").append(getWeekday().toUpperCase()).append(", ");
        result.append(month).append("/").append(day).append("/").append(year);
        return result.toString();
    }

    public static int getWeekdayIndex(String day) {
        return switch (day.toLowerCase()) {
            case "monday" -> 1;
            case "tuesday" -> 2;
            case "wednesday" -> 3;
            case "thursday" -> 4;
            case "friday" -> 5;
            case "saturday" -> 6;
            case "sunday" -> 7;
            default -> -1;
        };
    }

    public static int minutesToRounds(int minutes) {
        return minutes / MINUTES_PER_ROUND;
    }

    public static int roundsToMinutes(int rounds) {
        return rounds * MINUTES_PER_ROUND;
    }

    public List<SaveData> saveState() {
        List<SaveData> state = new ArrayList<>();
        state.add(new SaveData(SaveData.DataType.TIME, null, "minutes", minutes));
        state.add(new SaveData(SaveData.DataType.TIME, null, "year", year));
        state.add(new SaveData(SaveData.DataType.TIME, null, "month", month));
        state.add(new SaveData(SaveData.DataType.TIME, null, "day", day));
        state.add(new SaveData(SaveData.DataType.TIME, null, "weekday", weekday));
        return state;
    }

    public void loadState(SaveData state) {
        switch (state.getParameter()) {
            case "minutes" -> this.minutes = state.getValueInt();
            case "year" -> this.year = state.getValueInt();
            case "month" -> this.month = state.getValueInt();
            case "day" -> this.day = state.getValueInt();
            case "weekday" -> this.weekday = state.getValueInt();
        }
    }

}
