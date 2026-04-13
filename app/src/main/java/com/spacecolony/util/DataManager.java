package com.spacecolony.util;

import android.content.Context;
import android.util.Log;

import com.spacecolony.model.CrewMember;
import com.spacecolony.model.Storage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Handles saving and loading colony data to/from internal storage.
 * Bonus feature: Data Storage & Loading
 */
public class DataManager {

    private static final String TAG = "DataManager";
    private static final String FILE_NAME = "space_colony_save.json";
    private static final String KEY_CREW = "crew";
    private static final String KEY_MISSION_COUNT = "missionCount";
    private static final String KEY_ID_COUNTER = "idCounter";
    private static final String KEY_TOTAL_MISSIONS = "totalMissions";
    private static final String KEY_TOTAL_WON = "totalWon";
    private static final String KEY_TOTAL_RECRUITED = "totalRecruited";
    private static final String KEY_TOTAL_TRAINING = "totalTraining";

    // Crew member keys
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SPEC = "spec";
    private static final String KEY_SKILL = "skill";
    private static final String KEY_RESILIENCE = "resilience";
    private static final String KEY_EXPERIENCE = "experience";
    private static final String KEY_MAX_ENERGY = "maxEnergy";
    private static final String KEY_ENERGY = "energy";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_MISSIONS = "missions";
    private static final String KEY_WINS = "wins";
    private static final String KEY_TRAINING = "training";

    private final Context context;

    public DataManager(Context context) {
        this.context = context;
    }

    /**
     * Save colony state to JSON file
     */
    public boolean saveColony(Storage storage, int missionCount) {
        try {
            JSONObject root = new JSONObject();
            root.put(KEY_MISSION_COUNT, missionCount);
            root.put(KEY_ID_COUNTER, CrewMember.getIdCounter());
            root.put(KEY_TOTAL_MISSIONS, storage.getTotalMissionsLaunched());
            root.put(KEY_TOTAL_WON, storage.getTotalMissionsWon());
            root.put(KEY_TOTAL_RECRUITED, storage.getTotalCrewRecruited());
            root.put(KEY_TOTAL_TRAINING, storage.getTotalTrainingSessions());

            JSONArray crewArray = new JSONArray();
            List<CrewMember> allCrew = storage.listCrewMembers();
            for (CrewMember cm : allCrew) {
                JSONObject crewObj = new JSONObject();
                crewObj.put(KEY_ID, cm.getId());
                crewObj.put(KEY_NAME, cm.getName());
                crewObj.put(KEY_SPEC, cm.getSpecialization().name());
                crewObj.put(KEY_SKILL, cm.getSkill());
                crewObj.put(KEY_RESILIENCE, cm.getResilience());
                crewObj.put(KEY_EXPERIENCE, cm.getExperience());
                crewObj.put(KEY_MAX_ENERGY, cm.getMaxEnergy());
                crewObj.put(KEY_ENERGY, cm.getEnergy());
                crewObj.put(KEY_LOCATION, cm.getLocation().name());
                crewObj.put(KEY_MISSIONS, cm.getMissionsCompleted());
                crewObj.put(KEY_WINS, cm.getMissionsWon());
                crewObj.put(KEY_TRAINING, cm.getTrainingSessions());
                crewArray.put(crewObj);
            }
            root.put(KEY_CREW, crewArray);

            String json = root.toString(2);
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Save failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Load colony state from JSON file
     * @return SaveData object or null if no save exists
     */
    public SaveData loadColony() {
        try {
            FileInputStream fis = context.openFileInput(FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            JSONObject root = new JSONObject(sb.toString());
            SaveData saveData = new SaveData();
            saveData.missionCount = root.optInt(KEY_MISSION_COUNT, 0);
            saveData.idCounter = root.optInt(KEY_ID_COUNTER, 0);
            saveData.totalMissions = root.optInt(KEY_TOTAL_MISSIONS, 0);
            saveData.totalWon = root.optInt(KEY_TOTAL_WON, 0);
            saveData.totalRecruited = root.optInt(KEY_TOTAL_RECRUITED, 0);
            saveData.totalTraining = root.optInt(KEY_TOTAL_TRAINING, 0);

            JSONArray crewArray = root.optJSONArray(KEY_CREW);
            if (crewArray != null) {
                for (int i = 0; i < crewArray.length(); i++) {
                    JSONObject obj = crewArray.getJSONObject(i);
                    SaveData.CrewData cd = new SaveData.CrewData();
                    cd.id = obj.getInt(KEY_ID);
                    cd.name = obj.getString(KEY_NAME);
                    cd.spec = obj.getString(KEY_SPEC);
                    cd.experience = obj.optInt(KEY_EXPERIENCE, 0);
                    cd.energy = obj.optInt(KEY_ENERGY, 20);
                    cd.maxEnergy = obj.optInt(KEY_MAX_ENERGY, 20);
                    cd.location = obj.optString(KEY_LOCATION, "QUARTERS");
                    cd.missions = obj.optInt(KEY_MISSIONS, 0);
                    cd.wins = obj.optInt(KEY_WINS, 0);
                    cd.training = obj.optInt(KEY_TRAINING, 0);
                    saveData.crewDataList.add(cd);
                }
            }
            return saveData;
        } catch (Exception e) {
            Log.d(TAG, "No save file found or load failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Check if a save file exists
     */
    public boolean hasSaveFile() {
        String[] files = context.fileList();
        for (String f : files) {
            if (f.equals(FILE_NAME)) return true;
        }
        return false;
    }

    /**
     * Delete save file
     */
    public boolean deleteSave() {
        return context.deleteFile(FILE_NAME);
    }
}
