package com.spacecolony.model;

import java.util.List;

/**
 * Simulator – training area where crew members gain experience.
 */
public class Simulator {

    private final Storage storage;

    public Simulator(Storage storage) {
        this.storage = storage;
    }

    /**
     * Train a list of crew members – each gains one experience point.
     * @param crewIds IDs of crew members to train
     * @return training summary string
     */
    public String trainCrew(List<Integer> crewIds) {
        StringBuilder log = new StringBuilder();
        int count = 0;
        for (int id : crewIds) {
            CrewMember cm = storage.getCrewMember(id);
            if (cm != null && cm.getLocation() == CrewMember.Location.SIMULATOR) {
                cm.train();
                log.append(cm.getName()).append(" trained → XP: ").append(cm.getExperience()).append("\n");
                count++;
            }
        }
        storage.recordTrainingSession(count);
        return log.toString();
    }

    /**
     * Get all crew in Simulator
     */
    public List<CrewMember> getCrewInSimulator() {
        return storage.getCrewByLocation(CrewMember.Location.SIMULATOR);
    }

    /**
     * Return a crew member to Quarters (restores energy)
     */
    public void returnToQuarters(CrewMember cm) {
        cm.restoreEnergy();
        cm.setLocation(CrewMember.Location.QUARTERS);
    }
}
