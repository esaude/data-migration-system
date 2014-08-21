package org.esaude.dmt.xls;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the XLS processing tool
 * @author Valério João
 * @since 21-08-2014
 *
 */
public class XlsProcessorTest {
	private XslProcessor processor;

	@Before
	public void setUp() throws Exception {
		processor = new XslProcessor();
	}

	@Test
	public void testGetFirstIdValueOfTupleSheet() {
		final int ROW = 1;
		Object found = processor.process(Sheets.TUPLE.INDEX, Sheets.TUPLE.ID, ROW);
		
		assertNotNull(found);
		assertEquals("1", found);
	}

}
