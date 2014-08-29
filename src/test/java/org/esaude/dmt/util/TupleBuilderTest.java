package org.esaude.dmt.util;

import static org.junit.Assert.*;

import org.esaude.dmt.helper.SystemException;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link TupleBuilder } instances
 * 
 * @author Valério João
 * @since 28-08-2014
 *
 */
public class TupleBuilderTest {
	private TupleBuilder tupleBuilder;

	@Before
	public void setUp() throws Exception {
		tupleBuilder = new TupleBuilder();
	}

	@Test
	public void testCreateTupleSingle() throws SystemException {
		TupleTree tree = tupleBuilder.createTuple(1, "personal", "PERSON",
				"personal data", null).process();

		assertNotNull(tree);
		assertNotNull(tree.getHead());

		assertEquals(Integer.valueOf(1), tree.getHead().getId());
		assertEquals("personal", tree.getHead().getTerminology());
		assertEquals("PERSON", tree.getHead().getTable());
		assertEquals("personal data", tree.getHead().getDesc());

		assertTrue(tree.getSubTrees().isEmpty());
		assertNull(tree.getParent());

	}
	
	@Test
	public void testCreateTupleWithChildren() throws SystemException {
		TupleTree tree1 = tupleBuilder.createTuple(1, "personal", "PERSON",
				"personal data", null)
				.createTuple(2, "address", "ADDRESS",
				"address data", 1).process();

		assertNotNull(tree1);
		assertNotNull(tree1.getHead());

		assertEquals(Integer.valueOf(1), tree1.getHead().getId());
		assertEquals("personal", tree1.getHead().getTerminology());
		assertEquals("PERSON", tree1.getHead().getTable());
		assertEquals("personal data", tree1.getHead().getDesc());

		assertFalse(tree1.getSubTrees().isEmpty());
		assertEquals(1, tree1.getSubTrees().size());
		assertNull(tree1.getParent());
		
		TupleTree tree2 = tree1.getTree(2);
		
		assertNotNull(tree2);
		assertNotNull(tree2.getHead());

		assertEquals(Integer.valueOf(2), tree2.getHead().getId());
		assertEquals("address", tree2.getHead().getTerminology());
		assertEquals("ADDRESS", tree2.getHead().getTable());
		assertEquals("address data", tree2.getHead().getDesc());

		assertTrue(tree2.getSubTrees().isEmpty());
		assertNotNull(tree2.getParent());
		assertEquals(tree1, tree2.getParent());
	}
	
	@Test(expected = SystemException.class)
	public void testCreateChildTupleWithoutParent() throws SystemException {
		tupleBuilder.createTuple(1, "personal", "PERSON",
				"personal data", null)
				.createTuple(2, "address", "ADDRESS",
				"address data", null);
	}
}
