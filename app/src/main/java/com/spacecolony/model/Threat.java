package com.spacecolony.model;

import java.util.Random;

/**
 * Represents a system-generated threat in Mission Control.
 * Threat difficulty scales with mission count.
 */
public class Threat {

    // Possible threat types with associated mission type keys
    public static final String[][] THREAT_TYPES = {
        {"Asteroid Storm", "asteroid"},
        {"Alien Invasion", "alien"},
        {"Fuel Leakage", "fuel"},
        {"Solar Flare", "solar"},
        {"Fire in the Kitchen", "fire"},
        {"Broken Heating System", "heating"},
        {"Hull Breach", "repair"},
        {"Space Radiation", "radiation"},
        {"Rogue AI", "system"},
        {"Meteor Shower", "asteroid"}
    };

    private String name;
    private String missionType;
    private int skill;
    private int resilience;
    private int energy;
    private int maxEnergy;

    private static final Random random = new Random();

    /**
     * Create a threat scaled to mission number
     * @param missionCount number of missions completed (for scaling)
     */
    public Threat(int missionCount) {
        // Pick a random threat type
        String[] type = THREAT_TYPES[random.nextInt(THREAT_TYPES.length)];
        this.name = type[0];
        this.missionType = type[1];

        // Scale difficulty with missions completed
        this.skill = 4 + missionCount;
        this.resilience = 1 + (missionCount / 3);
        this.maxEnergy = 20 + (missionCount * 3);
        this.energy = this.maxEnergy;
    }

    /**
     * Threat attacks a crew member - returns damage dealt
     */
    public int attack(CrewMember target) {
        // Add small randomness (+0 to +2)
        int randomBonus = random.nextInt(3);
        int damage = Math.max(1, skill + randomBonus - target.getResilience());
        target.defend(skill + randomBonus);
        return damage;
    }

    /**
     * Raw attack value (before target defense) - for mission log display
     */
    public int rawAttackValue() {
        return skill + random.nextInt(3);
    }

    /**
     * Check if threat is defeated
     */
    public boolean isDefeated() {
        return energy <= 0;
    }

    /**
     * Threat takes damage
     * @return actual damage taken
     */
    public int takeDamage(int incomingDamage) {
        int actualDamage = Math.max(0, incomingDamage - resilience);
        energy = Math.max(0, energy - actualDamage);
        return actualDamage;
    }

    // Getters
    public String getName() { return name; }
    public String getMissionType() { return missionType; }
    public int getSkill() { return skill; }
    public int getResilience() { return resilience; }
    public int getEnergy() { return energy; }
    public int getMaxEnergy() { return maxEnergy; }

    @Override
    public String toString() {
        return name + " (skill:" + skill + " res:" + resilience + " energy:" + energy + "/" + maxEnergy + ")";
    }
}
