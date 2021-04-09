package types;

import static org.lwjgl.stb.STBImage.stbi_load;

import java.nio.ByteBuffer;

public class Image {

	public int width;
	public int height;
	public int colorDepth;
	public int fileSize;
	public ByteBuffer data;

	public Image(String fileName) {
		loadImage(fileName);
	}

	private void loadImage(String path) {
		int[] w = new int[1];
		int[] h = new int[1];
		int[] comp = new int[1];

		data = stbi_load(path, w, h, comp, 4);

		width = w[0];
		height = h[0];
		colorDepth = comp[0];
	}

	@Override
	public String toString() {
		return "Image { width = " + width + ", height = " + height + ", colorDepth = " + colorDepth + " }";
	}

}