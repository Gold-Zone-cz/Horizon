package cz.goldzone.horizon.enums;

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
}