package org.esaude.dmt.component;

import java.util.Calendar;

import org.esaude.dmt.helper.EventCodeContants;
import org.esaude.dmt.helper.MatchConstants;
import org.esaude.dmt.helper.ProcessPhases;
import org.esaude.dmt.helper.SystemException;
import org.esaude.dmt.util.MatchBuilder;
import org.esaude.dmt.util.TupleBuilder;
import org.esaude.dmt.util.TupleTree;
import org.esaude.dmt.util.datatypemapping.DatatypeMappingReader;
import org.esaude.dmt.util.log.Error;
import org.esaude.dmt.util.log.EventCode;
import org.esaude.dmt.util.log.LogWriter;
import org.esaude.dmt.xls.Sheets;
import org.esaude.dmt.xls.XlsProcessor;

/**
 * The manager that performs the validation of the matches
 * 
 * @author Valério João
 * @since 28-08-2014
 *
 */
public class ValidationManager {
	private XlsProcessor processor;
	private LogWriter writer;
	private DatatypeMappingReader dmr;
	private TupleTree tree;

	public ValidationManager(XlsProcessor processor, LogWriter writer,
			DatatypeMappingReader dmr) {
		this.processor = processor;
		this.writer = writer;
		this.dmr = dmr;
	}

	public ValidationManager() {
		processor = new XlsProcessor();
		writer = LogWriter.getWriter();
		dmr = new DatatypeMappingReader();
		dmr.process();
	}

	/**
	 * Executes the validation logic
	 * 
	 * @return
	 * @throws SystemException
	 */
	public boolean execute() throws SystemException {
		TupleBuilder tupleBuilder = new TupleBuilder();
		MatchBuilder matchBuilder = null;
		// read tuple
		for (int i = Sheets.TUPLE.ROW_START; i < processor
				.getSize(Sheets.TUPLE.INDEX); i++) {
			// set tuple values
			createTuple(tupleBuilder, i);
		}
		// process and validate tuple matches
		for (int j = Sheets.MATCH_L_TO_R.ROW_START; j < processor
				.getSize(Sheets.MATCH_L_TO_R.INDEX); j++) {
			// check if match has right side
			if (processor.process(Sheets.MATCH_L_TO_R.INDEX,
					Sheets.MATCH_L_TO_R.TABLE_R, j).equals(MatchConstants.NA)) {
				if (!validateDefaultValue(j))
					return false;
			}
			// check if left side is required and right is not
			if (processor.process(Sheets.MATCH_L_TO_R.INDEX,
					Sheets.MATCH_L_TO_R.REQUIRED_L, j).equals(
					MatchConstants.YES)
					&& processor.process(Sheets.MATCH_L_TO_R.INDEX,
							Sheets.MATCH_L_TO_R.REQUIRED_R, j).equals(
							MatchConstants.NO)) {
				if (!validateDefaultValue(j))
					return false;

			}
			// check datatype compatibility
			if (!validateDatatypeCompatibility(j)) {
				return false;
			}
			// create match and add to tuple
			matchBuilder = new MatchBuilder();
			createMatch(matchBuilder, j);

			// create left and right sides of match
			createMatchSides(matchBuilder, j);

			// TODO: Create left and right references

			// add match to tuple
			tupleBuilder.process()
					.getTree(matchBuilder.getMatch().getTupleId()).getHead()
					.getMatch().add(matchBuilder.getMatch());

		}
		//update the tree
		tree = tupleBuilder.process();
		return true;
	}

	/**
	 * Creates instances of {@link TupleType } using a builder
	 * @param tupleBuilder
	 * @param index
	 * @throws SystemException
	 */
	private void createTuple(final TupleBuilder tupleBuilder, final int index)
			throws SystemException {
		tupleBuilder
				.createTuple(
						Integer.valueOf(processor.process(Sheets.TUPLE.INDEX,
								Sheets.TUPLE.ID, index)),
						processor.process(Sheets.TUPLE.INDEX,
								Sheets.TUPLE.TERMINOLOGY, index),
						processor.process(Sheets.TUPLE.INDEX,
								Sheets.TUPLE.TABLE, index),
						processor.process(Sheets.TUPLE.INDEX,
								Sheets.TUPLE.DESC, index),
						(processor.process(Sheets.TUPLE.INDEX,
								Sheets.TUPLE.PREDECESSOR, index).equals(
								MatchConstants.NA) ? null : Integer
								.valueOf(processor.process(Sheets.TUPLE.INDEX,
										Sheets.TUPLE.PREDECESSOR, index))));

	}

	/**
	 * Creates instances of {@link MatchType } using a builder
	 * @param matchBuilder
	 * @param row
	 */
	private void createMatch(final MatchBuilder matchBuilder, final int row) {

		Integer tupleId = Integer.valueOf(processor.process(
				Sheets.MATCH_L_TO_R.INDEX, Sheets.MATCH_L_TO_R.TUPLE_ID, row));
		Integer matchId = Integer.valueOf(processor.process(
				Sheets.MATCH_L_TO_R.INDEX, Sheets.MATCH_L_TO_R.MATCH_ID, row));
		String terminology = processor.process(Sheets.MATCH_L_TO_R.INDEX,
				Sheets.MATCH_L_TO_R.TERMINOLOGY, row);
		Object valueMatch = processor.process(Sheets.MATCH_L_TO_R.INDEX,
				Sheets.MATCH_L_TO_R.VALUE_MATCH, row);
		Object defaultValue = processor.process(Sheets.MATCH_L_TO_R.INDEX,
				Sheets.MATCH_L_TO_R.DEFAULT_VALUE, row);
		// create the match using builder
		matchBuilder.createMatch(tupleId, matchId, terminology, valueMatch,
				defaultValue);

	}

