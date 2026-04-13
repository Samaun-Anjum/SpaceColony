package com.spacecolony.model;

import java.util.List;

/**
 * Quarters – the home location for crew members.
 * Handles creation and energy restoration.
 */
public class Quarters {

    private final Storage storage;

    public Quarters(Storage storage) {
        this.storage = storage;
    }

    /**
     * Create and recruit a new crew member based on specialization.
     * Newly created members start in QUARTERS. Increments totalRecruited.
     */
    public CrewMember createCrewMember(String name, CrewMember.Specialization spec) {
        CrewMember cm = buildCrewMember(name, spec);
        cm.setLocation(CrewMember.Location.QUARTERS);
        storage.addCrewMember(cm);
        return cm;
    }

    /**
     * Restore a crew member from save data without incrementing the recruitment counter.
     */
    public CrewMember createCrewMemberNoCount(String name, CrewMember.Specialization spec) {
        CrewMember cm = buildCrewMember(name, spec);
        cm.setLocation(CrewMember.Location.QUARTERS);
        storage.getCrewMembersMap().put(cm.getId(), cm);
        return cm;
    }

    /** Shared factory: instantiate the correct subclass. */
    private CrewMember buildCrewMember(String name, CrewMember.Specialization spec) {
        switch (spec) {
            case PILOT:     return new Pilot(name);
            case ENGINEER:  return new Engineer(name);
            case MEDIC:     return new Medic(name);
            case SCIENTIST: return new Scientist(name);
            case SOLDIER:   return new Soldier(name);
            default:        return new Pilot(name);
        }
    }

    /** Restore energy of a crew member returning to Quarters. Experience is retained. */
    public void restoreEnergy(CrewMember cm) {
        cm.restoreEnergy();
        cm.setLocation(CrewMember.Location.QUARTERS);
    }

    public List<CrewMember> getCrewInQuarters() {
        return storage.getCrewByLocation(CrewMember.Location.QUARTERS);
    }

    public void moveToSimulator(CrewMember cm) {
        cm.setLocation(CrewMember.Location.SIMULATOR);
    }

    public void moveToMissionControl(CrewMember cm) {
        cm.setLocation(CrewMember.Location.MISSION_CONTROL);
    }
}
