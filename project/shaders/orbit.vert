#version 410 core

layout(location = 0) in vec3 position;

layout(std140) uniform CameraMatrices {
  mat4 projection;
  mat4 view;
  mat4 inverse_view;
}
camera_matrices;

void main() {
  gl_Position = vec4(position, 1.0);
}