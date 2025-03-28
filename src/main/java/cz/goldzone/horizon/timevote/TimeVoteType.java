package cz.goldzone.horizon.timevote;

import lombok.Getter;

@Getter
public enum TimeVoteType {
    DAY("DAY", 1000),
    NIGHT("NIGHT", 13000);

    private final String name;
    private final int time;

    TimeVoteType(String name, int time) {
        this.name = name;
        this.time = time;
    }

    public static boolean contains(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }

        try {
            TimeVoteType.valueOf(s.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}