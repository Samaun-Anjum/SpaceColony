package com.spacecolony.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Central storage for all crew members.
 * Uses HashMap<Integer, CrewMember> for efficient ID-based access.
 * Also tracks global colony statistics.
 */
public class Storage implements Serializable {

    private static final String COLONY_NAME = "Alpha Colony";

    // Primary data structure: HashMap<ID, CrewMember>
    private final HashMap<Integer, CrewMember> crewMembers;

    // Global statistics
    private int totalMissionsLaunched;
    private int totalMissionsWon;
    private int totalCrewRecruited;
    private int totalTrainingSessions;

    public Storage() {
        this.crewMembers = new HashMap<>();
        this.totalMissionsLaunched = 0;
        this.totalMissionsWon = 0;
        this.totalCrewRecruited = 0;
        this.totalTrainingSessions = 0;
    }

    /**
     * Add a new crew member to storage
     */
    public void addCrewMember(CrewMember cm) {
        crewMembers.put(cm.getId(), cm);
        totalCrewRecruited++;
    }

    /**
     * Get crew member by ID
     */
    public CrewMember getCrewMember(int id) {
        return crewMembers.get(id);
    }

    /**
     * Remove crew member from storage (death)
     */
    public void removeCrewMember(int id) {
        crewMembers.remove(id);
    }

    /**
     * List all crew members
     */
    public List<CrewMember> listCrewMembers() {
        return new ArrayList<>(crewMembers.values());
    }

    /**
     * List crew members by location
     */
    public List<CrewMember> getCrewByLocation(CrewMember.Location location) {
        List<CrewMember> result = new ArrayList<>();
        for (CrewMember cm : crewMembers.values()) {
            if (cm.getLocation() == location) {
                result.add(cm);
            }
        }
        return result;
    }

    /**
     * Get total crew count
     */
    public int getTotalCrewCount() {
        return crewMembers.size();
    }

    /**
     * Record a mission launched
     */
    public void recordMissionLaunched() {
        totalMissionsLaunched++;
    }

    /**
     * Record a mission won
     */
    public void recordMissionWon() {
        totalMissionsWon++;
    }

    /**
     * Record training sessions
     */
    public void recordTrainingSession(int count) {
        totalTrainingSessions += count;
    }

    // Getters for statistics
    public String getColonyName() { return COLONY_NAME; }
    public int getTotalMissionsLaunched() { return totalMissionsLaunched; }
    public int getTotalMissionsWon() { return totalMissionsWon; }
    public int getTotalCrewRecruited() { return totalCrewRecruited; }
    public int getTotalTrainingSessions() { return totalTrainingSessions; }
    public float getColonyWinRate() {
        if (totalMissionsLaunched == 0) return 0f;
        return (float) totalMissionsWon / totalMissionsLaunched * 100f;
    }

    public HashMap<Integer, CrewMember> getCrewMembersMap() {
        return crewMembers;
    }

    // Setters for save/load restoration
    public void setTotalMissionsLaunched(int v) { this.totalMissionsLaunched = v; }
    public void setTotalMissionsWon(int v)      { this.totalMissionsWon = v; }
    public void setTotalCrewRecruited(int v)    { this.totalCrewRecruited = v; }
    public void setTotalTrainingSessions(int v) { this.totalTrainingSessions = v; }
}
