#version 330 core

in vec3 vertices;
in vec2 textures;

uniform mat4 shadowvie;
uniform mat4 vmI;

out vec2 tex_coords;
out vec3 shadowVec;
out vec3 cp;

void main() {
	tex_coords = vec2(textures.x, textures.y);
	gl_Position =  vec4(vec3(vertices.x, vertices.y, 0.2), 1);
	shadowVec = vec3(1)*inverse(mat3(shadowvie));
	cp = (vmI * vec4(0.0, 0.0, 0.0, 1.0)).xyz;
}
