package compute;

import java.awt.Dimension;

import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;

import misc.*;
import shaders.Shader;
import types.Json;

import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

public class AntTrailsTest {

	static long window;
	static Dimension windowSize;
	static Dimension imageSize;

	static int particlesImage;

	static long lastTime;
	static boolean isRunning;

	static int particlesCount = 40000;
	static float[] particles;
	static AntSpecie antSpecie;

	static int antsComputeProgram;
	static int antsComputeShader;
	static int blurComputeProgram;
	static int blurComputeShader;

	static int particlesBuffer;

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

		long monitor = glfwGetPrimaryMonitor();
		GLFWVidMode videoMode = glfwGetVideoMode(monitor);
		window = glfwCreateWindow(videoMode.width(), videoMode.height(), "Ants test", monitor, NULL);
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
		int[] windowWidht = new int[1];
		int[] windowHeight = new int[1];
		glfwGetWindowSize(window, windowWidht, windowHeight);
		windowSize = new Dimension(windowWidht[0], windowHeight[0]);
		float k = 1f;
		imageSize = new Dimension((int) (windowWidht[0] * k), (int) (windowHeight[0] * k));

		lastTime = System.currentTimeMillis();
		isRunning = false;

		glfwSetKeyCallback(window, new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key == GLFW_KEY_SPACE && action == GLFW_PRESS) {
					isRunning = !isRunning;
				}
				if (key == GLFW_KEY_ESCAPE) {
					glfwSetWindowShouldClose(window, true);
				}
			}
		});

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);

		createCapabilities();

		glViewport(0, 0, windowSize.width, windowSize.height);
		glEnable(GL_TEXTURE_2D);

		float aspect = (float) windowSize.height / windowSize.width;

		antSpecie = Json.load("./configs/ants.json", AntSpecie.class);
		System.out.println(antSpecie);

		particles = new float[particlesCount * 8];
		particlesBuffer = glGenBuffers();
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, particlesBuffer);
		glBufferData(GL_SHADER_STORAGE_BUFFER, particles, GL_DYNAMIC_DRAW);

		particlesImage = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, particlesImage);
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, imageSize.width, imageSize.height);

		antsComputeProgram = glCreateProgram();
		antsComputeShader = Shader.loadShader("./shaders/compute_ant_trails.glsl", GL_COMPUTE_SHADER);
		glAttachShader(antsComputeProgram, antsComputeShader);
		glLinkProgram(antsComputeProgram);
		glUseProgram(antsComputeProgram);

		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, particlesBuffer);
		glUniform1i(0, 1); // operation
		glUniform1f(1, aspect); // aspect
		glUniform1i(3, particlesCount); // count
		glDispatchCompute(particlesCount / 4, 1, 1);

		blurComputeProgram = glCreateProgram();
		blurComputeShader = Shader.loadShader("./shaders/compute_blur_trails.glsl", GL_COMPUTE_SHADER);
		glAttachShader(blurComputeProgram, blurComputeShader);
		glLinkProgram(blurComputeProgram);
	}

	private static void draw() {
		long currentTime = System.currentTimeMillis();
		float delta = (currentTime - lastTime) / 1000.0f;
		lastTime = currentTime;

		glUseProgram(antsComputeProgram);
		glUniform1i(0, 2); // operation
		glUniform1f(2, delta / (isRunning ? antSpecie.stepsPerFrame : 1)); // delta
		glUniform1i(3, particlesCount); // count
		glUniform1f(4, antSpecie.moveSpeed); // moveSpeed
		glUniform1f(5, antSpecie.sensorLength); // sensorLength
		glUniform1i(6, antSpecie.sensorSize); // sensorSize
		glUniform1f(7, antSpecie.turnSpeed); // turnSpeed
		glUniform1f(8, antSpecie.sensorAngle); // sensorAngle
		glUniform1i(9, isRunning ? antSpecie.stepsPerFrame : 0);

		glBindImageTexture(1, particlesImage, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);

		glDispatchCompute(particlesCount / 4, 1, 1);

		if (isRunning) {
			glUseProgram(blurComputeProgram);
			glUniform1f(1, delta / (isRunning ? antSpecie.stepsPerFrame : 1)); // delta
			glUniform1i(2, 1); // kernelSize
			glUniform1f(3, antSpecie.fadeRate); // fadeRate
			glUniform1f(4, antSpecie.diffuseRate); // diffuseRate
//			glUniform1i(5, antSpecie.stepsPerFrame);

			glBindImageTexture(0, particlesImage, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);

			glDispatchCompute(imageSize.width / 2, imageSize.height / 2, 1);
		}

		glBindTexture(GL_TEXTURE_2D, particlesImage);

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