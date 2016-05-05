package org.esaude.dmt.util;

import java.util.Arrays;

import org.apache.ibatis.jdbc.SQL;
import org.esaude.dmt.helper.MatchConstants;
import org.esaude.dmt.helper.SystemException;

/**
 * This class provides tool to manipulate WHERE condition operators
 * @author Valério João
 * @since 17-06-2015
 *
 */
public class MatchUtil {
	
	/**
	 * This method builds multiple OR conditions for the same parameter e.g:
	 * (name = "A" OR name = "B")
	 * 
	 * @param referenceIndex
	 * @param referencedValues
	 * @param table
	 * @param column
	 * @param operator
	 * @param sql
	 * @return
	 * @throws SystemException
	 */
	public String assignReferencedValuesWithMultipleOr(
			final int referenceIndex, final String[] referencedValues,
			final String table, final String column, final String operator,
			final SQL sql) throws SystemException {
		// in care there is >> condition
		String orCondition = "";

		for (int i = 0; i < referencedValues.length; i++) {
			if (referenceIndex != 0 && i == 0)
				sql.AND();// in case it's not the first reference use AND
							// conditions separating with braces

			orCondition += table + "." + column +
					assignReferencedValueOperationForRelationalCondition(
							referencedValues[i].trim());

			if (i < referencedValues.length - 1)
				orCondition += " OR ";
		}
		return orCondition;
	}

	/**
	 * This method splits and assigns the operator and operand values of relational conditions
	 * e.g. %>=%10 should returns >= 10, 10 should return = 10
	 * @param referencedValue
	 * @return
	 * @throws SystemException 
	 */
	public Object assignReferencedValueOperationForRelationalCondition(
			final Object referencedValue) throws SystemException {
		final String SPACE = " ";
		StringBuilder relationalOperation = new StringBuilder("");
		
		if (referencedValue.toString().contains(
				MatchConstants.CONDITIONAL_OPERATION_SEPARATOR)) {
			String[] splitedRelational = referencedValue.toString().trim()
					.split(MatchConstants.CONDITIONAL_OPERATION_SEPARATOR);
			
			// the separator must be valid
			if (!Arrays.asList(MatchConstants.EQ, MatchConstants.NEQ,
					MatchConstants.LT, MatchConstants.GT, MatchConstants.LTEQ,
					MatchConstants.GTEQ).contains(splitedRelational[1].trim())) {
				throw new SystemException(
						"Invalid relational condition operator "
								+ splitedRelational[1].trim());
			}
			
			relationalOperation.append(SPACE);
			relationalOperation.append(splitedRelational[1].trim());
			relationalOperation.append(SPACE);
			relationalOperation.append(this.cast(splitedRelational[2].trim()));

			return relationalOperation.toString();
		} else if (!Arrays.asList(MatchConstants.ALL, MatchConstants.EQUALS,
				MatchConstants.CURR, MatchConstants.CURR2,
				MatchConstants.CURR3, MatchConstants.CURR4).contains(
				referencedValue)) {
			// return the referenced value with
			return SPACE + "=" + SPACE
					+ this.cast(referencedValue);
		} else {
			return referencedValue;
		}

	}

	/**
	 * This method splits the different values of OR condition e.g A>>B>>C must
	 * return an array containing "A", "B"and "C"
	 * 
	 * @param referencedValue
	 * @return
	 */
	public String[] splitReferencedValuesForOrCondition(
			final Object referencedValue) {
		String[] referencedValues = null;
		final CharSequence OR = MatchConstants.OR;

		if (referencedValue.toString().contains(OR)) {
			referencedValues = referencedValue.toString().split(
					MatchConstants.OR);
		}
		return referencedValues;
	}
	
	/**
	 * This method takes a value and returns its string representation in the database
	 * @param value
	 * @return
	 */
	public String cast(Object value) throws SystemException {
		if(value == null) {
			throw new SystemException("Invalid database value to cast");
		}
		
		String valueStr = value.toString();
		
		if(value instanceof java.util.Date) {
			String valueDateStr = valueStr.substring(0, valueStr.lastIndexOf('.'));
			
			return "#" + valueDateStr + "#";
		}
		
		if(!valueStr.matches("^[-+]?\\d+(\\.\\d+)?$")) {
			if(valueStr.equalsIgnoreCase(MatchConstants.NULL)) {
				return valueStr;
			}
			return "\'" + valueStr + "\'";
		}
		return valueStr;
	}

}
