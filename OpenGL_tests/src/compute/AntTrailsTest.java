package compute;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.lwjgl.glfw.GLFWKeyCallback;

import com.google.gson.Gson;

import misc.*;

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

	static int particlesCount = 1000000;
	static float[] particles;
	static AntSpecie antSpecie;

	static int particlesProgram;
	static int particlesVertexShader;
	static int particlesFragmentShader;

	static int fireworkComputeProgram;
	static int fireworkComputeShader;
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

		window = glfwCreateWindow(1280, 1024, "Firework test", glfwGetPrimaryMonitor(), NULL);
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
		int[] windowWidht = new int[1];
		int[] windowHeight = new int[1];
		glfwGetWindowSize(window, windowWidht, windowHeight);
		windowSize = new Dimension(windowWidht[0], windowHeight[0]);
		float k = 2;
		imageSize = new Dimension((int)(windowWidht[0] * k), (int)(windowHeight[0] * k));

		lastTime = System.currentTimeMillis();
		isRunning = false;

		glfwSetKeyCallback(window, new GLFWKeyCallback() {

			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (action == GLFW_PRESS && key == ' ') {
					isRunning = !isRunning;
				}
				if (key == GLFW_KEY_ESCAPE) {
					glfwDestroyWindow(window);
				}
			}
		});

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);

		createCapabilities();

		glViewport(0, 0, windowSize.width, windowSize.height);
		glEnable(GL_TEXTURE_2D);

		double circleRadius = 0.3;
		float aspect = (float) windowSize.height / windowSize.width;

		particles = new float[particlesCount * 8];
		for (int i = 0; i < particlesCount * 8;) {
			double circleAngle = Math.random() * Math.PI * 2;
			double rad = Math.random() * circleRadius;
			particles[i++] = (float) (0.5 + (Math.cos(circleAngle) * rad));
			particles[i++] = (float) (0.5 + (Math.sin(circleAngle) * rad) / aspect);
			particles[i++] = (float) (Math.random() * Math.PI * 2);
			particles[i++] = 1.0f;
			particles[i++] = 1.0f;
			particles[i++] = 0.15f;
			particles[i++] = 0.3f;
			particles[i++] = 1.0f;
		}

		String text = "";
		
		try {
			byte[] data = Files.readAllBytes(new File(".\\configs\\ants.json").toPath());
			text = new String(data);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		antSpecie = new Gson().fromJson(text, AntSpecie.class);
		
		System.out.println(antSpecie);
		
		particlesBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, particlesBuffer);
		glBufferData(GL_ARRAY_BUFFER, particles, GL_DYNAMIC_DRAW);

		particlesImage = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, particlesImage);
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, imageSize.width, imageSize.height);

		fireworkComputeProgram = glCreateProgram();
		fireworkComputeShader = loadShader(".\\shaders\\compute_ant_trails.glsl", GL_COMPUTE_SHADER);
		glAttachShader(fireworkComputeProgram, fireworkComputeShader);
		glLinkProgram(fireworkComputeProgram);
		glUseProgram(fireworkComputeProgram);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, particlesBuffer);

		blurComputeProgram = glCreateProgram();
		blurComputeShader = loadShader(".\\shaders\\compute_blur_trails.glsl", GL_COMPUTE_SHADER);
		glAttachShader(blurComputeProgram, blurComputeShader);
		glLinkProgram(blurComputeProgram);
	}

	private static void draw() {
		long currentTime = System.currentTimeMillis();
		float delta = (currentTime - lastTime) / 1000.0f;
		lastTime = currentTime;

		glUseProgram(fireworkComputeProgram);
		glUniform1f(0, delta); // delta
		glUniform1i(1, particlesCount); // count
		glUniform1f(2, antSpecie.moveSpeed); // moveSpeed
		glUniform1f(3, antSpecie.sensorLength); // sensorLength
		glUniform1i(4, antSpecie.sensorSize); // sensorSize
		glUniform1f(5, antSpecie.turnSpeed); // turnSpeed
		glUniform1f(6, antSpecie.sensorAngle); // sensorAngle
		glUniform1i(7, isRunning ? antSpecie.stepsPerFrame : 0);

		glBindImageTexture(1, particlesImage, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);

		glDispatchCompute(particlesCount / 32, 1, 1);

		if (isRunning) {
			glUseProgram(blurComputeProgram);
			glUniform1f(1, delta); // delta
			glUniform1i(2, 1); // kernelSize
			glUniform1f(3, antSpecie.fadeRate); // fadeRate
			glUniform1f(4, antSpecie.diffuseRate); // diffuseRate
//			glUniform1i(5, antSpecie.stepsPerFrame);

			glBindImageTexture(0, particlesImage, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);

			glDispatchCompute(imageSize.width / 32 + 1, imageSize.height / 32 + 1, 1);
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

	private static int loadShader(String fileName, int shaderType) {
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