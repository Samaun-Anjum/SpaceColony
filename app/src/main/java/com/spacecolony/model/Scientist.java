package com.spacecolony.model;

/**
 * Scientist specialization.
 * Special ability: Data Analysis - finds weakness, deals double damage.
 * Bonus: +2 skill on research/alien/solar missions.
 */
public class Scientist extends CrewMember {

    public Scientist(String name) {
        super(name, Specialization.SCIENTIST, 8, 1, 17);
    }

    /**
     * Special Ability: Data Analysis - doubles damage output
     */
    @Override
    public int specialAbility(Threat threat) {
        return getEffectiveSkill() * 2;
    }

    @Override
    public String getSpecialAbilityName() {
        return "Data Analysis";
    }

    /**
     * Scientists get +2 on research/anomaly missions
     */
    @Override
    public int getMissionTypeBonus(String missionType) {
        if (missionType != null &&
                (missionType.toLowerCase().contains("research") ||
                 missionType.toLowerCase().contains("anomaly") ||
                 missionType.toLowerCase().contains("alien") ||
                 missionType.toLowerCase().contains("fire"))) {
            return 2;
        }
        return 0;
    }
}
