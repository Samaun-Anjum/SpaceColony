package com.spacecolony.ui;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.spacecolony.R;
import com.spacecolony.ui.fragments.ColonyStatsFragment;
import com.spacecolony.ui.fragments.CrewStatsFragment;

/**
 * Statistics Activity using ViewPager2 + TabLayout with two Fragments.
 * Bonus features: Fragments (+2) + Statistics Visualization (+2)
 *
 * Tab 1 — Colony Stats: colony-wide numbers + bar chart
 * Tab 2 — Crew Stats:   per-crew records + XP bar chart
 */
public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Statistics");
        }

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout  = findViewById(R.id.tab_layout);

        // Attach fragment pager adapter
        viewPager.setAdapter(new StatsPagerAdapter(this));

        // Connect tabs to pager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "🌌 Colony" : "👥 Crew");
        }).attach();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }

    // ── ViewPager2 adapter ─────────────────────────────────────────────────
    private static class StatsPagerAdapter extends FragmentStateAdapter {

        StatsPagerAdapter(FragmentActivity fa) { super(fa); }

        @Override
        public int getItemCount() { return 2; }

        @Override
        public Fragment createFragment(int position) {
            return position == 0 ? new ColonyStatsFragment() : new CrewStatsFragment();
        }
    }
}
