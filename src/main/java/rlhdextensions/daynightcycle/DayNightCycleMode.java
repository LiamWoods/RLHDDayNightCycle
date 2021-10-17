package rlhdextensions.daynightcycle;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DayNightCycleMode
{
    REALTIME("Real-time"),
    CUSTOM("Custom");

    private final String name;

    @Override
    public String toString()
    {
        return name;
    }
}
