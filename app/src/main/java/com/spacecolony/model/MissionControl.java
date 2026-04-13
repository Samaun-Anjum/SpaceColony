package com.spacecolony.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * MissionControl – handles cooperative mission logic.
 * Supports squads of 2 or 3 crew members.
 * Implements turn-based combat with tactical choices.
 */
public class MissionControl {

    public enum Action {
        ATTACK, DEFEND, SPECIAL
    }

    private final Storage storage;
    private int missionCount;
    private static final Random random = new Random();

    public MissionControl(Storage storage) {
        this.storage = storage;
        this.missionCount = 0;
    }

    /**
     * Get all crew in Mission Control
     */
    public List<CrewMember> getCrewInMissionControl() {
        return storage.getCrewByLocation(CrewMember.Location.MISSION_CONTROL);
    }

    /**
     * Generate a threat scaled to current mission count
     */
    public Threat generateThreat() {
        return new Threat(missionCount);
    }

    /**
     * Get the current mission count
     */
    public int getMissionCount() {
        return missionCount;
    }

    /**
     * Process a single crew member's turn action against a threat.
     * Returns log lines for this turn.
     *
     * @param crewMember  the acting crew member
     * @param threat      the threat being fought
     * @param action      the chosen action (ATTACK / DEFEND / SPECIAL)
     * @param missionType the mission type for bonus calculation
     * @return list of log lines describing what happened
     */
    public List<String> processCrewTurn(CrewMember crewMember, Threat threat,
                                        Action action, String missionType) {
        List<String> log = new ArrayList<>();

        if (!crewMember.isAlive() || threat.isDefeated()) return log;

        // Apply mission type specialization bonus
        int missionBonus = crewMember.getMissionTypeBonus(missionType);

        // Add randomness
        float randomFactor = 1.0f + (random.nextFloat() * 0.3f); // up to +30%

        int damageDealt = 0;
        String actionDesc = "";

        switch (action) {
            case ATTACK:
                damageDealt = (int)((crewMember.getEffectiveSkill() + missionBonus) * randomFactor);
                actionDesc = "attacks";
                break;
            case DEFEND:
                // Defend: deal reduced damage but absorb next hit better (tracked via energy boost)
                damageDealt = (int)((crewMember.getEffectiveSkill() + missionBonus) * 0.6f * randomFactor);
                // Temporarily increase resilience effect by healing 2 energy
                crewMember.setEnergy(Math.min(crewMember.getMaxEnergy(), crewMember.getEnergy() + 2));
                actionDesc = "defends and strikes";
                break;
            case SPECIAL:
                damageDealt = (int)((crewMember.specialAbility(threat) + missionBonus) * randomFactor);
                actionDesc = "uses " + crewMember.getSpecialAbilityName();
                break;
        }

        int actualDamage = threat.takeDamage(damageDealt);
        log.add(crewMember.getSpecialization().name() + "(" + crewMember.getName() + ") " + actionDesc + " " + threat.getName());

        if (missionBonus > 0) {
            log.add("  ⭐ Specialization bonus: +" + missionBonus);
        }
        log.add("  Damage dealt: " + damageDealt + " - " + threat.getResilience() + " = " + actualDamage);
        log.add("  " + threat.getName() + " energy: " + threat.getEnergy() + "/" + threat.getMaxEnergy());

        if (!threat.isDefeated()) {
            // Threat retaliates
            int threatAttack = threat.getSkill() + random.nextInt(3);
            int actualThreatDamage = Math.max(0, threatAttack - crewMember.getResilience());
            crewMember.setEnergy(crewMember.getEnergy() - actualThreatDamage);

            log.add(threat.getName() + " retaliates against " + crewMember.getSpecialization().name() + "(" + crewMember.getName() + ")");
            log.add("  Damage dealt: " + threatAttack + " - " + crewMember.getResilience() + " = " + actualThreatDamage);
            log.add("  " + crewMember.getSpecialization().name() + "(" + crewMember.getName() + ") energy: "
                    + crewMember.getEnergy() + "/" + crewMember.getMaxEnergy());
        }

        return log;
    }

    /**
     * Finalize a mission – award XP to survivors, handle casualties, update stats.
     *
     * @param participants  all crew in the mission
     * @param victory       whether the mission was won
     * @param noDeath       if true, defeated crew go to Medbay instead of dying
     * @param log           existing log to append to
     * @return MissionResult
     */
    public MissionResult finalizeMission(List<CrewMember> participants, boolean victory,
                                         boolean noDeath, List<String> log, String threatName, int rounds) {
        List<Integer> survivorIds = new ArrayList<>();
        List<Integer> casualtyIds = new ArrayList<>();

        missionCount++;
        storage.recordMissionLaunched();

        if (victory) {
            storage.recordMissionWon();
            log.add("=== MISSION COMPLETE ===");
            log.add("The " + threatName + " has been neutralized!");
            for (CrewMember cm : participants) {
                if (cm.isAlive()) {
                    cm.awardMissionExperience(1);
                    log.add(cm.getSpecialization().name() + "(" + cm.getName() + ") gains 1 experience point. (exp: " + cm.getExperience() + ")");
                    cm.setLocation(CrewMember.Location.MISSION_CONTROL);
                    survivorIds.add(cm.getId());
                } else {
                    if (noDeath) {
                        // No Death feature: send to Medbay
                        cm.recordMissionFailed();
                        cm.resetToInitialStats();
                        cm.setLocation(CrewMember.Location.MEDBAY);
                        log.add(cm.getSpecialization().name() + "(" + cm.getName() + ") is critically wounded and sent to Medbay.");
                        casualtyIds.add(cm.getId());
                    } else {
                        log.add(cm.getSpecialization().name() + "(" + cm.getName() + ") has been lost in the mission.");
                        storage.removeCrewMember(cm.getId());
                        casualtyIds.add(cm.getId());
                    }
                }
            }
        } else {
            log.add("=== MISSION FAILED ===");
            log.add("Mission failed. All crew members lost.");
            for (CrewMember cm : participants) {
                if (noDeath) {
                    cm.recordMissionFailed();
                    cm.resetToInitialStats();
                    cm.setLocation(CrewMember.Location.MEDBAY);
                    log.add(cm.getSpecialization().name() + "(" + cm.getName() + ") is sent to Medbay.");
                    casualtyIds.add(cm.getId());
                } else {
                    storage.removeCrewMember(cm.getId());
                    casualtyIds.add(cm.getId());
                }
            }
        }

        return new MissionResult(
                victory ? MissionResult.Outcome.VICTORY : MissionResult.Outcome.DEFEAT,
                log, survivorIds, casualtyIds, threatName, rounds
        );
    }

    public void setMissionCount(int count) {
        this.missionCount = count;
    }

    /**
     * Move crew member to Quarters from Mission Control
     */
    public void returnToQuarters(CrewMember cm) {
        cm.restoreEnergy();
        cm.setLocation(CrewMember.Location.QUARTERS);
    }
}
