#version 410 core

layout(location = 0) in vec3 position;
layout(location = 4) in mat4 model;

layout(std140) uniform CameraMatrices {
  mat4 projection;
  mat4 view;
  mat4 inverse_view;
}
camera_matrices;

void main() {
  gl_Position = camera_matrices.projection * camera_matrices.view * model * vec4(position, 1.0);
}