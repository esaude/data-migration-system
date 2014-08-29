package org.esaude.dmt.util.log;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Classe que faz a Leitura do ficheiro .properties contendo código e descrição
 * de eventos ERROR e WARNING
 * 
 * @author Edias Jambaia
 * @author Valério João
 * @since 27-08-2014
 */
public class EventCode {
	private static final String BUNDLE_NAME = "eventcode";
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private EventCode() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			e.printStackTrace();
		}
		return null;
	}
}
