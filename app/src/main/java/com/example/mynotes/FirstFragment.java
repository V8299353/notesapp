package com.example.mynotes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mynotes.databinding.FragmentFirstBinding;
import com.example.mynotes.firestore.FirestoreHelper;
import com.example.mynotes.newnote.NewNoteActivity;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class FirstFragment extends Fragment implements OnNoteClick {

    private Context context;

    @Override
    public void onNoteClicked(@NonNull TaskModel taskModel) {

    }

    private FragmentFirstBinding binding;

    private TasksAdapter tasksAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tasksAdapter = new TasksAdapter(requireActivity(), this,new TasksAdapter.TaskDiff());
        binding.homeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.homeRecyclerView.setAdapter(tasksAdapter);
        fetchData();
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), NewNoteActivity.class);
                intent.putExtra("IsNewTask",true);
                startActivity(intent);
            }
        });
    }

    private void fetchData(){
        if (context!=null) {
            new FirestoreHelper().getAllNotes().addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (context!=null) {
                        if (value != null) {
                            tasksAdapter.submitList(value.toObjects(TaskModel.class));
                        } else {
                                Toast.makeText(context, "Error Fetching Documents", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context =context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }
}

interface OnNoteClick {
    void onNoteClicked(TaskModel taskModel);
}