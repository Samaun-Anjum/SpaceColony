package com.spacecolony.ui;

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
 * Quarters screen - shows crew at home, allows moving them to Simulator or Mission Control.
 */
public class QuartersActivity extends AppCompatActivity {

    private ColonyApp app;
    private CrewMemberAdapter adapter;
    private TextView tvEmptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarters);
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quarters");
        }

        app = ColonyApp.getInstance();
        tvEmptyMessage = findViewById(R.id.tv_empty_message);

        RecyclerView recyclerView = findViewById(R.id.rv_crew);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<CrewMember> crewInQuarters = app.getStorage()
                .getCrewByLocation(CrewMember.Location.QUARTERS);
        adapter = new CrewMemberAdapter(crewInQuarters, true);
        recyclerView.setAdapter(adapter);

        Button btnToSimulator = findViewById(R.id.btn_to_simulator);
        Button btnToMission = findViewById(R.id.btn_to_mission);

        btnToSimulator.setOnClickListener(v -> moveSelected(CrewMember.Location.SIMULATOR));
        btnToMission.setOnClickListener(v -> moveSelected(CrewMember.Location.MISSION_CONTROL));

        updateEmptyMessage(crewInQuarters);
    }

    private void moveSelected(CrewMember.Location destination) {
        List<CrewMember> selected = adapter.getSelectedCrew();
        if (selected.isEmpty()) {
            Toast.makeText(this, "Select at least one crew member", Toast.LENGTH_SHORT).show();
            return;
        }
        for (CrewMember cm : selected) {
            cm.setLocation(destination);
        }
        Toast.makeText(this, selected.size() + " crew moved to "
                + destination.name().replace("_", " "), Toast.LENGTH_SHORT).show();
        refreshList();
        app.saveGame();
    }

    private void refreshList() {
        List<CrewMember> updated = app.getStorage()
                .getCrewByLocation(CrewMember.Location.QUARTERS);
        adapter.updateData(updated);
        updateEmptyMessage(updated);
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
