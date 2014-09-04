
package org.esaude.dmt.util.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;

import org.esaude.dmt.helper.EventCodeContants;
import org.esaude.dmt.helper.ProcessPhases;
import org.junit.Before;
import org.junit.Test;


/**
 * Teste de escrita no ficheiro log
 * @author Edias Jambaia
 * @author Valério João
 * @since 25-08-2014
 */
public class LogWriterTest {

private LogWriter writer;


	@Before
	public void setUp() throws Exception {
		 
		writer = LogWriter.getWriter();

	}

	@Test
	public void testCreateWriter() {
		assertNotNull(writer);
	}
	
	
	@Test
	public void testWriteInfoEvent() {
		Event info = new Info(EventCode.getString(EventCodeContants.ERR001), 
				ProcessPhases.VALIDATION,
				Calendar.getInstance().getTime(), 1, 1, "MATCH_L_TO_R");
		
		String log = writer.writeLog(info);
		
		assertEquals("INFO at: VALID TUPLE:1 MATCH_L_TO_R:1 - The mapping must have a default value", log);
		
	}
	
	@Test
	public void testWriteWarningEvent() {
		Event warning = new Warning(
				EventCode.getString(EventCodeContants.ERR001),
				ProcessPhases.VALIDATION, Calendar.getInstance().getTime(),
				EventCodeContants.ERR001, 1, 1, "MATCH_L_TO_R");

		String log = writer.writeLog(warning);

		assertEquals(
				"WARNING ERR001 at: VALID TUPLE:1 MATCH_L_TO_R:1 - The mapping must have a default value",
				log);

	}
	
	@Test
	public void testWriteErrorEvent() {
		Event warning = new Error(EventCode.getString(EventCodeContants.ERR001), 
				ProcessPhases.VALIDATION,
				Calendar.getInstance().getTime(),
				EventCodeContants.ERR001, 1, 1, "MATCH_L_TO_R");
		
		String log = writer.writeLog(warning);
		
		assertEquals(
				"ERROR ERR001 at: VALID TUPLE:1 MATCH_L_TO_R:1 - The mapping must have a default value",
				log);
		
	}
}
