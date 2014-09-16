package org.esaude.dmt.component;

import static org.junit.Assert.*;
import static ch.lambdaj.Lambda.*;

import java.util.List;
import java.util.Map;

import org.esaude.dmt.helper.MatchConstants;
import org.esaude.dmt.util.TupleTree;
import org.esaude.matchingschema.MatchType;
import org.esaude.matchingschema.ReferenceType;
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
		
		assertEquals(7, tree.getHead().getMatches().size());
		
		List<Integer> matchIds = extract(tree.getHead().getMatches(), on(MatchType.class).getId());
		
		assertTrue(matchIds.contains(1));
		assertTrue(matchIds.contains(2));
		assertTrue(matchIds.contains(3));
		assertTrue(matchIds.contains(4));
		assertTrue(matchIds.contains(5));
		assertTrue(matchIds.contains(6));
		assertTrue(matchIds.contains(7));
		
		MatchType firstMatch = tree.getHead().getMatches().get(0);
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
		
		assertNotNull(tree.getHead().getLeftReference());
		assertEquals(0, tree.getHead().getLeftReference().size());
		
		MatchType thirdMatch = tree.getHead().getMatches().get(2);
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
		
		
		assertEquals(4, tree.getTree(14).getHead().getMatches().size());
		
		matchIds = extract(tree.getTree(14).getHead().getMatches(), on(MatchType.class).getId());
		
		assertTrue(matchIds.contains(57));
		assertTrue(matchIds.contains(58));
		assertTrue(matchIds.contains(59));
		assertTrue(matchIds.contains(60));
		
		//tuple 14 references
		Map<Integer, ReferenceType> references14 = tree.getTree(14).getHead().getLeftReference();
		
		assertNotNull(references14);
		assertEquals(4, references14.size());
		assertTrue(references14.containsKey(Integer.valueOf(20)));
		assertTrue(references14.containsKey(Integer.valueOf(21)));
		assertTrue(references14.containsKey(Integer.valueOf(22)));
		assertTrue(references14.containsKey(Integer.valueOf(23)));
		
		ReferenceType reference14id20 = references14.get(20);
		assertEquals(Integer.valueOf(20), reference14id20.getId());
		assertEquals("PATIENT_STATE", reference14id20.getReferencee().getTable());
		assertEquals("patient_program_id", reference14id20.getReferencee().getColumn());
		assertEquals("PATIENT_PROGRAM", reference14id20.getReferenced().getTable());
		assertEquals("patient_program_id", reference14id20.getReferenced().getColumn());
		assertEquals(MatchConstants.TOP, reference14id20.getReferencedValue().getConstantValue());
		assertNull(reference14id20.getReferencedValue().getFkValue());
		assertEquals(MatchConstants.INT, reference14id20.getDatatype());
		assertEquals(Integer.valueOf(11), reference14id20.getSize());
		
	}
}
