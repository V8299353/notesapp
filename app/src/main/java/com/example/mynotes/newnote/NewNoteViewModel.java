package com.example.mynotes.newnote;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mynotes.TaskModel;
import com.google.android.gms.maps.model.LatLng;

import javax.annotation.Nullable;

public class NewNoteViewModel extends ViewModel {

    private MutableLiveData<LatLng> latLng = new MutableLiveData<LatLng>();

    @Nullable
    private TaskModel editTaskModel = null;

    private boolean isNewTask = true;


    void setLatLng(LatLng latLng) {
        this.latLng.postValue(latLng);
    }

    LiveData<LatLng> getLatLng() {
        return latLng;
    }

    void updateTaskModel(TaskModel taskModel) {
        editTaskModel = taskModel;
    }

    TaskModel getEditTaskMode() {
        return editTaskModel;
    }

    void updateIsNewTask(Boolean isNewTask) {
        this.isNewTask = isNewTask;
    }

    boolean isNewTask() {
        return isNewTask;
    }
}
