package common;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {

	/**
	 * Wrap data type and JSON data into JSON format. For example: 
	 * Data: type = "login", jsonObject = {"username": "Alice"}
	 * Result: {"type": "login", "data": {"username": "Alice"}}}
	 */
	public JSONObject parse(String type, JSONObject jsonObject) {
		JSONObject result = new JSONObject();
		result.put(Constants.TYPE, type);
		result.put(Constants.DATA, jsonObject);
		return result;
	}

	/**
	 * Wrap data type and String data into JSON format. For example: 
	 * Data: type = "login", jsonObject = "{'username': 'Alice'}"
	 * Result: {"type": "login", "data": {"username": "Alice"}}}
	 */
	public JSONObject parse(String type, String jsonString) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonString);
		JSONObject result = new JSONObject();
		result.put(Constants.TYPE, type);
		result.put(Constants.DATA, jsonObject);
		return result;
	}

	public String getType(String jsonString) {
		JSONObject jsonObject = new JSONObject(jsonString);
		return jsonObject.getString(Constants.TYPE);
	}

	public JSONObject getData(String jsonString) {
		JSONObject jsonObject = new JSONObject(jsonString);
		return (JSONObject) jsonObject.get(Constants.DATA);
	}
}