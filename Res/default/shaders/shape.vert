#version 330 core

layout (location = 0) in vec3 vertices;
layout (location = 1) in vec2 textures;
layout (location = 2) in vec3 normals;


layout (location = 6) in mat4 a;

uniform mat4 viewMat;
uniform mat4 viewMatInv;
uniform mat4 projectionMat;
uniform mat4 transformationMat;

out vec3 fragposition;
out vec3 cam_pos;

out vec2 tex_coords;
out vec4 clipspace;

out vec4 ndc;

uniform vec4[4] cplanes;

void main() {
	mat4 m = transformationMat;
	
	vec4 worldposition = m * vec4(vertices, 1);
	gl_ClipDistance[0] = dot(cplanes[0].xyz, worldposition.xyz)+cplanes[0].w;
	gl_ClipDistance[1] = dot(cplanes[1].xyz, worldposition.xyz)+cplanes[1].w;
	gl_ClipDistance[2] = dot(cplanes[2].xyz, worldposition.xyz)+cplanes[2].w;
	gl_ClipDistance[3] = dot(cplanes[3].xyz, worldposition.xyz)+cplanes[3].w;
	
	fragposition = worldposition.xyz;
	cam_pos = (viewMatInv * vec4(0.0, 0.0, 0.0, 1.0)).xyz;
	
	gl_Position = projectionMat * viewMat * worldposition;
	ndc = gl_Position;
	clipspace = gl_Position;
	tex_coords = textures;
}
