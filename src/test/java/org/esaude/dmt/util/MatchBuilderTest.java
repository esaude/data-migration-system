package org.esaude.dmt.util;

import static org.junit.Assert.*;

import java.util.List;

import org.esaude.dmt.helper.SystemException;
import org.esaude.matchingschema.MatchType;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link MatchBuilder } instances
 * 
 * @author Valério João
 * @since 28-08-2014
 *
 */
public class MatchBuilderTest {
	private MatchBuilder matchBuilder;

	@Before
	public void setUp() throws Exception {
		matchBuilder = new MatchBuilder();
	}

	@Test
	public void testCreateMatchWithoutSides() {
		List<MatchType> matches = matchBuilder.createMatch(1, "personal", true,
				"AI").process();

		assertNotNull(matches);
		assertFalse(matches.isEmpty());
		assertEquals(1, matches.size());

		MatchType match = matches.get(0);

		assertEquals(Integer.valueOf(1), match.getId());
		assertEquals("personal", match.getTerminology());
		assertTrue(match.isHasValueMatch());
		assertEquals("AI", match.getDefaultValue());
	}

	@Test
	public void testCreatesMatchWithoutSides() {
		List<MatchType> matches = matchBuilder
				.createMatch(1, "personal", true, "AI")
				.createMatch(2, "identifier", true, "NULL").process();

		assertNotNull(matches);
		assertFalse(matches.isEmpty());
		assertEquals(2, matches.size());

		MatchType match = matches.get(0);

		assertEquals(Integer.valueOf(1), match.getId());
		assertEquals("personal", match.getTerminology());
		assertTrue(match.isHasValueMatch());
		assertEquals("AI", match.getDefaultValue());

		match = matches.get(1);

		assertEquals(Integer.valueOf(2), match.getId());
		assertEquals("identifier", match.getTerminology());
		assertTrue(match.isHasValueMatch());
		assertEquals("NULL", match.getDefaultValue());
	}

	@Test
	public void testCreateMatchWithSides() throws SystemException {
		List<MatchType> matches = matchBuilder
				.createMatch(1, "personal", true, "AI")
				.createMatchSide(null, "left_column", "INT", 10, true,
						MatchBuilder.LEFT_SIDE)
				.createMatchSide("RIGHT_TABLE", "right_column", "VARCHAR", 100,
						true, MatchBuilder.RIGHT_SIDE).process();

		assertNotNull(matches);
		assertFalse(matches.isEmpty());
		assertEquals(1, matches.size());

		MatchType match = matches.get(0);

		assertEquals(Integer.valueOf(1), match.getId());
		assertEquals("personal", match.getTerminology());
		assertTrue(match.isHasValueMatch());
		assertEquals("AI", match.getDefaultValue());

		assertNotNull(match.getLeft());
		assertNull(match.getLeft().getTable());
		assertEquals("left_column", match.getLeft().getColumn());
		assertEquals("INT", match.getLeft().getDatatype());
		assertEquals(Integer.valueOf(10), match.getLeft().getSize());
		assertTrue(match.getLeft().isIsRequired());

		assertNotNull(match.getRight());
		assertEquals("RIGHT_TABLE", match.getRight().getTable());
		assertEquals("right_column", match.getRight().getColumn());
		assertEquals("VARCHAR", match.getRight().getDatatype());
		assertEquals(Integer.valueOf(100), match.getRight().getSize());
		assertTrue(match.getRight().isIsRequired());

	}

	@Test
	public void testCreateMatchesWithSides() throws SystemException {
		List<MatchType> matches = matchBuilder
				.createMatch(1, "personal", true, "AI")
				.createMatchSide(null, "left_column", "INT", 10, true,
						MatchBuilder.LEFT_SIDE)
				.createMatchSide("RIGHT_TABLE", "right_column", "VARCHAR", 100,
						true, MatchBuilder.RIGHT_SIDE)
				.createMatch(2, "identifier", true, "NULL")
				.createMatchSide(null, "left_column", "INT", 10, true,
						MatchBuilder.LEFT_SIDE)
				.createMatchSide("RIGHT_TABLE", "right_column", "VARCHAR", 100,
						true, MatchBuilder.RIGHT_SIDE).process();

		assertNotNull(matches);
		assertFalse(matches.isEmpty());
		assertEquals(2, matches.size());

		MatchType match = matches.get(0);

		assertEquals(Integer.valueOf(1), match.getId());
		assertEquals("personal", match.getTerminology());
		assertTrue(match.isHasValueMatch());
		assertEquals("AI", match.getDefaultValue());

		assertNotNull(match.getLeft());
		assertNull(match.getLeft().getTable());
		assertEquals("left_column", match.getLeft().getColumn());
		assertEquals("INT", match.getLeft().getDatatype());
		assertEquals(Integer.valueOf(10), match.getLeft().getSize());
		assertTrue(match.getLeft().isIsRequired());

		assertNotNull(match.getRight());
		assertEquals("RIGHT_TABLE", match.getRight().getTable());
		assertEquals("right_column", match.getRight().getColumn());
		assertEquals("VARCHAR", match.getRight().getDatatype());
		assertEquals(Integer.valueOf(100), match.getRight().getSize());
		assertTrue(match.getRight().isIsRequired());

		match = matches.get(1);

		assertEquals(Integer.valueOf(2), match.getId());
		assertEquals("identifier", match.getTerminology());
		assertTrue(match.isHasValueMatch());
		assertEquals("NULL", match.getDefaultValue());

		assertNotNull(match.getLeft());
		assertNull(match.getLeft().getTable());
		assertEquals("left_column", match.getLeft().getColumn());
		assertEquals("INT", match.getLeft().getDatatype());
		assertEquals(Integer.valueOf(10), match.getLeft().getSize());
		assertTrue(match.getLeft().isIsRequired());

		assertNotNull(match.getRight());
		assertEquals("RIGHT_TABLE", match.getRight().getTable());
		assertEquals("right_column", match.getRight().getColumn());
		assertEquals("VARCHAR", match.getRight().getDatatype());
		assertEquals(Integer.valueOf(100), match.getRight().getSize());
		assertTrue(match.getRight().isIsRequired());
	}

	@Test(expected = SystemException.class)
	public void testCreateMatchSideWithoutMatchType() throws SystemException {
		matchBuilder.createMatchSide(
				"RIGHT_TABLE", "right_column", "VARCHAR", 100, true,
				MatchBuilder.RIGHT_SIDE);
	}
	
	@Test(expected = SystemException.class)
	public void testCreateMatchSideWithInvalidSide() throws SystemException {
		matchBuilder.createMatch(1, "personal", true, "AI").createMatchSide(
				"RIGHT_TABLE", "right_column", "VARCHAR", 100, true,
				"invalid");
	}

}
