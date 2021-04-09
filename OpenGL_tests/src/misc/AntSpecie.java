/*package misc;

import java.io.Serializable;

@SuppressWarnings("preview")
public record AntSpecie(float moveSpeed, float turnSpeed, float sensorLength, int sensorSize, float sensorAngle,
		float fadeRate, float diffuseRate) implements Serializable {
	@Override
	public String toString() {
		return String.format("String { moveSpeed = %f, turnSpeed = %f, sensorLength = %f, sensorSize = %i, sensorAngle = %f, fadeRate = %f, diffuseRate = %f}",
				moveSpeed, turnSpeed, sensorLength, sensorSize, sensorAngle, fadeRate, diffuseRate);
	}
}*/

package misc;

import java.io.Serializable;

import org.lwjgl.util.Color;

// @SuppressWarnings("preview")
public class AntSpecie implements Serializable {

	private static final long serialVersionUID = -5891750847214479162L;

	public Color color;
	public float moveSpeed;
	public float turnSpeed;
	public float sensorLength;
	public int sensorSize;
	public float sensorAngle;
	public float fadeRate;
	public float diffuseRate;
	public int stepsPerFrame;

	@Override
	public String toString() {
		return String.format(
				"AntSpecie { color = %s, moveSpeed = %f, turnSpeed = %f, sensorLength = %f, sensorSize = %d, sensorAngle = %f, fadeRate = %f, diffuseRate = %f, stepsPerFrame = %d}",
				String.format("Color { red = %d, green = %d, blue = %d, alpha = %d }", color.getRed(), color.getGreen(),
						color.getBlue(), color.getAlpha()),
				moveSpeed, turnSpeed, sensorLength, sensorSize, sensorAngle, fadeRate, diffuseRate, stepsPerFrame);
	}

}