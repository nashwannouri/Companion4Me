package lightning.cyborg;


import org.junit.Test;

import lightning.cyborg.helper.InputVerification;
import lightning.cyborg.helper.SQLiAssistantStrings;


import static org.junit.Assert.*;

/**
 * Team CyborgLightning 2016 - King's College London - Project Run
 * testPasswordConfirmation implements the JUNIT testing for the Password Confirmation function of
 * RegistrationActivity. Tests include i/o testing, SQLi testing
 */

public class testPasswordVerification {

    /* InputVerification - implements tested functions
     * SQLAssistantStrings - implements SQL injection strings database*/

    SQLiAssistantStrings sqlStrings = new SQLiAssistantStrings();
    InputVerification inputVerification = new InputVerification();

    /* JUNIT TESTING - PASSWORD VERIFICATION INPUT TESTING
     Input Testing - True & False, Null */

    /*InputVerification NotNull() - Tests Passed*/
    @Test
    public void passwordInputVerificationFalse(){
        assertNotNull(inputVerification);
    }

    /*sqlStrings NotNull() - Tests Passed*/
    @Test
    public void passwordSQLStrings(){
        assertNotNull(sqlStrings);
    }

    /*Test passwords equivalence verification True - Test Passed*/
    @Test
    public void testPasswordEquivalenceTrue(){
        assertTrue(inputVerification.isValidPasswordPair("teamcyborg", "teamcyborg"));
    }

    /*Test passwords equivalence verification False - Test Passed*/
    @Test
    public void testPasswordEquivalenceFalse(){
        assertFalse(inputVerification.isValidPasswordPair("team_", "_maet"));
    }

    /*Test password1 null input  - Test Passed*/
    @Test
    public void testPasswordNotNullInputFirst(){
        assertFalse(inputVerification.isValidPasswordPair("", "moni"));
    }

    /*Test password2 null input  - Test Passed*/
    @Test
    public void testPasswordNotNullInputSecond(){
        assertFalse(inputVerification.isValidPasswordPair("stephen", ""));
    }


    /*Test password1 & password2 null input  - Test Passed*/
    @Test
    public void testPasswordNotNullBoth(){
        assertFalse(inputVerification.isValidPasswordPair("", ""));
    }

    /*Test password too short - Test Failed - java.lang.AssertionError*/
    @Test
    public void testPasswordTooShort(){
        assertFalse(inputVerification.isValidPasswordPair("zz", "zz"));
    }

    /*Test password too long - Test Failed - java.lang.AssertionError*/
    @Test
    public void testPasswordTooLong(){
        assertFalse(inputVerification.isValidPasswordPair("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz", "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz"));
    }

    /*Test Alphanumeric basic password tests null input  - Test Passed*/
    @Test //Test 1 - Passed
    public void testPasswordAlphaNumericBasic1(){
        assertTrue(inputVerification.isValidPasswordPair("simeon123", "simeon123"));
    }

    @Test //Test 2 - Passed
    public void testPasswordAlphaNumericBasic2(){
        assertTrue(inputVerification.isValidPasswordPair("Spiderze9992", "Spiderze9992"));
    }

    @Test //Test 3 - Passed
    public void testPasswordAlphaNumericBasic3(){
        assertTrue(inputVerification.isValidPasswordPair("De7777zA", "De7777zA"));
    }

    /*Test signs character "_"  - Test Passed*/
    @Test
    public void testPasswordAlphaNumericAdvanced(){
        assertFalse(inputVerification.isValidPasswordPair("4m4zInG_221", "4m4zInG_221"));
    }

    /*Test signs character "£"  - Test Passed*/
    @Test
    public void testPasswordAlphaNumericAndSymbols() {
        assertFalse(inputVerification.isValidPasswordPair("££SanSte7an0", "££SanSte7an0"));
    }

    /*Test UPPER CASE  password tests ALL UPPER CASE CHARS input  - Test Passed*/
    @Test
    public void testPasswordUpperLowerCaseValid(){
        assertTrue(inputVerification.isValidPasswordPair("ASTROLOGY", "ASTROLOGY"));
    }

    /*Test password1 lower case, password2 upper case  - Test Passed*/
    @Test
    public void testPasswordUpperLowerCaseNotValidFirst(){
        assertFalse(inputVerification.isValidPasswordPair("stephen", "STEPHEN"));
    }

    /*Test password1 upper case, password2 lower case  - Test Passed*/
    @Test
    public void testPasswordUpperLowerCaseNotValidSecond(){
        assertFalse(inputVerification.isValidPasswordPair("STEPHEN", "stephen"));
    }

    /*Test password1 upper case, password2 lower case  - Test Passed*/
    @Test
    public void testPasswordOnlyNumeric(){
        assertTrue(inputVerification.isValidPasswordPair("1234567", "1234567"));
    }

    /*
     * SQLi - SQL Injection Testing
     * 33 Tests - FAILED  - java.lang.AssertionError
     */

