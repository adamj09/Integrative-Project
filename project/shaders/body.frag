#version 410 core

uniform vec3 lightColor;
uniform vec3 lightPosition;
uniform vec3 viewPosition;

in vec3 outColor;
in vec3 outNormal;
in vec3 outFragmentPosition;

out vec4 fragColor;

void main() {
  vec3 normal = normalize(outNormal);

  // Light direction is always the light's position (directional lighting) to
  // simulate an infinitely distant light source (ex: Sun)
  vec3 lightDirection = normalize(lightPosition);

  // Ambient Lighting
  float ambientStrength = 0.01;
  vec3 ambient = ambientStrength * lightColor;

  // Diffuse Lighting
  // Note: A normal matrix is not used because non-uniform scaling will not be
  // used to render objects. If non-uniform scaling IS used, the normals will
  // become incorrect without implementing a normal matrix to transform them.
  float diffuseIntensity = max(dot(normal, lightDirection), 0.0);
  vec3 diffuse = diffuseIntensity * lightColor;

  // Specular Lighting
  float specularStrength = 0.5;
  vec3 viewDirection = normalize(viewPosition - outFragmentPosition);
  vec3 reflectDirection = reflect(-lightDirection, normal);
  float specularIntensity =
      pow(max(dot(viewDirection, reflectDirection), 0.0), 8);
  vec3 specular = specularStrength * specularIntensity * lightColor;

  vec3 result = (ambient + diffuse + specular) * outColor;

  fragColor = vec4(result, 1.0);
}