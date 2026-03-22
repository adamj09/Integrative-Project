#version 410 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 color;
layout(location = 2) in mat4 model;

out vec3 outColor;

layout(std140) uniform CameraMatrices {
  mat4 projection;
  mat4 view;
}
cameraMatrices;

void main() {
  gl_Position = cameraMatrices.projection * cameraMatrices.view * model * vec4(position, 1.0);
  outColor = color;
}