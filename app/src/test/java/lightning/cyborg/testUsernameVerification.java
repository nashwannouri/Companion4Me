package lightning.cyborg;

import org.junit.Test;
import lightning.cyborg.helper.SQLiStringChecker;
import lightning.cyborg.helper.SQLiAssistantStrings;
import lightning.cyborg.helper.InputVerification;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Team CyborgLightning 2016 - King's College London - Project Run
 * testUsernameVerification implements the JUNIT testing for the Password Confirmation function of
 * RegistrationActivity. Tests include i/o testing, SQLi testing
 */

public class testUsernameVerification {


    /* InputVerification - implements tested functions
     * SQLAssistantStrings - implements SQL injection strings database*/

    InputVerification inputVerification = new InputVerification();
    SQLiAssistantStrings sqlStrings = new SQLiAssistantStrings();

    /* JUNIT TESTING - USERNAME VERIFICATION INPUT TESTING
     Input Testing - True & False, Null */

    /*InputVerification NotNull() - Test Passed*/
    @Test
    public void usernameInputVerificationFalse(){
        assertFalse(inputVerification.checkUserNameInput("super.monka@gmail.com"));
    }

    /*sqlStrings NotNull() - Test Passed*/
    @Test //Test 1 - Passed
    public void userNameInputVerificationTrue1(){
        assertTrue(inputVerification.checkUserNameInput("Jonathan"));
    }

    @Test //Test 2 - Passed
    public void userNameInputVerificationTrue2(){
        assertTrue(inputVerification.checkUserNameInput("Ann"));
    }

    @Test //Test 3 - Passed
    public void userNameInputVerificationTrue3(){
        assertTrue(inputVerification.checkUserNameInput("Raksha"));
    }

    /*Test username null input - Test Fail - Expected Null Pointer Exception*/
    @Test
    public void userNameInputNotNull(){
        assertFalse(inputVerification.checkUserNameInput(null));
    }

    /*Test username short input- Tests Passed*/
    @Test
    public void userNameShortInput(){
        assertTrue(inputVerification.checkUserNameInput("aa"));
    }

    /*Test username short input - Tests Passed*/
    @Test
    public void userNameLongInput(){
        assertFalse(inputVerification.checkUserNameInput("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
    }

    /*Test username only numbers input - Tests Passed*/
    @Test //Test 1 - passed
    public void userNameDigitInput1(){
        assertFalse(inputVerification.checkUserNameInput("123123"));
    }

    @Test //Test 2 - passed
    public void userNameDigitInput2(){
        assertFalse(inputVerification.checkUserNameInput("7192378123"));
    }

    @Test //Test 3 - passed
    public void userNameDigitInput3(){
        assertFalse(inputVerification.checkUserNameInput("121231233123"));
    }

    /* TEAM CYBORGLIGHTNING TESTING
     * SQLi - SQL Injection JUNIT Testing
     * 33 Tests - PASSED
     */

    /* "or 1=1" input - SQL injection Test*/
    @Test
    public void testUsernameSQL1() {
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(0)));
    }

    /* "'or 1=1" input - SQL injection Test*/
    @Test
    public void testUsernameSQL2(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(1)));
    }

    /* ""or 1=1" input - SQL injection Test*/
    @Test
    public void testUsernameSQL3(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(2)));
    }

    /* "or 1=1-" input - SQL injection Test*/
    @Test
    public void testUsernameSQL4(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(3)));
    }

    /* "'or 1=1-" input - SQL injection Test*/
    @Test
    public void testUsernameSQL5(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(4)));
    }

    /* ""or 1=1-" input - SQL injection Test*/
    @Test
    public void testUsernameSQL6(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(5)));
    }

    /* "or 1=1#" input - SQL injection Test*/
    @Test
    public void testUsernameSQL7(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(6)));
    }

    /* "'or 1=1#" input - SQL injection Test*/
    @Test
    public void testUsernameSQL8(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(7)));
    }

    /* ""or" input - SQL injection Test*/
    @Test
    public void testUsernameSQL9(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(8)));
    }

    /* "1=1#" input - SQL injection Test*/
    @Test
    public void testUsernameSQL10(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(9)));
    }

    /* "or 1=1/*" input - SQL injection Test*/
    @Test
    public void testUsernameSQL11(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(10)));
    }

    /* "'or 1=1/*" input - SQL injection Test*/
    @Test
    public void testUsernameSQL12(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(11)));
    }

    /* ""or 1=1/ input - SQL injection Test*/
    @Test
    public void testUsernameSQL13(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(12)));
    }

    /* "or 1=1;%00" input - SQL injection Test*/
    @Test
    public void testUsernameSQL14(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(13)));
    }

    /* "or 1=1" input - SQL injection Test*/
    @Test
    public void testUsernameSQL15(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(14)));
    }

    /* "'or'" input - SQL injection Test*/
    @Test
    public void testUsernameSQL16(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(15)));
    }

    /* "'or" input - SQL injection Test*/
    @Test
    public void testUsernameSQL17(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(16)));
    }

    /* "'or'-" input - SQL injection Test*/
    @Test
    public void testUsernameSQL18(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(17)));
    }

    /* "'or-"  input - SQL injection Test*/
    @Test
    public void testUsernameSQL19(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(18)));
    }

    //"or a=a" input - SQL injection Test
    @Test
    public void testUsernameSQL20(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(19)));
    }

    /* "or a=a" input - SQL injection Test*/
    @Test
    public void testUsernameSQL21(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(20)));
    }

    /* "'or a=a" input - SQL injection Test*/
    @Test
    public void testUsernameSQL22(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(21)));
    }

    /* ""or a=a" input - SQL injection Test*/
    @Test
    public void testUsernameSQL23(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(22)));
    }

    /* "or a=a-" input - SQL injection Test*/
    @Test
    public void testUsernameSQL24(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(23)));
    }

    /* "'or a=a" input - SQL injection Test*/
    @Test
    public void testUsernameSQL25(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(24)));
    }

    /* ""or a=a" input - SQL injection Test*/
    @Test
    public void testUsernameSQL26(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(25)));
    }

    /* "'or a=a-"; input - SQL injection Test*/
    @Test
    public void testUsernameSQL27(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(26)));
    }

    /* ""or a=a-" input - SQL injection Test*/
    @Test
    public void testUsernameSQL28(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(27)));
    }

    /* "or 'a'='a'"  input - SQL injection Test*/
    @Test
    public void testUsernameSQL29(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(28)));
    }

    /* "'or 'a'='a'" input - SQL injection Test*/
    @Test
    public void testUsernameSQL30(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(29)));
    }

    /* ""or 'a'='a'"  input - SQL injection Test*/
    @Test
    public void testUsernameSQL31(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(30)));
    }

    /* "')or('a'='a'" input - SQL injection Test*/
    @Test
    public void testUsernameSQL32(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(31)));
    }

    /* "")"a"="a"" input - SQL injection Test*/
    @Test
    public void testUsernameSQL33(){
        assertFalse(inputVerification.checkUserNameInput(sqlStrings.getSQLibyID(32)));
    }

}
