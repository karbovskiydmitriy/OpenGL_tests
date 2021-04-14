package compute;

import java.awt.Dimension;

import org.lwjgl.glfw.GLFWVidMode;

import shaders.Shader;

import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

public class MyFirstComputeShaderTest {
	
	static long window;
	static Dimension dimension;
	
	static int image;
	
	static int computeShader;
	static int computeProgram;

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

		GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		window = glfwCreateWindow(videoMode.width(), videoMode.height(), "Compute shader example", glfwGetPrimaryMonitor(), NULL);
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
	    glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, dimension.width, dimension.height);
		
		computeShader = Shader.loadShader(".\\shaders\\compute_first.glsl", GL_COMPUTE_SHADER);
		computeProgram = glCreateProgram();
		glAttachShader(computeProgram, computeShader);
		glLinkProgram(computeProgram);
	}

	private static void draw() {
		glUseProgram(computeProgram);
		glBindImageTexture(0, image, 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		glDispatchCompute(dimension.width / 32 + 1, dimension.height / 32 + 1, 1);
		
		glClear(GL_COLOR_BUFFER_BIT);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex2i(-1, -1);
		glTexCoord2f(0, 1);
		glVertex2i(-1, 1);
		glTexCoord2f(1, 1);
		glVertex2i(1, 1);
		glTexCoord2f(1, 0);
		glVertex2i(1, -1);
		glEnd();
	}

}