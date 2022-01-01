package com.test.margo.model;

import com.test.margo.dto.EventDTO;
import com.test.margo.helper.EventHelper;
import org.junit.Test;

import static com.test.margo.model.EventState.FINISHED;
import static com.test.margo.model.EventState.STARTED;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

public class EventHelperTest {

    private static final String APPLICATION_LOG = "APPLICATION_LOG";
    private static final String ID = "123a";
    private static final String HOST_1 = "HOST1";
    private final EventHelper eventHelper = new EventHelper();

    @Test
    public void testDTOToEventWithoutAlert() {
        EventDTO start = new EventDTO(ID, STARTED, APPLICATION_LOG, HOST_1, 123L);
        EventDTO finish = new EventDTO(ID, FINISHED, APPLICATION_LOG, HOST_1, 124L);

        Event event = eventHelper.dtoToEvent(start, finish);
        assertFalse("Event should not be returned as alert", event.isAlert());
    }

    @Test
    public void testDTOToEventWithAlert() {
        EventDTO start = new EventDTO(ID, STARTED, APPLICATION_LOG, HOST_1, 123L);
        EventDTO finish = new EventDTO(ID, FINISHED, APPLICATION_LOG, HOST_1, 128L);

        Event event = eventHelper.dtoToEvent(start, finish);
        assertTrue("Event should be returned as alert", event.isAlert());
    }
}
