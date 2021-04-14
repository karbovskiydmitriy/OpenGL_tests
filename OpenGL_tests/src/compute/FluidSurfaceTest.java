package compute;

import java.awt.Dimension;
import java.awt.Point;

import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWVidMode;

import shaders.Shader;

import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

public class FluidSurfaceTest {

	static long window;
	static Dimension windowSize;
	static Dimension imageSize;

	static Point mousePosition;
	static boolean isPressed;

	static int densityMapTemp;
	static int densityMap;
	static int velocityMapTemp;
	static int velocityMap;
	static int image;

	static long lastTime;

	static int surfaceProgram;
	static int surfaceShader;

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
		window = glfwCreateWindow(videoMode.width(), videoMode.height(), "Firework test", glfwGetPrimaryMonitor(), NULL);
//		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
		int[] windowWidht = new int[1];
		int[] windowHeight = new int[1];
		glfwGetWindowSize(window, windowWidht, windowHeight);
		windowSize = new Dimension(windowWidht[0], windowHeight[0]);
		imageSize = new Dimension(windowWidht[0], windowHeight[0]);
		mousePosition = new Point();

		glfwSetCursorPosCallback(window, new GLFWCursorPosCallbackI() {
			@Override
			public void invoke(long window, double x, double y) {
				mousePosition.x = (int) x;
				mousePosition.y = (int) y;
			}
		});

		glfwSetMouseButtonCallback(window, new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				isPressed = action == GLFW_PRESS;
			}
		});

		glfwSetKeyCallback(window, new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key == GLFW_KEY_ESCAPE) {
					glfwSetWindowShouldClose(window, true);
				}
			}
		});

		lastTime = System.currentTimeMillis();

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);

		createCapabilities();

		glViewport(0, 0, windowSize.width, windowSize.height);
		glEnable(GL_TEXTURE_2D);

		densityMapTemp = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, densityMapTemp);
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_R32F, imageSize.width, imageSize.height);

		densityMap = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, densityMap);
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_R32F, imageSize.width, imageSize.height);

		velocityMapTemp = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, velocityMapTemp);
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RG32F, imageSize.width, imageSize.height);

		velocityMap = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, velocityMap);
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RG32F, imageSize.width, imageSize.height);

		image = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, image);
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, imageSize.width, imageSize.height);

		surfaceProgram = glCreateProgram();
		surfaceShader = Shader.loadShader(".\\shaders\\compute_fluid_surface.glsl", GL_COMPUTE_SHADER);
		glAttachShader(surfaceProgram, surfaceShader);
		glLinkProgram(surfaceProgram);
		glUseProgram(surfaceProgram);
		
		glBindImageTexture(0, densityMapTemp, 0, false, 0, GL_READ_WRITE, GL_R32F);
		glBindImageTexture(1, densityMap, 0, false, 0, GL_READ_WRITE, GL_R32F);
		glBindImageTexture(2, velocityMapTemp, 0, false, 0, GL_READ_WRITE, GL_RG32F);
		glBindImageTexture(3, velocityMap, 0, false, 0, GL_READ_WRITE, GL_RG32F);
		glBindImageTexture(4, image, 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
	}

	private static void draw() {
		long currentTime = System.currentTimeMillis();
		float delta = (currentTime - lastTime) / 1000.0f;
		lastTime = currentTime;

		glUseProgram(surfaceProgram);
		glUniform1i(5, 1);
		glUniform1f(10, delta);
		glUniform2i(11, mousePosition.x, mousePosition.y);
		glUniform1i(13, isPressed ? 1 : 0);
		glUniform1f(14, 50.0f);
		glUniform1f(15, 15.0f);
		// glUniform1f(16, 0.999f);
		glDispatchCompute(imageSize.width / 32, imageSize.height / 32, 1);
		glUniform1i(5, 2);
		glDispatchCompute(imageSize.width / 32, imageSize.height / 32, 1);

		glUseProgram(0);
		glBindTexture(GL_TEXTURE_2D, image);

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