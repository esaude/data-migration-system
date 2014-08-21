/**
 * 
 * Classe que define os atributos para eventos do tipo ERROR
 * 
 * @author Edias Jambaia
 * @author Valério João
 * @since 21-08-2014
 */
package org.esaude.dmt.util;

import java.util.Date;

public class Error extends Event implements Codable {
	
	private String codigo;
	public final static String TYPE = "ERROR"; 
	
	/**
	 * Default constructor
	 */
	public Error() {
		super();
	}
	
	/**
	 * Parameterized constructor
	 * @param descricao
	 * @param fase
	 * @param timestamp
	 * @param codigo
	 */
	public Error(String descricao, String fase, Date timestamp, String codigo) {
		super(descricao, fase, timestamp);
		this.codigo = codigo;
	}

	public String getCodigo() {
		return codigo;
	}


	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Override
	public String getType() {
		return Error.TYPE;
	}
}
