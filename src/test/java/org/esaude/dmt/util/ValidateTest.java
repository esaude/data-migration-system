/**
 * Teste de escrita no ficheiro log
 * @author Edias Jambaia
 * @since 25-08-2014
 */
package org.esaude.dmt.util;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;



public class ValidateTest {

private Info info;
private Warning warn;
private Error error;


	@Before
	public void setUp() throws Exception {
		 info=new Info("descricao", "fase", new Date());
		 warn=new Warning("descricao", "fase", new Date(),"codigo");
		 error=new Error("descricao", "fase", new Date(), "codigo");
	}

	

	@Test
	public void testWriteInfo() {
		
		assertNotNull(info);
		assertEquals("descricao",info.getDescricao());
		assertEquals("fase",info.getFase());
		assertEquals("INFO",info.getType());
		
	}

	@Test
	public void testWriteWarn() {
		assertNotNull(warn);
		assertEquals("codigo",warn.getCodigo());
		assertEquals("descricao",warn.getDescricao());
		assertEquals("fase",warn.getFase());
		assertEquals("WARNING", warn.getType());
		
	}

	@Test
	public void testWriteError() {
		assertNotNull(error);
		assertEquals("codigo",error.getCodigo());
		assertEquals("descricao",error.getDescricao());
		assertEquals("fase",error.getFase());
		assertEquals("ERROR",error.getType());
		
	}

}
