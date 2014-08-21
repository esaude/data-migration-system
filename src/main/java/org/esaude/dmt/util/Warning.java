package org.esaude.dmt.util;

import java.util.Date;

/**
 * 
 * Classe que define os atributos para eventos do tipo WARNING
 * 
 * @author Edias Jambaia
 * @author Valério João
 * @since 21-08-2014
 */
public class Warning extends Event implements Codable {
	private String codigo;
	public final static String TYPE = "ERROR";
	
	/**
	 * Default constructor
	 */
	public Warning() {
		super();
	}
	
	/**
	 * Parameterized constructor
	 * @param descricao
	 * @param fase
	 * @param timestamp
	 * @param codigo
	 */
	public Warning(String descricao, String fase, Date timestamp, String codigo) {
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
		return Warning.TYPE;
	}
}
