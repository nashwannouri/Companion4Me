package lightning.cyborg.app;

/**
 * Created by Team Cyborg Lightning
 */
public class EndPoints {
    public static final String base = "http://nashdomain.esy.es";
    public static final String SEND_PASS = base +"/sendPassword.php";
    public static final String LOGIN = base + "/users_login.php";
    public static final String USERr = base +"/updateGcmID.php";
    public static final String CHAT_THREAD = base +"/temp_GetSingleRoom.php";
    public static final String CHAT_ROOM_MESSAGE = base+"/temp_send.php";
    public static final String CHAT_ROOMS = base + "/getNormalChatRooms.php";
    public static final String REQUEST_RESPONSE = base+"/requestHandler.php";
    public static final String ADD_FREIND = base+"/convertChatRoom.php";
    public static final String ADD_INTERESTS = base+"/interests_insert.php";
    public static final String GET_INTERESTS = base+"/interests_get_user.php";
    public static final String DEL_INTERESTS = base+"/interests_delete.php";
    public static final String DISCOVER_USERS = base+"/discoverUsers.php";
    public static final String CREATE_CHATROOM = base+ "/t_createChatRoom.php";
    public static final String UPDATE_SIP = base+"/updateSip.php";
    public static final String FETCH_SIP = base+"/call.php";
    public static final String INCOMING_CALL = base+"/incomingCall.php";
    public static final String BLOCK_USER = base +"/block_update.php";
    public static final String HIDE_CHAT = base +"/makeInvisible.php";
    public static final String BLOCK_LIST = base +"/blockedList.php";
    public static final String UNBLOCK_USER = base +"/removed_blockedUser.php";
    public static final String GET_USER = base+"/getAllUserInfo.php";
    public static final String UPDATE_USERS = base+"/users_update.php";
    public static final String USERS_GET = base+"/users_get.php";
    public static final String USERS_ADD = base+"/users_insert.php";
}