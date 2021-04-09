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

// @SuppressWarnings("preview")
public class AntSpecie implements Serializable {

	private static final long serialVersionUID = -5891750847214479162L;

	public float moveSpeed;
	public float turnSpeed;
	public float sensorLength;
	public int sensorSize;
	public float sensorAngle;
	public float fadeRate;
	public float diffuseRate;

	@Override
	public String toString() {
		return String.format(
				"String { moveSpeed = %f, turnSpeed = %f, sensorLength = %f, sensorSize = %d, sensorAngle = %f, fadeRate = %f, diffuseRate = %f}",
				moveSpeed, turnSpeed, sensorLength, sensorSize, sensorAngle, fadeRate, diffuseRate);
	}

}