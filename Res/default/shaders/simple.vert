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
out vec4 clipspace;

uniform vec4[4] cplanes;

void main() {
	mat4 m = transformationMat;
	if(a[3][3] != 0)
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
	gl_ClipDistance[0] = dot(cplanes[0].xyz, worldposition.xyz)+cplanes[0].w;
	gl_ClipDistance[1] = dot(cplanes[1].xyz, worldposition.xyz)+cplanes[1].w;
	gl_ClipDistance[2] = dot(cplanes[2].xyz, worldposition.xyz)+cplanes[2].w;
	gl_ClipDistance[3] = dot(cplanes[3].xyz, worldposition.xyz)+cplanes[3].w;
	gl_Position = projectionMat * viewMat * worldposition;
	clipspace = gl_Position;
	tex_coords = textures;
}
