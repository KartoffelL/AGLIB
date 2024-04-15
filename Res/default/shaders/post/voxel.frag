#version 330 core

uniform sampler3D sampler0;

uniform sampler3D sampler1;

in vec2 tex_coords;

const float two = 1.41421;

float sdBox( vec3 p, vec3 b )
{
  vec3 q = abs(p) - b;
  return length(max(q,0.0)) + min(max(q.x,max(q.y,q.z)),0.0);
}

float sampleDistance(float distance, in out vec3 color, in out vec3 normal, vec3 pos, vec3 o, float size) {
	vec3 off = o*size;
	if(abs(pos.x+off.x) > 1 || abs(pos.y+off.y) > 1 || abs(pos.z+off.z) > 1)
		return distance;
	vec4 col = texture3D(sampler0, (pos+off)*.5+.5);
	if(col.a != 0) {
		float dist = sdBox(mod(pos, vec3(size)), vec3(size));
		if(dist < distance) {
			normal = texture3D(sampler1, (pos+off)*.5+.5).rgb;
			color *= col.rgb;
			return dist;
		}
	}
	return distance;
}

void main() {
	vec3 color = vec3(1);
	vec3 normal = vec3(0, 0, 1);
	
	vec3 pos = vec3(tex_coords*2-1, -1);
	vec3 dir = normalize(vec3(0, 0, 1));
	float vs = 1.0/64*2;
	
	for(int i = 0; i < 64*two; i++) {
		vec4 vcol = texture3D(sampler0, pos*.5+.5);
		float dtn = sdBox(mod(pos, vs), vec3(vs));
		float distance = two;
		
		distance = sampleDistance(distance, color, normal, pos, vec3(1, 0, 0), vs);
		distance = sampleDistance(distance, color, normal, pos, vec3(-1, 0, 0), vs);
		distance = sampleDistance(distance, color, normal, pos, vec3(0, 1, 0), vs);
		distance = sampleDistance(distance, color, normal, pos, vec3(0, -1, 0), vs);
		distance = sampleDistance(distance, color, normal, pos, vec3(0, 0, 1), vs);
		distance = sampleDistance(distance, color, normal, pos, vec3(0, 0, -1), vs);
		
		if(distance < 0.0001) {
			dir = reflect(dir, vec3(0, -1, 0));
			break;
		}
		pos += dir*vs;
		if(abs(pos.x) > 1 || abs(pos.y) > 1 || abs(pos.z) > 1)
			break;
	}
	
	gl_FragColor.a = 1;
	gl_FragColor.rgb = color;
}
