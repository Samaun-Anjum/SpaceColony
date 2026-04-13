package com.spacecolony.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.spacecolony.ColonyApp;
import com.spacecolony.R;
import com.spacecolony.model.CrewMember;
import com.spacecolony.model.MissionControl;
import com.spacecolony.model.MissionResult;
import com.spacecolony.model.Threat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Turn-based tactical mission screen.
 * Each button press = one crew member's full turn (attack + threat retaliation).
 * Round header prints when crew index wraps back to 0.
 */
public class MissionActivity extends AppCompatActivity {

    private ColonyApp app;
    private List<CrewMember> squad;
    private Threat threat;
    private String missionType;

    private int currentCrewIndex = 0;
    private int round = 1;
    private boolean missionOver = false;
    private boolean roundHeaderPrinted = false;

    private final List<String> missionLog = new ArrayList<>();
    private final Random random = new Random();

    // ── Views ──────────────────────────────────────────────────────────────
    private TextView tvMissionTitle, tvRound, tvMissionLog;
    private TextView tvThreatName, tvThreatEnergy;
    private ProgressBar pbThreatEnergy;
    private TextView tvCrewAName, tvCrewAEnergy;
    private ProgressBar pbCrewAEnergy;
    private ImageView ivCrewA;
    private TextView tvCrewBName, tvCrewBEnergy;
    private ProgressBar pbCrewBEnergy;
    private ImageView ivCrewB;
    private View layoutCrewC;
    private TextView tvCrewCName, tvCrewCEnergy;
    private ProgressBar pbCrewCEnergy;
    private ImageView ivCrewC;
    private TextView tvCurrentActor;
    private Button btnAttack, btnDefend, btnSpecial, btnClose;
    private ScrollView scrollLog;

    private final Handler handler = new Handler();

    // ── Lifecycle ──────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        app = ColonyApp.getInstance();

        int[] crewIds = getIntent().getIntArrayExtra(MissionControlActivity.EXTRA_CREW_IDS);
        if (crewIds == null || crewIds.length < 2) {
            Toast.makeText(this, "Error: no crew selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        squad = new ArrayList<>();
        for (int id : crewIds) {
            CrewMember cm = app.getStorage().getCrewMember(id);
            if (cm != null) squad.add(cm);
        }

        threat = app.getMissionControl().generateThreat();
        missionType = threat.getMissionType();

        bindViews();
        setupButtons();
        initMission();
    }

    // ── Setup ──────────────────────────────────────────────────────────────
    private void bindViews() {
        tvMissionTitle = findViewById(R.id.tv_mission_title);
        tvRound        = findViewById(R.id.tv_round);
        tvMissionLog   = findViewById(R.id.tv_mission_log);
        scrollLog      = findViewById(R.id.scroll_mission_log);
        tvThreatName   = findViewById(R.id.tv_threat_name);
        tvThreatEnergy = findViewById(R.id.tv_threat_energy);
        pbThreatEnergy = findViewById(R.id.pb_threat_energy);
        tvCrewAName    = findViewById(R.id.tv_crew_a_name);
        tvCrewAEnergy  = findViewById(R.id.tv_crew_a_energy);
        pbCrewAEnergy  = findViewById(R.id.pb_crew_a_energy);
        ivCrewA        = findViewById(R.id.iv_crew_a);
        tvCrewBName    = findViewById(R.id.tv_crew_b_name);
        tvCrewBEnergy  = findViewById(R.id.tv_crew_b_energy);
        pbCrewBEnergy  = findViewById(R.id.pb_crew_b_energy);
        ivCrewB        = findViewById(R.id.iv_crew_b);
        layoutCrewC    = findViewById(R.id.layout_crew_c);
        tvCrewCName    = findViewById(R.id.tv_crew_c_name);
        tvCrewCEnergy  = findViewById(R.id.tv_crew_c_energy);
        pbCrewCEnergy  = findViewById(R.id.pb_crew_c_energy);
        ivCrewC        = findViewById(R.id.iv_crew_c);
        tvCurrentActor = findViewById(R.id.tv_current_actor);
        btnAttack      = findViewById(R.id.btn_attack);
        btnDefend      = findViewById(R.id.btn_defend);
        btnSpecial     = findViewById(R.id.btn_special);
        btnClose       = findViewById(R.id.btn_close_mission);
    }

    private void setupButtons() {
        btnAttack.setOnClickListener(v -> processTurn(MissionControl.Action.ATTACK));
        btnDefend.setOnClickListener(v -> processTurn(MissionControl.Action.DEFEND));
        btnSpecial.setOnClickListener(v -> processTurn(MissionControl.Action.SPECIAL));
        btnClose.setOnClickListener(v -> finish());
        btnClose.setVisibility(View.GONE);
    }

    private void initMission() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(threat.getName());

        layoutCrewC.setVisibility(squad.size() >= 3 ? View.VISIBLE : View.GONE);

        // Opening log - matches assignment example format exactly
        appendLog("=== MISSION: " + threat.getName() + " ===");
        appendLog("Threat: " + threat.getName()
                + " (skill: " + threat.getSkill()
                + ", resilience: " + threat.getResilience()
                + ", energy: " + threat.getEnergy() + "/" + threat.getMaxEnergy() + ")");
        appendLog("");

        // Crew member lines: "Crew Member A: Pilot(Nova) skill: 5; res: 4; exp: 0; energy: 20/20"
        String[] letters = {"A", "B", "C"};
        for (int i = 0; i < squad.size(); i++) {
            CrewMember cm = squad.get(i);
            appendLog("Crew Member " + letters[i] + ": " + fmtNice(cm)
                    + " skill: " + cm.getEffectiveSkill()
                    + "; res: " + cm.getResilience()
                    + "; exp: " + cm.getExperience()
                    + "; energy: " + cm.getEnergy() + "/" + cm.getMaxEnergy());
        }

        appendLog("");
        appendLog("--- Round 1 ---");
        appendLog("");
        roundHeaderPrinted = true;

        updateHUD();
        refreshActorLabel();
    }

