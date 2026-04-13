package com.spacecolony.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spacecolony.ColonyApp;
import com.spacecolony.R;
import com.spacecolony.adapter.CrewMemberAdapter;
import com.spacecolony.model.CrewMember;
import com.spacecolony.ui.views.BarChartView;

import java.util.List;

/**
 * Fragment showing per-crew statistics with a bar chart of XP levels.
 * Bonus feature: Fragments + Statistics Visualization
 */
public class CrewStatsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crew_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ColonyApp app = ColonyApp.getInstance();
        List<CrewMember> allCrew = app.getStorage().listCrewMembers();

        // RecyclerView of crew
        RecyclerView rv = view.findViewById(R.id.rv_crew_stats);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new CrewMemberAdapter(allCrew, false));

        // Detailed text records
        TextView tvDetails = view.findViewById(R.id.tv_crew_stat_details);
        StringBuilder sb = new StringBuilder();
        if (allCrew.isEmpty()) {
            sb.append("No crew yet. Recruit some!");
        } else {
            for (CrewMember cm : allCrew) {
                sb.append("━━━━━━━━━━━━━━━━\n");
                sb.append(cm.getSpecialization().name()).append(" — ").append(cm.getName()).append("\n");
                sb.append("  Effective Skill : ").append(cm.getEffectiveSkill())
                  .append(" (").append(cm.getSkill()).append(" + ").append(cm.getExperience()).append(" XP)\n");
                sb.append("  Resilience      : ").append(cm.getResilience()).append("\n");
                sb.append("  Energy          : ").append(cm.getEnergy()).append("/").append(cm.getMaxEnergy()).append("\n");
                sb.append("  Location        : ").append(cm.getLocation().name()).append("\n");
                sb.append("  Missions        : ").append(cm.getMissionsCompleted())
                  .append("  Wins: ").append(cm.getMissionsWon()).append("\n");
                sb.append("  Win Rate        : ").append(String.format("%.0f%%", cm.getWinRate())).append("\n");
                sb.append("  Training Sessions: ").append(cm.getTrainingSessions()).append("\n");
            }
        }
        tvDetails.setText(sb.toString());

        // XP bar chart — one bar per crew member
        BarChartView chart = view.findViewById(R.id.bar_chart_crew);
        if (!allCrew.isEmpty()) {
            String[] labels = new String[allCrew.size()];
            float[]  values = new float[allCrew.size()];
            int[]    colors = new int[allCrew.size()];
            int[] specColors = {0xFF2196F3, 0xFFFFC107, 0xFF4CAF50, 0xFF9C27B0, 0xFFF44336};

            for (int i = 0; i < allCrew.size(); i++) {
                CrewMember cm = allCrew.get(i);
                labels[i] = cm.getName();
                values[i] = cm.getExperience();
                colors[i] = specColors[cm.getSpecialization().ordinal()];
            }
            chart.setData(labels, values, colors);
            chart.setChartTitle("XP per Crew Member");
        }
    }
}
