package org.esaude.dmt.util.datatypemapping;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the composition of datatype mapping from CSV file
 * @author Valério João
 * @since 25-08-2014
 *
 */
public class DatatypeMappingReaderTest {
	
	private DatatypeMappingReader reader;

	@Before
	public void setUp() throws Exception {
		reader = DatatypeMappingReader.getInstance();
	}
	
	@After
	public void tearDown() {
		reader = null;
		
	}

	@Test
	public void testInstanceWasCreated() {
		
		assertNotNull(reader);
	}
	
	@Test
	public void testVerifyBoolAndYesNoIIsTrue() {
		String left = "BOOL";
		String right = "YES/NO";
		
		assertTrue(reader.verify(left, right));
	}
	
	@Test
	public void testVerifyBitAndBoolIsTrue() {
		String left = "BIT";
		String right = "BOOL";
		
		assertTrue(reader.verify(left, right));
	}
	
	@Test
	public void testVerifyBitAndYesNoIsTrue() {
		String left = "BIT";
		String right = "YES/NO";
		
		assertTrue(reader.verify(left, right));
	}
	
	@Test
	public void testVerifyTinyIntAndYesNoIsTrue() {
		String left = "TINYINT";
		String right = "YES/NO";
		
		assertTrue(reader.verify(left, right));
	}
	
	@Test
	public void testVerifyTinyIntAndBitTrue() {
		String left = "TINYINT";
		String right = "BIT";
		
		assertTrue(reader.verify(left, right));
	}
	
	@Test
	public void testVerifyTinyIntAndBoolTrue() {
		String left = "TINYINT";
		String right = "BOOL";
		
		assertTrue(reader.verify(left, right));
	}
	
	@Test
	public void testVerifyIntAndYesNoTrue() {
		String left = "INT";
		String right = "YES/NO";
		
		assertTrue(reader.verify(left, right));
	}
	
	@Test
	public void testVerifyIntAndBitTrue() {
		String left = "INT";
		String right = "BIT";
		
		assertTrue(reader.verify(left, right));
	}
	
	@Test
	public void testVerifyIntAndLongIntegerTrue() {
		String left = "INT";
		String right = "LONG INTEGER";
		
		assertTrue(reader.verify(left, right));
	}
	
	@Test
	public void testVerifyBoolAndBitIsFalse() {
		String left = "BOOL";
		String right = "BIT";
		
		assertFalse(reader.verify(left, right));
	}
	
	@Test
	public void testVerifyYesNoAndIntFalse() {
		String left = "YES/NO";
		String right = "INT";
		
		assertFalse(reader.verify(left, right));
	}
	
	@Test
	public void testVerifyBoolAndIntFalse() {
		String left = "BOOL";
		String right = "INT";
		
		assertFalse(reader.verify(left, right));
	}
	
	@Test
	public void testVerifyBoolAndNonExistingFalse() {
		String left = "BOOL";
		String right = "NON EXISTING";
		
		assertFalse(reader.verify(left, right));
	}
	
	@Test
	public void testVerifyNonExistingAndTextFalse() {
		String left = "NON EXISTING";
		String right = "TEXT";
		
		assertFalse(reader.verify(left, right));
	}
	
	@Test
	public void testVerifyDateTimeAndIntFalse() {
		String left = "DATETIME";
		String right = "INT";
		
		assertFalse(reader.verify(left, right));
	}
	
	@Test
	public void testVerifyIntAndDatetimeFalse() {
		String left = "INT";
		String right = "DATETIME";
		
		assertFalse(reader.verify(left, right));
	}

}
