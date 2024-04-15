#version 330 core

layout (location = 0) in vec3 vertices;
layout (location = 1) in vec2 textures;
layout (location = 2) in vec3 normals;
layout (location = 3) in float material;

layout (location = 4) in vec4 a_bones;
layout (location = 5) in vec4 a_weights;


layout (location = 6) in mat4 a;

uniform mat4 viewMat;
uniform mat4 projectionMat;
uniform mat4 transformationMat;

uniform mat4 anim_0[124];
uniform mat4 anim_1[124];
uniform vec3 anim_trans;
uniform float anim_time = -1;

out vec2 tex_coords;
out vec3 normalR;

mat3 normalize(mat3 mat) {
	mat[0] = normalize(mat[0]);
	mat[1] = normalize(mat[1]);
	mat[2] = normalize(mat[2]);
	return mat;
}

void main() {
	mat4 m = transformationMat;
	if(a[0][0] != 0)
		m *= a;
	
	if(anim_time != -1) {
		mat4 sk = anim_0[int(a_bones.x)]*a_weights.x;
		sk += anim_0[int(a_bones.y)]*a_weights.y;
		sk += anim_0[int(a_bones.z)]*a_weights.z;
		sk += anim_0[int(a_bones.w)]*a_weights.w;
		mat4 sk2 = anim_1[int(a_bones.x)]*a_weights.x;
		sk2 += anim_1[int(a_bones.y)]*a_weights.y;
		sk2 += anim_1[int(a_bones.z)]*a_weights.z;
		sk2 += anim_1[int(a_bones.w)]*a_weights.w;
		//Lineary interpolating between the two frames.
		m *= (sk2*anim_time+sk*(1-anim_time));
	}
	
	vec4 worldposition = m * vec4(vertices, 1);
	gl_Position = projectionMat * viewMat * worldposition;
	tex_coords = textures;
	
	mat3 ti = normalize(mat3(m)); //instead of doing that, we just normalize the upper 3x3 matrix (responsible for rotation scaling and shearing) and normalize it. (Dont know what happens when shearing)
	normalR = normalize(mat3(viewMat)*ti*normals);
}
