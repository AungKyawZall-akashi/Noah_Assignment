package com.example.assignment.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assignment.R;
import com.example.assignment.models.Exercise;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private List<Exercise> exercises;
    private OnExerciseClickListener listener;
    private boolean showActions;

    public interface OnExerciseClickListener {
        void onEditExercise(Exercise exercise, int position);
        void onDeleteExercise(Exercise exercise, int position);
    }

    public ExerciseAdapter(List<Exercise> exercises, OnExerciseClickListener listener) {
        this(exercises, listener, true);
    }

    public ExerciseAdapter(List<Exercise> exercises, OnExerciseClickListener listener, boolean showActions) {
        this.exercises = exercises;
        this.listener = listener;
        this.showActions = showActions;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);

        holder.tvExerciseName.setText(exercise.getName());
        holder.tvSetsReps.setText(String.format("%s sets × %s reps",
                exercise.getSets(), exercise.getReps()));

        if (exercise.getEquipment() != null && !exercise.getEquipment().isEmpty()) {
            holder.tvEquipment.setVisibility(View.VISIBLE);
            holder.tvEquipment.setText(String.format("Equipment: %s", exercise.getEquipment()));
        } else {
            holder.tvEquipment.setVisibility(View.GONE);
        }

        if (exercise.getInstructions() != null && !exercise.getInstructions().isEmpty()) {
            holder.tvInstructions.setVisibility(View.VISIBLE);
            holder.tvInstructions.setText(String.format("Instructions: %s", exercise.getInstructions()));
        } else {
            holder.tvInstructions.setVisibility(View.GONE);
        }

        if (showActions && listener != null) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> listener.onEditExercise(exercise, position));
            holder.btnDelete.setOnClickListener(v -> listener.onDeleteExercise(exercise, position));
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnEdit.setOnClickListener(null);
            holder.btnDelete.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvExerciseName, tvSetsReps, tvEquipment, tvInstructions;
        Button btnEdit, btnDelete;

        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvExerciseName = itemView.findViewById(R.id.tvExerciseName);
            tvSetsReps = itemView.findViewById(R.id.tvSetsReps);
            tvEquipment = itemView.findViewById(R.id.tvEquipment);
            tvInstructions = itemView.findViewById(R.id.tvInstructions);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
