package shaders;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderiv;
import static org.lwjgl.opengl.GL20.glShaderSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Shader {

	public ShaderType type;
	public String source;

	public Shader(ShaderType shaderType) {
		type = shaderType;
	}

	public void setSource(String shaderSource) {
		source = shaderSource;
	}

	public void compile() {

	}

	public static int loadShader(String fileName, int shaderType) {
		try {
			String shaderText = new String(Files.readAllBytes(new File(fileName).toPath()));
			int shader = glCreateShader(shaderType);
			glShaderSource(shader, shaderText);
			glCompileShader(shader);

			int[] compiled = new int[1];
			glGetShaderiv(shader, GL_COMPILE_STATUS, compiled);

			if (compiled[0] != 0) {
				return shader;
			} else {
				System.out.println(glGetShaderInfoLog(shader));
				glDeleteShader(shader);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0;
	}

}