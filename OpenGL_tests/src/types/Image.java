package types;

import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

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
		try (MemoryStack stack = stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);

			data = stbi_load(path, w, h, comp, 4);

			width = w.get(0);
			height = h.get(0);
			colorDepth = comp.get(0);
		}
	}

	@Override
	public String toString() {
		return "Image { width = " + width + ", height = " + height + ", colorDepth = " + colorDepth + " }";
	}

}