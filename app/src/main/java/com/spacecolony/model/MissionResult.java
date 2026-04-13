package com.spacecolony.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the complete result of a mission including log entries.
 */
public class MissionResult {

    public enum Outcome {
        VICTORY, DEFEAT
    }

    private final Outcome outcome;
    private final List<String> missionLog;
    private final List<Integer> survivorIds;
    private final List<Integer> casualtyIds;
    private final String threatName;
    private final int roundsCompleted;

    public MissionResult(Outcome outcome, List<String> missionLog,
                         List<Integer> survivorIds, List<Integer> casualtyIds,
                         String threatName, int roundsCompleted) {
        this.outcome = outcome;
        this.missionLog = new ArrayList<>(missionLog);
        this.survivorIds = new ArrayList<>(survivorIds);
        this.casualtyIds = new ArrayList<>(casualtyIds);
        this.threatName = threatName;
        this.roundsCompleted = roundsCompleted;
    }

    public Outcome getOutcome() { return outcome; }
    public List<String> getMissionLog() { return missionLog; }
    public List<Integer> getSurvivorIds() { return survivorIds; }
    public List<Integer> getCasualtyIds() { return casualtyIds; }
    public String getThreatName() { return threatName; }
    public int getRoundsCompleted() { return roundsCompleted; }
    public boolean isVictory() { return outcome == Outcome.VICTORY; }
}
