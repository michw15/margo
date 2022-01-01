package com.test.margo.helper;

import com.test.margo.dto.EventDTO;
import com.test.margo.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventHelper {

    public Event dtoToEvent(EventDTO startEvent, EventDTO finishEvent) {
        long duration = finishEvent.getTimestamp() - startEvent.getTimestamp();
        boolean isAlert = duration > 4;
        return new Event(startEvent.getId(), startEvent.getType(), startEvent.getHost(), duration, isAlert);
    }
}
