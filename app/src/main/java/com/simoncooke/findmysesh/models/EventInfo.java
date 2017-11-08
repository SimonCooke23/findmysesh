package com.simoncooke.findmysesh.models;

/**
 * Created by simon on 30/10/2017.
 */

public class EventInfo {
    public String eventLocation,
            eventDate,
            eventTime,
            eventName,
            eventDescription,
            creatorUid,
            creatorName,
            eventPictureUrl,
            eventCoordinates;

    public EventInfo() {

    }

    public EventInfo(String eventLocation, String eventDate, String eventTime, String eventName, String eventDescription, String creatorUid, String creatorName, String eventPictureUrl, String eventCoordinates) {
        this.eventLocation = eventLocation;
        this.eventTime = eventTime;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.creatorUid = creatorUid;
        this.creatorName = creatorName;
        this.eventPictureUrl = eventPictureUrl;
        this.eventCoordinates = eventCoordinates;
        this.eventDate = eventDate;
    }

    //Getters
    public String getEventName() {
        return eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public String getEVentTime() {
        return eventTime;
    }

    public String getCreatorUid() {
        return creatorUid;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getEventPictureUrl() {
        return eventPictureUrl;
    }

    public String getEventCoordinates() {
        return eventCoordinates;
    }

    public String getEventDate(){
        return eventDate;
    }
}