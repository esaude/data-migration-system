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
		assertEquals(Integer.valueOf(8), tree.getTree(14).getParent().getHead()
				.getId());

		assertEquals(7, tree.getHead().getMatches().size());

		List<Integer> matchIds = extract(tree.getHead().getMatches(),
				on(MatchType.class).getId());

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
		// match references
		assertEquals(1, match1.getReferences().size());
		ReferenceType reference1OfMatch1 = match1.getReferences().get(1);
		assertNotNull(reference1OfMatch1);
		assertNull(reference1OfMatch1.getReferencee());
		assertEquals("T_PACIENTE", reference1OfMatch1.getReferenced()
				.getTable());
		assertEquals("nid", reference1OfMatch1.getReferenced().getColumn());
		assertEquals("TEXT", reference1OfMatch1.getDatatype());
		assertEquals(Integer.valueOf(50), reference1OfMatch1.getSize());
		assertEquals(MatchConstants.ALL, reference1OfMatch1
				.getReferencedValue().toString());

		assertNotNull(tree.getHead().getReferences());
		assertEquals(0, tree.getHead().getReferences().size());

		MatchType match3 = tree.getHead().getMatches().get(2);
		assertEquals(Integer.valueOf(1), match3.getTupleId());
		assertEquals("Data de Nascimento", match3.getTerminology());
		assertEquals(Integer.valueOf(3), match3.getId());
		assertEquals(MatchConstants.NULL, match3.getDefaultValue());
		assertEquals(MatchConstants.NA, match3.getValueMatchId());
		assertEquals("birthdate", match3.getLeft().getColumn());
		assertEquals("DATE", match3.getLeft().getDatatype());
		assertEquals(Integer.valueOf(3), match3.getLeft().getSize());
		assertEquals(MatchConstants.NO, match3.getLeft().isIsRequired());
		assertNotNull(match3.getRight());
		assertEquals("T_PACIENTE", match3.getRight().getTable());
		assertEquals("datanasc", match3.getRight().getColumn());
		assertEquals("DATE/TIME", match3.getRight().getDatatype());
		assertEquals(Integer.valueOf(8), match3.getRight().getSize());
		assertEquals(MatchConstants.NO, match3.getRight().isIsRequired());

		assertEquals(2, tree.getTree(15).getHead().getMatches().size());

		matchIds = extract(tree.getTree(15).getHead().getMatches(),
				on(MatchType.class).getId());

		assertTrue(matchIds.contains(60));
		assertTrue(matchIds.contains(61));

		// tuple 15 references
		Map<Integer, ReferenceType> references15 = tree.getTree(15).getHead()
				.getReferences();

		assertNotNull(references15);
		assertEquals(4, references15.size());
		assertTrue(references15.containsKey(Integer.valueOf(24)));
		assertTrue(references15.containsKey(Integer.valueOf(25)));
		assertTrue(references15.containsKey(Integer.valueOf(26)));
		assertTrue(references15.containsKey(Integer.valueOf(27)));

		ReferenceType reference14id24 = references15.get(24);
		assertEquals(Integer.valueOf(24), reference14id24.getId());
		assertEquals("ENCOUNTER", reference14id24.getReferencee().getTable());
		assertEquals("patient_id", reference14id24.getReferencee().getColumn());
		assertEquals("PATIENT", reference14id24.getReferenced().getTable());
		assertEquals("patient_id", reference14id24.getReferenced().getColumn());
		assertEquals(MatchConstants.TOP2, reference14id24.getReferencedValue()
				.toString());
		assertEquals(MatchConstants.INT, reference14id24.getDatatype());
		assertEquals(Integer.valueOf(11), reference14id24.getSize());

	}
}
