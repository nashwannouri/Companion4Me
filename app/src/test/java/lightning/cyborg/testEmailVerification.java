package lightning.cyborg;

import org.junit.Test;

import lightning.cyborg.helper.InputVerification;
import lightning.cyborg.helper.SQLiAssistantStrings;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Team CyborgLightning 2016 - King's College London - Project Run
 * testEmailVerification implements the JUNIT testing for the Password Confirmation function of
 * RegistrationActivity. Tests include i/o testing, SQLi testing, Sound and Correctness
 */


public class testEmailVerification {


    /* InputVerification - implements tested functions
     * SQLAssistantStrings - implements SQL injection strings database*/

    InputVerification inputVerification = new InputVerification();
    SQLiAssistantStrings sqlStrings = new SQLiAssistantStrings();

    /* JUNIT TESTING - EMAIL VERIFICATION INPUT TESTING
     Input Testing - True & False, Null */

    /*InputVerification NotNull() - Tests Passed*/
    @Test
    public void testVerificationNotNull(){
        assertNotNull(inputVerification);
    }

    /*sqlStrings NotNull() - Tests Passed*/
    @Test
    public void testObjectNotNullCheckSQLstrings(){
        assertNotNull(sqlStrings);
    }


    /*Test email verification True - Test Passed*/
    @Test
    public void testEmailValidationTestTrue(){
        assertTrue(inputVerification.isValidEmail("crazy_boo@gmail.com"));
    }

    /*Test email verification False - Test Passed*/
    @Test
    public void testEmailValidationTestFalse(){
        assertFalse(inputVerification.isValidEmail("xxxxxx_zz"));
    }

    /*Test email verification .ORG domain - Test Passed*/
    @Test
    public void testEmailORG(){
        assertTrue(inputVerification.isValidEmail("xman9488fili@yahoo.org"));
    }

    /*Test email verification .NET domain - Test Passed*/
    @Test
    public void EmailVerificationNET(){
        assertTrue(inputVerification.isValidEmail("sherlock.holmes@outlook.net"));
    }

    /*Test email verification .CO.UK domain - Test Passed*/
    @Test
    public void EmailVerificationCOUK(){
        assertTrue(inputVerification.isValidEmail("anthonty_hopkins43@kcl.co.uk"));
    }

    /*Test email verification .COM domain, UPPERCASE TEST - Tests Passed*/
    @Test
    public void EmailVerificationCOM(){
        assertTrue(inputVerification.isValidEmail("DESTRUCTO@MAIL.COM"));
    }

    /*Test email verification .JP domain, Japanese alphabet - Test Passed, Japanese not supported*/
    @Test
    public void EmailVerificationJP(){
        assertFalse(inputVerification.isValidEmail("漢字漢字@ummy.jp"));
    }

    /*Test email verification .RU domain, Cyrillic alphabet - Test Passed, Cyrillic not supported*/
    @Test
    public void EmailVerificationRU(){
        assertFalse(inputVerification.isValidEmail("ящика@apapo.ru"));
    }

    /*Test email verification .DK domain, Danish alphabet - Test Passed, Cyrillic not supported*/
    @Test
    public void EmailVerificationDK(){
        assertFalse(inputVerification.isValidEmail("ÆÆÆÆ@yahoo.dk"));
    }


