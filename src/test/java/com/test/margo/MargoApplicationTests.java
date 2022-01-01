package com.test.margo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.margo.helper.EventHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.Connection;

@RunWith(MockitoJUnitRunner.class)
public class MargoApplicationTests {

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private Connection connection;

	@Mock
	private EventHelper eventHelper;

	private MargoApplication margoApplication;

	@Before
	public void setup() {
		MockitoAnnotations.openMocks(this);
		margoApplication = new MargoApplication(objectMapper, connection, eventHelper);
	}

	@Test(expected = InvalidParameterException.class)
	public void testRunWithEmptyArgs() throws IOException {
		String[] args = {};
		margoApplication.run(args);
	}

	@Test(expected = InvalidParameterException.class)
	public void testRunWithEmptyFilePath() throws IOException {
		String[] args = {""};
		margoApplication.run(args);
	}

	@Test(expected = FileNotFoundException.class)
	public void testRunWithInvalidFilePath() throws IOException {
		String[] args = {"testInvalid"};
		margoApplication.run(args);
	}

	@Test(expected = NullPointerException.class)
	public void testRunWithValidJsonAndMissingRequiredFields() throws IOException {
		String[] args = {"src/test/resources/testFile_with_missing_fields"};
		margoApplication.run(args);
	}

	@Test(expected = NullPointerException.class)
	public void testRunWithInvalidJson() throws IOException {
		String[] args = {"src/test/resources/testFile_with_invalid_json"};
		margoApplication.run(args);
	}

}
