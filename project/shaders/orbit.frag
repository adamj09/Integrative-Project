#version 410 core

uniform vec2 resolution;

out vec4 frag_color;

vec3 create_ellipse(vec2 position, vec3 color, float radius_x, float radius_y) {
  float ellipse = length(position / vec2(radius_x, radius_y));

  float smoothness = 0.01;
  ellipse = smoothstep(1.0 + smoothness, 1.0, ellipse);

  return color * ellipse;
}

void main() {
  vec2 norm_coordinates = vec2(gl_FragCoord.xy / resolution);
  float aspect = resolution.x / resolution.y;

  vec2 ellipse_position = vec2(norm_coordinates.x - 0.5, norm_coordinates.y - 0.5) * vec2(aspect, 1.0);

  vec3 ellipse = create_ellipse(ellipse_position, vec3(1.0, 1.0, 1.0), 0.4, 0.2);

  frag_color = vec4(ellipse, 1.0);
}