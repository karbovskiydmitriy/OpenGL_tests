#version 430 core

#define SIZE 32

layout(local_size_x = SIZE, local_size_y = SIZE) in;

layout(rgba32f) uniform image2D image;

void main(void)
{
	ivec2 size = ivec2(imageSize(image));
	ivec2 coord = ivec2(gl_GlobalInvocationID.xy);
	vec2 uv = vec2(coord) / size;
	vec3 color = ((int(uv.x * 8) % 2 == 0) ^^ (int(uv.y * 8) % 2 == 1)) ? vec3(1) : vec3(0);
	imageStore(image, ivec2(gl_GlobalInvocationID.xy), vec4(color, 1.0));
}