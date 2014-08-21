/*
 * 
 * classe que define um evento
 */
package org.esaude.core.util;

import java.util.Date;

public class Event {
	
	private String descricao;
	private String fase;
	Date timestamp;
	
	public Event(String descricao, String fase, Date timestamp) {
		super();
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


	

}
