package org.esaude.dmt.component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.ibatis.jdbc.SQL;
import org.esaude.dmt.dao.DAOFactory;
import org.esaude.dmt.dao.DatabaseUtil;
import org.esaude.dmt.helper.DAOTypes;
import org.esaude.dmt.helper.MatchConstants;
import org.esaude.dmt.helper.SystemException;
import org.esaude.dmt.helper.ValidationStatuses;
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

	public boolean execute() throws SystemException {
		read(tree, null, null);

		return true;
	}

	private void read(final TupleTree t, final String parentUUID,
			final String parentCurr) {
		// how many tuples?
		// select from source using the reference of the target side PK´s
		// r_reference
		String selectCurrsQuery = this.selectCurrs(t.getHead(), parentCurr);
		List<List<Object>> currResults = sourceDAO.executeQuery(selectCurrsQuery);
		// System.out.println(selectQuery);
		System.out
				.println("---------------------------------------------------------");

		for (List<Object> currRow : currResults) {
			String curr = currRow.get(0).toString();
			Object top = null;//the PK value of parent tuple
			// Get the primary key of parent reference. Select from target DB,
			if (parentUUID != null) {
				// find the PK match of parent tuple using q UUID if not null
				MatchType pkMatch = findPkMatch(t.getParent().getHead());
				String selectParentIdQuery = selectParentId(t.getParent().getHead()
						.getTable(), pkMatch.getLeft().getColumn(), parentUUID);
				//execute query
//				List<List<Object>> parentIdResults = targetDAO.executeQuery(selectParentIdQuery);
//				top = parentIdResults.get(0).get(0);//get the single element
				// System.out.println(query);
				top = 100;
			}
			// keep the UUID of current insert
			String uuid = UUID.randomUUID().toString();
			// Build insert statement based on translation logic
			String insertTupleQuery = insertTuple(t.getHead(), uuid, curr, top);
			System.out.println(insertTupleQuery);
			// queue insert statement
			for (TupleTree eachTree : t.getSubTrees()) {
				read(eachTree, uuid, curr);
			}
		}
	}

	/**
	 * This method builds and returns a query to retrieve the PK value of a
	 * parent tuple
	 * 
	 * @param table
	 * @param column
	 * @param parentUUID
	 * @return
	 */
	private String selectParentId(final String table, final String column,
			final String parentUUID) {
		return new SQL() {
			{
				SELECT(table + "." + column);
				FROM(table);
				WHERE(table + ".uuid = " + parentUUID);

			}
		}.toString();
	}

	/**
	 * 
	 * @param tuple
	 * @param uuid
	 * @param curr
	 * @param top
	 * @return
	 */
	private String insertTuple(final TupleType tuple, final String uuid,
			final String curr, final Object top) {
		return new SQL() {
			{
				INSERT_INTO(tuple.getTable());
				// access matches of tuple
				for (MatchType match : tuple.getMatches()) {
					// if match default value is auto increment, skip it
					if (match.getDefaultValue()
							.equals(MatchConstants.AI)) {
						continue;
					}
					String selectQuery = null;// composed select query

					// 1. If a match doesn’t have the right side, it should
					// insert the default value
					if (match.getRight() == null) {
						// 8. TOP – Should use the PK value of the parent tuple
						if (match.getDefaultValue().equals(MatchConstants.TOP)) {
							VALUES(match.getLeft().getColumn(),
									sourceDAO.castValue(top));
						} else {
							// use default value
							VALUES(match.getLeft().getColumn(),
									sourceDAO.castValue(match.getDefaultValue()));
						}
					} else {
						selectQuery = selectMatch(match, curr);// generate
																// select query
						final List<List<Object>> results = sourceDAO
								.executeQuery(selectQuery);// execute select statement
						final Object value = results.get(0).get(0);//gets the only one result
						
						System.out.println(selectQuery);

						// 5. If default value of a match is SKIP, the entire
						// tuple must be skipped if the match select doesn’t
						// find any value.
						// generate select statement
						if (match.getDefaultValue().equals(MatchConstants.SKIP)
								&& value == null) {
							continue;
						}
						// 4. If left side of a match is required but right side
						// is not, it must insert default value in case the
						// right side select doesn’t find any value
						else if (match.getLeft().isIsRequired()
								.equals(MatchConstants.YES)
								&& match.getRight().isIsRequired()
										.equals(MatchConstants.NO)
								&& value == null) {
							// use default value
							VALUES(match.getLeft().getColumn(),
									sourceDAO.castValue(match.getDefaultValue()));
						} else {
							VALUES(match.getLeft().getColumn(),
									sourceDAO.castValue(value));
						}
					}
				}
			}
		}.toString();
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
									+ " = " + sourceDAO.castValue(referencedValue));
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
									+ " = " + sourceDAO.castValue(referencedValue));
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
									+ " = " + sourceDAO.castValue(referencedValue));
						}
					} else {
						WHERE(referencedTable + "." + referencedColumn + " = "
								+ sourceDAO.castValue(referencedValue));
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
