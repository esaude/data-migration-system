/*
 * @@
 * Classe que define os atributos para eventos do tipo WARN
 */
package org.esaude.core.util;

import java.util.Date;

public class Warning extends Event{
	private String codigo;
	



	public String getCodigo() {
		return codigo;
	}


	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}


	public Warning(String descricao, String fase, Date timestamp, String codigo) {
		super(descricao, fase, timestamp);
		// TODO Auto-generated constructor stub
		this.codigo = codigo;
	}


	@Override
	public String getDescricao() {
		// TODO Auto-generated method stub
		return super.getDescricao();
	}


	@Override
	public void setDescricao(String descricao) {
		// TODO Auto-generated method stub
		super.setDescricao(descricao);
	}


	@Override
	public Date getTimestamp() {
		// TODO Auto-generated method stub
		return super.getTimestamp();
	}


	@Override
	public void setTimestamp(Date timestamp) {
		// TODO Auto-generated method stub
		super.setTimestamp(timestamp);
	}


	@Override
	public String getFase() {
		// TODO Auto-generated method stub
		return super.getFase();
	}


	@Override
	public void setFase(String fase) {
		// TODO Auto-generated method stub
		super.setFase(fase);
	}
	

}
