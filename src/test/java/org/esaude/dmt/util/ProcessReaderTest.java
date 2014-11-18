package org.esaude.dmt.util;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import org.esaude.dmt.process.schema.Process;
import org.esaude.dmt.util.ProcessReader;
import org.esaude.dmt.util.ProcessStatuses;
import org.junit.Before;
import org.junit.Test;

public class ProcessReaderTest {
	private ProcessReader pr;
	
	public ProcessReaderTest() {
		pr = ProcessReader.getInstance();
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testReadProcess() {
		Process p = pr.getProcess();
		assertNotNull(p.getLastStopPoint());
		assertNotNull(p.getLastStopDate());
		assertNotNull(p.getLastStopStatus());
	}
	
	@Test
	public void testRecordProcess() {
		Date now = Calendar.getInstance().getTime();
		pr.recordProcess(6, now, ProcessStatuses.COMPLETED);
		
		Process p = pr.getProcess();
		assertEquals(new BigInteger("6"), p.getLastStopPoint());
		assertEquals(now, p.getLastStopDate().toGregorianCalendar().getTime());
		assertEquals(ProcessStatuses.COMPLETED, p.getLastStopStatus());
		
	}
}
