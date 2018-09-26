package client.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigLoader {
	
	public static Config load() {
		Config config = null;
		try {
			InputStream is = ConfigLoader.class.getResourceAsStream("config");
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader fileReader = new BufferedReader(isr);
			String line = fileReader.readLine();
			if (line != null) {
				String[] params = line.split("\t");
				if (params.length == 2) {
					config = new Config(params[0], Integer.parseInt(params[1]));
					System.out.println(
							"Config loaded, server: " + params[0] + ", port: " + params[1]);
				}
			}
			fileReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("Config file not found.");
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
		return config;
	}
}