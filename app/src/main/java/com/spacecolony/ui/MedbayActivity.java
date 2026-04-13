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
 * Medbay screen – part of the No Death bonus feature.
 * Crew members who were defeated are sent here instead of dying.
 * They recover with reset stats and return to Quarters.
 */
public class MedbayActivity extends AppCompatActivity {

    private ColonyApp app;
    private CrewMemberAdapter adapter;
    private TextView tvEmptyMessage, tvMedbayInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medbay);
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Medbay");
        }

        app = ColonyApp.getInstance();
        tvEmptyMessage = findViewById(R.id.tv_empty_message);
        tvMedbayInfo   = findViewById(R.id.tv_medbay_info);

        tvMedbayInfo.setText("Defeated crew recover here. Stats are reset to baseline upon discharge.");

        RecyclerView recyclerView = findViewById(R.id.rv_crew);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<CrewMember> inMedbay = app.getStorage()
                .getCrewByLocation(CrewMember.Location.MEDBAY);
        adapter = new CrewMemberAdapter(inMedbay, true);
        recyclerView.setAdapter(adapter);
        updateEmptyMessage(inMedbay);

        Button btnDischarge = findViewById(R.id.btn_discharge);
        btnDischarge.setOnClickListener(v -> dischargeSelected());
    }

    private void dischargeSelected() {
        List<CrewMember> selected = adapter.getSelectedCrew();
        if (selected.isEmpty()) {
            Toast.makeText(this, "Select crew to discharge", Toast.LENGTH_SHORT).show();
            return;
        }
        for (CrewMember cm : selected) {
            // Reset stats and send to Quarters
            cm.resetToInitialStats();
            cm.setLocation(CrewMember.Location.QUARTERS);
        }
        Toast.makeText(this, selected.size() + " crew discharged to Quarters", Toast.LENGTH_SHORT).show();
        refreshList();
        app.saveGame();
    }

    private void refreshList() {
        List<CrewMember> updated = app.getStorage()
                .getCrewByLocation(CrewMember.Location.MEDBAY);
        adapter.updateData(updated);
        updateEmptyMessage(updated);
    }

    private void updateEmptyMessage(List<CrewMember> list) {
        tvEmptyMessage.setVisibility(list.isEmpty()
                ? android.view.View.VISIBLE : android.view.View.GONE);
        tvEmptyMessage.setText("No crew in Medbay. Everyone is healthy!");
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
