package compute;

import java.awt.Dimension;
import java.awt.Point;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import shaders.Shader;

import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

public class BasicfragmentRaytracerTest {

	static long window;
	static Dimension windowSize;

	static long lastTime;

	static int raytracerProgram;
	static int raytracerShader;

	static Vector3f playerPosition;
	static Point walk;
	static Vector2f cameraAngle;
	static float angle;

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

		window = glfwCreateWindow(1280, 1024, "Firework test", glfwGetPrimaryMonitor(), NULL);
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
		int[] windowWidht = new int[1];
		int[] windowHeight = new int[1];
		glfwGetWindowSize(window, windowWidht, windowHeight);
		windowSize = new Dimension(windowWidht[0], windowHeight[0]);

		lastTime = System.currentTimeMillis();
		playerPosition = new Vector3f();
		walk = new Point();
		cameraAngle = new Vector2f();
		angle = 0.0f;

		glfwSetKeyCallback(window, new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (action == GLFW_PRESS) {
					switch (key) {
					case GLFW_KEY_S:
						walk.y = -1;
						break;
					case GLFW_KEY_W:
						walk.y = 1;
						break;
					case GLFW_KEY_A:
						walk.x = -1;
						break;
					case GLFW_KEY_D:
						walk.x = 1;
						break;
					case GLFW_KEY_ESCAPE:
						glfwSetWindowShouldClose(window, true);
						break;
					}
				} else if (action == GLFW_RELEASE) {
					switch (key) {
					case GLFW_KEY_S:
					case GLFW_KEY_W:
						walk.y = 0;
						break;
					case GLFW_KEY_A:
					case GLFW_KEY_D:
						walk.x = 0;
						break;
					}
				}
			}
		});

		glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double x, double y) {
				int dx = (int) x - windowSize.width / 2;
				int dy = (int) y - windowSize.height / 2;
				cameraAngle.x -= dx / 360.0f;
				cameraAngle.y -= dy / 360.0f;
				glfwSetCursorPos(window, windowSize.width / 2, windowSize.height / 2);
			}
		});

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);

		createCapabilities();

		glViewport(0, 0, windowSize.width, windowSize.height);
		glEnable(GL_TEXTURE_2D);

		raytracerProgram = glCreateProgram();
		raytracerShader = Shader.loadShader(".\\shaders\\fragment_basic_raytracer.glsl", GL_FRAGMENT_SHADER);
		glAttachShader(raytracerProgram, raytracerShader);
		glLinkProgram(raytracerProgram);
		glUseProgram(raytracerProgram);
	}

	private static void draw() {
		long currentTime = System.currentTimeMillis();
		float delta = (currentTime - lastTime) / 1000.0f;
		lastTime = currentTime;
		float speed = 500.0f * delta;
		angle += 2.2f * delta;

		if (walk.x == -1) {
			playerPosition.x -= Math.cos(cameraAngle.x) * speed;
			playerPosition.z -= Math.sin(cameraAngle.x) * speed;
		} else if (walk.x == 1) {
			playerPosition.x += Math.cos(cameraAngle.x) * speed;
			playerPosition.z += Math.sin(cameraAngle.x) * speed;
		}

		if (walk.y == -1) {
			playerPosition.x -= Math.cos(cameraAngle.x + Math.PI / 2) * speed;
			playerPosition.z -= Math.sin(cameraAngle.x + Math.PI / 2) * speed;
		} else if (walk.y == 1) {
			playerPosition.x += Math.cos(cameraAngle.x + Math.PI / 2) * speed;
			playerPosition.z += Math.sin(cameraAngle.x + Math.PI / 2) * speed;
		}

		glUniform3f(0, playerPosition.x, playerPosition.y, playerPosition.z);
		glUniform2f(3, cameraAngle.x, cameraAngle.y);
		glUniform1f(5, angle);

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