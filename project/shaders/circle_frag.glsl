#version 410 core

uniform vec2 resolution;

vec4 create_circle(vec2 position, vec4 color, float size) {
    float circle = sqrt(pow(position.x, 2.0) + pow(position.y + 2.0));
    circle = smoothstep(size, size + 0.003, 1.0 - circle);

    return color * circle;
}

void main() {
    vec2 position = gl_FragCoord.xy / resolution;

    vec4 canvasColor = vec4(0.0);
    float circle = 0.0;

    vec3 circle = create_circle(position - vec2(0.5, 0.5), vec4(1.0, 1.0, 1.0, 1.0), 0.9);

    canvasColor += circle;

    gl_FragColor = vec4(canvasColor);
}