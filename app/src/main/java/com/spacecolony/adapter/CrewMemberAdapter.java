package com.spacecolony.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spacecolony.R;
import com.spacecolony.model.CrewMember;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Set;

/**
 * RecyclerView adapter for displaying crew members.
 * Supports checkboxes for multi-selection.
 * Bonus feature: RecyclerView + Crew Images
 */
public class CrewMemberAdapter extends RecyclerView.Adapter<CrewMemberAdapter.CrewViewHolder> {

    public interface OnCrewClickListener {
        void onCrewClick(CrewMember crewMember);
    }

    private List<CrewMember> crewList;
    private final boolean showCheckboxes;
    private final Set<Integer> selectedIds = new HashSet<>();
    private OnCrewClickListener clickListener;

    public CrewMemberAdapter(List<CrewMember> crewList, boolean showCheckboxes) {
        this.crewList = new ArrayList<>(crewList);
        this.showCheckboxes = showCheckboxes;
    }

    public void setOnCrewClickListener(OnCrewClickListener listener) {
        this.clickListener = listener;
    }

    public void updateData(List<CrewMember> newList) {
        this.crewList = new ArrayList<>(newList);
        selectedIds.clear();
        notifyDataSetChanged();
    }

    public Set<Integer> getSelectedIds() {
        return selectedIds;
    }

    public List<CrewMember> getSelectedCrew() {
        List<CrewMember> selected = new ArrayList<>();
        for (CrewMember cm : crewList) {
            if (selectedIds.contains(cm.getId())) {
                selected.add(cm);
            }
        }
        return selected;
    }

    public void clearSelection() {
        selectedIds.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crew_member, parent, false);
        return new CrewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CrewViewHolder holder, int position) {
        CrewMember cm = crewList.get(position);
        holder.bind(cm, showCheckboxes, selectedIds.contains(cm.getId()));

        // Handle checkbox toggle
        if (showCheckboxes) {
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(selectedIds.contains(cm.getId()));
            holder.checkBox.setOnCheckedChangeListener((button, isChecked) -> {
                if (isChecked) {
                    selectedIds.add(cm.getId());
                } else {
                    selectedIds.remove(cm.getId());
                }
            });
        }

        holder.itemView.setOnClickListener(v -> {
            if (showCheckboxes) {
                boolean newState = !selectedIds.contains(cm.getId());
                if (newState) selectedIds.add(cm.getId());
                else selectedIds.remove(cm.getId());
                holder.checkBox.setChecked(newState);
            }
            if (clickListener != null) {
                clickListener.onCrewClick(cm);
            }
        });
    }

    @Override
    public int getItemCount() {
        return crewList.size();
    }

    static class CrewViewHolder extends RecyclerView.ViewHolder {
        ImageView crewImage;
        TextView tvName;
        TextView tvSpec;
        TextView tvStats;
        TextView tvLocation;
        ProgressBar energyBar;
        CheckBox checkBox;
        View colorStripe;

        CrewViewHolder(View itemView) {
            super(itemView);
            crewImage = itemView.findViewById(R.id.iv_crew_image);
            tvName = itemView.findViewById(R.id.tv_crew_name);
            tvSpec = itemView.findViewById(R.id.tv_crew_spec);
            tvStats = itemView.findViewById(R.id.tv_crew_stats);
            tvLocation = itemView.findViewById(R.id.tv_crew_location);
            energyBar = itemView.findViewById(R.id.pb_energy);
            checkBox = itemView.findViewById(R.id.cb_select);
            colorStripe = itemView.findViewById(R.id.view_color_stripe);
        }

        void bind(CrewMember cm, boolean showCheckbox, boolean selected) {
            tvName.setText(cm.getName());
            tvSpec.setText(cm.getSpecialization().name());
            tvStats.setText(String.format("SKL:%d  RES:%d  XP:%d",
                    cm.getEffectiveSkill(), cm.getResilience(), cm.getExperience()));
            tvLocation.setText(cm.getLocation().name());

            // Energy bar
            energyBar.setMax(cm.getMaxEnergy());
            energyBar.setProgress(cm.getEnergy());

            // Checkbox visibility
            checkBox.setVisibility(showCheckbox ? View.VISIBLE : View.GONE);
            checkBox.setChecked(selected);

            // Specialization color stripe and image
            int color;
            int imageRes;
            switch (cm.getSpecialization()) {
                case PILOT:
                    color = Color.parseColor("#2196F3"); // Blue
                    imageRes = R.drawable.ic_pilot;
                    break;
                case ENGINEER:
                    color = Color.parseColor("#FFC107"); // Yellow
                    imageRes = R.drawable.ic_engineer;
                    break;
                case MEDIC:
                    color = Color.parseColor("#4CAF50"); // Green
                    imageRes = R.drawable.ic_medic;
                    break;
                case SCIENTIST:
                    color = Color.parseColor("#9C27B0"); // Purple
                    imageRes = R.drawable.ic_scientist;
                    break;
                case SOLDIER:
                    color = Color.parseColor("#F44336"); // Red
                    imageRes = R.drawable.ic_soldier;
                    break;
                default:
                    color = Color.GRAY;
                    imageRes = R.drawable.ic_pilot;
            }
            colorStripe.setBackgroundColor(color);
            crewImage.setImageResource(imageRes);

            // Highlight selected items
            itemView.setAlpha(selected ? 0.85f : 1.0f);
            itemView.setBackgroundColor(selected ?
                    Color.parseColor("#1A2196F3") : Color.TRANSPARENT);
        }
    }
}
