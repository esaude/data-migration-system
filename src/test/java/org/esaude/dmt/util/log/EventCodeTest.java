/**
 * Teste de Leitura do ficheiro .properties contendo código e descrição de eventos ERROR e WARNING
 * @author Edias Jambaia
 * @since 27-08-2014
 */
package org.esaude.dmt.util.log;

import static org.junit.Assert.*;

import org.esaude.dmt.config.schema.Config;
import org.esaude.dmt.util.ConfigReader;
import org.junit.Before;
import org.junit.Test;

public class EventCodeTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testReadConfigData() {
	
			
		assertEquals("!key!",EventCode.getString("key"));
		assertEquals("code",EventCode.getString("code.test"));
		assertNotNull(EventCode.getString("something"));
		
	}

}
