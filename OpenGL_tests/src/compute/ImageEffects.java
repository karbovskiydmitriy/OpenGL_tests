package compute;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import types.Image;

import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

public class ImageEffects {

	static long window;
	static Dimension dimension;

	static int sourceImage;
	static int resultImage;
	
	static int computeShader;
	static int computeProgram;
	
	static long startTime;
	static int effect;

	public static void main(String[] args) {
		init();

		while (!glfwWindowShouldClose(window)) {
			draw();

			glfwSwapBuffers(window);
			glfwPollEvents();
		}

		setCapabilities(null);
		glfwDestroyWindow(window);
		glfwTerminate();
	}

	private static void init() {
		glfwInit();

		window = glfwCreateWindow(1024, 1024, "Different image effects done in compute shader", NULL, NULL);
		int[] windowWidht = new int[1];
		int[] windowHeight = new int[1];
		glfwGetWindowSize(window, windowWidht, windowHeight);
		dimension = new Dimension(windowWidht[0], windowHeight[0]);
		
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);

		createCapabilities();

		glViewport(0, 0, dimension.width, dimension.height);
		glEnable(GL_TEXTURE_2D);

		String path = ".\\images\\sadCat.bmp";
		Image bitmap = new Image(path);

		sourceImage = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, sourceImage);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bitmap.width, bitmap.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, bitmap.data);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		resultImage = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, resultImage);
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, bitmap.width, bitmap.height);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		computeProgram = glCreateProgram();
		computeShader = loadShader(".\\shaders\\compute_effects.glsl", GL_COMPUTE_SHADER);
		glAttachShader(computeProgram, computeShader);
		glLinkProgram(computeProgram);
		glUseProgram(computeProgram);
		
		startTime = System.currentTimeMillis();
		effect = 4;
	}

	private static void draw() {
		long currentTime = System.currentTimeMillis();
		float time = (currentTime - startTime) / 1000.0f;
		
		glUniform1f(2, time);
		glUniform1i(3, effect);
		glUniform1i(4, 18);
		
		glBindImageTexture(0, sourceImage, 0, false, 0, GL_READ_ONLY, GL_RGBA8);
		glBindImageTexture(1, resultImage, 0, false, 0, GL_WRITE_ONLY, GL_RGBA8);
		glDispatchCompute(dimension.width / 32 + 1, dimension.height / 32 + 1, 1);
		
		glClear(GL_COLOR_BUFFER_BIT);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 1);
		glVertex2i(-1, -1);
		glTexCoord2f(0, 0);
		glVertex2i(-1, 1);
		glTexCoord2f(1, 0);
		glVertex2i(1, 1);
		glTexCoord2f(1, 1);
		glVertex2i(1, -1);
		glEnd();
	}

	private static int loadShader(String fileName, int shaderType) {
		try {
			String shaderText = new String(Files.readAllBytes(new File(fileName).toPath()));
			int shader = glCreateShader(shaderType);
			glShaderSource(shader, shaderText);
			glCompileShader(shader);

			if (glGetShaderi(shader, GL_COMPILE_STATUS) != 0) {
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