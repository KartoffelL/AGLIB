#version 330 core

in vec3 vertices;
in vec2 textures;

out vec2 tex_coords;

uniform mat4 transformationMat;

void main() {
	tex_coords = textures;
	gl_Position = transformationMat*vec4(vertices, 1);
	
}