    /* "or 1=1" input - SQL injection Test*/
    @Test
    public void testPasswordSQL1(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(0), sqlStrings.getSQLibyID(0)));
    }

    /* "'or 1=1" input - SQL injection Test*/
    @Test
    public void testPasswordSQL2(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(1), sqlStrings.getSQLibyID(1)));
    }

    /* ""or 1=1" input - SQL injection Test*/
    @Test
    public void testPasswordSQL3(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(3), sqlStrings.getSQLibyID(3)));
    }

    /* "or 1=1-" input - SQL injection Test*/
    @Test
    public void testPasswordSQL4(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(4), sqlStrings.getSQLibyID(4)));
    }

    /* "'or 1=1-" input - SQL injection Test*/
    @Test
    public void testPasswordSQL5(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(5), sqlStrings.getSQLibyID(5)));
    }

    /* ""or 1=1-" input - SQL injection Test*/
    @Test
    public void testPasswordSQL6(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(6), sqlStrings.getSQLibyID(6)));
    }

    /* "or 1=1#" input - SQL injection Test*/
    @Test
    public void testPasswordSQL7(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(7), sqlStrings.getSQLibyID(7)));
    }

    /* "'or 1=1#" input - SQL injection Test*/
    @Test
    public void testPasswordSQL8(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(8), sqlStrings.getSQLibyID(8)));
    }

    /* ""or" input - SQL injection Test*/
    @Test
    public void testPasswordSQL9(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(9), sqlStrings.getSQLibyID(9)));
    }

    /* "1=1#" input - SQL injection Test*/
    @Test
    public void testPasswordSQL10(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(10), sqlStrings.getSQLibyID(10)));
    }

    /* "or 1=1/*" input - SQL injection Test*/
    @Test
    public void testPasswordSQL11(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(11), sqlStrings.getSQLibyID(11)));
    }

    /* "'or 1=1/*" input - SQL injection Test*/
    @Test
    public void testPasswordSQL12(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(12), sqlStrings.getSQLibyID(12)));
    }

    /* ""or 1=1/ input - SQL injection Test*/
    @Test
    public void testPasswordSQL13(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(13), sqlStrings.getSQLibyID(13)));
    }

    /* "or 1=1;%00" input - SQL injection Test*/
    @Test
    public void testPasswordSQL14(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(14), sqlStrings.getSQLibyID(14)));
    }

    /* "or 1=1" input - SQL injection Test*/
    @Test
    public void testPasswordSQL15(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(15), sqlStrings.getSQLibyID(15)));
    }

    /* "'or'" input - SQL injection Test*/
    @Test
    public void testPasswordSQL16(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(16), sqlStrings.getSQLibyID(16)));
    }

    /* "'or" input - SQL injection Test*/
    @Test
    public void testPasswordSQL17(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(17), sqlStrings.getSQLibyID(17)));
    }

    /* "'or'-" input - SQL injection Test*/
    @Test
    public void testPasswordSQL18(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(18), sqlStrings.getSQLibyID(18)));
    }

    /* "'or-"  input - SQL injection Test*/
    @Test
    public void testPasswordSQL19(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(19), sqlStrings.getSQLibyID(19)));
    }

    /* "or a=a" input - SQL injection Test*/
    @Test
    public void testPasswordSQL20(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(20), sqlStrings.getSQLibyID(20)));
    }

    /* "'or a=a" input - SQL injection Test*/
    @Test
    public void testPasswordSQL21(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(21), sqlStrings.getSQLibyID(21)));
    }

    /* ""or a=a" input - SQL injection Test*/
    @Test
    public void testPasswordSQL22(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(22), sqlStrings.getSQLibyID(22)));
    }

    /* "or a=a-" input - SQL injection Test*/
    @Test
    public void testPasswordSQL23(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(23), sqlStrings.getSQLibyID(23)));
    }

    /* "'or a=a" input - SQL injection Test*/
    @Test
    public void testPasswordSQL24(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(24), sqlStrings.getSQLibyID(24)));
    }

    /* ""or a=a" input - SQL injection Test*/
    @Test
    public void testPasswordSQL25(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(25), sqlStrings.getSQLibyID(25)));
    }

    /* "or a=a-" input - SQL injection Test*/
    @Test
    public void testPasswordSQL26(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(26), sqlStrings.getSQLibyID(26)));
    }

    /* "'or a=a-"; input - SQL injection Test*/
    @Test
    public void testPasswordSQL27(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(27), sqlStrings.getSQLibyID(27)));
    }

    /* ""or a=a-" input - SQL injection Test*/
    @Test
    public void testPasswordSQL28(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(28), sqlStrings.getSQLibyID(28)));
    }

    /* "or 'a'='a'"  input - SQL injection Test*/
    @Test
    public void testPasswordSQL29(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(29), sqlStrings.getSQLibyID(29)));
    }

    /* "'or 'a'='a'" input - SQL injection Test*/
    @Test
    public void testPasswordSQL30(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(30), sqlStrings.getSQLibyID(30)));
    }

    /* ""or 'a'='a'"  input - SQL injection Test*/
    @Test
    public void testPasswordSQL31(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(31), sqlStrings.getSQLibyID(31)));
    }

    /* "')or('a'='a'" input - SQL injection Test*/
    @Test
    public void testPasswordSQL32(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(32), sqlStrings.getSQLibyID(32)));
    }

    /* "")"a"="a"" input - SQL injection Test*/
    @Test
    public void testPasswordSQL33(){
        assertFalse(inputVerification.isValidPasswordPair(sqlStrings.getSQLibyID(32), sqlStrings.getSQLibyID(32)));
    }


}
