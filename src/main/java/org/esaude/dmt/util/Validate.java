
/*
 * 
 * Esta classe define os metodos que escrevem os eventos no logfile 
 * 
 */
package org.esaude.dmt.util;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


public class Validate{

	
	/*
	 * Escreve o evento do tipo Info
	 */
public void writeInfo(Event event, Object clss){

	Logger log = Logger.getLogger(clss.getClass());
	configuraLog();
	log.info("Fase: "+event.getFase() +" Desc: "+event.getDescricao());
}

/*
 * Escreve o evento do tipo warning
  */
public void writeWarn(Warning warn, Object clss){


	Logger log = Logger.getLogger(clss.getClass());
	configuraLog();
	log.error("Código: "+warn.getCodigo()+" Código: "+warn.getFase()+" Desc: "+warn.getDescricao());
}

/*
 *  Escreve o evento do tipo Error
 */
public void writeError(Error error, Object clss){


	
	Logger log = Logger.getLogger(clss.getClass());
	configuraLog();
	log.error("Código: "+error.getCodigo()+" Fase: "+error.getFase()+" Desc: "+error.getDescricao());
	

}

/*
 *  Configuracao do log
 */
private void configuraLog(){

	   DOMConfigurator.configure("log4j.xml");
	   
}

}
