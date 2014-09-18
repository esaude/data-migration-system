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
		
		MatchType match1 = tree.getHead().getMatches().get(0);
		assertEquals(Integer.valueOf(1), match1.getTupleId());
		assertEquals(MatchConstants.NA, match1.getTerminology());
		assertEquals(Integer.valueOf(1), match1.getId());
		assertEquals(MatchConstants.AI, match1.getDefaultValue());
		assertEquals(MatchConstants.NA, match1.getValueMatchId());
		assertEquals("person_id", match1.getLeft().getColumn());
		assertEquals("INT", match1.getLeft().getDatatype());
		assertEquals(Integer.valueOf(11), match1.getLeft().getSize());
		assertEquals(MatchConstants.YES, match1.getLeft().isIsRequired());
		assertEquals(MatchConstants.YES, match1.isPk());
		assertNull(match1.getRight());
		//match references
		assertEquals(1, match1.getReferences().size());
		ReferenceType reference1OfMatch1 = match1.getReferences().get(1);
		assertNotNull(reference1OfMatch1);
		assertNull(reference1OfMatch1.getReferencee());
		assertEquals("T_PACIENTE", reference1OfMatch1.getReferenced().getTable());
		assertEquals("nid", reference1OfMatch1.getReferenced().getColumn());
		assertEquals("TEXT", reference1OfMatch1.getDatatype());
		assertEquals(Integer.valueOf(50), reference1OfMatch1.getSize());
		assertEquals(MatchConstants.ALL, reference1OfMatch1.getReferencedValue().getConstantValue());
		assertNull(reference1OfMatch1.getReferencedValue().getFkValue());
		
		
		assertNotNull(tree.getHead().getReferences());
		assertEquals(0, tree.getHead().getReferences().size());
		
		MatchType match3 = tree.getHead().getMatches().get(2);
		assertEquals(Integer.valueOf(1), match3.getTupleId());
		assertEquals("Data de Nascimento", match3.getTerminology());
		assertEquals(Integer.valueOf(3), match3.getId());
		assertEquals(MatchConstants.NA, match3.getDefaultValue());
		assertEquals(MatchConstants.NA, match3.getValueMatchId());
		assertEquals("birth_date", match3.getLeft().getColumn());
		assertEquals("DATE", match3.getLeft().getDatatype());
		assertEquals(Integer.valueOf(3), match3.getLeft().getSize());
		assertEquals(MatchConstants.NO, match3.getLeft().isIsRequired());
		assertNotNull(match3.getRight());
		assertEquals("T_PACIENTE", match3.getRight().getTable());
		assertEquals("datanasc", match3.getRight().getColumn());
		assertEquals("DATE/TIME", match3.getRight().getDatatype());
		assertEquals(Integer.valueOf(8), match3.getRight().getSize());
		assertEquals(MatchConstants.NO, match3.getRight().isIsRequired());
		
		
		assertEquals(4, tree.getTree(14).getHead().getMatches().size());
		
		matchIds = extract(tree.getTree(14).getHead().getMatches(), on(MatchType.class).getId());
		
		assertTrue(matchIds.contains(57));
		assertTrue(matchIds.contains(58));
		assertTrue(matchIds.contains(59));
		assertTrue(matchIds.contains(60));
		
		//tuple 14 references
		Map<Integer, ReferenceType> references14 = tree.getTree(14).getHead().getReferences();
		
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
