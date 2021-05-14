#version 430 core

#define PI 3.141592653589793238462643383279

layout(local_size_x = 4) in;

struct Ant
{
	vec2 position;
	float angle;
	float speed;
	vec4 color;
};

layout(std430, binding = 0) buffer entities
{
	Ant ants[];
};

layout(rgba32f, binding = 1) uniform image2D image;

layout(location = 0) uniform int operation;
layout(location = 1) uniform float aspect;
layout(location = 2) uniform float delta;
layout(location = 3) uniform int count;
layout(location = 4) uniform float moveSpeed;
layout(location = 5) uniform float sensorLength;
layout(location = 6) uniform int sensorSize;
layout(location = 7) uniform float turnSpeed;
layout(location = 8) uniform float sensorAngle;
layout(location = 9) uniform int stepsPerFrame;

ivec2 size;

float random(float f)
{
	return fract(sin(f) * 43758.5453123);
}

float random(vec2 st)
{
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

float normrandom(float f)
{
	return (1 + random(f)) / 2.0;
}

void init(int id)
{
	float angle = random(id + 0) * PI * 2;
	float rad = random(id + 1) * 0.22;
	ants[id].position = vec2(0.5 + cos(angle) * rad, (0.5 + sin(angle) * rad / aspect));
	ants[id].angle = normrandom(id + 2) * PI * 2;
	ants[id].speed = 1.0;
	ants[id].color = vec4(0.118, 0.235, 0.784, 1.0);
}

float sampleTrails(int id, float seekAngle)
{
	float trailSum = 0;
	
	float sampleAngle = ants[id].angle + seekAngle;
	ivec2 samplePosition = ivec2(ants[id].position * size + vec2(cos(sampleAngle), sin(sampleAngle)) * sensorLength);
	
	for (int i = -sensorSize; i <= sensorSize; i++)
	{
		for (int j = -sensorSize; j <= sensorSize; j++)
		{
			ivec2 xy = samplePosition + ivec2(i, j);
			xy = min(size - 1, max(xy, 0));
			trailSum += length(imageLoad(image, xy));
		}
	}
	
	return trailSum;
}

void move(int id)
{
	ants[id].position += vec2(cos(ants[id].angle), sin(ants[id].angle)) * moveSpeed / size * ants[id].speed * delta;
}

void turn(int id)
{
	float leftSensor = sampleTrails(id, sensorAngle);
	float forwardSensor = sampleTrails(id, 0);
	float rightSensor = sampleTrails(id, -sensorAngle);
	
	if (forwardSensor < leftSensor && forwardSensor < rightSensor)
	{
		float turn = turnSpeed * delta;
		if (leftSensor > rightSensor)
		{
			ants[id].angle += turn;
		}
		else
		{
			ants[id].angle -= turn;
		}
	}
	else
	{
		ants[id].angle += (normrandom(ants[id].position.x)) * turnSpeed * delta;
	}
}

void collide(int id)
{
	if (ants[id].position.x < 0)
	{
		ants[id].position.x = -ants[id].position.x;
		if (ants[id].angle > PI)
		{
			ants[id].angle = -(ants[id].angle - PI);
		}
		else
		{
			ants[id].angle = (PI - ants[id].angle);
		}
	}
	if (ants[id].position.y < 0)
	{
		ants[id].position.y = -ants[id].position.y;
		if (ants[id].angle > PI / 2)
		{
			ants[id].angle = PI + (PI - ants[id].angle);
		}
		else
		{
			ants[id].angle = -(ants[id].angle);
		}
	}
	if (ants[id].position.x > 1.0)
	{
		ants[id].position.x = 1.0 - (ants[id].position.x - 1.0);
		if (ants[id].angle > PI)
		{
			ants[id].angle = -(ants[id].angle - PI);
		}
		else
		{
			ants[id].angle = (PI - ants[id].angle);
		}
	}
	if (ants[id].position.y > 1.0)
	{
		ants[id].position.y = 1.0 - (ants[id].position.y - 1.0);
		if (ants[id].angle > PI + PI / 2)
		{
			ants[id].angle = PI - (ants[id].angle);
		}
		else
		{
			ants[id].angle = PI - (ants[id].angle - PI);
		}
	}
}

void draw(int id)
{
	Ant a = ants[id];
	imageStore(image, ivec2((a.position * size)), a.color);
}

void main(void)
{
	int id = int(gl_GlobalInvocationID.x);
	size = imageSize(image);
	
	if (id < count)
	{
		switch (operation) {
		case 1:
			init(id);
			break;
		case 2:
			if (stepsPerFrame > 0)
			{
				for (int i = 0; i < stepsPerFrame; i++)
				{
					move(id);
					turn(id);
					collide(id);
					draw(id);
				}
			}
			else
			{
				draw(id);
			}
			break;
		}
	}
}