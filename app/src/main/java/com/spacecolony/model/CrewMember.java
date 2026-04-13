package com.spacecolony.model;

import java.io.Serializable;

/**
 * Abstract base class for all crew member specializations.
 * Implements core OOP principles: encapsulation, inheritance, polymorphism.
 */
public abstract class CrewMember implements Serializable {

    // Enum for crew member locations
    public enum Location {
        QUARTERS, SIMULATOR, MISSION_CONTROL, MEDBAY
    }

    // Enum for crew member specializations
    public enum Specialization {
        PILOT, ENGINEER, MEDIC, SCIENTIST, SOLDIER
    }

    // Static ID counter for unique IDs
    private static int idCounter = 0;

    // Core fields
    private final int id;
    private String name;
    private final Specialization specialization;
    private int skill;
    private int resilience;
    private int experience;
    private int energy;
    private int maxEnergy;
    private Location location;

    // Statistics
    private int missionsCompleted;
    private int missionsWon;
    private int trainingSessions;
    private int totalDamageDealt;

    /**
     * Constructor for CrewMember
     */
    public CrewMember(String name, Specialization specialization, int skill, int resilience, int maxEnergy) {
        this.id = ++idCounter;
        this.name = name;
        this.specialization = specialization;
        this.skill = skill;
        this.resilience = resilience;
        this.experience = 0;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
        this.location = Location.QUARTERS;
        this.missionsCompleted = 0;
        this.missionsWon = 0;
        this.trainingSessions = 0;
        this.totalDamageDealt = 0;
    }

    /**
     * Calculates the effective skill (base skill + experience)
     */
    public int getEffectiveSkill() {
        return skill + experience;
    }

    /**
     * Perform an action (attack) - returns damage dealt
     * Subclasses can override for special abilities
     */
    public int act() {
        int damage = getEffectiveSkill();
        totalDamageDealt += damage;
        return damage;
    }

    /**
     * Defend against incoming damage - returns remaining energy
     */
    public int defend(int incomingDamage) {
        int actualDamage = Math.max(0, incomingDamage - resilience);
        energy = Math.max(0, energy - actualDamage);
        return energy;
    }

    /**
     * Perform a special ability - overridden by subclasses
     */
    public abstract int specialAbility(Threat threat);

    /**
     * Get description of the special ability
     */
    public abstract String getSpecialAbilityName();

    /**
     * Get the mission type bonus (for specialization bonuses)
     */
    public abstract int getMissionTypeBonus(String missionType);

    /**
     * Train the crew member - gains experience
     */
    public void train() {
        experience++;
        trainingSessions++;
    }

    /**
     * Restore energy to maximum (when returning to Quarters)
     */
    public void restoreEnergy() {
        energy = maxEnergy;
    }

    /**
     * Reset to initial stats (for Medbay - No Death bonus)
     */
    public void resetToInitialStats() {
        experience = 0;
        energy = maxEnergy;
    }

    /**
     * Check if crew member is alive
     */
    public boolean isAlive() {
        return energy > 0;
    }

    /**
     * Award experience points after a successful mission
     */
    public void awardMissionExperience(int points) {
        experience += points;
        missionsWon++;
        missionsCompleted++;
    }

    /**
     * Record a failed mission
     */
    public void recordMissionFailed() {
        missionsCompleted++;
    }

    // ---- Getters and Setters ----

    public int getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Specialization getSpecialization() { return specialization; }

    public int getSkill() { return skill; }

    public int getResilience() { return resilience; }

    public int getExperience() { return experience; }

    public void setExperience(int experience) { this.experience = experience; }

    public int getEnergy() { return energy; }

    public void setEnergy(int energy) { this.energy = Math.max(0, Math.min(maxEnergy, energy)); }

    public int getMaxEnergy() { return maxEnergy; }

    public Location getLocation() { return location; }

    public void setLocation(Location location) { this.location = location; }

    public int getMissionsCompleted() { return missionsCompleted; }

    public int getMissionsWon() { return missionsWon; }

    public int getTrainingSessions() { return trainingSessions; }

    public int getTotalDamageDealt() { return totalDamageDealt; }

    public float getWinRate() {
        if (missionsCompleted == 0) return 0f;
        return (float) missionsWon / missionsCompleted * 100f;
    }

    public static void setIdCounter(int counter) { idCounter = counter; }

    public static int getIdCounter() { return idCounter; }

    @Override
    public String toString() {
        return specialization.name() + "(" + name + ") skill:" + getEffectiveSkill()
                + " res:" + resilience + " exp:" + experience + " energy:" + energy + "/" + maxEnergy;
    }
}
