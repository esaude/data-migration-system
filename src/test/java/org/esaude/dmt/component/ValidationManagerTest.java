package org.esaude.dmt.component;

import static org.junit.Assert.*;
import static ch.lambdaj.Lambda.*;

import java.util.List;

import org.esaude.dmt.helper.MatchConstants;
import org.esaude.dmt.util.TupleTree;
import org.esaude.matchingschema.MatchType;
import org.junit.Before;
import org.junit.Test;

public class ValidationManagerTest {
	private ValidationManager vm;

	@Before
	public void setUp() throws Exception {
		vm = new ValidationManager();
		 vm.execute();
	}

	@Test
	public void testTupleTreeStructure() {
		TupleTree tree = vm.getTree();
		assertNotNull(tree.getHead());
		
		assertEquals(Integer.valueOf(1), tree.getHead().getId());
		assertEquals(7, tree.getSubTrees().size());
		assertEquals(Integer.valueOf(13), tree.getTree(14).getParent().getHead().getId());
		
		assertEquals(7, tree.getHead().getMatch().size());
		
		List<Integer> matchIds = extract(tree.getHead().getMatch(), on(MatchType.class).getId());
		
		assertTrue(matchIds.contains(1));
		assertTrue(matchIds.contains(2));
		assertTrue(matchIds.contains(3));
		assertTrue(matchIds.contains(4));
		assertTrue(matchIds.contains(5));
		assertTrue(matchIds.contains(6));
		assertTrue(matchIds.contains(7));
		
		MatchType firstMatch = tree.getHead().getMatch().get(0);
		assertEquals(Integer.valueOf(1), firstMatch.getTupleId());
		assertEquals(MatchConstants.NA, firstMatch.getTerminology());
		assertEquals(Integer.valueOf(1), firstMatch.getId());
		assertEquals(MatchConstants.AI, firstMatch.getDefaultValue());
		assertEquals(MatchConstants.NA, firstMatch.getValueMatchId());
		assertEquals("person_id", firstMatch.getLeft().getColumn());
		assertEquals("INT", firstMatch.getLeft().getDatatype());
		assertEquals(Integer.valueOf(11), firstMatch.getLeft().getSize());
		assertEquals(MatchConstants.YES, firstMatch.getLeft().isIsRequired());
		assertNull(firstMatch.getRight());
		
		MatchType thirdMatch = tree.getHead().getMatch().get(2);
		assertEquals(Integer.valueOf(1), thirdMatch.getTupleId());
		assertEquals("Data de Nascimento", thirdMatch.getTerminology());
		assertEquals(Integer.valueOf(3), thirdMatch.getId());
		assertEquals(MatchConstants.NA, thirdMatch.getDefaultValue());
		assertEquals(MatchConstants.NA, thirdMatch.getValueMatchId());
		assertEquals("birth_date", thirdMatch.getLeft().getColumn());
		assertEquals("DATE", thirdMatch.getLeft().getDatatype());
		assertEquals(Integer.valueOf(3), thirdMatch.getLeft().getSize());
		assertEquals(MatchConstants.NO, thirdMatch.getLeft().isIsRequired());
		assertNotNull(thirdMatch.getRight());
		assertEquals("T_PACIENTE", thirdMatch.getRight().getTable());
		assertEquals("datanasc", thirdMatch.getRight().getColumn());
		assertEquals("DATE/TIME", thirdMatch.getRight().getDatatype());
		assertEquals(Integer.valueOf(8), thirdMatch.getRight().getSize());
		assertEquals(MatchConstants.NO, thirdMatch.getRight().isIsRequired());
		
		
		assertEquals(4, tree.getTree(14).getHead().getMatch().size());
		
		matchIds = extract(tree.getTree(14).getHead().getMatch(), on(MatchType.class).getId());
		
		assertTrue(matchIds.contains(53));
		assertTrue(matchIds.contains(54));
		assertTrue(matchIds.contains(55));
		assertTrue(matchIds.contains(56));
	}
}
