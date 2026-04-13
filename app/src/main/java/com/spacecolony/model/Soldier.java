package com.spacecolony.model;

/**
 * Soldier specialization.
 * Special ability: Suppressive Fire - attacks twice but with reduced damage.
 * Bonus: +3 skill on combat/alien attack missions.
 */
public class Soldier extends CrewMember {

    public Soldier(String name) {
        super(name, Specialization.SOLDIER, 9, 0, 16);
    }

    /**
     * Special Ability: Suppressive Fire - attacks twice (1.75x total)
     */
    @Override
    public int specialAbility(Threat threat) {
        // Two hits at reduced power
        int hit1 = (int)(getEffectiveSkill() * 0.75);
        int hit2 = getEffectiveSkill();
        return hit1 + hit2;
    }

    @Override
    public String getSpecialAbilityName() {
        return "Suppressive Fire";
    }

    /**
     * Soldiers get +3 on combat/alien/attack missions
     */
    @Override
    public int getMissionTypeBonus(String missionType) {
        if (missionType != null &&
                (missionType.toLowerCase().contains("attack") ||
                 missionType.toLowerCase().contains("combat") ||
                 missionType.toLowerCase().contains("alien") ||
                 missionType.toLowerCase().contains("invasion"))) {
            return 3;
        }
        return 0;
    }
}
