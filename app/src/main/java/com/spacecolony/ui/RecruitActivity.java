package com.spacecolony.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.spacecolony.ColonyApp;
import com.spacecolony.R;
import com.spacecolony.model.CrewMember;

/**
 * Screen for recruiting new crew members.
 */
public class RecruitActivity extends AppCompatActivity {

    private EditText etName;
    private RadioGroup rgSpecialization;
    private TextView tvStatPreview;
    private ColonyApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit);
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Recruit Crew Member");
        }

        app = ColonyApp.getInstance();
        etName = findViewById(R.id.et_crew_name);
        rgSpecialization = findViewById(R.id.rg_specialization);
        tvStatPreview = findViewById(R.id.tv_stat_preview);
        Button btnCreate = findViewById(R.id.btn_create);
        Button btnCancel = findViewById(R.id.btn_cancel);

        // Update stat preview when spec selection changes
        rgSpecialization.setOnCheckedChangeListener((group, checkedId) -> updateStatPreview(checkedId));

        // Set initial preview
        updateStatPreview(R.id.rb_pilot);

        btnCreate.setOnClickListener(v -> recruitCrewMember());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void updateStatPreview(int checkedId) {
        String preview;
        if (checkedId == R.id.rb_pilot) {
            preview = "Pilot\nSkill: 5 | Resilience: 4 | Max Energy: 20\nSpecial: Evasive Maneuver\nBonus: +3 on asteroid/navigation missions";
        } else if (checkedId == R.id.rb_engineer) {
            preview = "Engineer\nSkill: 6 | Resilience: 3 | Max Energy: 19\nSpecial: System Override\nBonus: +2 on repair/fuel/heating missions";
        } else if (checkedId == R.id.rb_medic) {
            preview = "Medic\nSkill: 7 | Resilience: 2 | Max Energy: 18\nSpecial: Field Medic (heals self)\nBonus: +2 on radiation/alien missions";
        } else if (checkedId == R.id.rb_scientist) {
            preview = "Scientist\nSkill: 8 | Resilience: 1 | Max Energy: 17\nSpecial: Data Analysis (2x damage)\nBonus: +2 on research/anomaly missions";
        } else if (checkedId == R.id.rb_soldier) {
            preview = "Soldier\nSkill: 9 | Resilience: 0 | Max Energy: 16\nSpecial: Suppressive Fire (double hit)\nBonus: +3 on combat/alien attack missions";
        } else {
            preview = "Select a specialization";
        }
        tvStatPreview.setText(preview);
    }

    private void recruitCrewMember() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            etName.setError("Please enter a name");
            return;
        }
        if (name.length() > 20) {
            etName.setError("Name too long (max 20 chars)");
            return;
        }

        CrewMember.Specialization spec = getSelectedSpec();
        if (spec == null) {
            Toast.makeText(this, "Please select a specialization", Toast.LENGTH_SHORT).show();
            return;
        }

        CrewMember cm = app.getQuarters().createCrewMember(name, spec);
        Toast.makeText(this, cm.getName() + " the " + spec.name() + " has joined the colony!", Toast.LENGTH_LONG).show();
        app.saveGame();
        etName.setText("");
    }

    private CrewMember.Specialization getSelectedSpec() {
        int checkedId = rgSpecialization.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_pilot) return CrewMember.Specialization.PILOT;
        if (checkedId == R.id.rb_engineer) return CrewMember.Specialization.ENGINEER;
        if (checkedId == R.id.rb_medic) return CrewMember.Specialization.MEDIC;
        if (checkedId == R.id.rb_scientist) return CrewMember.Specialization.SCIENTIST;
        if (checkedId == R.id.rb_soldier) return CrewMember.Specialization.SOLDIER;
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
