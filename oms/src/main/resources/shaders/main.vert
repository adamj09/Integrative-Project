#version 410 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 texCoords;
layout(location = 3) in vec3 color;
layout(location = 4) in mat4 model;

layout(std140) uniform CameraMatrices {
  mat4 projection;
  mat4 view;
  mat4 inverse_view;
}
camera_matrices;

out vec3 out_color;
out vec3 out_normal;
out vec3 out_fragment_position;

void main() {
  out_color = color;
  out_normal = normal;
  out_fragment_position = vec3(model * vec4(position, 1.0));

  gl_Position = camera_matrices.projection * camera_matrices.view * model * vec4(position, 1.0);
}