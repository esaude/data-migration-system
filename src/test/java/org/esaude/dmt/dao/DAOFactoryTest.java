package org.esaude.dmt.dao;

import static org.junit.Assert.*;

import org.esaude.dmt.helper.DAOTypes;
import org.esaude.dmt.helper.SystemException;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for { @link DAOFactory }
 * @author Valério João
 * @since 18-09-2014
 *
 */
public class DAOFactoryTest {
	private DAOFactory df;

	@Before
	public void setUp() throws Exception {
		df = DAOFactory.getInstance();
	}

	@Test
	public void testCreateSourceDS() throws SystemException {
		DatabaseUtil sourceDAO = df.getDAO(DAOTypes.SOURCE);
		assertNotNull(sourceDAO);
	}
	
	@Test
	public void testCreateTargetDS() throws SystemException {
		DatabaseUtil targetDAO = df.getDAO(DAOTypes.TARGET);
		assertNotNull(targetDAO);
	}

}
