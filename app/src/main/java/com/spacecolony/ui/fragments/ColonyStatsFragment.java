package com.spacecolony.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.spacecolony.ColonyApp;
import com.spacecolony.R;
import com.spacecolony.model.Storage;
import com.spacecolony.ui.views.BarChartView;

/**
 * Fragment showing colony-wide statistics with a bar chart visualization.
 * Bonus feature: Fragments + Statistics Visualization
 */
public class ColonyStatsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_colony_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ColonyApp app = ColonyApp.getInstance();
        Storage storage = app.getStorage();

        // Text stats
        TextView tvMissions    = view.findViewById(R.id.tv_col_missions);
        TextView tvWon         = view.findViewById(R.id.tv_col_won);
        TextView tvWinRate     = view.findViewById(R.id.tv_col_winrate);
        TextView tvRecruited   = view.findViewById(R.id.tv_col_recruited);
        TextView tvCurrent     = view.findViewById(R.id.tv_col_current);
        TextView tvTraining    = view.findViewById(R.id.tv_col_training);
        TextView tvDifficulty  = view.findViewById(R.id.tv_col_difficulty);

        tvMissions.setText("Total Missions Launched: " + storage.getTotalMissionsLaunched());
        tvWon.setText("Missions Won: " + storage.getTotalMissionsWon());
        tvWinRate.setText(String.format("Win Rate: %.1f%%", storage.getColonyWinRate()));
        tvRecruited.setText("Total Crew Recruited: " + storage.getTotalCrewRecruited());
        tvCurrent.setText("Current Crew Size: " + storage.getTotalCrewCount());
        tvTraining.setText("Total Training Sessions: " + storage.getTotalTrainingSessions());
        tvDifficulty.setText("Current Threat Level: " + app.getMissionControl().getMissionCount());

        // Bar chart visualization
        BarChartView chart = view.findViewById(R.id.bar_chart_colony);
        chart.setData(
            new String[]{"Launched", "Won", "Lost", "Recruited", "Training"},
            new float[]{
                storage.getTotalMissionsLaunched(),
                storage.getTotalMissionsWon(),
                Math.max(0, storage.getTotalMissionsLaunched() - storage.getTotalMissionsWon()),
                storage.getTotalCrewRecruited(),
                storage.getTotalTrainingSessions()
            },
            new int[]{
                0xFF00E5FF,   // cyan  - launched
                0xFF4CAF50,   // green - won
                0xFFF44336,   // red   - lost
                0xFF9C27B0,   // purple - recruited
                0xFFFFC107    // yellow - training
            }
        );
    }
}
