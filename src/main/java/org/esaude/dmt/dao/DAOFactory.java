package org.esaude.dmt.dao;

import java.sql.Connection;
import java.sql.DriverManager;

import org.esaude.dmt.config.schema.DatasourceType;
import org.esaude.dmt.helper.SystemException;
import org.esaude.dmt.util.ConfigReader;
import org.esaude.dmt.util.DAOTypes;

/**
 * This class is a factory that generates instances of {@link DatabaseUtil }
 * @author Valério João
 * @since 10-09-2014
 *
 */
public final class DAOFactory {
	private ConfigReader cr;
	private DatabaseUtil sourceDAO;
	private DatabaseUtil targetDAO;
	private static DAOFactory instance;

	private DAOFactory() {
		cr = ConfigReader.getInstance();
	}
	
	private DAOFactory(ConfigReader cr) {
		this.cr = cr;
	}
	
	/**
	 * This is a method that returns a single instance of this class
	 * @return
	 */
	public static DAOFactory getInstance() {
		if(instance == null) {
			instance = new DAOFactory();
		}
		return instance;
	}
	
	/**
	 * This is a method that returns a single instance of this class
	 * @param cr
	 * @return
	 */
	public static DAOFactory getInstance(ConfigReader cr) {
		if(instance == null) {
			instance = new DAOFactory(cr);
		}
		return instance;
	}

	/**
	 * This method returns a DAO based on its type (target or source)
	 * The drive name is configured before return
	 * @param type
	 * @return
	 * @throws SystemException
	 */
	public DatabaseUtil getDAO(DAOTypes type) throws SystemException {
		DatasourceType ds = null;

		if (type == DAOTypes.SOURCE) {
			ds = cr.getConfig().getSourceDs();
			// create DAO if not exists
			if (sourceDAO == null) {
				sourceDAO = createDAOs(ds);
			}
			// set the database driver class
			try {
				Class.forName(ds.getDriveName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return sourceDAO;
		} else if (type == DAOTypes.TARGET) {
			ds = cr.getConfig().getTargetDs();
			targetDAO = createDAOs(ds);
			// create DAO if not exists
			if (targetDAO == null) {
				targetDAO = createDAOs(ds);
			}
			// set the database driver class
			try {
				Class.forName(ds.getDriveName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return targetDAO;
		} else {
			throw new SystemException("The type of datasource is invalid");
		}
	}

	/**
	 * This method creates a DAO based on the type of DS
	 * @param ds
	 * @return
	 * @throws SystemException
	 */
	private static DatabaseUtil createDAOs(final DatasourceType ds)
			throws SystemException {
		// create connections using config.xml
		if (ds == null) {
			throw new SystemException(
					"The datasource info doesn't exist in config.xml");
		}
		try {
			Connection connection = DriverManager.getConnection(
					ds.getDatabaseLocation() + ds.getDatabaseName(),
					ds.getUsername(), ds.getPassword());

			if (connection != null) {
				return new DatabaseUtil(connection);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
