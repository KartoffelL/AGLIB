#version 330 core

layout (location = 0) in vec3 vertices;
layout (location = 1) in vec2 textures;
layout (location = 2) in vec3 normals;
layout (location = 3) in float material;

layout (location = 4) in vec4 a_bones;
layout (location = 5) in vec4 a_weights;

layout (location = 6) in mat4 a;

out Data {
	vec2 tex_coords;
	flat float mat;
	vec3 cam_vec;
	vec3 fragposition;
	vec3 cam_pos;

	vec3 normalR;
	mat3 tbn;
	vec4 debug;
};


uniform mat4 viewMat;
uniform mat4 projectionMat;
uniform mat4 viewMatInv;
uniform mat4 transformationMat;
uniform mat4 transformationMatInv;

//Max Bones
uniform mat4 anim_0[124];
uniform mat4 anim_1[124];
uniform vec3 anim_trans;
uniform float anim_time = -1;

mat3 normalize(mat3 mat) {
	mat[0] = normalize(mat[0]);
	mat[1] = normalize(mat[1]);
	mat[2] = normalize(mat[2]);
	return mat;
}

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
	tex_coords = textures;
	mat = material;
	cam_pos = (viewMatInv * vec4(0.0, 0.0, 0.0, 1.0)).xyz;
	fragposition = worldposition.xyz;

	//Annoying Normal calculations
//	mat3 ti = transpose(inverse(mat3(m)));
	mat3 ti = normalize(mat3(m)); //instead of doing that, we just normalize the upper 3x3 matrix (responsible for rotation scaling and shearing) and normalize it. (Dont know what happens when shearing)
	normalR = normalize(ti*normals);
	//Even more annoying tbn calculations
	cam_vec = normalize(cam_pos - worldposition.xyz);
	
}
