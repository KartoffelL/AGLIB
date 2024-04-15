#version 430 core

in vec2 tex_coords;
in vec3 normalR;

writeonly /*layout (RGBA32F)*/ uniform image3D texture1;
writeonly /*layout (RGBA32F)*/ uniform image3D texture2;
writeonly /*layout (RGBA32F)*/ uniform image3D texture3;

uniform sampler2D sampler0;

uniform float power = 64;

void main() {
	imageStore(texture1, ivec3(gl_FragCoord.xy, pow(gl_FragCoord.z, power)*imageSize(texture1).z), vec4(texture2D(sampler0, tex_coords).rgb, 1));
	imageStore(texture2, ivec3(gl_FragCoord.xy, pow(gl_FragCoord.z, power)*imageSize(texture2).z), vec4(normalR, 1));
//	imageStore(texture1, ivec3(gl_FragCoord.xy, pow(gl_FragCoord.z, power)*imageSize(texture1).z), vec4(texture2D(sampler0, tex_coords).rgb, 1));
}