/**
 * Teste de escrita no ficheiro log
 * @author Edias Jambaia
 * @since 25-08-2014
 */
package org.esaude.dmt.util;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;



public class ValidateTest {

private Validate validate; 
private Info info;
private Warning warn;
private Error error;


	@Before
	public void setUp() throws Exception {
		 
		validate=Validate.getValidate();
		 info=new Info("descricao", "fase", new Date());
		 warn=new Warning("descricao", "fase", new Date(),"codigo");
		 error=new Error("descricao", "fase", new Date(), "codigo");

	}

	@Test
	public void testOjectoValidate() {
		assertNotNull(validate);
		
		
	}
	
	
	@Test
	public void testObjectoInfo() {
		assertNotNull(validate);
		assertNotNull(info);
		assertEquals("descricao",info.getDescricao());
		assertEquals("fase",info.getFase());
		assertEquals("INFO",info.getType());
		
	}

	@Test
	public void testObjectoWarn() {
		assertNotNull(warn);
		assertEquals("codigo",warn.getCodigo());
		assertEquals("descricao",warn.getDescricao());
		assertEquals("fase",warn.getFase());
		assertEquals("WARNING", warn.getType());
		
	}

	@Test 
	public void testObjectoError() {
		assertNotNull(error);
		assertEquals("codigo",error.getCodigo());
		assertEquals("descricao",error.getDescricao());
		assertEquals("fase",error.getFase());
		assertEquals("ERROR",error.getType());


		
	}
	
	@SuppressWarnings("static-access")
	@Test 
	public void testWriteInfo() {
		assertNotNull(info);
		
		int escreveu=validate.getEscreveu();
		validate.writeInfo(info, this.getClass());
	
		assertTrue(escreveu<validate.getEscreveu());

	}
	
	@SuppressWarnings("static-access")
	@Test 
	public void testWriteWarn() {
		assertNotNull(warn);
		
		int escreveu=validate.getEscreveu();
		validate.writeWarn(warn, this.getClass());
	
		assertTrue(escreveu<validate.getEscreveu());

	}
	
	@SuppressWarnings("static-access")
	@Test 
	public void testWriteError() {
		assertNotNull(error);
		
		int escreveu=validate.getEscreveu();
		validate.writeError(error, this.getClass());
	
		assertTrue(escreveu<validate.getEscreveu());

	}
	
}
