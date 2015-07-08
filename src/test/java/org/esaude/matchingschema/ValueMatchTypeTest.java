package org.esaude.matchingschema;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the value match representation in objects
 * @author Val�rio Jo�o
 * @since 19-10-2014
 *
 */
public class ValueMatchTypeTest {

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testCorrectGroupOneSize() {
		Map<String, String> groupOne = ValueMatchType.valueMatches.get(1);
		
		assertEquals(8, groupOne.size());
	}
	
	@Test
	public void testCorrectGroupOneElements() {
		Map<String, String> groupOne = ValueMatchType.valueMatches.get(1);
		
		//assertEquals(1446, groupOne.get("N�vel Prim�rio".toLowerCase()));
		
		//assertEquals("1448", groupOne.get("N�vel Superior".toLowerCase()));
	}

}
