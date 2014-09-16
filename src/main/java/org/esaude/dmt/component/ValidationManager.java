package org.esaude.dmt.component;

import java.util.Calendar;

import org.esaude.dmt.helper.EventCodeContants;
import org.esaude.dmt.helper.MatchConstants;
import org.esaude.dmt.helper.ProcessPhases;
import org.esaude.dmt.helper.SystemException;
import org.esaude.dmt.util.MatchBuilder;
import org.esaude.dmt.util.ReferenceBuilder;
import org.esaude.dmt.util.TupleBuilder;
import org.esaude.dmt.util.TupleTree;
import org.esaude.dmt.util.datatypemapping.DatatypeMappingReader;
import org.esaude.dmt.util.log.Error;
import org.esaude.dmt.util.log.Event;
import org.esaude.dmt.util.log.EventCode;
import org.esaude.dmt.util.log.Info;
import org.esaude.dmt.util.log.LogWriter;
import org.esaude.dmt.xls.Sheets;
import org.esaude.dmt.xls.XlsProcessor;
import org.esaude.matchingschema.ReferenceType;
import org.esaude.matchingschema.TupleType;

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
	private EventCode eventCode;
	// counters for log usage
	private int warningCount, tupleCount, matchCount = 0;

	public ValidationManager(XlsProcessor processor, LogWriter writer,
			DatatypeMappingReader dmr, EventCode eventCode) {
		this.processor = processor;
		this.writer = writer;
		this.dmr = dmr;
		this.eventCode = eventCode;
	}

	public ValidationManager() {
		processor = new XlsProcessor();
		writer = LogWriter.getWriter();
		dmr = new DatatypeMappingReader();
		dmr.process();
		eventCode = new EventCode();
	}

	/**
	 * Executes the validation logic
	 * 
	 * @return
	 * @throws SystemException
	 */
	public boolean execute() throws SystemException {

		// log start of process
		writeSimpleInfoLog(ProcessPhases.VALIDATION,
				eventCode.getString(EventCodeContants.INF001));
		writeSimpleInfoLog(null,
				eventCode.getString(EventCodeContants.SEPARATOR));

		TupleBuilder tupleBuilder = new TupleBuilder();
		MatchBuilder matchBuilder = null;
		// read tuple
		for (int i = Sheets.TUPLE.ROW_START; i < processor
				.getSize(Sheets.TUPLE.INDEX); i++) {
			// set tuple values
			createTuple(tupleBuilder, i);
			tupleCount++;//keep counting the number of tuples affected
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
			// check primary key conditions
			if (!validatePk(j)) {
				return false;
			}
			// create match and add to tuple
			matchBuilder = new MatchBuilder();
			createMatch(matchBuilder, j);

			// create left and right sides of match
			createMatchSides(matchBuilder, j);

			// add match to tuple
			tupleBuilder.process()
					.getTree(matchBuilder.getMatch().getTupleId()).getHead()
					.getMatches().add(matchBuilder.getMatch());
			matchCount++;//keep counting the number of matches affected
		}
		// validate left references
		for (int row = Sheets.REFERENCES_L.ROW_START; row < processor
				.getSize(Sheets.REFERENCES_L.INDEX); row++) {

			// create left reference
			ReferenceBuilder referenceBuilder = new ReferenceBuilder();

			createLeftReference(referenceBuilder, row);

			Integer tupleId = Integer.valueOf(processor.process(
					Sheets.REFERENCES_L.INDEX, Sheets.REFERENCES_L.TUPLE_ID,
					row));

			TupleType tuple = tupleBuilder.process().getTree(tupleId).getHead();
			// validate referenced value
			if (!validateLReferenceSequenceValue(
					referenceBuilder.processSingle(), tuple, row)) {
				return false;
			}
			// add reference to tuple
			tuple.getLeftReference().put(
					referenceBuilder.processSingle().getId(),
					referenceBuilder.processSingle());

		}
		// update the tree
		tree = tupleBuilder.process();
		// log end of process
		logEndOfProcess();

		return true;
	}

	/**
	 * Write the log report at the end of the validation process
	 */
	private void logEndOfProcess() {
		// check if there is any warning
		if (warningCount > 0) {
			writeSimpleInfoLog(ProcessPhases.VALIDATION,
					eventCode.getString(EventCodeContants.INF003));
			writeSimpleInfoLog(null,
					eventCode.getString(EventCodeContants.SEPARATOR));
		} else {
			writeSimpleInfoLog(ProcessPhases.VALIDATION,
					eventCode.getString(EventCodeContants.INF002));
			writeSimpleInfoLog(null,
					eventCode.getString(EventCodeContants.SEPARATOR));
		}
		writeSimpleInfoLog(
				null,
				tupleCount + " "
						+ eventCode.getString(EventCodeContants.INF004));
		writeSimpleInfoLog(
				null,
				matchCount + " "
						+ eventCode.getString(EventCodeContants.INF005));
		writeSimpleInfoLog(
				null,
				warningCount + " "
						+ eventCode.getString(EventCodeContants.INF006));
	}

	/**
	 * Writes a simple text report containing the phase and message
	 * @param phase
	 * @param text
	 */
	private void writeSimpleInfoLog(String phase, String text) {
		Event event = new Info();
		event.setFase(phase);
		event.setDescricao(text);
		writer.writeLog(event);
	}

	/**
	 * Create instance of {@link ReferenceType } using builder
	 * 
	 * @param referenceBuilder
	 * @param row
	 * @throws SystemException
	 */
	private void createLeftReference(ReferenceBuilder referenceBuilder, int row)
			throws SystemException {
		Integer referenceId = Integer.valueOf(processor.process(
				Sheets.REFERENCES_L.INDEX, Sheets.REFERENCES_L.ID, row));
		String datatype = processor.process(Sheets.REFERENCES_L.INDEX,
				Sheets.REFERENCES_L.DATATYPE, row);
		Integer size = Integer.valueOf(processor.process(
				Sheets.REFERENCES_L.INDEX, Sheets.REFERENCES_L.SIZE, row));
		String predecessorStr = processor.process(Sheets.REFERENCES_L.INDEX,
				Sheets.REFERENCES_L.SEQUENCE, row);
		Integer predecessor = (predecessorStr.equals(MatchConstants.NA)) ? 0
				: Integer.valueOf(predecessorStr);
		String nameDesc = processor.process(Sheets.REFERENCES_L.INDEX,
				Sheets.REFERENCES_L.NAME_DESC, row);
		// referencee side values
		String table_l = processor.process(Sheets.REFERENCES_L.INDEX,
				Sheets.REFERENCES_L.REFERENCE_TABLE, row);
		String column_l = processor.process(Sheets.REFERENCES_L.INDEX,
				Sheets.REFERENCES_L.REFERENCE_COLUMN, row);
		// referenced side values
		String table_r = processor.process(Sheets.REFERENCES_L.INDEX,
				Sheets.REFERENCES_L.REFERENCED_TABLE, row);
		String column_r = processor.process(Sheets.REFERENCES_L.INDEX,
				Sheets.REFERENCES_L.REFERENCED_COLUMN, row);
		// referenced value
		Object value = processor.process(Sheets.REFERENCES_L.INDEX,
				Sheets.REFERENCES_L.REFERENCED_VALUE, row);

		referenceBuilder
				.createReference(referenceId, datatype, size, nameDesc,
						predecessor)
				.createReferenceSide(table_l, column_l,
						ReferenceBuilder.REFERENCEE)
				.createReferenceSide(table_r, column_r,
						ReferenceBuilder.REFERENCED)
				.createReferencedValue(value);
	}

	/**
	 * Validates the sequence of left reference according to validation logic
	 * 
	 * @param reference
	 * @param tuple
	 * @param row
	 * @return
	 */
	private boolean validateLReferenceSequenceValue(ReferenceType reference,
			TupleType tuple, int row) {

		if (tuple.getTable().equals(reference.getReferencee().getTable())) {
			// If a left reference is direct (the reference table is equal to
			// the tuple table)
			// then it should not have sequence value, an error must be logged
			// otherwise.
			if (!reference.getPredecessor().equals(Integer.valueOf(0))) {
				// write error log
				writer.writeLog(new Error(eventCode
						.getString(EventCodeContants.ERR008),
						ProcessPhases.VALIDATION, Calendar.getInstance()
								.getTime(), EventCodeContants.ERR008, Integer
								.valueOf(processor.process(
										Sheets.REFERENCES_L.INDEX,
										Sheets.REFERENCES_L.TUPLE_ID, row)),
						Integer.valueOf(processor.process(
								Sheets.REFERENCES_L.INDEX,
								Sheets.REFERENCES_L.ID, row)),
						Sheets.REFERENCES_L.NAME));
				return false;
			}

		} else {
			if (reference.getPredecessor().equals(Integer.valueOf(0))) {
				// write error log
				writer.writeLog(new Error(eventCode
						.getString(EventCodeContants.ERR009),
						ProcessPhases.VALIDATION, Calendar.getInstance()
								.getTime(), EventCodeContants.ERR009, Integer
								.valueOf(processor.process(
										Sheets.REFERENCES_L.INDEX,
										Sheets.REFERENCES_L.TUPLE_ID, row)),
						Integer.valueOf(processor.process(
								Sheets.REFERENCES_L.INDEX,
								Sheets.REFERENCES_L.ID, row)),
						Sheets.REFERENCES_L.NAME));
				return false;
			}
			// The sequence of a left reference must belong to the same tuple,
			// an error must be logged otherwise
			if (!tuple.getLeftReference().containsKey(
					reference.getPredecessor())) {
				// write error log
				writer.writeLog(new Error(eventCode
						.getString(EventCodeContants.ERR010),
						ProcessPhases.VALIDATION, Calendar.getInstance()
								.getTime(), EventCodeContants.ERR010, Integer
								.valueOf(processor.process(
										Sheets.REFERENCES_L.INDEX,
										Sheets.REFERENCES_L.TUPLE_ID, row)),
						Integer.valueOf(processor.process(
								Sheets.REFERENCES_L.INDEX,
								Sheets.REFERENCES_L.ID, row)),
						Sheets.REFERENCES_L.NAME));
				return false;
			}
			// The REFERENCE TABLE of an indirect reference must be the same as
			// the REFERENCED TABLE of its sequence, an error must be logged
			// otherwise
			String indirectReferenceTable = reference.getReferencee()
					.getTable();
			String sequenceReferencedTable = tuple.getLeftReference()
					.get(reference.getPredecessor()).getReferenced().getTable();
			if (!indirectReferenceTable.equals(sequenceReferencedTable)) {
				// write error log
				writer.writeLog(new Error(eventCode
						.getString(EventCodeContants.ERR011),
						ProcessPhases.VALIDATION, Calendar.getInstance()
								.getTime(), EventCodeContants.ERR011, Integer
								.valueOf(processor.process(
										Sheets.REFERENCES_L.INDEX,
										Sheets.REFERENCES_L.TUPLE_ID, row)),
						Integer.valueOf(processor.process(
								Sheets.REFERENCES_L.INDEX,
								Sheets.REFERENCES_L.ID, row)),
						Sheets.REFERENCES_L.NAME));
				return false;
			}
		}
		return true;
	}

	/**
	 * Creates instances of {@link TupleType } using a builder
	 * 
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
	 * 
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
		String pk = processor.process(Sheets.MATCH_L_TO_R.INDEX,
				Sheets.MATCH_L_TO_R.PK, row);
		// create the match using builder
		matchBuilder.createMatch(tupleId, matchId, terminology, valueMatch,
				defaultValue, pk);

	}

	/**
	 * Creates instances of {@link MatchSideType } using a builder
	 * 
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
	 * 
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
						Sheets.MATCH_L_TO_R.DATATYPE_R, row), processor
						.process(Sheets.MATCH_L_TO_R.INDEX,
								Sheets.MATCH_L_TO_R.DATATYPE_L, row))) {
					// write error log
					writer.writeLog(new Error(eventCode
							.getString(EventCodeContants.ERR002),
							ProcessPhases.VALIDATION, Calendar.getInstance()
									.getTime(), EventCodeContants.ERR002,
							Integer.valueOf(processor.process(
									Sheets.MATCH_L_TO_R.INDEX,
									Sheets.MATCH_L_TO_R.TUPLE_ID, row)),
							Integer.valueOf(processor.process(
									Sheets.MATCH_L_TO_R.INDEX,
									Sheets.MATCH_L_TO_R.MATCH_ID, row)),
							Sheets.MATCH_L_TO_R.NAME));

					return false;// end execution
				} else {
					// TODO: Log warning but don't return
					warningCount++;
				}

			}
		}
		return true;
	}

	/**
	 * Checks the default values constraints based on validation logic
	 * 
	 * @param index
	 * @return
	 */
	private boolean validateDefaultValue(final int index) {
		Object defaultValue = processor.process(Sheets.MATCH_L_TO_R.INDEX,
				Sheets.MATCH_L_TO_R.DEFAULT_VALUE, index);
		// check if match has a default value
		if (defaultValue.equals(MatchConstants.NA) || defaultValue.equals("")) {
			writer.writeLog(new Error(eventCode
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
		// in case the default value is AI, the left datatype must be INT or
		// compatible
		else if (defaultValue.equals(MatchConstants.AI)
				&& !processor.process(Sheets.MATCH_L_TO_R.INDEX,
						Sheets.MATCH_L_TO_R.DATATYPE_L, index).equals(
						MatchConstants.INT)) {
			writer.writeLog(new Error(eventCode
					.getString(EventCodeContants.ERR003),
					ProcessPhases.VALIDATION, Calendar.getInstance().getTime(),
					EventCodeContants.ERR003, Integer.valueOf(processor
							.process(Sheets.MATCH_L_TO_R.INDEX,
									Sheets.MATCH_L_TO_R.TUPLE_ID, index)),
					Integer.valueOf(processor.process(
							Sheets.MATCH_L_TO_R.INDEX,
							Sheets.MATCH_L_TO_R.MATCH_ID, index)),
					Sheets.MATCH_L_TO_R.NAME));
			return false;
		}
		// If default value is AI/SKIP/TRUE or AI/SKIP/FALSE, then the right
		// side datatype must be logic (true, false)
		else if (defaultValue.equals(MatchConstants.AI_SKIP_TRUE)
				|| defaultValue.equals(MatchConstants.AI_SKIP_FALSE)) {
			String datatype = processor.process(Sheets.MATCH_L_TO_R.INDEX,
					Sheets.MATCH_L_TO_R.DATATYPE_R, index);
			if (!dmr.verify(MatchConstants.BOOL, datatype)) {
				writer.writeLog(new Error(eventCode
						.getString(EventCodeContants.ERR004),
						ProcessPhases.VALIDATION, Calendar.getInstance()
								.getTime(), EventCodeContants.ERR004, Integer
								.valueOf(processor.process(
										Sheets.MATCH_L_TO_R.INDEX,
										Sheets.MATCH_L_TO_R.TUPLE_ID, index)),
						Integer.valueOf(processor.process(
								Sheets.MATCH_L_TO_R.INDEX,
								Sheets.MATCH_L_TO_R.MATCH_ID, index)),
						Sheets.MATCH_L_TO_R.NAME));
				return false;
			}
		}
		// If right side is required, then the default value cannot not be SKIP
		else if (defaultValue.equals(MatchConstants.SKIP)
				&& processor.process(Sheets.MATCH_L_TO_R.INDEX,
						Sheets.MATCH_L_TO_R.REQUIRED_R, index).equals(
						MatchConstants.YES)) {
			writer.writeLog(new Error(eventCode
					.getString(EventCodeContants.ERR005),
					ProcessPhases.VALIDATION, Calendar.getInstance().getTime(),
					EventCodeContants.ERR005, Integer.valueOf(processor
							.process(Sheets.MATCH_L_TO_R.INDEX,
									Sheets.MATCH_L_TO_R.TUPLE_ID, index)),
					Integer.valueOf(processor.process(
							Sheets.MATCH_L_TO_R.INDEX,
							Sheets.MATCH_L_TO_R.MATCH_ID, index)),
					Sheets.MATCH_L_TO_R.NAME));
			return false;
		}
		return true;
	}

	public TupleTree getTree() {
		return tree;
	}

	/**
	 * Validate the conditions of the matches with PK value equals to YES
	 * 
	 * @param index
	 * @return
	 */
	private boolean validatePk(final int index) {
		final String pk = processor.process(Sheets.MATCH_L_TO_R.INDEX,
				Sheets.MATCH_L_TO_R.PK, index);
		// If match is PK, then it’s datatype must be INT or compatible,
		// otherwise an error must be logged
		if (pk.equals(MatchConstants.YES)) {
			if (!dmr.verify(MatchConstants.INT, processor.process(
					Sheets.MATCH_L_TO_R.INDEX, Sheets.MATCH_L_TO_R.DATATYPE_L,
					index))) {
				writer.writeLog(new Error(eventCode
						.getString(EventCodeContants.ERR006),
						ProcessPhases.VALIDATION, Calendar.getInstance()
								.getTime(), EventCodeContants.ERR006, Integer
								.valueOf(processor.process(
										Sheets.MATCH_L_TO_R.INDEX,
										Sheets.MATCH_L_TO_R.TUPLE_ID, index)),
						Integer.valueOf(processor.process(
								Sheets.MATCH_L_TO_R.INDEX,
								Sheets.MATCH_L_TO_R.MATCH_ID, index)),
						Sheets.MATCH_L_TO_R.NAME));
				return false;
			}
			// If match is PK, then it must be required, otherwise an error must
			// be logged
			if (!processor.process(Sheets.MATCH_L_TO_R.INDEX,
					Sheets.MATCH_L_TO_R.REQUIRED_L, index).equals(
					MatchConstants.YES)) {
				writer.writeLog(new Error(eventCode
						.getString(EventCodeContants.ERR007),
						ProcessPhases.VALIDATION, Calendar.getInstance()
								.getTime(), EventCodeContants.ERR007, Integer
								.valueOf(processor.process(
										Sheets.MATCH_L_TO_R.INDEX,
										Sheets.MATCH_L_TO_R.TUPLE_ID, index)),
						Integer.valueOf(processor.process(
								Sheets.MATCH_L_TO_R.INDEX,
								Sheets.MATCH_L_TO_R.MATCH_ID, index)),
						Sheets.MATCH_L_TO_R.NAME));
				return false;
			}
		}
		return true;
	}
}