    /* "or 1=1" input - SQL injection Test*/
    @Test
    public void testEmailSQL1(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(0) + "@gmail.com"));
    }

    /* "'or 1=1" input - SQL injection Test*/
    @Test
    public void testEmailSQL2(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(1) + "@yahoo.co.uk"));
    }

    /* ""or 1=1" input - SQL injection Test*/
    @Test
    public void testEmailSQL3(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(2) + "@mail.com"));
    }

    /* "or 1=1-" input - SQL injection Test*/
    @Test
    public void testEmailSQL4(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(3) + "@outlook.com"));
    }

    /* "'or 1=1-" input - SQL injection Test*/
    @Test
    public void testEmailSQL5(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(4) + "@kcl.ac.uk"));
    }

    /* ""or 1=1-" input - SQL injection Test*/
    @Test
    public void testEmailSQL6(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(5) + "@hotmail.com"));
    }

    /* "or 1=1#" input - SQL injection Test*/
    @Test
    public void testEmailSQL7(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(6) + "@gmail.com"));
    }


    /* "'or 1=1#" input - SQL injection Test*/
    @Test
    public void testEmailSQL8(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(7) + "@gmail.com"));
    }

    /* ""or" input - SQL injection Test*/
    @Test
    public void testEmailSQL9(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(8) + "@gmail.com"));
    }

    /* "1=1#" input - SQL injection Test*/
    @Test
    public void testEmailSQL10(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(9) + "@gmail.com"));
    }

    /* "or 1=1/*" input - SQL injection Test*/
    @Test
    public void testEmailSQL11(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(10) + "@gmail.com"));
    }

    /* "'or 1=1/*" input - SQL injection Test*/
    @Test
    public void testEmailSQL12(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(11) + "@gmail.com"));
    }

    /* ""or 1=1/ input - SQL injection Test*/
    @Test
    public void testEmailSQL13(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(12) + "@gmail.com"));
    }

    /* "or 1=1;%00" input - SQL injection Test*/
    @Test
    public void testEmailSQL14(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(13) + "@gmail.com"));
    }

    /* "or 1=1" input - SQL injection Test*/
    @Test
    public void testEmailSQL15(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(14) + "@gmail.com"));
    }

    /* "'or'" input - SQL injection Test*/
    @Test
    public void testEmailSQL16(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(15) + "@gmail.com"));
    }

    /* "'or" input - SQL injection Test*/
    @Test
    public void testEmailSQL17(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(16) + "@gmail.com"));
    }

    /* "'or'-" input - SQL injection Test*/
    @Test
    public void testEmailSQL18(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(17) + "@gmail.com"));
    }

    /* "'or-"  input - SQL injection Test*/
    @Test
    public void testEmailSQL19(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(18) + "@gmail.com"));
    }

    /* "or a=a" input - SQL injection Test*/
    @Test
    public void testEmailSQL20(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(19) + "@gmail.com"));
    }

    /* "'or a=a" input - SQL injection Test*/
    @Test
    public void testEmailSQL21(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(20) + "@gmail.com"));
    }

    /* ""or a=a" input - SQL injection Test*/
    @Test
    public void testEmailSQL22(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(21) + "@gmail.com"));
    }

    /* "or a=a-" input - SQL injection Test*/
    @Test
    public void testEmailSQL23(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(22) + "@gmail.com"));
    }

    /* "'or a=a" input - SQL injection Test*/
    @Test
    public void testEmailSQL24(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(23) + "@gmail.com"));
    }

    /* ""or a=a" input - SQL injection Test*/
    @Test
    public void testEmailSQL25(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(24) + "@gmail.com"));
    }

    /* "or a=a-" input - SQL injection Test*/
    @Test
    public void testEmailSQL26(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(25) + "@gmail.com"));
    }

    /* "'or a=a-"; input - SQL injection Test*/
    @Test
    public void testEmailSQL27(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(26) + "@gmail.com"));
    }

    /* ""or a=a-" input - SQL injection Test*/
    @Test
    public void testEmailSQL28(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(27) + "@gmail.com"));
    }

    /* "or 'a'='a'"  input - SQL injection Test*/
    @Test
    public void testEmailSQL29(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(28) + "@gmail.com"));
    }

    /* "'or 'a'='a'" input - SQL injection Test*/
    @Test
    public void testEmailSQL30(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(29) + "@gmail.com"));
    }

    /* ""or 'a'='a'"  input - SQL injection Test*/
    @Test
    public void testEmailSQL31(){
        assertFalse(inputVerification.isValidEmail("steaphain@gmail.com" + sqlStrings.getSQLibyID(30)));
    }

    /* "')or('a'='a'" input - SQL injection Test*/
    @Test
    public void testEmailSQL32(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(31) + "@gmail.com"));
    }

    /* "")"a"="a"" input - SQL injection Test*/
    @Test
    public void testEmailSQL33(){
        assertFalse(inputVerification.isValidEmail(sqlStrings.getSQLibyID(32) + "@gmail.com"));
    }

}
