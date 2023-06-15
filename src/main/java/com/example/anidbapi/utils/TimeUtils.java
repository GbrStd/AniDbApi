package com.example.anidbapi.utils;

import org.springframework.data.util.Pair;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    /**
     * @param str format: 1h 30m 20s
     */
    public static Duration durationFrom(String str) {
        // h m s
        Pair<Character, TimeUnit>[] pairs = new Pair[]
                {
                        Pair.of('h', TimeUnit.HOURS),
                        Pair.of('m', TimeUnit.MINUTES),
                        Pair.of('s', TimeUnit.SECONDS)
                };
        final String[] split = str.trim().split(" ");
        Duration duration = Duration.ZERO;
        for (String s : split) {
            TimeUnit unit = null;
            for (Pair<Character, TimeUnit> pair : pairs) {
                final int index = s.indexOf(pair.getFirst());
                if (index > 0) {
                    s = s.substring(0, index);
                    unit = pair.getSecond();
                    break;
                }
            }
            if (unit != null)
                duration = duration.plus(Long.parseLong(s), unit.toChronoUnit());
        }
        return duration;
    }

}
