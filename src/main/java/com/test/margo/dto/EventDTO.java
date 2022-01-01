package com.test.margo.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.test.margo.model.EventState;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventDTO {

    private final String id;
    private final EventState state;
    private final String type;
    private final String host;
    private final Long timestamp;

    @JsonCreator
    public EventDTO(@JsonProperty(value = "id", required = true) String id, @JsonProperty(value = "state", required = true) EventState state, @JsonProperty("type") String type,
                    @JsonProperty("host") String host, @JsonProperty(value = "timestamp", required = true) Long timestamp) {
        this.id = id;
        this.state = state;
        this.type = type;
        this.host = host;
        this.timestamp = timestamp;
    }
}
