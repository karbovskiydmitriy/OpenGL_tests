#version 430 core

in vec4 pos;
in vec4 color;

layout (location = 0) uniform float size;

out vec4 var_color;

void main(void)
{
    gl_PointSize = size;
    gl_Position = vec4(pos.xy, 0.0, 1.0);
    var_color = color;
}