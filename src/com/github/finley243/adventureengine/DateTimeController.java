package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.load.SaveData;

import java.util.ArrayList;
import java.util.List;

public class DateTimeController {

    public static final int MINUTES_PER_ROUND = 2;
    public static final boolean USE_24_HOUR_FORMAT = false;

    private int minutes;
    private int day;
    private int month;
    private int year;
    private int weekday;

    public DateTimeController() {}

    public void onNextRound() {
        minutes += MINUTES_PER_ROUND;
        if(minutes >= 1440) {
            minutes = 0;
            day += 1;
            weekday += 1;
            if(weekday > 7) {
                weekday = 1;
            }
            if(day > daysInMonth()) {
                day = 1;
                month += 1;
                if(month > 12) {
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
        if(!crossZero && minutes >= totalMinutes1 && minutes <= totalMinutes2) {
            return true;
        } else return crossZero && (minutes >= totalMinutes1 || minutes <= totalMinutes2);
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

    public int daysInMonth() {
        switch(month) {
            case 0:
            case 2:
            case 4:
            case 6:
            case 7:
            case 9:
            case 11:
                return 31;
            case 3:
            case 5:
            case 8:
            case 10:
                return 30;
            case 1:
                return 28;
            default:
                return -1;
        }
    }

    public String getWeekdayName() {
        switch(weekday) {
            case 1:
                return "monday";
            case 2:
                return "tuesday";
            case 3:
                return "wednesday";
            case 4:
                return "thursday";
            case 5:
                return "friday";
            case 6:
                return "saturday";
            case 7:
                return "sunday";
            default:
                return null;
        }
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
        result.append(" - ").append(getWeekdayName().toUpperCase()).append(", ");
        result.append(month).append("/").append(day).append("/").append(year);
        return result.toString();
    }

    public static int getWeekdayIndex(String day) {
        switch(day.toLowerCase()) {
            case "monday":
                return 1;
            case "tuesday":
                return 2;
            case "wednesday":
                return 3;
            case "thursday":
                return 4;
            case "friday":
                return 5;
            case "saturday":
                return 6;
            case "sunday":
                return 7;
            default:
                return -1;
        }
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
        switch(state.getParameter()) {
            case "minutes":
                this.minutes = state.getValueInt();
                break;
            case "year":
                this.year = state.getValueInt();
                break;
            case "month":
                this.month = state.getValueInt();
                break;
            case "day":
                this.day = state.getValueInt();
                break;
            case "weekday":
                this.weekday = state.getValueInt();
                break;
        }
    }

}
