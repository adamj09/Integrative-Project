#version 410 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec4 color;

out vec4 outColor;

layout(std140) uniform Matrices {
    mat4 projection;
    mat4 view;
};

uniform mat4 model;

void main()
{
    gl_Position = projection * view * model * vec4(position, 1.0);
    outColor = color;
}