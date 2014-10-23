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
	private XlsProcessor processor;

	@Before
	public void setUp() throws Exception {
		processor = new XlsProcessor();
	}

	@Test
	public void testGetFirstIdValueOfTupleSheet() {
		Object found = processor.process(Sheets.TUPLE.INDEX, Sheets.TUPLE.ID, Sheets.TUPLE.ROW_START);
		
		assertNotNull(found);
		assertEquals("1", found);
	}
	
	@Test
	public void testGetFirstTableValueOfTupleSheet() {
		Object found = processor.process(Sheets.TUPLE.INDEX, Sheets.TUPLE.TABLE, Sheets.TUPLE.ROW_START);
		
		assertNotNull(found);
		assertEquals("PERSON", found);
	}
	
	@Test
	public void testGetFirstPredecessorValueOfTupleSheet() {
		Object found = processor.process(Sheets.TUPLE.INDEX, Sheets.TUPLE.PREDECESSOR, Sheets.TUPLE.ROW_START);
		
		assertNotNull(found);
		assertEquals("N/A", found);
	}
	
	@Test
	public void testGetFirstTerminologyValueOfTupleSheet() {
		Object found = processor.process(Sheets.TUPLE.INDEX, Sheets.TUPLE.TERMINOLOGY, Sheets.TUPLE.ROW_START);
		
		assertNotNull(found);
		assertEquals("Personal Data", found);
	}

}
