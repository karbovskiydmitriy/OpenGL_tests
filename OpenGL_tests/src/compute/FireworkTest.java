package compute;

import java.awt.Dimension;

import org.lwjgl.glfw.GLFWVidMode;

import shaders.Shader;

import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

public class FireworkTest {

	static long window;
	static Dimension dimension;

	static int particlesImage;

	static long lastTime;
	
	static int particlesCount = 1024;
	static float[] particles;

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

		GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		window = glfwCreateWindow(videoMode.width(), videoMode.height(), "Firework test", glfwGetPrimaryMonitor(), NULL);
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
		glEnable(GL_PROGRAM_POINT_SIZE);
		glEnable(GL_BLEND);
		glEnable(GL_POINT_SPRITE);

		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		particles = new float[particlesCount * 8];
		for (int i = 0; i < particlesCount * 8;) {
			particles[i++] = (float) (Math.random());
			particles[i++] = (float) (Math.random());
			particles[i++] = (float) (Math.random() * 2 - 1) / 10;
			particles[i++] = (float) (Math.random() * 2 - 1) / 10;
			particles[i++] = (float) (Math.random());
			particles[i++] = (float) (Math.random());
			particles[i++] = (float) (Math.random());
			particles[i++] = 1.0f;
		}

		particlesBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, particlesBuffer);
		glBufferData(GL_ARRAY_BUFFER, particles, GL_DYNAMIC_DRAW);

		particlesImage = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, particlesImage);
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, dimension.width, dimension.height);

		fireworkComputeProgram = glCreateProgram();
		fireworkComputeShader = Shader.loadShader(".\\shaders\\compute_particles_firework.glsl", GL_COMPUTE_SHADER);
		glAttachShader(fireworkComputeProgram, fireworkComputeShader);
		glLinkProgram(fireworkComputeProgram);
		glUseProgram(fireworkComputeProgram);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, particlesBuffer);
		
		blurComputeProgram = glCreateProgram();
		blurComputeShader = Shader.loadShader(".\\shaders\\compute_blur_trails.glsl", GL_COMPUTE_SHADER);
		glAttachShader(blurComputeProgram, blurComputeShader);
		glLinkProgram(blurComputeProgram);

		lastTime = System.currentTimeMillis();
	}

	private static void draw() {
		long currentTime = System.currentTimeMillis();
		float delta = (currentTime - lastTime) / 1000.0f;
		lastTime = currentTime;
		
		glUseProgram(fireworkComputeProgram);
		glUniform1f(0, delta);
		
		glBindImageTexture(1, particlesImage, 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glBindImageTexture(2, particlesImage, 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);		
		
		glDispatchCompute(particlesCount / 32 + 1, 1, 1);

		glUseProgram(blurComputeProgram);
		glUniform1f(1, delta);
		glUniform1i(2, 1);
		glUniform1f(3, 5f);
		glUniform1f(4, 0.001f);

		glBindImageTexture(0, particlesImage, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		
		glDispatchCompute(dimension.width / 32 + 1, dimension.height / 32 + 1, 1);

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