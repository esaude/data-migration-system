package org.esaude.dmt;

import org.esaude.dmt.component.ValidationManager;
import org.esaude.dmt.helper.SystemException;







/**
 * The main class
 *
 */
public class App 
{

    public static void main( String[] args ) throws SystemException
    {
        new ValidationManager().execute();
    }



}