package com.spacecolony;

import android.app.Application;

import com.spacecolony.model.CrewMember;
import com.spacecolony.model.MissionControl;
import com.spacecolony.model.Quarters;
import com.spacecolony.model.Simulator;
import com.spacecolony.model.Storage;
import com.spacecolony.util.DataManager;
import com.spacecolony.util.SaveData;

/**
 * Application class – holds global singleton instances of game objects.
 */
public class ColonyApp extends Application {

    private static ColonyApp instance;

    private Storage storage;
    private Quarters quarters;
    private Simulator simulator;
    private MissionControl missionControl;
    private DataManager dataManager;

    // Settings
    private boolean noDeathMode = true; // bonus: No Death feature enabled by default

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        dataManager = new DataManager(this);
        initializeGame();
    }

    /**
     * Initialize or restore game state
     */
    public void initializeGame() {
        storage = new Storage();
        quarters = new Quarters(storage);
        simulator = new Simulator(storage);
        missionControl = new MissionControl(storage);

        // Try to load saved state
        if (dataManager.hasSaveFile()) {
            SaveData save = dataManager.loadColony();
            if (save != null) {
                restoreFromSave(save);
            }
        }
    }

    private void restoreFromSave(SaveData save) {
        CrewMember.setIdCounter(save.idCounter);
        missionControl.setMissionCount(save.missionCount);
        storage.setTotalMissionsLaunched(save.totalMissions);
        storage.setTotalMissionsWon(save.totalWon);
        storage.setTotalCrewRecruited(save.totalRecruited);
        storage.setTotalTrainingSessions(save.totalTraining);

        for (SaveData.CrewData cd : save.crewDataList) {
            CrewMember cm = quarters.createCrewMemberNoCount(cd.name,
                    CrewMember.Specialization.valueOf(cd.spec));
            cm.setExperience(cd.experience);
            cm.setEnergy(cd.energy);
            cm.setLocation(CrewMember.Location.valueOf(cd.location));
        }
    }

    public void saveGame() {
        dataManager.saveColony(storage, missionControl.getMissionCount());
    }

    public static ColonyApp getInstance() { return instance; }
    public Storage getStorage() { return storage; }
    public Quarters getQuarters() { return quarters; }
    public Simulator getSimulator() { return simulator; }
    public MissionControl getMissionControl() { return missionControl; }
    public DataManager getDataManager() { return dataManager; }
    public boolean isNoDeathMode() { return noDeathMode; }
    public void setNoDeathMode(boolean noDeathMode) { this.noDeathMode = noDeathMode; }
}
