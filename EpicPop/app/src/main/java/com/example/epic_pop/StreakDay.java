package com.example.epic_pop;

public class StreakDay {
    public enum Type { NONE, ON_TIME, CATCH_UP }
    public int dayOfMonth; // 1..31 or 0 for placeholder
    public long dayStartMillis; // start of day in millis
    public boolean inCurrentMonth;
    public Type type;

    public StreakDay(int dayOfMonth, long dayStartMillis, boolean inCurrentMonth, Type type) {
        this.dayOfMonth = dayOfMonth;
        this.dayStartMillis = dayStartMillis;
        this.inCurrentMonth = inCurrentMonth;
        this.type = type;
    }
}
