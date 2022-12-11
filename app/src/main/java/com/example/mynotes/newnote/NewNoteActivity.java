package com.example.mynotes.newnote;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.mynotes.R;
import com.example.mynotes.TaskModel;

public class NewNoteActivity extends AppCompatActivity {

    private NewNoteViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        viewModel = new ViewModelProvider(this).get(NewNoteViewModel.class);

        TaskModel taskModel;
        boolean isNewTask = getIntent().getBooleanExtra("IsNewTask",true);

        if (!isNewTask){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                taskModel = getIntent().getSerializableExtra("TaskModel",TaskModel.class);
            } else {
                taskModel = (TaskModel) getIntent().getSerializableExtra("TaskModel");
            }
            viewModel.updateTaskModel(taskModel);
            viewModel.updateIsNewTask(false);
        }else{
            viewModel.updateIsNewTask(true);
        }

    }
}
