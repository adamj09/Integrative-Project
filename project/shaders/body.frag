#version 410 core

uniform vec3 lightColor;
uniform vec3 lightPosition;
uniform vec3 viewPosition;

in vec3 outColor;
in vec3 outNormal;
in vec3 outFragmentPosition;

out vec4 fragColor;

void main() {
  // Lighting variables applied universally.
  vec3 normal = normalize(outNormal);
  vec3 lightDirection = normalize(lightPosition);

  // Ambient Lighting
  float ambientStrength = 0.01;
  vec3 ambient = ambientStrength * lightColor;

  // Diffuse Lighting
  // TODO: add normal matrix to properly calculate lighting in cases where model
  // scaling is not uniform.
  float diffuseIntensity = max(dot(normal, lightDirection), 0.0);
  vec3 diffuse = diffuseIntensity * lightColor;

  // Specular Lighting
  float specularStrength = 0.5;
  vec3 viewDirection = normalize(viewPosition - outFragmentPosition);
  vec3 reflectDirection = reflect(-lightDirection, normal);
  float specularIntensity = pow(max(dot(viewDirection, reflectDirection), 0.0), 8);
  vec3 specular = specularStrength * specularIntensity * lightColor;

  vec3 result = (ambient + diffuse + specular) * outColor;

  fragColor = vec4(result, 1.0);
}