	/**
	 * Creates instances of {@link MatchSideType } using a builder
	 * @param matchBuilder
	 * @param row
	 * @throws SystemException
	 */
	private void createMatchSides(final MatchBuilder matchBuilder, final int row)
			throws SystemException {
		// create left side of match
		String table_l = null;
		String column_l = processor.process(Sheets.MATCH_L_TO_R.INDEX,
				Sheets.MATCH_L_TO_R.COLUMN_L, row);
		String datatype_l = processor.process(Sheets.MATCH_L_TO_R.INDEX,
				Sheets.MATCH_L_TO_R.DATATYPE_L, row);
		Integer size_l = Integer.valueOf(processor.process(
				Sheets.MATCH_L_TO_R.INDEX, Sheets.MATCH_L_TO_R.SIZE_L, row));
		String required_l = processor.process(Sheets.MATCH_L_TO_R.INDEX,
				Sheets.MATCH_L_TO_R.REQUIRED_L, row);

		matchBuilder.createMatchSide(table_l, column_l, datatype_l, size_l,
				required_l, MatchBuilder.LEFT_SIDE);
		// create right side match if there is any
		if (!processor.process(Sheets.MATCH_L_TO_R.INDEX,
				Sheets.MATCH_L_TO_R.TABLE_R, row).equals(MatchConstants.NA)) {
			String table_r = processor.process(Sheets.MATCH_L_TO_R.INDEX,
					Sheets.MATCH_L_TO_R.TABLE_R, row);
			String column_r = processor.process(Sheets.MATCH_L_TO_R.INDEX,
					Sheets.MATCH_L_TO_R.COLUMN_R, row);
			String datatype_r = processor.process(Sheets.MATCH_L_TO_R.INDEX,
					Sheets.MATCH_L_TO_R.DATATYPE_R, row);
			Integer size_r = Integer
					.valueOf(processor.process(Sheets.MATCH_L_TO_R.INDEX,
							Sheets.MATCH_L_TO_R.SIZE_R, row));
			String required_r = processor.process(Sheets.MATCH_L_TO_R.INDEX,
					Sheets.MATCH_L_TO_R.REQUIRED_R, row);

			matchBuilder.createMatchSide(table_r, column_r, datatype_r, size_r,
					required_r, MatchBuilder.RIGHT_SIDE);
		}
	}

	/**
	 * Validates the datatypes compatibilities in both directions: left_to_right
	 * and righnt_to_left
	 * @param row
	 * @return
	 */
	private boolean validateDatatypeCompatibility(int row) {
		if (!processor.process(Sheets.MATCH_L_TO_R.INDEX,
				Sheets.MATCH_L_TO_R.TABLE_R, row).equals(MatchConstants.NA)) {
			// check compatibility left to right
			if (!dmr.verify(processor.process(Sheets.MATCH_L_TO_R.INDEX,
					Sheets.MATCH_L_TO_R.DATATYPE_L, row), processor.process(
					Sheets.MATCH_L_TO_R.INDEX, Sheets.MATCH_L_TO_R.DATATYPE_R,
					row))
					&& !dmr.verify(processor.process(Sheets.MATCH_L_TO_R.INDEX,
							Sheets.MATCH_L_TO_R.DATATYPE_R, row), processor
							.process(Sheets.MATCH_L_TO_R.INDEX,
									Sheets.MATCH_L_TO_R.DATATYPE_L, row))) {
				// check compatibility right to left
				if (!dmr.verify(processor.process(Sheets.MATCH_L_TO_R.INDEX,
						Sheets.MATCH_L_TO_R.DATATYPE_R, row), processor.process(
						Sheets.MATCH_L_TO_R.INDEX,
						Sheets.MATCH_L_TO_R.DATATYPE_L, row))) {
					// write error log
					writer.writeLog(new Error(EventCode
							.getString(EventCodeContants.ERR002),
							ProcessPhases.VALIDATION, Calendar.getInstance()
									.getTime(), EventCodeContants.ERR002,
							Integer.valueOf(processor.process(
									Sheets.MATCH_L_TO_R.INDEX,
									Sheets.MATCH_L_TO_R.TUPLE_ID, row)), Integer
									.valueOf(processor.process(
											Sheets.MATCH_L_TO_R.INDEX,
											Sheets.MATCH_L_TO_R.MATCH_ID, row)),
							Sheets.MATCH_L_TO_R.NAME));

					return false;// end execution
				} else {
					// TODO: Log warning but don't return
				}

			}
		}
		return true;
	}

	/**
	 * Checks the default values constraints based on validation logic
	 * @param index
	 * @return
	 */
	private boolean validateDefaultValue(int index) {
		Object defaultValue = processor.process(Sheets.MATCH_L_TO_R.INDEX,
				Sheets.MATCH_L_TO_R.DEFAULT_VALUE, index);
		// check if match has a default value
		if (defaultValue.equals(MatchConstants.NA) || defaultValue.equals("")) {
			writer.writeLog(new Error(EventCode
					.getString(EventCodeContants.ERR001),
					ProcessPhases.VALIDATION, Calendar.getInstance().getTime(),
					EventCodeContants.ERR001, Integer.valueOf(processor
							.process(Sheets.MATCH_L_TO_R.INDEX,
									Sheets.MATCH_L_TO_R.TUPLE_ID, index)),
					Integer.valueOf(processor.process(
							Sheets.MATCH_L_TO_R.INDEX,
							Sheets.MATCH_L_TO_R.MATCH_ID, index)),
					Sheets.MATCH_L_TO_R.NAME));

			return false;// end execution
		}
		// TODO: Check if default value if compatible with datatype
		return true;
	}

	public TupleTree getTree() {
		return tree;
	}
}
