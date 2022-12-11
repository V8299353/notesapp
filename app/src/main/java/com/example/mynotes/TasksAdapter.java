package com.example.mynotes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mynotes.databinding.LayoutSingleTaskBinding;
import com.example.mynotes.helper.Helper;

import java.util.Objects;


public class TasksAdapter extends ListAdapter<TaskModel, TasksAdapter.VH> {
    private Context context;
    private OnNoteClick onNoteClickA;

    public TasksAdapter(Context context, OnNoteClick onNoteClickA, TaskDiff taskDiff) {
        super(taskDiff);
        this.context = context;
        this.onNoteClickA = onNoteClickA;
    }

    @NonNull
    @Override
    public TasksAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutSingleTaskBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    class VH extends RecyclerView.ViewHolder {
        LayoutSingleTaskBinding binding;

        VH(LayoutSingleTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TasksAdapter.VH holder, @SuppressLint("RecyclerView") int position) {
        LayoutSingleTaskBinding binding = holder.binding;
        binding.title.setText(getItem(position).getTitle());
        binding.desc.setText(getItem(position).getDescription());
        String mapUrl = getItem(position).getMapUrl();
        if (mapUrl != null) {
            binding.mapLink.setText(mapUrl);
            binding.mapIcon.setVisibility(View.VISIBLE);
        } else {
            binding.mapLink.setVisibility(View.GONE);
            binding.mapIcon.setVisibility(View.GONE);
        }

        String imageUrl = getItem(position).getImageUrl();
        if (imageUrl != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions().fitCenter())
                    .error(R.drawable.ic_baseline_account_circle_24)
                    .into(binding.image);
        } else {
            binding.image.setVisibility(View.GONE);
        }
        String created = "Date Created : "+ Helper.getDateString(getItem(position).getCreatedDate());
        binding.dateCreated.setText(created);
        Long updatedDate = getItem(position).getUpdatedDate();
        if (updatedDate!=null){
            String dateModified = "Date Modified : "+ Helper.getDateString(updatedDate);
            binding.dateModified.setText(dateModified);
        }else {
            binding.dateModified.setVisibility(View.GONE);
        }

        binding.getRoot().setOnClickListener(v -> onNoteClickA.onNoteClicked(getItem(position)));
    }

    static class TaskDiff extends DiffUtil.ItemCallback<TaskModel> {

        @Override
        public boolean areItemsTheSame(@NonNull TaskModel oldItem, @NonNull TaskModel newItem) {
            return Objects.equals(oldItem.getUuid(), newItem.getUuid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull TaskModel oldItem, @NonNull TaskModel newItem) {
            return Objects.equals(oldItem.getTitle(), newItem.getTitle()) &&
                    Objects.equals(oldItem.getDescription(), newItem.getDescription()) &&
                    Objects.equals(oldItem.getMapUrl(), newItem.getMapUrl()) &&
                    Objects.equals(oldItem.getImageUrl(), newItem.getImageUrl());
        }
    }
}
