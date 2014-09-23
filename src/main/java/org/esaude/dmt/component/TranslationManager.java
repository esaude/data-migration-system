package org.esaude.dmt.component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.ibatis.jdbc.SQL;
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

	/**
	 * Parameterized constructor
	 * 
	 * @param tree
	 */
	public TranslationManager(TupleTree tree) {
		this.tree = tree;
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
		String selectQuery = this.selectCurrs(t.getHead(), parentCurr);
		//System.out.println(selectQuery);
		System.out
				.println("---------------------------------------------------------");

		List<String> all = new ArrayList<String>();// the result of the select
													// above. How many
		// inserts to do?
		all.add("UEOQEU11/02/2006");// TODO: Must be removed
		for (String curr : all) {
			// Get the primary key of parent reference. Select from target DB,
			if(parentUUID != null) {
				// find the PK match of parent tuple
				MatchType pkMatch = findPkMatch(t.getParent().getHead());
				String query = selectParentId(t.getParent().getHead().getTable(), pkMatch.getLeft().getColumn(), parentUUID);
				
				System.out.println(query);
			}
			
			// using q UUID if not null
			// Build insert statement based on translation logic
			// keep the UUID of current insert
			
			String uuid = UUID.randomUUID().toString();
			// queue insert statement
			for (TupleTree eachTree : t.getSubTrees()) {

				read(eachTree, uuid, curr);
			}
		}
	}

	private String selectParentId(final String table, final String column, final String parentUUID) {
		return new SQL() {{
		    SELECT(table + "." + column);
		    FROM(table);
		    WHERE(table + ".uuid = " + parentUUID);
		    
		  }}.toString();
	}

	/**
	 * This method reads the tuple tree recursively
	 * 
	 * @param tuple
	 * @param parentUddi
	 *            the UDDI of the parent tuple already inserted. Used to access
	 *            the FK value
	 * @param curr
	 *            the value used to select the HEAD tuple
	 */
	private void read(TupleType tuple, String parentUddi, Object curr) {
		if (tuple == null)
			return;

		// access matches of tuple
		for (MatchType match : tuple.getMatches()) {
			String selectQuery = null;// composed select query
			Object retrivedFromSelect = null;// keep the SELECT result for the
												// match
			// 1. If the match doesn’t have a right side, it should insert
			// default value
			if (match.getRight() == null) {
				// TODO: use default value
			} else {
				// TODO: generate select statement
				// TODO: execute select statement
			}
			// 3. If there is no compatibility left to right but right to left,
			// it should insert the value selected in the right transformed if
			// necessary.
			if (match.getValidationStatuses().contains(
					ValidationStatuses.RIGHT_TO_LEFT_DATATYPE_COMPATIBILITY)) {
				// TODO: call data transformation algorithm
			}
			// 4. If left side is required but right side is not, it must insert
			// default value
			// in case the right side select doesn’t find the value
			if (match.getLeft().isIsRequired().equals(MatchConstants.YES)
					&& match.getRight() != null
					&& match.getRight().isIsRequired()
							.equals(MatchConstants.NO)
					&& retrivedFromSelect == null) {
				// TODO: use default value
			}
			// 5. If default value is SKIP, the tuple must be skipped.
			if (match.getDefaultValue() == null) {
				// TODO: Tuple must be skipped
				// match.setTupleId(null);
			}

			// 6. If left side size is smaller than right side size,
			// should transform the data selected in the right side, if
			// necessary
			if (match.getValidationStatuses().contains(
					ValidationStatuses.LEFT_TO_RIGHT_SIZE_INCOMPATIBILITY)) {
				// TODO: Call the Data Size Transform algorithm
			}

			// 7. If there is a value match, it must insert the value that the
			// value match points to
			if (match.getRight() != null) {
				// TODO: Call Insert algorithm.
			}
		}
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
				boolean isFirstDirectReference = true;//used to flag whether or not the reference 
				//is the first direct, in case there are many direct references
				// construct the select query using the L-References of the PK
				// match of the tuple
				for (ReferenceType reference : pkMatch.getReferences().values()) {

					String referencedTable = reference.getReferenced().getTable();
					String referencedColumn = reference.getReferenced().getColumn();
					String referencedValue = reference.getReferencedValue().toString();
					// check whether the reference is direct or indirect
					if (reference.getPredecessor().equals(Integer.valueOf(0)) && isFirstDirectReference) {
						// the reference should be used in the result set
						SELECT(referencedTable + "." + referencedColumn);
						FROM(referencedTable);
						if (!referencedValue.equals(MatchConstants.ALL)) {
							WHERE(referencedTable + "." + referencedColumn + " = " + curr);
						}
						isFirstDirectReference = false;
					} else {
						String referenceeTable = reference.getReferencee().getTable();
						String referenceeColumn = reference.getReferencee().getColumn();
						INNER_JOIN(referencedTable);
						WHERE(referenceeTable + "." + referenceeColumn + " = " + referencedTable + "." + referencedColumn);
						// in case the referenced value is not EQUALS
						if (!referencedValue.equals(MatchConstants.EQUALS)) {
							WHERE(referenceeTable + "." + referenceeColumn + " = " + referencedValue);
						}
					}
				}
			}
		}.toString();
	}
	
	/**
	 * This method finds and returns the PK match of the tuple
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
