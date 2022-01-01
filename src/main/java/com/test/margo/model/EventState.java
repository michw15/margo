package com.test.margo.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EventState {

    STARTED,
    FINISHED
}
