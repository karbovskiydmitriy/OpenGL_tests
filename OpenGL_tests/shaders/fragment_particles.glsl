#version 430 core

in vec4 var_color;

void main(void)
{
    float alpha = smoothstep(1, 0, 2 * length(gl_PointCoord - 0.5));
    gl_FragColor = vec4(var_color.rgb, alpha);
}