#version 430 core

#define SIZE 2

layout(local_size_x = SIZE, local_size_y = SIZE) in;

layout(rgba32f, binding = 0, location = 0) uniform image2D image;

layout(location = 1) uniform float delta;
layout(location = 2) uniform int kernelSize;
layout(location = 3) uniform float fadeRate;
layout(location = 4) uniform float diffuseRate;
// layout(location = 5) uniform int stepsPerFrame;

void main(void)
{
	ivec2 size = ivec2(imageSize(image));
	ivec2 coord = ivec2(gl_GlobalInvocationID.xy);
	vec4 color = vec4(0.0);
	int count = 0;
	
	for (int i = -kernelSize; i <= kernelSize; i++)
	{
		for (int j = -kernelSize; j <= kernelSize; j++)
		{
			ivec2 xy = coord + ivec2(i, j);
			xy = min(size - 1, max(xy, 0));
			color += imageLoad(image, xy);
		}
	}
	
	vec4 blurredColor = color / (2 * kernelSize * 2 * kernelSize);
	float diffuse = clamp(diffuseRate * delta, 0, 1);
	blurredColor = imageLoad(image, coord) * (1 - diffuse) + blurredColor * diffuseRate;
	
	imageStore(image, coord, vec4(max(blurredColor.xyz - fadeRate  * delta, 0), 1.0));
}