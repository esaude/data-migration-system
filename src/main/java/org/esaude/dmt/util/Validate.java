
/**
 * 
 * Esta classe define os metodos que escrevem os eventos no logfile 
 * @author Edias Jambaia
 * @since 21-08-2014
 */
package org.esaude.dmt.util;


import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


public class Validate{

	
	/**
	 * Escreve o evento do tipo Info
	 */
public void writeInfo(Info info, Object clss){

	Logger log = Logger.getLogger(clss.getClass());
	configuraLog();
	log.info("Fase: "+info.getFase() +" Desc: "+info.getDescricao());
}

/**
 * Escreve o evento do tipo warning
  */
public void writeWarn(Warning warn, Object clss){


	Logger log = Logger.getLogger(clss.getClass());
	configuraLog();
	log.warn("Tipo Evento: "+warn.getType()+ " Código: "+warn.getCodigo()+" Código: "+warn.getFase()+" Desc: "+warn.getDescricao());
}

/**
 *  Escreve o evento do tipo Error
 */
public void writeError(Error error, Object clss){

	Logger log = Logger.getLogger(clss.getClass());
	configuraLog();
	log.error("Tipo Evento: "+error.getType() +" Código: "+error.getCodigo()+" Fase: "+error.getFase()+" Desc: "+error.getDescricao());
	

}

/**
 *  Configuração do Log
 */
private void configuraLog(){
	
	   DOMConfigurator.configure("log4j.xml");
	   
}

}
