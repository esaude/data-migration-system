package org.esaude.dmt.component;

import org.esaude.dmt.helper.MatchConstants;
import org.esaude.dmt.helper.SystemException;
import org.esaude.dmt.helper.ValidationStatuses;
import org.esaude.dmt.util.TupleTree;
import org.esaude.matchingschema.MatchType;
import org.esaude.matchingschema.TupleType;

/**
 * This manager is responsible to generate SQL queries to either
 * SELECT or INSERT data from source into target databases
 * @author Valério João
 * @since 5-09-2014
 *
 */
public class TranslationManager {
	private TupleTree tree;
	
	/**
	 * Parameterized constructor
	 * @param tree
	 */
	public TranslationManager(TupleTree tree) {
		this.tree = tree;
	}
	
	public boolean execute() throws SystemException {
		
		return true;
	}
	
	/**
	 * This method reads the tuple tree recursively
	 */
	private void read(TupleTree tree) {
		if (tree == null)
			return;

		TupleType tuple = tree.getHead();
		// access matches of tuple
		for (MatchType match : tuple.getMatch()) {
			String selectQuery = null;//composed select query
			Object retrivedFromSelect = null;//keep the SELECT result for the match
			// 1. If the match doesn’t have a right side, it should insert
			// default value
			if (match.getRight() == null) {
				//TODO: use default value
			} else {
				//TODO: generate select statement
				//TODO: execute select statement
			}
			// 3. If there is no compatibility left to right but right to left,
			// it should insert
			// the value selected in the right transformed if necessary.
			if (match.getValidationStatuses().contains(
					ValidationStatuses.RIGHT_TO_LEFT_DATATYPE_COMPATIBILITY)) {
				//TODO: call datatype transformation algorithm
			}
			//4.	If left side is required but right side is not, it must insert default value 
			//		in case the right side select doesn’t find the value
			if(match.getLeft().isIsRequired().equals(MatchConstants.YES) &&
					match.getRight() != null &&
					match.getRight().isIsRequired().equals(MatchConstants.NO) &&
					retrivedFromSelect == null) {
				//TODO: use default value
			}
			
			// 5.	If default value is SKIP, the tuple must be skipped.
			 if (match.getDefaultValue()==null){
			     //TODO: Tuple must be skipped
				 //match.setTupleId(null);
			 }
			
			 // 6.	If left side size is smaller than right side size, 
			 //should transform the data selected in the right side, if necessary
			 if (match.getValidationStatuses().contains(
					 ValidationStatuses.LEFT_TO_RIGHT_SIZE_INCOMPATIBILITY)){
				     //TODO: Call the Data Size Transform algorithm 
			 }
			 
			 // 7.	If there is a value match, it must insert the value that the value match points to
			 if (match.getRight()!=null){
				 //TODO: Call Insert algorithm
			 }
		 
		}
	}

}
