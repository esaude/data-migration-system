package org.esaude.dmt.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.esaude.dmt.config.schema.Config;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Tool that reads info from XML configuration file. This tool uses JAXB API to
 * unmarshal XML into Java OObjects
 * 
 * @author Valério João
 * @since 21-08-2014
 *
 */
@SuppressWarnings("restriction")
public final class ConfigReader {
	private static ConfigReader instance;

	private ConfigReader() {
	}
	
	/**
	 * Get the unique instance of <link>ConfigReader</link>
	 * @return
	 */
	public static ConfigReader getInstance() {
		//create a new instance if doesnt exist
		if(instance == null) {
			instance = new ConfigReader();
		}
		return instance;
	}
	
	/**
	 * Used to get the configuration objects
	 * @return
	 */
	public Config getConfig() {

		Config config = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance("org.esaude.dmt.config.schema");

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			config  = (Config) jaxbUnmarshaller.unmarshal(new FileInputStream("src/main/resources/config.xml"));

		} catch (JAXBException | FileNotFoundException e) {
			e.printStackTrace();
		}
		return config;
	}
}
