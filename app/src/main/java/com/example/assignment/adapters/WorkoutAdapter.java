package com.example.assignment.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assignment.R;
import com.example.assignment.models.Workout;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private List<Workout> workouts;
    private OnWorkoutClickListener listener;

    public interface OnWorkoutClickListener {
        void onEditClick(Workout workout);
        void onDeleteClick(Workout workout);
        void onDelegateClick(Workout workout);
        void onCompleteClick(Workout workout, boolean isChecked);
        void onWorkoutClick(Workout workout);
    }

    public WorkoutAdapter(List<Workout> workouts, OnWorkoutClickListener listener) {
        this.workouts = workouts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workouts.get(position);

        holder.tvWorkoutName.setText(workout.getName());

        // Show description or exercise count
        String details = workout.getDescription();
        if (details == null || details.isEmpty()) {
            details = "No description";
        }
        holder.tvWorkoutDetails.setText(details);

        holder.cbCompleted.setOnCheckedChangeListener(null);
        holder.cbCompleted.setChecked(workout.isCompleted());
        holder.tvCompletedBadge.setVisibility(workout.isCompleted() ? View.VISIBLE : View.GONE);

        // Set click listeners
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(workout));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(workout));
        holder.btnDelegate.setOnClickListener(v -> listener.onDelegateClick(workout));

        holder.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed()) return;
            holder.tvCompletedBadge.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            listener.onCompleteClick(workout, isChecked);
        });

        holder.cardView.setOnClickListener(v -> listener.onWorkoutClick(workout));
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvWorkoutName, tvWorkoutDetails, tvCompletedBadge;
        Button btnEdit, btnDelete, btnDelegate;
        CheckBox cbCompleted;

        WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvWorkoutName = itemView.findViewById(R.id.tvWorkoutName);
            tvWorkoutDetails = itemView.findViewById(R.id.tvWorkoutDetails);
            tvCompletedBadge = itemView.findViewById(R.id.tvCompletedBadge);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDelegate = itemView.findViewById(R.id.btnDelegate);
            cbCompleted = itemView.findViewById(R.id.cbCompleted);
        }
    }
}
