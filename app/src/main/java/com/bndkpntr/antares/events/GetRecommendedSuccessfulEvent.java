package com.bndkpntr.antares.events;

import com.bndkpntr.antares.model.ActivitiesContent;

import java.util.List;

public class GetRecommendedSuccessfulEvent {
    private List<ActivitiesContent> contents;

    public GetRecommendedSuccessfulEvent(List<ActivitiesContent> contents) {
        this.contents = contents;
    }

    public List<ActivitiesContent> getContents() {
        return contents;
    }
}
