package server.common;

public class Constants {

	public final static String[] CHARACTERS = new String[] { "A", "B", "C", "D", "E", "F", "G",
			"H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
			"Y", "Z" };
	public final static String[] COUNT_TABLE_COLUMN_NAME = new String[] { "User Name", "Count" };
	public final static int WIDTH = 800;
	public final static int HEIGHT = 640;

	// Data transformation format
	public final static String TYPE = "type";
	public final static String DATA = "data";

	/* ************************* Request Type *********************************/

	// Login Request type
	public final static String LOGIN = "login";
	// Lobby Request type
	public final static String ADD_USER_TO_LOBBY = "addUserToLobby";
	public final static String REMOVE_USER_FROM_LOBBY = "removeUserFromLobby";
	public final static String ADD_USER_TO_ROOM = "addUserToRoom";
	public final static String REMOVE_USER_FROM_ROOM = "removeUserFromRoom";
	public final static String ADD_USER_TO_GAME = "addUserToGame";
	public final static String REMOVE_USER_FROM_GAME = "removeUserFromGame";
	public final static String INVITE = "invite";
	public final static String INVITE_REPLY = "inviteReply";
	public final static String REFRESH = "refresh";
	public final static String START_GAME = "startGame";
	public final static String REPLY_START_GAME = "replyStartGame";
	public final static String CLEAR_ROOM = "clearRoom";
	// Game Request type
	public final static String PLACE_CHARACTER = "placeCharacter";
	public final static String PASS = "pass";
	public final static String VOTE = "vote";
	public final static String LOGOUT = "logout";
	public final static String GAME_OVER = "gameOver";
	public final static String VOTE_REPLY = "voteReply";
	// ...

	/* ************************* Data Type *********************************/

	// JsonArray data type
	public final static String LOBBY_LIST = "lobbyList";
	public final static String ROOM_LIST = "roomList";
	public final static String GAME_LIST = "gameList";
	public final static String COUNT_LIST = "countList";
	// String data type
	public final static String USER_NAME = "userName";
	public final static String NEXT_USER_NAME = "nextUserName";
	public final static String PLACE_VALUE = "placeValue";
	public final static String CHOSEN_WORD = "chosenWord";
	public final static String WINNER = "winner";
	// Integer data type
	public final static String USER_STATUS = "userStatus";
	public final static String USER_COUNT = "userCount";
	public final static String PLACE_ROW = "placeRow";
	public final static String PLACE_COLUMN = "placeColumn";
	// Boolean data type
	public final static String IS_UNIQUE = "isUnique";
	public final static String IS_ACCEPTED = "isAccepted";
	public final static String IS_STARTED = "isStarted";
	public final static String IS_YOUR_ROUND = "isYourRound";
	public final static String IS_WORD = "isWord";
	public final static String IS_TIE = "isTie";
	// ...

}