    // ── Core turn logic ────────────────────────────────────────────────────
    private void processTurn(MissionControl.Action action) {
        if (missionOver) return;

        // Skip to next alive crew member
        if (!seekAlive()) { endMission(false); return; }

        CrewMember actor = squad.get(currentCrewIndex);

        // ── 1. Crew acts ──────────────────────────────────────────────────
        // Add blank line before each crew member's turn except the very first
        if (currentCrewIndex > 0 || round > 1) appendLog("");

        int missionBonus = actor.getMissionTypeBonus(missionType);
        int damage;

        switch (action) {
            case DEFEND:
                damage = (int)((actor.getEffectiveSkill() + missionBonus) * 0.6f);
                actor.setEnergy(Math.min(actor.getMaxEnergy(), actor.getEnergy() + 2));
                break;
            case SPECIAL:
                damage = actor.specialAbility(threat) + missionBonus;
                break;
            default: // ATTACK
                damage = actor.getEffectiveSkill() + missionBonus;
                break;
        }

        // Log crew action - all actions use "acts against" to match assignment example
        appendLog(fmtNice(actor) + " acts against " + threat.getName());

        if (missionBonus > 0) {
            appendLog("  ⭐ Specialization bonus: +" + missionBonus);
        }

        int actualDmg = threat.takeDamage(damage);
        appendLog("  Damage dealt: " + damage + " - " + threat.getResilience() + " = " + actualDmg);
        appendLog("  " + threat.getName() + " energy: " + threat.getEnergy() + "/" + threat.getMaxEnergy());

        // ── 2. Check victory BEFORE threat retaliates ─────────────────────
        if (threat.isDefeated()) {
            updateHUD();
            endMission(true);
            return;
        }

        // ── 3. Threat retaliates against this crew member ─────────────────
        int threatAtk = threat.getSkill();
        int actualThreatDmg = Math.max(0, threatAtk - actor.getResilience());
        actor.setEnergy(actor.getEnergy() - actualThreatDmg);
        appendLog(threat.getName() + " retaliates against " + fmtNice(actor));
        appendLog("  Damage dealt: " + threatAtk + " - " + actor.getResilience() + " = " + actualThreatDmg);
        appendLog("  " + fmtNice(actor) + " energy: " + actor.getEnergy() + "/" + actor.getMaxEnergy());

        updateHUD();

        // ── 4. Check if actor just died ───────────────────────────────────
        if (!actor.isAlive()) {
            appendLog(fmtNice(actor) + " has been defeated.");
        }

        // ── 5. Check if ALL crew dead ─────────────────────────────────────
        boolean allDead = true;
        for (CrewMember cm : squad) { if (cm.isAlive()) { allDead = false; break; } }
        if (allDead) { endMission(false); return; }

        // ── 6. Advance to next crew member ────────────────────────────────
        currentCrewIndex++;

        // Wrap: if we've gone past the last member, new round starts
        if (currentCrewIndex >= squad.size()) {
            currentCrewIndex = 0;
            round++;
            appendLog("");
            appendLog("--- Round " + round + " ---");
            appendLog("");
            tvRound.setText("Round " + round);
        }

        // Skip dead members at start of new position
        if (!seekAlive()) { endMission(false); return; }

        refreshActorLabel();
    }

