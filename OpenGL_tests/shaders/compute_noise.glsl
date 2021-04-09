#version 430 core

#define SIZE 32

layout(local_size_x = SIZE, local_size_y = SIZE) in;

layout(rgba32f, binding = 0) uniform image2D image;

float random (vec2 st)
{
    return fract(sin(dot(st.xy, vec2(12.9898,78.233))) * 43758.5453123);
}

void main(void)
{
	ivec2 size = ivec2(imageSize(image));
	ivec2 coord = ivec2(gl_GlobalInvocationID.xy);
	vec2 uv = vec2(coord) / size;
	vec3 color = vec3(random(uv));
	
	imageStore(image, coord, vec4(color, 1.0));
}