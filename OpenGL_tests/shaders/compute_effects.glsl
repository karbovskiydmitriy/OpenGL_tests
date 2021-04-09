#version 430 core

#define SIZE 32

layout(local_size_x = SIZE, local_size_y = SIZE) in;

layout(rgba8, binding = 0, location = 0) uniform image2D inputImage;
layout(rgba8, binding = 1, location = 1) uniform image2D outputImage;

layout(location = 2) uniform float time;
layout(location = 3) uniform int effect;

layout(location = 4) uniform int blurKernelSize = 1;

vec3 getPixel(ivec2 pos)
{
	ivec2 size = ivec2(imageSize(inputImage));
	pos = (pos + size) % size;
	
	return imageLoad(inputImage, pos).xyz;
}

vec3 getSmoothPixel(ivec2 pos, int kernelSize)
{
	vec3 color = vec3(0.0);
	
	for (int i = 0; i < kernelSize; i++)
	{
		for (int j = 0; j < kernelSize; j++)
		{
			color += getPixel(pos + ivec2(i, j));
		}
	}
	
	color /= (kernelSize * kernelSize);
	
	return color;
}

void main(void)
{
	ivec2 size = ivec2(imageSize(inputImage));
	ivec2 coord = ivec2(gl_GlobalInvocationID.xy);
	vec2 uv = vec2(coord) / size;
	vec3 color = vec3(0.0);
	
	switch (effect)
	{
		case 0:
			color = getPixel(coord);
			break;
		case 1:
			color = 1.0 - getPixel(coord);
			break;
		case 2:
			color = getPixel(coord + ivec2(int(time * 64), 0));
			break;
		case 3:
			color = getPixel(coord) * (0.7 - length(uv - 0.5));
			break;
		case 4:
			color = getSmoothPixel(coord, blurKernelSize);
			break;
		default:
			color = vec3(uv, 0.0);
			break;
	}
	imageStore(outputImage, coord, vec4(color, 1.0));
}