    /**
     * Advance currentCrewIndex forward until we find an alive member.
     * Returns false if everyone is dead.
     */
    private boolean seekAlive() {
        for (int i = 0; i < squad.size(); i++) {
            int idx = (currentCrewIndex + i) % squad.size();
            if (squad.get(idx).isAlive()) {
                currentCrewIndex = idx;
                return true;
            }
        }
        return false;
    }

    // ── End mission ────────────────────────────────────────────────────────
    private void endMission(boolean victory) {
        missionOver = true;
        setButtonsEnabled(false);
        btnAttack.setVisibility(View.GONE);
        btnDefend.setVisibility(View.GONE);
        btnSpecial.setVisibility(View.GONE);
        btnClose.setVisibility(View.VISIBLE);

        MissionResult result = app.getMissionControl().finalizeMission(
                squad, victory, app.isNoDeathMode(), missionLog, threat.getName(), round);

        // Append summary lines - matches assignment example format exactly
        appendLog("=== MISSION COMPLETE ===");
        if (victory) {
            appendLog("The " + threat.getName() + " has been neutralized!");
            for (CrewMember cm : squad) {
                if (cm.isAlive()) {
                    appendLog(fmtNice(cm) + " gains 1 experience point. (exp: " + cm.getExperience() + ")");
                }
            }
        } else {
            appendLog("Mission failed. All crew members lost.");
        }

        app.saveGame();

        tvMissionTitle.setTextColor(victory
                ? Color.parseColor("#4CAF50") : Color.parseColor("#F44336"));
        tvMissionTitle.setText(victory ? "✅ MISSION COMPLETE!" : "❌ MISSION FAILED");
        tvCurrentActor.setText(victory ? "Victory! Crew gained experience." : "All crew defeated.");

        handler.postDelayed(() -> showResultDialog(result), 600);
    }

    private void showResultDialog(MissionResult result) {
        if (isFinishing()) return;
        String msg = result.isVictory()
                ? "The " + threat.getName() + " has been neutralized!\n\nSurviving crew gained +1 XP."
                : "Mission failed. All crew lost.";
        new AlertDialog.Builder(this)
                .setTitle(result.isVictory() ? "🏆 Victory!" : "💀 Mission Failed")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .setCancelable(false)
                .show();
    }

