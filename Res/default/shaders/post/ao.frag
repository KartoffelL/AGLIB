#version 330 core

uniform sampler2D sampler0; //Position
uniform sampler2D sampler1; //Normal
uniform sampler2D sampler2; //Depth
uniform sampler2D sampler3; //Color

uniform float radius = 0.2;
uniform int SampleKernelSize = 16;
uniform float power = 1;
uniform float smoothf = 4;
uniform float clampV = 0.8;
uniform vec3 random = vec3(20, 0, 0);



uniform mat4 ProViewMatrix;
uniform vec2 clip;

in vec2 tex_coords;

const float pi = 3.141592653;

float linearize_depth(float d)
{
    return clip.x * clip.y / (clip.y + d * (clip.x - clip.y));
}
float rand(vec3 co){
    return fract(sin(dot(co, vec3(12.9898, 78.233, -2.544))) * 43758.5453);
}
vec3 randv(vec3 seed) {
	return vec3( rand(seed), rand(seed+1), rand(seed+2))*2-1;
}

void main() {
	vec3 origin = texture(sampler0, tex_coords).xyz;
	vec3 normal = texture(sampler1, tex_coords).xyz;
	if(normal.x == 0 && normal.y == 0 && normal.z == 0) {
		gl_FragColor = vec4(1, 1, 1, 1);
		return;
	}
//	vec3 color = texture(sampler3, tex_coords).xyz;
	float depth = texture(sampler2, tex_coords).z;
	
	float occlusion = 0.0;
	vec3 c = vec3(0);
	
	for (int i = 0; i < SampleKernelSize; ++i) {
		// get sample position
		   vec3 sample = randv(vec3(i)+random);
		   sample *= dot(sample, normal);
		   sample = sample*sample*sample;
		   sample = sample * radius + origin;

		   // project sample position:
		   vec4 offset = ProViewMatrix * vec4(sample, 1.0); //(offset is now clipspace/pixel position)
		   offset.xyz /= offset.w;
		   offset.xy =  offset.xy*0.5+0.5;
		   // get sample depth:
		   float sampleDepth = texture(sampler2, offset.xy).z;
		   // range check & accumulate:
		   float dst = radius / abs(linearize_depth(sampleDepth)-linearize_depth(depth));
		   float rangeCheck = smoothstep(0, smoothf, dst);
		   float d = max(dot( normal, texture(sampler1, offset.xy).rgb), 0);
		   rangeCheck *= (1-pow(d, 1));
		   rangeCheck *= sampleDepth-depth < 0.0005 ? 1 : 0;
		   occlusion += rangeCheck;
//		   c += texture(sampler3, offset.xy).rgb;
	}
	occlusion = 1-(occlusion / SampleKernelSize);
	occlusion = clamp(occlusion, 0, clampV)/clampV;
	occlusion = pow(occlusion, power);
//	c /= SampleKernelSize;
//	c = clamp(c, 0, 1);
//	gl_FragColor = vec4(mix(c, vec3(1), occlusion), 1);
	gl_FragColor = vec4(vec3(occlusion), 1);
}
