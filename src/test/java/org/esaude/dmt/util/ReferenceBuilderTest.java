package org.esaude.dmt.util;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.esaude.dmt.helper.SystemException;
import org.esaude.matchingschema.ReferenceType;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link ReferenceBuilder }
 * @author Valério João
 * @since 28-08-2014
 *
 */
public class ReferenceBuilderTest {
	private ReferenceBuilder referenceBuilder;
	
	public ReferenceBuilderTest() {
	}

	@Before
	public void setUp() throws Exception {
		referenceBuilder = new ReferenceBuilder();
	}
	
	@Test
	public void testCreateReferenceWithoutSideAndValueMany() throws SystemException {
		Map<Integer, ReferenceType> references = referenceBuilder.createReference(1,
				"INT", 10, "reference without side and value one", 0)
				.createReference(2,
				"VARCHAR", 100, "reference without side and value two", 1)
				.processMap();
		
		assertNotNull(references);
		assertFalse(references.isEmpty());
		assertEquals(2, references.size());
		
		ReferenceType referenceType = references.get(1);
		
		assertEquals(new Integer(1), referenceType.getId());
		assertEquals("INT", referenceType.getDatatype());
		assertEquals(new Integer(10), referenceType.getSize());
		assertEquals("reference without side and value one", referenceType.getNameDesc());
		assertEquals(Integer.valueOf(0), referenceType.getPredecessor());
		
		referenceType = references.get(2);
		
		assertEquals(new Integer(2), referenceType.getId());
		assertEquals("VARCHAR", referenceType.getDatatype());
		assertEquals(new Integer(100), referenceType.getSize());
		assertEquals("reference without side and value two", referenceType.getNameDesc());
		assertEquals(Integer.valueOf(1), referenceType.getPredecessor());
	}
	
	@Test
	public void testCreateReferenceWithoutSideAndValue() throws SystemException {
		Map<Integer, ReferenceType> references = referenceBuilder.createReference(1,
				"INT", 10, "reference without side and value", 0)
				.processMap();
		
		assertNotNull(references);
		assertFalse(references.isEmpty());
		assertEquals(1, references.size());
		
		ReferenceType referenceType = references.get(1);
		
		assertEquals(new Integer(1), referenceType.getId());
		assertEquals("INT", referenceType.getDatatype());
		assertEquals(new Integer(10), referenceType.getSize());
		assertEquals("reference without side and value", referenceType.getNameDesc());
		assertEquals(Integer.valueOf(0), referenceType.getPredecessor());
	}
	
	@Test
	public void testCreateReferenceWithSideAndValue() throws SystemException {
		Map<Integer, ReferenceType> references = referenceBuilder.createReference(1,
				"INT", 10, "reference without side and value", 0)
				.createReferenceSide("LEFT_TABLE", "left_column", ReferenceBuilder.REFERENCEE)
				.createReferenceSide("RIGHT_TABLE", "right_column", ReferenceBuilder.REFERENCED)
				.createReferencedValue("TOP")
				.processMap();
		
		assertNotNull(references);
		assertFalse(references.isEmpty());
		assertEquals(1, references.size());
		
		ReferenceType referenceType = references.get(1);
		
		assertEquals(new Integer(1), referenceType.getId());
		assertEquals("INT", referenceType.getDatatype());
		assertEquals(new Integer(10), referenceType.getSize());
		assertEquals("reference without side and value", referenceType.getNameDesc());
		assertEquals(Integer.valueOf(0), referenceType.getPredecessor());
		
		assertNotNull(referenceType.getReferencee());
		assertEquals("LEFT_TABLE", referenceType.getReferencee().getTable());
		assertEquals("left_column", referenceType.getReferencee().getColumn());
		
		assertNotNull(referenceType.getReferenced());
		assertEquals("RIGHT_TABLE", referenceType.getReferenced().getTable());
		assertEquals("right_column", referenceType.getReferenced().getColumn());
		
		assertNotNull(referenceType.getReferencedValue());
		assertEquals("TOP", referenceType.getReferencedValue().toString());
	}
	
	@Test
	public void testCreateReferenceWithSideAndValueMany() throws SystemException {
		Map<Integer, ReferenceType> references = referenceBuilder.createReference(1,
				"INT", 10, "reference without side and value one", 0)
				.createReferenceSide("LEFT_TABLE", "left_column", ReferenceBuilder.REFERENCEE)
				.createReferenceSide("RIGHT_TABLE", "right_column", ReferenceBuilder.REFERENCED)
				.createReferencedValue("TOP")
				.createReference(2,
				"VARCHAR", 100, "reference without side and value two", 1)
				.createReferenceSide("LEFT_TABLE", "left_column", ReferenceBuilder.REFERENCEE)
				.createReferenceSide("RIGHT_TABLE", "right_column", ReferenceBuilder.REFERENCED)
				.createReferencedValue(200L)
				.processMap();
		
		assertNotNull(references);
		assertFalse(references.isEmpty());
		assertEquals(2, references.size());
		
		ReferenceType referenceType = references.get(1);
		
		assertEquals(new Integer(1), referenceType.getId());
		assertEquals("INT", referenceType.getDatatype());
		assertEquals(new Integer(10), referenceType.getSize());
		assertEquals("reference without side and value one", referenceType.getNameDesc());
		assertEquals(Integer.valueOf(0), referenceType.getPredecessor());
		
		assertNotNull(referenceType.getReferencee());
		assertEquals("LEFT_TABLE", referenceType.getReferencee().getTable());
		assertEquals("left_column", referenceType.getReferencee().getColumn());
		
		assertNotNull(referenceType.getReferenced());
		assertEquals("RIGHT_TABLE", referenceType.getReferenced().getTable());
		assertEquals("right_column", referenceType.getReferenced().getColumn());
		
		assertNotNull(referenceType.getReferencedValue());
		assertEquals("TOP", referenceType.getReferencedValue().toString());
		
		referenceType = references.get(2);
		
		assertEquals(new Integer(2), referenceType.getId());
		assertEquals("VARCHAR", referenceType.getDatatype());
		assertEquals(new Integer(100), referenceType.getSize());
		assertEquals("reference without side and value two", referenceType.getNameDesc());
		assertEquals(Integer.valueOf(1), referenceType.getPredecessor());
		
		assertNotNull(referenceType.getReferencee());
		assertEquals("LEFT_TABLE", referenceType.getReferencee().getTable());
		assertEquals("left_column", referenceType.getReferencee().getColumn());
		
		assertNotNull(referenceType.getReferenced());
		assertEquals("RIGHT_TABLE", referenceType.getReferenced().getTable());
		assertEquals("right_column", referenceType.getReferenced().getColumn());
		
		assertNotNull(referenceType.getReferencedValue());
		assertEquals("200", referenceType.getReferencedValue().toString());
	}
	
	@Test(expected = SystemException.class)
	public void testCreateReferenceSideWithoutReferenceType()
			throws SystemException {
		referenceBuilder.createReferenceSide("LEFT_TABLE", "left_column",
				ReferenceBuilder.REFERENCEE).processMap();
	}
	
	@Test(expected = SystemException.class)
	public void testCreateReferenceValueWithoutReferenceType() throws SystemException {
		referenceBuilder
				.createReferencedValue("TOP")
				.processMap();
	}
	
	@Test(expected = SystemException.class)
	public void testCreateReferenceSideWithInvalidSide()
			throws SystemException {
		referenceBuilder.createReference(1,
				"INT", 10, "reference without side and value one", 0)
				.createReferenceSide("LEFT_TABLE", "left_column",
				"invalid").processMap();
	}
}
