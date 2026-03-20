#version 410 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec4 color;
layout (location = 2) in mat4 model;

out vec4 outColor;

layout(std140) uniform Matrices {
    mat4 projection;
    mat4 view;
};

void main()
{
    gl_Position = projection * view * model * vec4(position, 1.0);
    outColor = color;
}