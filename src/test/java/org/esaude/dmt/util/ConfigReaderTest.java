package org.esaude.dmt.util;

import static org.junit.Assert.*;

import org.esaude.dmt.config.schema.Config;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the ability to read configurations from XML into Java objects
 * @author Valério João
 *
 */
public class ConfigReaderTest {
	private ConfigReader configReader;

	@Before
	public void setUp() throws Exception {
		configReader = ConfigReader.getInstance();
	}

	@Test
	public void testReadConfigData() {
		Config config = configReader.getConfig();
		
		assertNotNull(config);
		assertNotNull(config.getMatchingInput());
		assertNotNull(config.getMatchingInput().getFileName());
		assertNotNull(config.getMatchingInput().getFormat());
		assertNotNull(config.getMatchingInput().getLocation());
		assertNotNull(config.isResetProcess());
	}
	
	@Test
	public void testSingleton() {
		ConfigReader anotherConfigReader = ConfigReader.getInstance();
		
		assertSame(configReader, anotherConfigReader);
	}

}
