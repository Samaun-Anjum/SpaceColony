package com.spacecolony.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.spacecolony.ColonyApp;
import com.spacecolony.R;
import com.spacecolony.model.CrewMember;
import com.spacecolony.model.Storage;

/**
 * Main home screen showing colony overview and navigation.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ColonyApp app;
    private TextView tvQuartersCount, tvSimulatorCount, tvMissionCount, tvMedbayCount;
    private TextView tvTotalCrew, tvMissionsWon, tvWinRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) setSupportActionBar(toolbar);
        } catch (Exception e) {
            Log.e(TAG, "Toolbar error: " + e.getMessage());
        }

        app = ColonyApp.getInstance();
        if (app == null) {
            Toast.makeText(this, "App init error, restarting...", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Bind views
        tvQuartersCount = findViewById(R.id.tv_quarters_count);
        tvSimulatorCount = findViewById(R.id.tv_simulator_count);
        tvMissionCount   = findViewById(R.id.tv_mission_count);
        tvMedbayCount    = findViewById(R.id.tv_medbay_count);
        tvTotalCrew      = findViewById(R.id.tv_total_crew);
        tvMissionsWon    = findViewById(R.id.tv_missions_won);
        tvWinRate        = findViewById(R.id.tv_win_rate);

        // Navigation cards
        CardView cardQuarters = findViewById(R.id.card_quarters);
        CardView cardSimulator = findViewById(R.id.card_simulator);
        CardView cardMission   = findViewById(R.id.card_mission);
        CardView cardRecruit   = findViewById(R.id.card_recruit);
        CardView cardStats     = findViewById(R.id.card_stats);
        CardView cardMedbay    = findViewById(R.id.card_medbay);

        if (cardQuarters != null) cardQuarters.setOnClickListener(v -> startActivity(new Intent(this, QuartersActivity.class)));
        if (cardSimulator != null) cardSimulator.setOnClickListener(v -> startActivity(new Intent(this, SimulatorActivity.class)));
        if (cardMission   != null) cardMission.setOnClickListener(v -> startActivity(new Intent(this, MissionControlActivity.class)));
        if (cardRecruit   != null) cardRecruit.setOnClickListener(v -> startActivity(new Intent(this, RecruitActivity.class)));
        if (cardStats     != null) cardStats.setOnClickListener(v -> startActivity(new Intent(this, StatisticsActivity.class)));
        if (cardMedbay    != null) cardMedbay.setOnClickListener(v -> startActivity(new Intent(this, MedbayActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (app != null) {
            updateUI();
            app.saveGame();
        }
    }

    private void updateUI() {
        try {
            Storage storage = app.getStorage();
            tvQuartersCount.setText(String.valueOf(storage.getCrewByLocation(CrewMember.Location.QUARTERS).size()));
            tvSimulatorCount.setText(String.valueOf(storage.getCrewByLocation(CrewMember.Location.SIMULATOR).size()));
            tvMissionCount.setText(String.valueOf(storage.getCrewByLocation(CrewMember.Location.MISSION_CONTROL).size()));
            tvMedbayCount.setText(String.valueOf(storage.getCrewByLocation(CrewMember.Location.MEDBAY).size()));
            tvTotalCrew.setText("Total Crew: " + storage.getTotalCrewCount());
            tvMissionsWon.setText("Missions Won: " + storage.getTotalMissionsWon() + "/" + storage.getTotalMissionsLaunched());
            tvWinRate.setText(String.format("Win Rate: %.0f%%", storage.getColonyWinRate()));
        } catch (Exception e) {
            Log.e(TAG, "updateUI error: " + e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            app.saveGame();
            Toast.makeText(this, "Colony saved!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_settings) {
            showSettingsDialog();
            return true;
        } else if (id == R.id.action_new_game) {
            showNewGameConfirmation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSettingsDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Settings")
            .setMessage("No Death Mode: Defeated crew go to Medbay instead of dying.")
            .setPositiveButton("Enable No Death", (d, w) -> {
                app.setNoDeathMode(true);
                Toast.makeText(this, "No Death Mode ON", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Permadeath", (d, w) -> {
                app.setNoDeathMode(false);
                Toast.makeText(this, "Permadeath Mode ON", Toast.LENGTH_SHORT).show();
            })
            .setNeutralButton("Cancel", null)
            .show();
    }

    private void showNewGameConfirmation() {
        new AlertDialog.Builder(this)
            .setTitle("New Game")
            .setMessage("Start a new colony? All progress will be lost.")
            .setPositiveButton("Start Fresh", (d, w) -> {
                app.getDataManager().deleteSave();
                app.initializeGame();
                updateUI();
                Toast.makeText(this, "New colony started!", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
