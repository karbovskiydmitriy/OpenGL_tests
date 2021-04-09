#version 430 core

#define SIZE 32

layout(local_size_x = SIZE, local_size_y = SIZE) in;

layout(rgba8, binding = 0) uniform image2D inputImage;
layout(rgba8, binding = 1) uniform image2D outputImage;

void main(void)
{
	ivec2 coord = ivec2(gl_GlobalInvocationID.xy);
	vec3 color = imageLoad(inputImage, coord).xyz;
	color = 1.0 - color;
	imageStore(outputImage, coord, vec4(color, 0.0));
}
