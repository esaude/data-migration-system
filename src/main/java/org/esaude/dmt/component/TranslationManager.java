package org.esaude.dmt.component;

import java.util.List;

import org.esaude.dmt.helper.MatchConstants;
import org.esaude.dmt.helper.SystemException;
import org.esaude.dmt.helper.ValidationStatuses;
import org.esaude.dmt.util.TupleTree;
import org.esaude.matchingschema.MatchType;
import org.esaude.matchingschema.TupleType;

/**
 * This manager is responsible to generate SQL queries to either SELECT or
 * INSERT data from source into target databases
 * 
 * @author Valério João
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
		read(tree, null);

		return true;
	}

	private void read(final TupleTree t, final String parentUUID) {
		// how many tuples?
		// select from source using the reference of the target side PK´s r_reference
		List<String> currs = null;// the result of the select above. How many
									// inserts to do?
		for (String curr : currs) {
			// Get the primary key of parent reference. Select from target DB,
			// using the UUID if not null
			// Build insert statement based on translation logic
			// keep the UUID of current insert
			String uuid = null;
			// queue insert statement
			for (TupleTree eachTree : t.getSubTrees()) {

				read(eachTree, uuid);
			}
		}
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
		for (MatchType match : tuple.getMatch()) {
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
		}
	}
}
