package com.example.assignment.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.util.TypedValue;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assignment.R;
import com.example.assignment.models.Workout;
import java.util.ArrayList;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_WORKOUT = 1;

    private final List<DisplayItem> items = new ArrayList<>();
    private final OnWorkoutClickListener listener;
    private Context context;
    private boolean groupByCompletion;

    public interface OnWorkoutClickListener {
        void onEditClick(Workout workout);
        void onDeleteClick(Workout workout);
        void onDelegateClick(Workout workout);
        void onCompleteClick(Workout workout, boolean isChecked);
        void onWorkoutClick(Workout workout);
    }

    public WorkoutAdapter(OnWorkoutClickListener listener) {
        this.listener = listener;
    }

    public void setGroupByCompletion(boolean groupByCompletion) {
        this.groupByCompletion = groupByCompletion;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            TextView tv = new TextView(parent.getContext());
            tv.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            int paddingHorizontal = dpToPx(parent.getContext(), 16);
            int paddingTop = dpToPx(parent.getContext(), 12);
            int paddingBottom = dpToPx(parent.getContext(), 4);
            tv.setPadding(paddingHorizontal, paddingTop, paddingHorizontal, paddingBottom);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv.setTextColor(parent.getContext().getResources().getColor(R.color.primary_color, parent.getContext().getTheme()));
            tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
            return new HeaderViewHolder(tv);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DisplayItem item = items.get(position);
        if (item.type == VIEW_TYPE_HEADER) {
            ((HeaderViewHolder) holder).tvHeader.setText(item.headerText);
            return;
        }

        Workout workout = item.workout;
        WorkoutViewHolder workoutHolder = (WorkoutViewHolder) holder;

        workoutHolder.tvWorkoutName.setText(workout.getName());

        String details = workout.getDescription();
        if (details == null || details.isEmpty()) {
            details = "No description";
        }
        workoutHolder.tvWorkoutDetails.setText(details);

        workoutHolder.cbCompleted.setOnCheckedChangeListener(null);
        workoutHolder.cbCompleted.setChecked(workout.isCompleted());
        workoutHolder.tvCompletedBadge.setVisibility(workout.isCompleted() ? View.VISIBLE : View.GONE);

        workoutHolder.btnEdit.setOnClickListener(v -> listener.onEditClick(workout));
        workoutHolder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(workout));
        workoutHolder.btnDelegate.setOnClickListener(v -> listener.onDelegateClick(workout));

        workoutHolder.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed()) return;
            workoutHolder.tvCompletedBadge.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            listener.onCompleteClick(workout, isChecked);
        });

        workoutHolder.cardView.setOnClickListener(v -> listener.onWorkoutClick(workout));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).type;
    }

    public void setWorkouts(List<Workout> workouts) {
        items.clear();

        if (!groupByCompletion) {
            if (workouts != null) {
                for (Workout workout : workouts) {
                    if (workout != null) {
                        items.add(DisplayItem.workout(workout));
                    }
                }
            }
            notifyDataSetChanged();
            return;
        }

        List<Workout> incomplete = new ArrayList<>();
        List<Workout> completed = new ArrayList<>();

        if (workouts != null) {
            for (Workout workout : workouts) {
                if (workout != null && workout.isCompleted()) {
                    completed.add(workout);
                } else if (workout != null) {
                    incomplete.add(workout);
                }
            }
        }

        if (!incomplete.isEmpty()) {
            items.add(DisplayItem.header("⌛ " + contextString(R.string.incomplete_workouts)));
            for (Workout workout : incomplete) {
                items.add(DisplayItem.workout(workout));
            }
        }

        if (!completed.isEmpty()) {
            items.add(DisplayItem.header("✓ " + contextString(R.string.completed_workouts)));
            for (Workout workout : completed) {
                items.add(DisplayItem.workout(workout));
            }
        }

        notifyDataSetChanged();
    }

    public boolean isHeaderAt(int position) {
        return position >= 0 && position < items.size() && items.get(position).type == VIEW_TYPE_HEADER;
    }

    public Workout getWorkoutAt(int position) {
        if (position < 0 || position >= items.size()) return null;
        DisplayItem item = items.get(position);
        if (item.type == VIEW_TYPE_HEADER) return null;
        return item.workout;
    }

    private String contextString(int resId) {
        if (context == null) return "";
        return context.getString(resId);
    }

    private static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
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

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = (TextView) itemView;
        }
    }

    static class DisplayItem {
        final int type;
        final String headerText;
        final Workout workout;

        private DisplayItem(int type, String headerText, Workout workout) {
            this.type = type;
            this.headerText = headerText;
            this.workout = workout;
        }

        static DisplayItem header(String text) {
            return new DisplayItem(VIEW_TYPE_HEADER, text, null);
        }

        static DisplayItem workout(Workout workout) {
            return new DisplayItem(VIEW_TYPE_WORKOUT, null, workout);
        }
    }
}
