package com.spacecolony.model;

/**
 * Pilot specialization.
 * Special ability: Evasive Maneuver - doubles resilience for this turn.
 * Bonus: +3 skill on navigation/asteroid missions.
 */
public class Pilot extends CrewMember {

    public Pilot(String name) {
        super(name, Specialization.PILOT, 5, 4, 20);
    }

    /**
     * Special Ability: Evasive Maneuver - Pilot deals bonus damage based on resilience
     */
    @Override
    public int specialAbility(Threat threat) {
        // Evasive Maneuver: Deal damage equal to skill + resilience
        return getEffectiveSkill() + getResilience();
    }

    @Override
    public String getSpecialAbilityName() {
        return "Evasive Maneuver";
    }

    /**
     * Pilots get +3 bonus on asteroid and navigation missions
     */
    @Override
    public int getMissionTypeBonus(String missionType) {
        if (missionType != null &&
                (missionType.toLowerCase().contains("asteroid") ||
                 missionType.toLowerCase().contains("navigation") ||
                 missionType.toLowerCase().contains("storm"))) {
            return 3;
        }
        return 0;
    }
}
