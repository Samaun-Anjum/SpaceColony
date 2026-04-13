package com.spacecolony.model;

/**
 * Engineer specialization.
 * Special ability: System Override - deals extra damage to mechanical threats.
 * Bonus: +2 skill on repair/fuel/heating missions.
 */
public class Engineer extends CrewMember {

    public Engineer(String name) {
        super(name, Specialization.ENGINEER, 6, 3, 19);
    }

    /**
     * Special Ability: System Override - deals 150% normal damage
     */
    @Override
    public int specialAbility(Threat threat) {
        int damage = (int)(getEffectiveSkill() * 1.5);
        return damage;
    }

    @Override
    public String getSpecialAbilityName() {
        return "System Override";
    }

    /**
     * Engineers get +2 bonus on repair/fuel/heating missions
     */
    @Override
    public int getMissionTypeBonus(String missionType) {
        if (missionType != null &&
                (missionType.toLowerCase().contains("repair") ||
                 missionType.toLowerCase().contains("fuel") ||
                 missionType.toLowerCase().contains("heating") ||
                 missionType.toLowerCase().contains("leak") ||
                 missionType.toLowerCase().contains("system"))) {
            return 2;
        }
        return 0;
    }
}
