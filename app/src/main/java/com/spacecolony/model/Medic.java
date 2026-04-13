package com.spacecolony.model;

/**
 * Medic specialization.
 * Special ability: Field Medic - heals self or ally (used here as self-heal + attack).
 * Bonus: +2 skill on disease/radiation missions.
 */
public class Medic extends CrewMember {

    public Medic(String name) {
        super(name, Specialization.MEDIC, 7, 2, 18);
    }

    /**
     * Special Ability: Field Medic - restores 3 energy and deals normal damage
     */
    @Override
    public int specialAbility(Threat threat) {
        // Restore 3 energy to self
        int healed = Math.min(3, getMaxEnergy() - getEnergy());
        setEnergy(getEnergy() + healed);
        // Still deal normal damage
        return getEffectiveSkill();
    }

    @Override
    public String getSpecialAbilityName() {
        return "Field Medic";
    }

    /**
     * Medics get +2 bonus on disease/radiation/alien missions
     */
    @Override
    public int getMissionTypeBonus(String missionType) {
        if (missionType != null &&
                (missionType.toLowerCase().contains("disease") ||
                 missionType.toLowerCase().contains("radiation") ||
                 missionType.toLowerCase().contains("alien") ||
                 missionType.toLowerCase().contains("solar") ||
                 missionType.toLowerCase().contains("flare"))) {
            return 2;
        }
        return 0;
    }
}
