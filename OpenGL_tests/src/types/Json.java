package types;

import java.io.File;
import java.nio.file.Files;

import com.google.gson.Gson;

public class Json {

	public static <T extends Object> T load(String fileName, Class<T> type) {
		String text = null;

		try {
			text = new String(Files.readAllBytes(new File(fileName).toPath()));
		} catch (Exception e) {
			System.err.println(e.getMessage());

			return null;
		}

		return new Gson().fromJson(text, type);
	}

	public static <T extends Object> boolean save(String fileName, T data) {
		String text = new Gson().toJson(data, data.getClass());

		if (text != null) {
			try {
				Files.writeString(new File(fileName).toPath(), text);

				return true;
			} catch (Exception e) {
				System.err.println(e.getMessage());

				return false;
			}
		} else {
			return false;
		}
	}

}