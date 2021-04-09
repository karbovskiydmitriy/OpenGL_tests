package texture;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.Dimension;

import types.Image;

public class DrawTextureTest {

	static long window;
	static Dimension dimension;

	static int image;

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

		window = glfwCreateWindow(1024, 11024280, "Compute shader example", NULL, NULL);
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

		image = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, image);

		String imagePath = ".\\images\\sadCat.bmp";
		Image bitmap = new Image(imagePath);
			
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB8, bitmap.width, bitmap.height, 0, GL_RGB, GL_UNSIGNED_BYTE, bitmap.data);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	}

	private static void draw() {
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

}