
/**
 * 
 * Esta classe define os metodos que escrevem os eventos no logfile 
 * @author Edias Jambaia
 * @since 21-08-2014
 */
package org.esaude.dmt.util;

import java.io.FileNotFoundException;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


public final class Validate{

	private static Validate validate;
	private static int escreveu;
	
	public static Validate getValidate() {


		if(validate == null) {
			validate = new Validate();
			escreveu=0;
			
		}
		return validate;
	}
	
	/**
	 * Escreve o evento do tipo Info
	 */
public void writeInfo(Info info, Object clss){

	Logger log = Logger.getLogger(clss.getClass());
	try {
		configuraLog();
		log.info("Fase: "+info.getFase() +" Desc: "+info.getDescricao());
		escreveu++;
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
}

/**
 * Escreve o evento do tipo warning
  */
public void writeWarn(Warning warn, Object clss) {


	Logger log = Logger.getLogger(clss.getClass());
	try {
		configuraLog();
		log.warn("Tipo Evento: "+warn.getType()+ " Código: "+warn.getCodigo()+" Código: "+warn.getFase()+" Desc: "+warn.getDescricao());
		escreveu++;
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}

/**
 *  Escreve o evento do tipo Error
 */
public void writeError(Error error, Object clss){

	Logger log = Logger.getLogger(clss.getClass());
	try {
		configuraLog();
		log.error("Tipo Evento: "+error.getType() +" Código: "+error.getCodigo()+" Fase: "+error.getFase()+" Desc: "+error.getDescricao());
		escreveu++;
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	

}

/**
 *  Configuração do Log
 */
public void configuraLog() throws FileNotFoundException{
	
	DOMConfigurator.configure("log4j.xml");
	
}

public static int getEscreveu() {
	return escreveu;
}



}
