package com.example.mynotes;

import java.io.Serializable;

public class TaskModel implements Serializable {
    String uuid = null;
    String title = null;
    String description = null;
    String mapUrl = null;
    String imageUrl = null;
    Long createdDate = null;
    Long updatedDate = null;

    public TaskModel(String uuid, String title, String description, String mapUrl, String imageUrl, Long createdDate, Long updatedDate) {
        this.uuid = uuid;
        this.title = title;
        this.description = description;
        this.mapUrl = mapUrl;
        this.imageUrl = imageUrl;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public TaskModel() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Long updatedDate) {
        this.updatedDate = updatedDate;
    }
}
