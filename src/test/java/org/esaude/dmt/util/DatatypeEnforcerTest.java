package org.esaude.dmt.util;

import static org.junit.Assert.*;

import org.esaude.dmt.helper.MatchConstants;
import org.esaude.dmt.helper.SystemException;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the enforcement of datatypes
 * @author Valério João
 *
 */
public class DatatypeEnforcerTest {
	private DatatypeEnforcer de;
	
	public DatatypeEnforcerTest() {
		de = new DatatypeEnforcer();
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testEnforceStringWithCommaToDouble() throws SystemException {
		final Object value = "12,2222";
		
		String result = de.enforce(MatchConstants.DOUBLE, value);
		
		assertNotNull(result);
		assertEquals("12.2222", result);
	}
	
	@Test
	public void testEnforceStringWithDotToDouble() throws SystemException {
		final String value = "12.2222";
		
		String result = de.enforce(MatchConstants.DOUBLE, value);
		
		assertNotNull(result);
		assertEquals("12.2222", result);
	}
	
	@Test(expected = SystemException.class)
	public void testEnforceStringWithNonNumbersToDouble() throws SystemException {
		final String value = "12.2222 cm";
		
		de.enforce(MatchConstants.DOUBLE, value);
	}
	
	@Test(expected = SystemException.class)
	public void testEnforceNullStringToDouble() throws SystemException {
		final String value = null;
		
		de.enforce(MatchConstants.DOUBLE, value);
	}

}
