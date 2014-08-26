package org.esaude.dmt.util.datatypemapping;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class reads a CSV file of datatype mapping and composes a data structure representation
 * of the mappings.
 * This mapping algorithm is as follows:
 * (1) The first value of each line is a datatype in OpenMRS database
 * (2) The first value of each line (head) is compatible with all the types that follows the line
 * (3) If the value that follows the line is also a head in another line, it means that there is a 
 * compatibility between the two lines:
 * 		(a) The containing line can take the contained line values without any issue
 * 		(b) The contained line can be transformed to take values of the containing line
 * (4) The logic assumes that the contained line is always on top of the containing line
 * 
 * @author Valério João
 * @since 25-08-2014
 *
 */
public final class DatatypeMappingReader {
	private boolean match;
	private static BufferedReader br = null;
	private final String csvFile = "src/main/resources/datatype_mapping.csv";
	private Map<String, DatatypeMapping> datatypeMappings;
	private static DatatypeMappingReader instance;
	
	private DatatypeMappingReader() {
		datatypeMappings  = new HashMap<String, DatatypeMapping>();
		try {
			br = new BufferedReader(new FileReader(csvFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static DatatypeMappingReader getInstance() {
		if(instance == null) {
			instance = new DatatypeMappingReader();
			instance.process();
		}
		return instance;
	}
	
	/**
	 * Processes the CSV file and composes a data structure of datatype mapping
	 * @return
	 */
	private void process() {
		final String CSV_SPLIT_BY = ";";
		datatypeMappings = new HashMap<String, DatatypeMapping>();
		//read CSV file top down
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] datatypes = line.split(CSV_SPLIT_BY);
				//the mapping object
				DatatypeMapping datatypeMapping = new DatatypeMapping();
				datatypeMapping.setHead(datatypes[0]);
				//set all the menbers of the group
				for(String member : datatypes) {
					datatypeMapping.getMembers().add(member);
					//check if member is the head of a mapping line
					if(datatypeMappings.containsKey(member)) {
						datatypeMapping.getContainedMappings().add(member);
					}
				}
				datatypeMappings.put(datatypeMapping.getHead(), datatypeMapping);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Verifies whether or not the right datatype matches with the left datatype
	 * @param left
	 * @param right
	 * @return
	 */
	public boolean verify(String left, String right) {
		match = false;//reset this variable because there is only one instance
		DatatypeMapping datatypeMapping = datatypeMappings.get(left);
		//if left datatype doesnt exist in the CSV, there is no match
		if(datatypeMapping == null) {
			return match;
		}
		checkLine(datatypeMapping, right);
		return match;
	}
	
	/**
	 * Recursively look for containing mapping lines
	 * @param datatypeMapping
	 * @param right
	 * @return
	 */
	private void checkLine(DatatypeMapping datatypeMapping, String right) {
		if(datatypeMapping.getMembers().contains(right)) {
			match = true;
		} else {
			for(String containedMapping : datatypeMapping.getContainedMappings()) {
				checkLine(datatypeMappings.get(containedMapping), right);
			}
		}
	}
}
