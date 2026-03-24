#version 410 core

uniform vec3 light_color;
uniform vec3 light_position;
uniform vec2 resolution;

layout(std140) uniform CameraMatrices {
  mat4 projection;
  mat4 view;
  mat4 inverse_view;
}
camera_matrices;

in vec3 out_color;
in vec3 out_normal;
in vec3 out_fragment_position;
flat in int out_instance_ID;

out vec4 fragColor;

vec4 create_ellipse(vec2 position, vec4 color, float radius_x, float radius_y) {
    float ellipse = sqrt(pow(radius_x, 2.0) - pow(radius_y, 2.0));
    ellipse = smoothstep(size, size + 0.003, 1.0 - ellipse);

    return color * ellipse;
}

void main() {
  vec3 result;

  // --- Orbital Path ---

  vec2 norm_coordinates = vec2(gl_FragCoord / resolution);
  float aspect = resolution.x / resolution.y;
  vec2 point = 


  // --- Lighting ---
  vec3 view_position = vec3(camera_matrices.inverse_view[3][0], camera_matrices.inverse_view[3][1], camera_matrices.inverse_view[3][2]);
  vec3 normal = normalize(out_normal);

  // Light direction is always the light's position (directional lighting) to
  // simulate an infinitely distant light source (ex: Sun)
  vec3 light_direction = normalize(light_position);

  // Ambient Lighting
  float ambient_strength = 0.01;
  vec3 ambient = ambient_strength * light_color;

  // Diffuse Lighting
  // Note: A normal matrix is not used because non-uniform scaling will not be
  // used to render objects. If non-uniform scaling IS used, the normals will
  // become incorrect without implementing a normal matrix to transform them.
  float diffuse_intensity = max(dot(normal, light_direction), 0.0);
  vec3 diffuse = diffuse_intensity * light_color;

  // Specular Lighting
  float specular_strength = 0.5;
  vec3 view_direction = normalize(view_position - out_fragment_position);
  vec3 reflect_direction = reflect(-light_direction, normal);
  float specular_intensity =
      pow(max(dot(view_direction, reflect_direction), 0.0), 8);
  vec3 specular = specular_strength * specular_intensity * light_color;

  result = (ambient + diffuse + specular) * out_color;

  fragColor = vec4(result, 1.0);
}