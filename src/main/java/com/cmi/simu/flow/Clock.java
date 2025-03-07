package com.cmi.simu.flow;

import lombok.Getter;

public class Clock {
    @Getter
    private static int time = 0;

    @Getter
    private final static Clock clock = new Clock();

    public static void addOneHour() {
        time = (time+1) % 24;
    }

}
