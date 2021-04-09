package shaders;

public class Shader {

	public ShaderType type;
	public String source;

	public Shader(ShaderType shaderType) {
		type = shaderType;
	}

	public void setSource(String shaderSource) {
		source = shaderSource;
	}

	public void compile() {

	}

}