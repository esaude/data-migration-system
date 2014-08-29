
package org.esaude.dmt.util.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;

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
		Event info = new Info(EventCode.getString("error.test.00001"), 
				ProcessPhases.VALIDATION,
				Calendar.getInstance().getTime());
		
		String log = writer.writeLog(info);
		
		assertEquals("INFO AT: VALID: O Tipo de dados a esquerda não corresponde ao tipo de dados a direita", log);
		
	}
	
	@Test
	public void testWriteWarningEvent() {
		String code = "error.test.00001";
		Event warning = new Warning(EventCode.getString(code), 
				ProcessPhases.VALIDATION,
				Calendar.getInstance().getTime(),
				code);
		
		String log = writer.writeLog(warning);
		
		assertEquals("WARNING error.test.00001 AT: VALID: O Tipo de dados a esquerda não "
				+ "corresponde ao tipo de dados a direita", log);
		
	}
	
	@Test
	public void testWriteErrorEvent() {
		String code = "error.test.00001";
		Event warning = new Error(EventCode.getString(code), 
				ProcessPhases.VALIDATION,
				Calendar.getInstance().getTime(),
				code);
		
		String log = writer.writeLog(warning);
		
		assertEquals("ERROR error.test.00001 AT: VALID: O Tipo de dados a esquerda não "
				+ "corresponde ao tipo de dados a direita", log);
		
	}
}
