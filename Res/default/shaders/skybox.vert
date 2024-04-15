#version 330 core

in vec3 vertices;
in vec2 textures;

uniform mat4 projectionMat;
uniform mat4 viewMat;
uniform mat4 transformationMat;

out vec3 tex_coords;

void main() {
	vec4 worldposition = transformationMat * vec4(vertices, 1);
	vec4 pos = projectionMat * viewMat * worldposition;
	gl_Position = vec4(pos.x, pos.y, pos.w, pos.w+0.0000001);
	tex_coords = vertices;
}
