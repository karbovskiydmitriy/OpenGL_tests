#version 430 core

#define SIZE 32

layout(local_size_x = SIZE, local_size_y = SIZE) in;

layout(rgba32f) uniform image2D image;

void main(void)
{
	vec2 size = vec2(imageSize(image));
	vec2 uv = vec2(gl_GlobalInvocationID.xy) / size;
	imageStore(image, ivec2(gl_GlobalInvocationID.xy), vec4(uv, 0.0, 1.0));
}