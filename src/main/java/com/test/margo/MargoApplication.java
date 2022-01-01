package com.test.margo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.margo.dao.EventDAO;
import com.test.margo.dto.EventDTO;
import com.test.margo.helper.EventHelper;
import com.test.margo.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.test.margo.model.EventState.STARTED;

@SpringBootApplication
@Slf4j
public class MargoApplication implements CommandLineRunner {

	private final ObjectMapper objectMapper;
	private final Connection connection;
	private final EventHelper eventHelper;

	private final Map<String, EventDTO> startedMap = new ConcurrentHashMap<>();
	private final Map<String, EventDTO> finishedMap = new ConcurrentHashMap<>();

	@Autowired
	public MargoApplication(ObjectMapper objectMapper, Connection connection, EventHelper eventHelper) {
		this.objectMapper = objectMapper;
		this.connection = connection;
		this.eventHelper = eventHelper;
	}

	public static void main(String[] args) {
		SpringApplication.run(MargoApplication.class, args);
	}

	@Override
	public void run(String... args) throws IOException {
		if (args.length != 1 || args[0].isEmpty()) {
			throw new InvalidParameterException("Please provide a single log filePath argument");
		}

		String filePath = args[0];

		log.info("Open processing file {}", filePath);
		try(BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
			bufferedReader.lines().forEach(this::groupByState);
			processData(startedMap.keySet());

			log.info("Finished processing file {}", filePath);
		} catch (IOException e) {
			log.error("Failure at reading file", e);
			throw e;
		}
	}


	private void groupByState(String json) {
		try {
			log.info("Convert JSON to DTO");
			EventDTO eventDTO = Optional.ofNullable(objectMapper.readValue(json, EventDTO.class))
					.orElseThrow(() -> new NullPointerException("Failed to convert JSON to DTO"));

			log.info("Create STARTED and FINISHED events map");
			if (eventDTO.getState().equals(STARTED)) {
				startedMap.put(eventDTO.getId(), eventDTO);
			} else {
				finishedMap.put(eventDTO.getId(), eventDTO);
			}
		} catch (IOException e) {
			log.error("Failure processing JSON {}", json);
		}
	}


	private void processData(Set<String> ids) {
		try(EventDAO eventDao = new EventDAO(connection)) {
			for (String id : ids) {
				EventDTO startEvent = startedMap.get(id);
				EventDTO finishEvent = finishedMap.get(id);
				if (startEvent!= null && finishEvent != null) {
					log.info("Converting eventDTO to event");
					Event event = eventHelper.dtoToEvent(startEvent, finishEvent);

					log.info("Saving event with id {}", event.getId());
					eventDao.save(event);
				} else {
					log.error("The {} ids is missing", id);
				}
			}
		}
	}
}
