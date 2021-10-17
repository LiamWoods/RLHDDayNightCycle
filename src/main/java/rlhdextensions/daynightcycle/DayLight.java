package rlhdextensions.daynightcycle;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalTime;

@Slf4j
public enum DayLight {
    DAY(LocalTime.of(6, 0)),
    NIGHT(LocalTime.of(20, 0));

    public static final float CYCLE_LENGTH = 14;

    public LocalTime startTime;

    DayLight(LocalTime startTime) {
        this.startTime = startTime;
    }

    public static DayLight getTimeOfDay(LocalTime currentTime) {
        return currentTime.isAfter(DAY.startTime) && currentTime.isBefore(NIGHT.startTime) ? DAY : NIGHT;
    }

    public float percentageOfDaylight(LocalTime currentTime) {
        return Math.abs(Duration.between(currentTime, startTime).toMillis()) / (CYCLE_LENGTH * 60 * 60 * 1000);
    }
}
