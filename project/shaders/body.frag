#version 410 core

in vec3 outColor;
out vec4 fragColor;

uniform vec3 lightColor;

void main() { 
    float ambientStrength = 0.1;
    vec3 ambient = ambientStrength * lightColor;
    vec3 result = ambient * outColor;

    fragColor = vec4(result, 1.0); 
}