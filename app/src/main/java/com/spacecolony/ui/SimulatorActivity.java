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

import java.util.ArrayList;
import java.util.List;

/**
 * Simulator screen - train crew to gain experience points.
 */
public class SimulatorActivity extends AppCompatActivity {

    private ColonyApp app;
    private CrewMemberAdapter adapter;
    private TextView tvEmptyMessage;
    private TextView tvTrainingLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator);
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Simulator");
        }

        app = ColonyApp.getInstance();
        tvEmptyMessage = findViewById(R.id.tv_empty_message);
        tvTrainingLog = findViewById(R.id.tv_training_log);

        RecyclerView recyclerView = findViewById(R.id.rv_crew);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<CrewMember> inSimulator = app.getStorage()
                .getCrewByLocation(CrewMember.Location.SIMULATOR);
        adapter = new CrewMemberAdapter(inSimulator, true);
        recyclerView.setAdapter(adapter);
        updateEmptyMessage(inSimulator);

        Button btnTrain = findViewById(R.id.btn_train);
        Button btnToQuarters = findViewById(R.id.btn_to_quarters);
        Button btnSelectAll = findViewById(R.id.btn_select_all);

        btnTrain.setOnClickListener(v -> trainSelected());
        btnToQuarters.setOnClickListener(v -> returnToQuarters());
        btnSelectAll.setOnClickListener(v -> selectAll());
    }

    private void trainSelected() {
        List<CrewMember> selected = adapter.getSelectedCrew();
        if (selected.isEmpty()) {
            Toast.makeText(this, "Select crew members to train", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Integer> ids = new ArrayList<>();
        for (CrewMember cm : selected) ids.add(cm.getId());

        String log = app.getSimulator().trainCrew(ids);
        tvTrainingLog.setText(log.isEmpty() ? "No training completed." : log);
        refreshList();
        app.saveGame();
        Toast.makeText(this, selected.size() + " crew member(s) trained!", Toast.LENGTH_SHORT).show();
    }

    private void returnToQuarters() {
        List<CrewMember> selected = adapter.getSelectedCrew();
        if (selected.isEmpty()) {
            Toast.makeText(this, "Select crew to return to Quarters", Toast.LENGTH_SHORT).show();
            return;
        }
        for (CrewMember cm : selected) {
            app.getSimulator().returnToQuarters(cm);
        }
        Toast.makeText(this, selected.size() + " crew returned to Quarters (energy restored)", Toast.LENGTH_SHORT).show();
        refreshList();
        app.saveGame();
    }

    private void selectAll() {
        List<CrewMember> inSim = app.getStorage().getCrewByLocation(CrewMember.Location.SIMULATOR);
        for (CrewMember cm : inSim) {
            adapter.getSelectedIds().add(cm.getId());
        }
        adapter.notifyDataSetChanged();
        Toast.makeText(this, inSim.size() + " crew selected", Toast.LENGTH_SHORT).show();
    }

    private void refreshList() {
        List<CrewMember> updated = app.getStorage()
                .getCrewByLocation(CrewMember.Location.SIMULATOR);
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
