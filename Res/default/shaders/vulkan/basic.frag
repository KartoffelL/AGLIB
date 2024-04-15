#version 450

layout(binding = 0) uniform UniformBufferObject {
    mat4 mv;
    vec4 cp;
    
    float[128][128][128] voxels;
} ubo;


layout(location = 0) in vec2 pos;
layout(location = 0) out vec4 outColor;

const int MAX_STEPS = 600;

vec3 calcRMD() {
	vec3 r = vec3(pos, 1);
	r.y = -r.y;
	r = inverse(mat3(ubo.mv)) * r;
	return normalize(r);
}

void main() {
	vec3 ray_dir = calcRMD()*.1;
	vec3 ray_pos = vec3(ubo.cp);	
	vec3 fcol = vec3(0, 0, 0);
	
	
	for(int step = 0; step < MAX_STEPS; step++) {
		
		if(ray_pos.y < -1) {
			fcol.r++;
			break;
		}
		if(ray_pos.x < -1 && ray_pos.x > -2 && ray_pos.z < -1 && ray_pos.z > -2) {
			fcol.b++;
			break;
		}
		
		ray_pos += ray_dir;
	}
	
	outColor = vec4(ray_pos, 1.0);
}