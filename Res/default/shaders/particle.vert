#version 330 core

layout (location = 0) in vec3 vertices;
layout (location = 1) in vec2 textures;
layout (location = 2) in vec3 normals;
layout (location = 3) in float material;

layout (location = 4) in int[] a_bones;
layout (location = 5) in float[] a_weightss;


layout (location = 6) in mat4 a;

out vec2 tex_coords;

uniform mat4 transformationMat;
uniform mat4 viewMat;
uniform mat4 projMat;

uniform vec3 settings;

void main() {
	mat4 m = transformationMat;
	if(a[3][3] != 0)
		m *= a;

	vec4(vertices, 1);
	mat4 mat = viewMat * m;
	// Column 0:
	if(settings.x == 0) {
	mat[0][0] = 1;
	mat[0][1] = 0;
	mat[0][2] = 0;
	}
	// Column 1:
	if(settings.y == 0) {
	mat[1][0] = 0;
	mat[1][1] = 1;
	mat[1][2] = 0;
	}
	// Column 2:
	if(settings.z == 0) {
	mat[2][0] = 0;
	mat[2][1] = 0;
	mat[2][2] = 1;
	}
	gl_Position = projMat * mat * vec4(vertices, 1);


	tex_coords = textures;
}
