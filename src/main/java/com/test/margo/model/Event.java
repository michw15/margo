package com.test.margo.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Event {

    private String id;
    private String type;
    private String host;
    private final Long duration;
    private final boolean alert;

}
