package org.esaude.dmt.util;

import org.esaude.dmt.helper.SystemException;
import org.esaude.matchingschema.TupleType;

/**
 * A builder that creates the {@link TupleType } instances
 * @author Valério João
 * @since 27-08-2014
 *
 */
public class TupleBuilder {
	private TupleType tupleType;
	private final TupleTree tree = new TupleTree();

	public TupleBuilder() {
	}

	/**
	 * Create a tuple with basic data
	 * 
	 * @param id
	 * @param terminology
	 * @param table
	 * @param desc
	 * @return
	 */
	public TupleBuilder createTuple(final Integer id,
			final String terminology, 
			final String table, 
			final String desc,
			final Integer parent) throws SystemException {
		tupleType = new TupleType();
		tupleType.setId(id);
		tupleType.setTerminology(terminology);
		tupleType.setTable(table);
		tupleType.setDesc(desc);
		//set the head of the three if doesn't exist one
		if(tree.getHead() == null) {
			tree.setHead(tupleType);
		} else {
			if(parent == null) {
				throw new SystemException("Value of parent parameter cannot be null for leaf tuple");
			}
			tree.addLeaf(tupleType);
		}
		return this;
	}
	
	
	public TupleTree process() {
		return tree;
	}
}
