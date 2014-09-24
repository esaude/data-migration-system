package org.esaude.dmt.dao;

import static org.junit.Assert.*;

import java.util.List;

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
	public void testExecuteSelectQuery() throws SystemException {
		DatabaseUtil sourceDAO = df.getDAO(DAOTypes.SOURCE);
		List<List<Object>> results = sourceDAO.executeQuery("SELECT * FROM T_PACIENTE");
		
		assertEquals(80, results.size());
	}
	
	@Test
	public void testExecuteSelectQueryS() throws SystemException {
		
		String query = "SELECT T_ADULTO.telefone FROM T_ADULTO, T_PACIENTE WHERE (T_ADULTO.nid = T_PACIENTE.nid AND T_PACIENTE.nid = '01vvvv1307/012/47')";
		DatabaseUtil sourceDAO = df.getDAO(DAOTypes.SOURCE);
		List<List<Object>> results = sourceDAO
				.executeQuery(query);

		assertEquals(1, results.size());
	}

	@Test
	public void testCreateTargetDS() throws SystemException {
		DatabaseUtil targetDAO = df.getDAO(DAOTypes.TARGET);
		assertNotNull(targetDAO);
	}

}
