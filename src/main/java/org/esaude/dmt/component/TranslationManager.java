package org.esaude.dmt.component;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.jdbc.SQL;
import org.esaude.dmt.dao.DAOFactory;
import org.esaude.dmt.dao.DatabaseUtil;
import org.esaude.dmt.helper.DAOTypes;
import org.esaude.dmt.helper.MatchConstants;
import org.esaude.dmt.helper.SystemException;
import org.esaude.dmt.util.TupleTree;
import org.esaude.matchingschema.MatchType;
import org.esaude.matchingschema.ReferenceType;
import org.esaude.matchingschema.TupleType;

/**
 * This manager is responsible to generate SQL queries to either SELECT or
 * INSERT data from source into target databases
 * 
 * @author Valério João
 * @author Salimone Nhancume
 * @since 5-09-2014
 *
 */
public class TranslationManager {
	private TupleTree tree;
	private DatabaseUtil sourceDAO;
	private DatabaseUtil targetDAO;
	private boolean skip;// this variable indicates whether or not a tuple must
							// return an insert query or an empty string.

	// This is an attribute just because the a local field must be final in SQL
	// class

	/**
	 * Parameterized constructor
	 * 
	 * @param tree
	 */
	public TranslationManager(TupleTree tree) {
		this.tree = tree;
		try {
			sourceDAO = DAOFactory.getInstance().getDAO(DAOTypes.SOURCE);
			targetDAO = DAOFactory.getInstance().getDAO(DAOTypes.TARGET);
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method starts the process of translation
	 * @return
	 * @throws SystemException
	 */
	public boolean execute() throws SystemException {
		read(tree, null, null, null);

		return true;
	}

	/**
	 * This method traverses the tree of tuples recursively and performs the translation
	 * @param t
	 * @param parentUUID
	 * @param parentCurr
	 * @param top
	 * @throws SystemException 
	 */
	private void read(final TupleTree t, final String parentUUID,
			final String parentCurr , final Object parentTop) throws SystemException {
		// how many tuples?
		// select from source using the reference of the target side PK´s
		// r_reference
		String selectCurrsQuery = this.selectCurrs(t.getHead(), parentCurr);
		List<List<Object>> currResults = sourceDAO
				.executeQuery(selectCurrsQuery);
		System.out
				.println("---------------------------------------------------------");

		for (List<Object> currRow : currResults) {
			//init a transaction from root
			if(t.getParent() == null) {
				targetDAO.setSavePoint();//rollback till this point
			}
			String curr = currRow.get(0).toString();
			// keep the UUID of current insert
			String uuid = UUID.randomUUID().toString();
			// Build insert statement based on translation logic
			String insertTupleQuery = insertTuple(t.getHead(), uuid, curr, parentTop);
			//TODO remove print
			System.out.println(insertTupleQuery);
			//execute insert query and retrieve inserted PKs
			Object top = null;
			if(!insertTupleQuery.isEmpty()) {
				List<List<Object>> tops = targetDAO.executeUpdate(insertTupleQuery);
				top = tops.get(0).get(0);
			}
			// do the same for children
			for (TupleTree eachTree : t.getSubTrees()) {
				read(eachTree, uuid, curr, top);
			}
			// commit a transaction from root
			if(t.getParent() == null) {
				targetDAO.commit();
			}
		}
		//close DAOs
		if(t.getParent() == null) {
			try {
				targetDAO.close();
				sourceDAO.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method generates insert query based on translation logic
	 * 
	 * @param tuple
	 * @param uuid
	 * @param curr
	 * @param top
	 * @return
	 * @throws SystemException 
	 */
	private synchronized String insertTuple(final TupleType tuple,
			final String uuid, final String curr, final Object top) throws SystemException {
		skip = false;// reset skip to false
		String query = new SQL() {
			{
				INSERT_INTO(tuple.getTable());
				// access matches of tuple
				for (MatchType match : tuple.getMatches()) {
					// if match default value is auto increment, skip it
					if (match.getDefaultValue().equals(MatchConstants.AI)) {
						continue;
					}
					String selectQuery = null;// keep the composed select query

					// 1. If a match doesn’t have the right side, it should
					// insert the default value
					if (match.getRight() == null) {
						// 8. TOP – Should use the PK value of the parent tuple
						if (match.getDefaultValue().equals(MatchConstants.TOP)) {
							VALUES(match.getLeft().getColumn(),
									sourceDAO.cast(top));
						} else {
							// use default value
							VALUES(match.getLeft().getColumn(),
									sourceDAO.cast(match.getDefaultValue()));
						}
					} else {
						selectQuery = selectMatch(match, curr);// generate
																// select query
						final List<List<Object>> results = sourceDAO
								.executeQuery(selectQuery);// execute select
															// statement
						final Object value = results.get(0).get(0);// gets the
																	// only one
																	// result

						// in case the default value is AI_SKIP_TRUE or
						// AI_SKIP_FALSE
						if (match.getDefaultValue().equals(
								MatchConstants.AI_SKIP_TRUE)
								|| match.getDefaultValue().equals(
										MatchConstants.AI_SKIP_FALSE)) {
							boolean boolValue = Boolean.valueOf(value
									.toString());
							// 12.AI/SKIP/TRUE – Should skip the entire tuple if
							// the value selected in the right side of the match
							// is TRUE. Must use auto increment otherwise
							if (match.getDefaultValue().equals(
									MatchConstants.AI_SKIP_TRUE)
									&& boolValue) {
								skip = true;// indicate that all the tuple must
											// be skipped
								break;
							}
							// 13. AI/SKIP/FALSE – Should skip the entire tuple
							// if the value selected in the right side of the
							// match is FALSE. Must use auto increment
							// otherwise.
							else if (match.getDefaultValue().equals(
									MatchConstants.AI_SKIP_FALSE)
									&& !boolValue) {
								skip = true;// indicate that all the tuple must
											// be skipped
								break;
							} else {
								continue;
							}
						}
						// 5. If default value of a match is SKIP, the entire
						// tuple must be skipped if the match select doesn’t
						// find any value.
						// generate select statement
						else if (match.getDefaultValue().equals(
								MatchConstants.SKIP)
								&& value == null) {
							skip = true;// indicate that all the tuple must be
										// skipped
							break;
						}
						// 4. If right side of the match is not required, it
						// must insert default value in case the right side
						// select doesn’t find any value
						else if (match.getRight().isIsRequired()
								.equals(MatchConstants.NO)
								&& value == null) {
							// use default value
							VALUES(match.getLeft().getColumn(),
									sourceDAO.cast(match.getDefaultValue()));
						} else {
							VALUES(match.getLeft().getColumn(),
									sourceDAO.cast(value));
						}
					}
				}
				//foreign  key columns
				if(!skip) {
					for(ReferenceType reference : tuple.getReferences().values()) {
						//check if reference is direct
						if(reference.getReferencee().getTable().equalsIgnoreCase(tuple.getTable())) {
							String referencedValue = reference.getReferencedValue().toString();
							// 8. TOP – Should use the PK value of the parent tuple
							if (referencedValue.equalsIgnoreCase(MatchConstants.TOP)) {
								VALUES(reference.getReferencee().getColumn(),
										sourceDAO.cast(top));
							} else {
								// use default value
								VALUES(reference.getReferencee().getColumn(),
										sourceDAO.cast(referencedValue));
							}
						}
					}
					//metadata
					VALUES("creator", sourceDAO.cast(1));
					VALUES("date_created", "NOW()");
					VALUES("voided", sourceDAO.cast(0));
					//avoid PATIENT table
					if(!tuple.getTable().equalsIgnoreCase("PATIENT")) {
						VALUES("uuid", sourceDAO.cast(uuid));
					}
				}
				
			}
		}.toString();
		// check whether or not the query was skipped
		if (skip)
			return "";
		return query;
	}

	/**
	 * This method generates and returns and SQL query that should be executed
	 * in the source database to indicate the number of tuples that should be
	 * inserted into the target database
	 * 
	 * @param tuple
	 * @return
	 */
	private String selectCurrs(final TupleType tuple, final String curr) {
		return new SQL() {
			{
				// the select should be constructed based on L-References of one
				// of the PKs match of the tuple
				MatchType pkMatch = findPkMatch(tuple);
				boolean isFirstDirectReference = true;// used to flag whether or
														// not the reference
				// is the first direct, in case there are many direct references
				// construct the select query using the L-References of the PK
				// match of the tuple
				for (ReferenceType reference : pkMatch.getReferences().values()) {

					String referencedTable = reference.getReferenced()
							.getTable();
					String referencedColumn = reference.getReferenced()
							.getColumn();
					String referencedValue = reference.getReferencedValue()
							.toString();
					// set the right referenced value
					if (referencedValue.equals(MatchConstants.CURR)) {
						referencedValue = curr;
					}
					// check whether the reference is direct or indirect
					if (reference.getPredecessor().equals(Integer.valueOf(0))
							&& isFirstDirectReference) {
						// the reference should be used in the result set
						SELECT(referencedTable + "." + referencedColumn);
						FROM(referencedTable);
						if (!referencedValue.equals(MatchConstants.ALL)) {
							WHERE(referencedTable + "." + referencedColumn
									+ " = " + sourceDAO.cast(referencedValue));
						}
						isFirstDirectReference = false;
					} else {
						String referenceeTable = reference.getReferencee()
								.getTable();
						String referenceeColumn = reference.getReferencee()
								.getColumn();
						FROM(referencedTable);
						WHERE(referenceeTable + "." + referenceeColumn + " = "
								+ referencedTable + "." + referencedColumn);
						// in case the referenced value is not EQUALS
						if (!referencedValue.equals(MatchConstants.EQUALS)) {
							WHERE(referenceeTable + "." + referenceeColumn
									+ " = " + sourceDAO.cast(referencedValue));
						}
					}
				}
			}
		}.toString();
	}

	/**
	 * This method generates and returns and SQL query that should be executed
	 * in the source database to retrieve the data that should be used as a
	 * value for the match while building the insert query
	 * 
	 * @param match
	 * @param curr
	 * @return
	 */
	private String selectMatch(final MatchType match, final String curr) {
		return new SQL() {
			{
				SELECT(match.getRight().getTable() + "."
						+ match.getRight().getColumn());
				FROM(match.getRight().getTable());
				// is the first direct, in case there are many direct references
				// build WHERE clause
				for (ReferenceType reference : match.getReferences().values()) {
					String referencedTable = reference.getReferenced()
							.getTable();
					String referencedColumn = reference.getReferenced()
							.getColumn();
					String referencedValue = reference.getReferencedValue()
							.toString();
					String referenceeTable = null;
					String referenceeColumn = null;
					// set the right referenced value
					if (referencedValue.equals(MatchConstants.CURR)) {
						referencedValue = curr;
					}
					// in case the referencee exist
					if (reference.getReferencee() != null) {
						referenceeTable = reference.getReferencee().getTable();
						referenceeColumn = reference.getReferencee()
								.getColumn();

						FROM(referencedTable);
						WHERE(referenceeTable + "." + referenceeColumn + " = "
								+ referencedTable + "." + referencedColumn);
						// in case the referenced value is not EQUALS
						if (!referencedValue.equals(MatchConstants.EQUALS)) {
							WHERE(referencedTable + "." + referencedColumn
									+ " = " + sourceDAO.cast(referencedValue));
						}
					} else {
						WHERE(referencedTable + "." + referencedColumn + " = "
								+ sourceDAO.cast(referencedValue));
					}
				}
			}
		}.toString();
	}

	/**
	 * This method finds and returns the PK match of the tuple
	 * 
	 * @param tuple
	 * @return
	 */
	private MatchType findPkMatch(TupleType tuple) {
		MatchType pkMatch = null;
		// find one of the PK match
		for (MatchType match : tuple.getMatches()) {
			if (match.isPk().equals(MatchConstants.YES)) {
				pkMatch = match;
				break;
			}
		}
		return pkMatch;
	}
}
