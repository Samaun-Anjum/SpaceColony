package com.spacecolony.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spacecolony.ColonyApp;
import com.spacecolony.R;
import com.spacecolony.adapter.CrewMemberAdapter;
import com.spacecolony.model.CrewMember;

import java.util.List;

/**
 * Mission Control screen - select 2 or 3 crew members and launch cooperative mission.
 * Supports squads of 2-3 (bonus: Larger Squads).
 */
public class MissionControlActivity extends AppCompatActivity {

    public static final String EXTRA_CREW_IDS = "crew_ids";

    private ColonyApp app;
    private CrewMemberAdapter adapter;
    private TextView tvEmptyMessage, tvMissionCounter, tvSelectionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_control);
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mission Control");
        }

        app = ColonyApp.getInstance();
        tvEmptyMessage = findViewById(R.id.tv_empty_message);
        tvMissionCounter = findViewById(R.id.tv_mission_counter);
        tvSelectionInfo = findViewById(R.id.tv_selection_info);

        RecyclerView recyclerView = findViewById(R.id.rv_crew);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<CrewMember> inMission = app.getStorage()
                .getCrewByLocation(CrewMember.Location.MISSION_CONTROL);
        adapter = new CrewMemberAdapter(inMission, true);
        recyclerView.setAdapter(adapter);
        updateEmptyMessage(inMission);

        Button btnLaunch = findViewById(R.id.btn_launch_mission);
        Button btnToQuarters = findViewById(R.id.btn_to_quarters);

        btnLaunch.setOnClickListener(v -> launchMission());
        btnToQuarters.setOnClickListener(v -> returnSelectedToQuarters());

        tvMissionCounter.setText("Missions launched: " + app.getMissionControl().getMissionCount());
        tvSelectionInfo.setText("Select 2 or 3 crew members for a mission");
    }

    private void launchMission() {
        List<CrewMember> selected = adapter.getSelectedCrew();
        if (selected.size() < 2 || selected.size() > 3) {
            Toast.makeText(this, "Select 2 or 3 crew members", Toast.LENGTH_SHORT).show();
            return;
        }

        // Pass selected crew IDs to MissionActivity
        int[] ids = new int[selected.size()];
        for (int i = 0; i < selected.size(); i++) {
            ids[i] = selected.get(i).getId();
        }
        Intent intent = new Intent(this, MissionActivity.class);
        intent.putExtra(EXTRA_CREW_IDS, ids);
        startActivity(intent);
    }

    private void returnSelectedToQuarters() {
        List<CrewMember> selected = adapter.getSelectedCrew();
        if (selected.isEmpty()) {
            Toast.makeText(this, "Select crew to return", Toast.LENGTH_SHORT).show();
            return;
        }
        for (CrewMember cm : selected) {
            app.getMissionControl().returnToQuarters(cm);
        }
        Toast.makeText(this, selected.size() + " crew returned to Quarters (energy restored)", Toast.LENGTH_SHORT).show();
        refreshList();
        app.saveGame();
    }

    private void refreshList() {
        List<CrewMember> updated = app.getStorage()
                .getCrewByLocation(CrewMember.Location.MISSION_CONTROL);
        adapter.updateData(updated);
        updateEmptyMessage(updated);
        tvMissionCounter.setText("Missions launched: " + app.getMissionControl().getMissionCount());
    }

    private void updateEmptyMessage(List<CrewMember> list) {
        tvEmptyMessage.setVisibility(list.isEmpty()
                ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
