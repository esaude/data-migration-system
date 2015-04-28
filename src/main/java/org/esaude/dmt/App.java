package org.esaude.dmt;

import org.esaude.dmt.component.TranslationManager;
import org.esaude.dmt.component.ValidationManager;
import org.esaude.dmt.helper.SystemException;

/**
 * The main class
 *
 */
public class App 
{
	public static final String MAIN_PATH = "src/main/resources";
	public static final String TEST_PATH = "src/test/resources";

    public static void main( String[] args ) throws SystemException
    {
    	ValidationManager vm = new ValidationManager();
    	if(!vm.execute()) return;
    	TranslationManager tm = new TranslationManager(vm.getTree());
    	tm.execute();
    }



}