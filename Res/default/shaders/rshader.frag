#version 330 core

struct material {
	vec3 color;
	vec3 e_color;
	float roughness;
	float alpha;
	float refraction;
};

struct ray {
	vec3 origin;
	vec3 direction;
};

struct hitInfo {
	vec3 pos;
	vec3 normal;
	material mat;
	float dst;
	int hit;
};

struct sphere {
	vec3 pos;
	float radius;
	int mat;
};
struct box {
	vec3 pos;
	vec3 size;
	int mat;
};


in vec2 tex_coords;

in vec3 cp;

in mat4 vmi;
in mat4 pmi;
in mat4 pm;
in mat4 vm;


layout (location = 0) out vec4 FragColor;
layout (location = 1) out vec4 FragPos;
layout (location = 2) out vec4 FragNormal;
layout (location = 3) out vec4 FragMaterial;

//Shapes
//layout(std140) uniform objects {
	uniform sphere spheres[256];
	uniform box boxes[256];
	uniform material materials[256];
//};
uniform int amount_spheres;
uniform int amount_boxes;

uniform vec2 random;

uniform bool useSampler;
uniform sampler2D sampler0;


uniform int MAX_BOUNCES;
uniform int SAMPLES;
uniform float WEIGHT;

uniform vec2 clip;

uniform vec3 ambient;

const float bias = 0.0001;
const float pi = 3.141592653;

//Misc------------------------------
vec3 rand(vec3 normal, vec2 seed) {
	return normalize(texture2D(sampler0, seed*vec2(seed.y+46, 416)).rgb*2-1);
}
float step(float a, float b) {
	if(a-bias <= -b) return -1;
	if(a+bias >= b) return 1;
	return 0;
}
vec3 step(vec3 a, vec3 size) {
	return vec3(step(a.x, size.x), step(a.y, size.y), step(a.z, size.z));
}
//INTERSECTION TEST###########################
void intersection_sphere(ray r, sphere s, in out hitInfo hi) {
	vec3 off = r.origin - s.pos;
	float a = dot(r.direction, r.direction);
	float b = 2 * dot(off, r.direction);
	float c = dot(off, off) - s.radius * s.radius;
	float discr = b * b - 4 * a * c;
	if(discr >= 0) {
		float dst = (-b - sqrt(discr)) / (2*a);
		if(dst > clip.x && dst < hi.dst) {
			hi.pos = r.origin+r.direction*dst;
			hi.normal = normalize(hi.pos-s.pos);
			hi.dst = dst;
			hi.hit++;
			hi.mat = materials[s.mat];
		}
		else {	//Backface
			dst = (-b + sqrt(discr)) / (2*a);
			if(dst > clip.x && dst < hi.dst) {
				hi.pos = r.origin+r.direction*dst;
				hi.normal = -normalize(hi.pos-s.pos);
				hi.dst = dst;
				hi.hit = 1;
				hi.mat = materials[s.mat];
			}
		}
	}
}
void intersection_box(ray r, box b, in out hitInfo hi) {
	vec3 ird = 1/r.direction;
	vec3 n = ird*(r.origin-b.pos);
	vec3 k = abs(ird)*(b.size);
	vec3 t1 = -n - k;
	vec3 t2 = -n + k;
	float tN = max(max(t1.x, t1.y), t1.z);
	float tF = min(min(t2.x, t2.y), t2.z);
	if(tN > tF || tF < 0) return;
	if(tN < 0) return; //Backface
	if(tN > clip.x && tN < hi.dst) {
		hi.mat = materials[b.mat];
		hi.dst = tN;
		hi.hit++;
		hi.pos = r.origin+r.direction*tN;
		hi.normal = normalize(step(hi.pos-b.pos, b.size));
	}
}

//COLOR
vec3 trace(ray r, vec3 ambient, vec2 idex, in out vec3 normal, in out vec3 position, in out float distance, in out vec4 prop) {
	vec3 inl = vec3(0);
	vec3 rayC = vec3(1);
	for(int i = 0; i < MAX_BOUNCES; i++) {
		//Intersection testing
		hitInfo hi = hitInfo(vec3(0), vec3(0), material(vec3(0), vec3(0), 0, 0, 0), clip.y, 0);
		for(int i = 0; i < amount_spheres; i++) {
			intersection_sphere(r, spheres[i], hi);
		}
		for(int i = 0; i < amount_boxes; i++) {
			intersection_box(r, boxes[i], hi);
		}
		if(hi.hit == 0)
			break;
		//Evaluating next Ray
		if(SAMPLES != -1) {
			//With roughness
			if(hi.mat.alpha > 0.5) //mix(hi.normal, rand(hi.normal, idex), hi.mat.roughness)
				r.direction = mix(reflect(r.direction, hi.normal), rand(hi.normal, idex), hi.mat.roughness);
			else
				r.direction = refract(normalize(r.direction), mix(hi.normal, rand(hi.normal, idex), hi.mat.roughness), hi.mat.refraction);
		}
		else {
			//Without roughness
			if(hi.mat.roughness >= 0.5) {
				i = MAX_BOUNCES;
			}
			else {
				if(hi.mat.alpha > 0.5)
					r.direction = reflect(r.direction, hi.normal);
				else
					r.direction = refract(r.direction, hi.normal, hi.mat.refraction);
			}
		}
		r.origin = hi.pos;
		//Evaluating Color
		inl += hi.mat.e_color * rayC;
		rayC *= hi.mat.color;
		if(position == vec3(0))
			position = hi.pos;
		if(normal == vec3(0))
			normal = hi.normal;
		if(distance == 0)
			distance = hi.dst;
		if(prop.w == 0)
			prop = vec4(1, hi.mat.roughness, (hi.mat.e_color.r+hi.mat.e_color.g+hi.mat.e_color.b)/3, 1);
	}
	inl += ambient * rayC;
	return inl;
}
void main() {
	vec3 vPL = vec3(tex_coords*2, -1);
	vec4 vp = (pmi * vec4(vPL, 1));
	vp /= vp.w;
	vp = vmi * vp;
	ray r = ray(cp, normalize(vp.xyz-cp));

	//Raytracing
	vec3 hi = vec3(0);
	float distance = 0;
	vec3 pos = vec3(0);
	vec3 normal = vec3(0);
	vec4 prop = vec4(0);
	for(float i = 0; i < abs(SAMPLES);) {
		vec2 idex = random+tex_coords+vec2(i, i+i);
		hi += trace(r, ambient, idex, normal, pos, distance, prop);
		i += 1/distance;
	}
	hi /= abs(1.0*SAMPLES);
	//Buffers
	FragColor.rgb = hi.rgb;
	FragColor.a = 1.0/WEIGHT;
	//Depth
	vec4 dp = pm * vm * vec4(pos, 1);
	dp.z /= dp.w;
	float depth = dp.z*.5+.5;
	gl_FragDepth = depth;
	FragPos = vec4(pos, 1);
	FragNormal = vec4(normal, 1);
	FragMaterial = prop;
}
