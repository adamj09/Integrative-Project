#version 410 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec3 color;
layout(location = 3) in mat4 model;

layout(std140) uniform CameraMatrices {
  mat4 projection;
  mat4 view;
}
cameraMatrices;

out vec3 outColor;
out vec3 outNormal;
out vec3 outFragmentPosition;

void main() {
  outColor = color;
  outNormal = normal;
  outFragmentPosition = vec3(model * vec4(position, 1.0));

  gl_Position = cameraMatrices.projection * cameraMatrices.view * model * vec4(position, 1.0);
}