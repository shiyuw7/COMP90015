package common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {

	/**
	 * Wrap data type and JSON data into JSON format.
	 * 
	 * @param type
	 *            = "login", jsonObject = {"username": "Alice"}
	 * @Return {"type": "login", "data": {"username": "Alice"}}
	 */
	public static JSONObject parse(String type, JSONObject jsonObject) {
		JSONObject result = new JSONObject();
		result.put(Constants.TYPE, type);
		result.put(Constants.DATA, jsonObject);
		return result;
	}

	/**
	 * Wrap data type and String data into JSON format.
	 * 
	 * @param type
	 *            = "login", jsonObject = "{'username': 'Alice'}"
	 * @Return {"type": "login", "data": {"username": "Alice"}}
	 */
	public static JSONObject parse(String type, String jsonString) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonString);
		JSONObject result = new JSONObject();
		result.put(Constants.TYPE, type);
		result.put(Constants.DATA, jsonObject);
		return result;
	}

	/**
	 * Wrap data type and String data into JSON format.
	 * 
	 * @param jsonString
	 *            = "{"type": "login", "data": {"username": "Alice"}}"
	 * @Return "login"
	 */
	public static String getRequestType(String jsonString) {
		JSONObject jsonObject = new JSONObject(jsonString);
		return jsonObject.getString(Constants.TYPE);
	}

	/**
	 * Wrap data type and String data into JSON format.
	 * 
	 * @param jsonString
	 *            = "{"type": "login", "data": {"username": "Alice"}}"
	 * @Return "{"username": "Alice"}"
	 */
	public static JSONObject getAllData(String jsonString) {
		JSONObject jsonObject = new JSONObject(jsonString);
		return (JSONObject) jsonObject.get(Constants.DATA);
	}

	/**
	 * Wrap data type and String data into JSON format.
	 * 
	 * @param jsonString
	 *            = "{"type": "login", "data": {"username": "Alice"}}", dataType =
	 *            "username"
	 * @Return "Alice"
	 */
	public static String getStringDataByType(String jsonString, String dataType) {
		JSONObject jsonObject = new JSONObject(jsonString);
		JSONObject data = (JSONObject) jsonObject.get(Constants.DATA);
		return data.getString(dataType);
	}

	/**
	 * Wrap data type and String data into JSON format.
	 * 
	 * @param jsonString
	 *            = "{"type": "login", "data": {"id": 1}}", dataType = "id"
	 * @Return 1
	 */
	public static int getIntDataByType(String jsonString, String dataType) {
		JSONObject jsonObject = new JSONObject(jsonString);
		JSONObject data = (JSONObject) jsonObject.get(Constants.DATA);
		return data.getInt(dataType);
	}

	/**
	 * Wrap data type and String data into JSON format.
	 * 
	 * @param jsonString
	 *            = "{"type": "login", "data": {"status": true}}", dataType =
	 *            "status"
	 * @Return true
	 */
	public static boolean getBooleanDataByType(String jsonString, String dataType) {
		JSONObject jsonObject = new JSONObject(jsonString);
		JSONObject data = (JSONObject) jsonObject.get(Constants.DATA);
		return data.getBoolean(dataType);
	}

	public static String[] jsonArrayToStringArray(JSONArray jsonArray, String dataType) {
		String[] stringArray = new String[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); i++) {
			stringArray[i] = jsonArray.getJSONObject(i).getString(dataType);
		}
		return stringArray;
	}
}