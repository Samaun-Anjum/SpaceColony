package com.spacecolony.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Data container for saved colony state.
 */
public class SaveData {

    public int missionCount;
    public int idCounter;
    public int totalMissions;
    public int totalWon;
    public int totalRecruited;
    public int totalTraining;
    public List<CrewData> crewDataList = new ArrayList<>();

    public static class CrewData {
        public int id;
        public String name;
        public String spec;
        public int experience;
        public int energy;
        public int maxEnergy;
        public String location;
        public int missions;
        public int wins;
        public int training;
    }
}
