package lightning.cyborg.helper;

/**
 * Team CyborgLightning 2016 - King's College London - Project Run
 * SQLiAssistantStrings stores the SQLinjection database
 */
public class SQLiAssistantStrings {

    public String[] strings;

    public SQLiAssistantStrings(){
    setArray();
    }

    public void setArray(){
        strings = new String[34];
        strings[0] = "or 1=1"; strings[10] = "1=1#"; strings[20] = "'or-"; strings[30] = "')or('a'='a'";
        strings[1] = "'or 1=1"; strings[11] = "or 1=1/*"; strings[21] = "or a=a"; strings[31] = "\")\"a\"=\"a\"";
        strings[2] = "\" or1=1"; strings[12] = "'or 1=1/*"; strings[22] = "'or a=a"; strings[32] = "')'a='a";
        strings[3] = "or 1=1-"; strings[13] = "or 1=1/*"; strings[23] = "\"or a=a"; strings[33] = "'or\"='";
        strings[4] = "'or 1=1-"; strings[14] = "or 1=1;%00"; strings[24] = "or a=a-";
        strings[5] = "\"or 1=1-"; strings[15] = "'or 1=1;%00"; strings[25] = "'or a=a-";
        strings[6] = "or 1=1#"; strings[16] = "\"or 1=1;%00"; strings[26] = "\"or a=a-";
        strings[7] = "'or 1=1#"; strings[17] = "\"or 1-1/*"; strings[27] = "or 'a'='a'";
        strings[8] = "\"or"; strings[18] = "'or'"; strings[28] = "'or 'a'='a'";
        strings[9] = "1=1#"; strings[19] = "'or-'"; strings[29] = "\"or 'a'='a'";

    }

    /**
     * getSQLibyID - returns string by id (Integer)
     * @param id - in
     * @return String SQLi
     */
    public String getSQLibyID(int id){
        if(id>=0 && id<=33){
            return strings[id];
        } else return null;
    }
}
