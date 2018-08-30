package lightning.cyborg.helper;

/**
 * Team CyborgLightning 2016 - King's College London - Project Run
 * SQLiAssistantStrings stores the SQLinjection database
 */

public class SQLiStringChecker {
    public SQLiStringChecker(){
    }
    public boolean checkerFoo(String string){
        if(string.contains("=")) return false;
        else if(string.contains("%")) return false;
        else if(string.contains("'")) return false;
        else if(string.contains("\"")) return false;
        else if(string.contains(";")) return false;
        else if(string.contains(":")) return false;
        else return true;
    }


}
