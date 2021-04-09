package compute;

import java.awt.Dimension;

import shaders.Shader;

import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

public class ParticlesTest {

	static long window;
	static Dimension dimension;

	static int image;

	static int particlesCount = 1024 * 16;
	static float[] particles;

	static int particlesProgram;
	static int particlesVertexShader;
	static int particlesFragmentShader;

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

		window = glfwCreateWindow(1024, 1024, "Particles test", NULL, NULL);
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
			particles[i++] = (float) (Math.random() * 2 - 1);
			particles[i++] = (float) (Math.random() * 2 - 1);
			particles[i++] = 0.0f;
			particles[i++] = 0.0f;
			particles[i++] = (float) (Math.random());
			particles[i++] = (float) (Math.random());
			particles[i++] = (float) (Math.random());
			particles[i++] = 1.0f;
		}

		particlesBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, particlesBuffer);
		glBufferData(GL_ARRAY_BUFFER, particles, GL_DYNAMIC_DRAW);

		image = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, image);
		
		particlesVertexShader = Shader.loadShader(".\\shaders\\vertex_particles.glsl", GL_VERTEX_SHADER);
		particlesFragmentShader = Shader.loadShader(".\\shaders\\fragment_particles.glsl", GL_FRAGMENT_SHADER);
		particlesProgram = glCreateProgram();
		glAttachShader(particlesProgram, particlesVertexShader);
		glAttachShader(particlesProgram, particlesFragmentShader);
		glLinkProgram(particlesProgram);
	}

	private static void draw() {
		glClear(GL_COLOR_BUFFER_BIT);
		glUseProgram(particlesProgram);

		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		glBindBuffer(GL_ARRAY_BUFFER, particlesBuffer);

		glVertexAttribPointer(0, 2, GL_FLOAT, false, 32, 0);
		glVertexAttribPointer(1, 4, GL_FLOAT, false, 32, 16);

		glUniform1f(0, 10.0f);
		glDrawArrays(GL_POINTS, 0, particlesCount);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
	}

}