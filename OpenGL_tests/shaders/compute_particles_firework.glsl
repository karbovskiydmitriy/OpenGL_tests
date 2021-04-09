#version 430 core

#define PI 3.141592653589793238462643383279
#define COUNT 1024

layout(local_size_x = 32) in;

struct Particle
{
	vec2 position;
	vec2 velocity;
	vec4 color;
};

layout (std430, binding = 0) buffer entities
{
	Particle particles[];
};

layout(rgba32f, binding = 1) uniform image2D prevImage;
layout(rgba32f, binding = 2) uniform image2D image;

layout(location = 0) uniform float delta;

ivec2 size;

float random(vec2 st)
{
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

void draw(Particle p)
{
	imageStore(image, ivec2((p.position * size)), p.color);
}

void main(void)
{
	int id = int(gl_GlobalInvocationID.x);
	size = imageSize(image);
	
	if (id < COUNT)
	{
		particles[id].position += particles[id].velocity * delta;
		if (particles[id].position.x < 0)
		{
			particles[id].position.x = -particles[id].position.x;
			particles[id].velocity.x = -particles[id].velocity.x;
		}
		if (particles[id].position.y < 0)
		{
			particles[id].position.y = -particles[id].position.y;
			particles[id].velocity.y = -particles[id].velocity.y;
		}
		if (particles[id].position.x > 1.0)
		{
			particles[id].position.x = 1.0 - (particles[id].position.x - 1.0);
			particles[id].velocity.x = -particles[id].velocity.x;
		}
		if (particles[id].position.y > 1.0)
		{
			particles[id].position.y = 1.0 - (particles[id].position.y - 1.0);
			particles[id].velocity.y = -particles[id].velocity.y;
		}
	}
	draw(particles[id]);
}