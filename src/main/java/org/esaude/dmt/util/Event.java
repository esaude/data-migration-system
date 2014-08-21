package org.esaude.dmt.util;

import java.util.Date;

/**
 * Classe que define um evento para Log
 * @author Edias Jambaia
 * @author Valério João
 * @since 21-08-2014
 */
public abstract class Event {
	
	private String descricao;
	private String fase;
	private Date timestamp;
	
	/**
	 * Default constructor
	 */
	public Event() {
	}
	
	/**
	 * Parameterized constructor
	 * @param descricao
	 * @param fase
	 * @param timestamp
	 */
	public Event(String descricao, String fase, Date timestamp) {
		this.descricao = descricao;
		this.fase = fase;
		this.timestamp = timestamp;
	}

	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public String getFase() {
		return fase;
	}
	public void setFase(String fase) {
		this.fase = fase;
	}
	
	/**
	 * Specializations must provide their event type through this method
	 * @return
	 */
	public abstract String getType();
}
