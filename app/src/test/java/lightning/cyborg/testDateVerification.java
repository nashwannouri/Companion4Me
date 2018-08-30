package lightning.cyborg;


import org.junit.Test;

import java.text.ParseException;

import lightning.cyborg.helper.InputVerification;
import lightning.cyborg.helper.SQLiAssistantStrings;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Team CyborgLightning 2016 - King's College London - Project Run
 * testDateVerification implements the JUNIT testing for the Date Parser function of
 * RegistrationActivity. Tests include i/o testing, SQLi testing
 */


public class testDateVerification {

    /* InputVerification - implements tested functions
     * SQLAssistantStrings - implements SQL injection strings database*/

    InputVerification inputVerification = new InputVerification();
    SQLiAssistantStrings sqlStrings = new SQLiAssistantStrings();

    /* JUNIT TESTING - DATE VERIFICATION INPUT TESTING
     Input Testing - True & False, Null */

    /*InputVerification NotNull() - Tests Passed*/
    @Test
    public void testObjectNotNullCheckPasswordAssistant(){

        assertNotNull(inputVerification);
    }

    /*sqlStrings NotNull() - Tests Passed*/
    @Test
    public void testObjectNotNullCheckSQLstrings(){
        assertNotNull(sqlStrings);

    }


    /*Test date verification True - Test Passed*/
    @Test
    public void testDateValidity1() throws ParseException{
        assertTrue(inputVerification.isValidDOB("1990-12-20"));
    }

    /*Tests under 18 years of age*/
    @Test //Test 1
    public void testDateValidityUnderAge1() throws ParseException{
        assertFalse(inputVerification.isValidDOB("2002-12-20"));
    }

    @Test //Test 2
    public void testDateValidityUnderAge2() throws ParseException{
        assertFalse(inputVerification.isValidDOB("2003-04-12"));
    }

    @Test //Test 3
    public void testDateValidityUnderAge3() throws ParseException{
        assertFalse(inputVerification.isValidDOB("1999-10-31"));
    }


    /*Test Non-Date INPUT - Test Fail - Cannot Parse Data*/
    @Test
    public void testDateValidityBadInput1() throws ParseException{
        assertFalse(inputVerification.isValidDOB("ss-qq-xx"));
    }

    /*Test NULL INPUT - Test Fail - NullPointerException*/
    @Test
    public void testDateValidityNullCheck() throws ParseException {
        assertFalse(inputVerification.isValidDOB(null));
    }

    /*Test INVALID DATE - Test Fail - Assertion Error*/
    //TODO input 1920 - 1998 Above 18: 905
    @Test
    public void testDateValidityCheckDigits() throws ParseException {
        assertTrue(inputVerification.isValidDOB("11-11-1111"));
    }

    /*Test INVALID DATE - Test Fail - Assertion Error*/
    //TODO input 1920 - 1998 Above 18: 2015
    @Test
    public void testDateValidityCheckIllogicalInput()throws ParseException {
        assertTrue(inputVerification.isValidDOB("01-02-0000"));
    }

    /*
     * SQLi - SQL Injection Testing
     * 33 Tests - FAILED  - java.text.ParseException: Unparseable date: "')'a='a"
     */

    /* "or 1=1" input - SQL injection Test*/
    @Test
    public void testDateSQL1() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(0)));
    }

    /* "'or 1=1" input - SQL injection Test*/
    @Test
    public void testDateSQL2() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(1)));
    }

    /* ""or 1=1" input - SQL injection Test*/
    @Test
    public void testDateSQL3() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(3)));
    }

    /* "or 1=1-" input - SQL injection Test*/
    @Test
    public void testDateSQL4() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(3)));
    }

    /* "'or 1=1-" input - SQL injection Test*/
    @Test
    public void testDateSQL5() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(4)));
    }

    /* ""or 1=1-" input - SQL injection Test*/
    @Test
    public void testDateSQL6() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(5)));
    }

    /* "or 1=1#" input - SQL injection Test*/
    @Test
    public void testDateSQL7() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(6)));
    }

    /* "'or 1=1#" input - SQL injection Test*/
    @Test
    public void testDateSQL8() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(7)));
    }

    /* ""or" input - SQL injection Test*/
    @Test
    public void testDateSQL9() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(8)));
    }

    /* "1=1#" input - SQL injection Test*/
    @Test
    public void testDateSQL10() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(9)));
    }

    /* "or 1=1/*" input - SQL injection Test*/
    @Test
    public void testDateSQL11() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(10)));
    }

    /* "'or 1=1/*" input - SQL injection Test*/
    @Test
    public void testDateSQL12() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(11)));
    }

    /* ""or 1=1/ input - SQL injection Test*/
    @Test
    public void testDateSQL13() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(12)));
    }

    /* "or 1=1;%00" input - SQL injection Test*/
    @Test
    public void testDateSQL14() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(13)));
    }

    /* "or 1=1" input - SQL injection Test*/
    @Test
    public void testDateSQL15() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(14)));
    }

    /* "'or'" input - SQL injection Test*/
    @Test
    public void testDateSQL16() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(15)));
    }

    /* "'or" input - SQL injection Test*/
    @Test
    public void testDateSQL17() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(16)));
    }

    /* "'or'-" input - SQL injection Test*/
    @Test
    public void testDateSQL18() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(17)));
    }

    /* "'or-"  input - SQL injection Test*/
    @Test
    public void testDateSQL19() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(18)));
    }

    /* "or a=a" input - SQL injection Test*/
    @Test
    public void testDateSQL20() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(19)));
    }

    /* "'or a=a" input - SQL injection Test*/
    @Test
    public void testDateSQL21() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(20)));
    }

    /* ""or a=a" input - SQL injection Test*/
    @Test
    public void testDateSQL22() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(21)));
    }

    /* "or a=a-" input - SQL injection Test*/
    @Test
    public void testDateSQL23() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(22)));
    }

    /* "'or a=a" input - SQL injection Test*/
    @Test
    public void testDateSQL24() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(23)));
    }

    /* ""or a=a" input - SQL injection Test*/
    @Test
    public void testDateSQL25() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(24)));
    }

    /* "or a=a-" input - SQL injection Test*/
    @Test
    public void testDateSQL26() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(25)));
    }

    /* "'or a=a-"; input - SQL injection Test*/
    @Test
    public void testDateSQL27() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(26)));
    }

    /* ""or a=a-" input - SQL injection Test*/
    @Test
    public void testDateSQL28() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(27)));
    }

    /* "or 'a'='a'"  input - SQL injection Test*/
    @Test
    public void testDateSQL29() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(28)));
    }

    /* "'or 'a'='a'" input - SQL injection Test*/
    @Test
    public void testDateSQL30() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(29)));
    }

    /* ""or 'a'='a'"  input - SQL injection Test*/
    @Test
    public void testDateSQL31() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(30)));
    }

    /* "')or('a'='a'" input - SQL injection Test*/
    @Test
    public void testDateSQL32() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(31)));
    }

    /* "")"a"="a"" input - SQL injection Test*/
    @Test
    public void testDateSQL33() throws ParseException{
        assertFalse(inputVerification.isValidDOB(sqlStrings.getSQLibyID(32)));
    }


}
