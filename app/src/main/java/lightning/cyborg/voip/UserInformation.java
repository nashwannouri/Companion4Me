package lightning.cyborg.voip;

/**
 * Created by Amos Madalin Neculau on 25/02/2016.
 */
public class UserInformation {

    protected String username;
    protected String password;
    protected String server;

    public UserInformation(String username, String password, String server){
        this.username = username;
        this.password = password;
        this.server = server;
    }

    public UserInformation(){
        username = "";
        password = "";
        server = "";
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String getServer(){
        return server;
    }

    public void setUsername(String newUsername){
        username = newUsername;
    }

    public void setPassword(String newPassword){
        password = newPassword;
    }

    public void setServer(String newServer){
        server = newServer;
    }


}
