#version 430 core

uniform sampler2D image;

layout(location = 0) out vec4 color;

void main()
{
	// color = vec4(vec2(gl_FragCoord.xy) / vec2(512.0), 0.0, 0.0);
	color = texture2D(image, vec2(gl_FragCoord.xy) / vec2(textureSize(image, 0)));
}