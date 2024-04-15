#version 330 core

in vec3 vertices;
in vec2 textures;

out vec2 tex_coords;
out vec2 pos;

uniform float Xoff;
uniform float Yoff;
uniform mat4 transmat;

uniform vec4 textMat;
uniform vec2 modelMat;
uniform vec2 selMat;
uniform mat4 camMat;
uniform bool selL;

uniform vec4[4] cplanes;

void main() {
	tex_coords = textures*textMat.zw+textMat.xy;
	gl_Position = vec4(vertices.xy*(selL ? selMat : modelMat), 0, 1);
	gl_Position.x += Xoff;
	gl_Position.y -= Yoff;
	vec4 worldposition = transmat * gl_Position;
	gl_Position = camMat * worldposition;
	gl_ClipDistance[0] = dot(cplanes[0].xyz, worldposition.xyz)+cplanes[0].w;
	gl_ClipDistance[1] = dot(cplanes[1].xyz, worldposition.xyz)+cplanes[1].w;
	gl_ClipDistance[2] = dot(cplanes[2].xyz, worldposition.xyz)+cplanes[2].w;
	gl_ClipDistance[3] = dot(cplanes[3].xyz, worldposition.xyz)+cplanes[3].w;
	pos = gl_Position.xy;
}