    // ── UI helpers ─────────────────────────────────────────────────────────
    private void refreshActorLabel() {
        if (missionOver) return;
        CrewMember actor = squad.get(currentCrewIndex);
        tvCurrentActor.setText("▶ " + fmtNice(actor) + " — Choose action:");
        btnSpecial.setText("⚡ " + actor.getSpecialAbilityName());

        for (int i = 0; i < squad.size(); i++) {
            float alpha = (i == currentCrewIndex) ? 1.0f : 0.45f;
            switch (i) {
                case 0: tvCrewAName.setAlpha(alpha); pbCrewAEnergy.setAlpha(alpha); break;
                case 1: tvCrewBName.setAlpha(alpha); pbCrewBEnergy.setAlpha(alpha); break;
                case 2: tvCrewCName.setAlpha(alpha); pbCrewCEnergy.setAlpha(alpha); break;
            }
        }
    }

    private void updateHUD() {
        // Threat bar
        tvThreatName.setText(threat.getName());
        tvThreatEnergy.setText(threat.getEnergy() + "/" + threat.getMaxEnergy());
        pbThreatEnergy.setMax(threat.getMaxEnergy());
        pbThreatEnergy.setProgress(threat.getEnergy());

        // Crew panels
        updateCrewPanel(0, tvCrewAName, tvCrewAEnergy, pbCrewAEnergy, ivCrewA);
        updateCrewPanel(1, tvCrewBName, tvCrewBEnergy, pbCrewBEnergy, ivCrewB);
        if (squad.size() >= 3)
            updateCrewPanel(2, tvCrewCName, tvCrewCEnergy, pbCrewCEnergy, ivCrewC);

        tvRound.setText("Round " + round);
        scrollLog.post(() -> scrollLog.fullScroll(View.FOCUS_DOWN));
    }

    private void updateCrewPanel(int idx, TextView nameView, TextView energyView,
                                 ProgressBar bar, ImageView img) {
        if (idx >= squad.size()) return;
        CrewMember cm = squad.get(idx);
        nameView.setText(cm.getSpecialization().name() + "\n" + cm.getName());
        energyView.setText(cm.getEnergy() + "/" + cm.getMaxEnergy());
        bar.setMax(cm.getMaxEnergy());
        bar.setProgress(cm.getEnergy());
        img.setImageResource(iconFor(cm));
        nameView.setTextColor(cm.isAlive() ? Color.WHITE : Color.RED);
        img.setAlpha(cm.isAlive() ? 1.0f : 0.3f);
    }

    private int iconFor(CrewMember cm) {
        switch (cm.getSpecialization()) {
            case PILOT:     return R.drawable.ic_pilot;
            case ENGINEER:  return R.drawable.ic_engineer;
            case MEDIC:     return R.drawable.ic_medic;
            case SCIENTIST: return R.drawable.ic_scientist;
            case SOLDIER:   return R.drawable.ic_soldier;
            default:        return R.drawable.ic_pilot;
        }
    }

    private void appendLog(String line) {
        missionLog.add(line);
        String cur = tvMissionLog.getText().toString();
        tvMissionLog.setText(cur.isEmpty() ? line : cur + "\n" + line);
        scrollLog.post(() -> scrollLog.fullScroll(View.FOCUS_DOWN));
    }

    private void setButtonsEnabled(boolean on) {
        btnAttack.setEnabled(on);
        btnDefend.setEnabled(on);
        btnSpecial.setEnabled(on);
    }

    /**
     * Original fmt - keeps PILOT(Nova) format, used internally
     */
    private String fmt(CrewMember cm) {
        return cm.getSpecialization().name() + "(" + cm.getName() + ")";
    }

    /**
     * Nice format - converts PILOT to Pilot for assignment-style log output
     * e.g. Pilot(Nova), Soldier(Rex), Medic(Zara)
     */
    private String fmtNice(CrewMember cm) {
        String spec = cm.getSpecialization().name();
        spec = spec.charAt(0) + spec.substring(1).toLowerCase();
        return spec + "(" + cm.getName() + ")";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (missionOver) { finish(); return true; }
            new AlertDialog.Builder(this)
                    .setTitle("Abandon Mission?")
                    .setMessage("Crew will remain in Mission Control.")
                    .setPositiveButton("Leave", (d, w) -> finish())
                    .setNegativeButton("Stay", null).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
