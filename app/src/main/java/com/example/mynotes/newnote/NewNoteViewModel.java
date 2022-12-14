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

    private LatLng oldLatLng = null;

    void setOldLatLng(LatLng latLng) {
        this.oldLatLng = latLng;
    }

    LatLng getOldLatLng() {
        return  oldLatLng;
    }

    void setLatLngFromUrl(String url) {
        String[] latLng = url.replace("https://www.google.com/maps/place/","").split(",");
        setOldLatLng(new LatLng(Double.parseDouble(latLng[0]),Double.parseDouble(latLng[1])));
    }

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
