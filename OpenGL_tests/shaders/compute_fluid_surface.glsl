#version 430 core

#define LOCAL_SIZE 32

layout(local_size_x = LOCAL_SIZE, local_size_y = LOCAL_SIZE) in;

layout(r32f, 	binding = 0, location = 0) uniform image2D densityMapTemp;
layout(r32f, 	binding = 1, location = 1) uniform image2D densityMap;
layout(rg32f, 	binding = 2, location = 2) uniform image2D velocityMapTemp;
layout(rg32f, 	binding = 3, location = 3) uniform image2D velocityMap;
layout(rgba32f, binding = 4, location = 4) uniform image2D image;
layout(						 location = 5) uniform int operation;

layout(						location = 10) uniform float delta;
layout(						location = 11) uniform ivec2 mousePosition;
layout(						location = 13) uniform int mousePressed;
layout(						location = 14) uniform float touchRadius;
layout(						location = 15) uniform float touchForce;

float nextDensity(ivec2 xy, float k)
{
	float currentDensity = imageLoad(densityMap, xy).x;
	float nextAverage = 1;
	float newDensity = (currentDensity + k * nextAverage) / (1 + k);
	
	return newDensity;
}

float nextDensity2(ivec2 xy, float k)
{	
	float density = imageLoad(densityMap, xy + ivec2(-1, 0)).x;
	float average = (
		imageLoad(densityMap, xy + ivec2(-1, 0)) + imageLoad(densityMap, xy + ivec2(1, 0)) +
		imageLoad(densityMap, xy + ivec2(0, -1)) + imageLoad(densityMap, xy + ivec2(0, 1))).x / 4;
	
	return mix(density, average, k);
}

void main()
{
	ivec2 id = ivec2(gl_GlobalInvocationID.xy);
	if (operation == 1)
	{
		float density = nextDensity2(id, 10 * delta);
		if (mousePressed == 1)
		{
			float len = distance(id, mousePosition) / touchRadius;
			if (len < 1)
			{
				density += smoothstep(1, 0, len) * touchForce * delta;
			}
		}
		
		imageStore(densityMapTemp, id, vec4(density, 0, 0, 1));
	}
	else if (operation == 2)
	{
		float density = imageLoad(densityMapTemp, id).x;
		vec2 velocity = imageLoad(velocityMapTemp, id).xy;
		imageStore(densityMap, id, vec4(density, 0, 0, 1));
		imageStore(velocityMap, id, vec4(velocity, 0, 1));
		imageStore(image, id, vec4(vec3(density), 1));
	}
}