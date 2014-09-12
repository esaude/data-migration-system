/**
 * Teste de Leitura do ficheiro .properties contendo código e descrição de eventos ERROR e WARNING
 * @author Edias Jambaia
 * @since 27-08-2014
 */
package org.esaude.dmt.util.log;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class EventCodeTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testReadConfigData() {
		assertEquals("The mapping must have a default value", new EventCode().getString("ERR001"));
	}

}
