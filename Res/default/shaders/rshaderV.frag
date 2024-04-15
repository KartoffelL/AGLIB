#version 430 core

struct material {
	vec4 color;
	float emissive;
	float roughness;
	float metallic;
	float refraction;
};

struct ray {
	vec3 origin;
};

struct hitInfo {
	int material;
	int level;
	float size;
	vec3 octant;
	vec3 normal;
	vec3 pos;
};

layout (std430, binding = 0) buffer grid
{
    float[] octree;
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

struct DirLight {
    vec3 direction;
    vec3 color;
    vec3 ambient;
};
struct PointLight {
    vec3 position;
    vec3 color;
};
struct SpotLight {
    vec3 position;
    vec3 direction;
    vec3 color;
    float cutoff;
    float edge;
};

#define NR_POINT_LIGHTS 128
uniform PointLight PointedLights[NR_POINT_LIGHTS];
uniform SpotLight SpotLights[NR_POINT_LIGHTS];
uniform int slAm = 0;
uniform int plAm = 0;
uniform float lightClip = .2;

uniform DirLight DirectionalLight;


uniform material[256] materials;
uniform int MAX_DEPTH = 256;
uniform int MAX_TESTS = 512;
uniform int MAX_BOUNCES = 64;
uniform float bias = 0.00001; //0.000008 theoretical sweetspot,
uniform vec4 offset;

void isHit(vec3 pos, in out hitInfo info) {
	if(abs(pos.x-.5) > .5 || abs(pos.y-.5) > .5 || abs(pos.z-.5) > .5)
		return;
	int current = 0; //Pointer to current octant
	for(int i = 0; i < MAX_DEPTH; i++) {
		float val = octree[current+0]; //Current value of octant
		float l = 1.0/pow(2, i); //Calculate scale of octant (1, 0.5, 0.25, ...)
		
		if(val <= 0) {//Octant is solid, no childs exist
			info.material = int(abs(val));
			info.level = i;
			info.octant = vec3(mod(pos.x, l) < l*.51 ? 0.2 : 0, mod(pos.y, l) < l*.5 ? 0.2 : 0, mod(pos.z, l) < l*.5 ? 0.2 : 0);
			info.size = l;
			return;
		}
		
		int index = 0;
		index += mod(pos.x, l) < l*.5 ? 1 : 0; //Set index based on position
		index += mod(pos.y, l) < l*.5 ? 2 : 0;
		index += mod(pos.z, l) < l*.5 ? 4 : 0;
		current = int(octree[current+1+index]*9); //set current to correct child-octant
		
		if(current == 0) { //Traversed to smallest LOD
			info.material = int(abs(val));
			info.level = i;
			info.octant = vec3(mod(pos.x, l) < l*.5 ? 1 : 0, mod(pos.y, l) < l*.5 ? 1 : 0, mod(pos.z, l) < l*.5 ? 1 : 0);
			info.size = l;
			return;
		}
	}
	return;
}

float bb(vec3 pos, vec3 dir, float size) {
    vec3 invDir = 1.0 / dir;
    vec3 tbot = -pos * invDir;
    vec3 ttop = (size - pos) * invDir;
    vec3 tmax = max(ttop, tbot);
    float t1 = min(min(tmax.x, tmax.y), tmax.z);
    
    return max(t1, 0.0);
}

vec2 bf(vec3 pos, vec3 dir, float size) {
	vec3 invDir = 1.0 / dir;
    vec3 tbot = -pos * invDir;
    vec3 ttop = (size - pos) * invDir;
    
    vec3 tmin = min(ttop, tbot);
    vec3 tmax = max(ttop, tbot);

    float t0 = max(max(tmin.x, tmin.y), tmin.z);
    float t1 = min(min(tmax.x, tmax.y), tmax.z);
    return vec2(max(t0, 0), t1);
}

float calcDistance(vec3 pos, vec3 dir, float size) {
	return bb(mod(pos, size), dir, size)+bias;
}

vec3 calcNorm(vec3 n, float scale) {
	vec3 dir = n*2-1;
	float mm = max(abs(dir.x), max(abs(dir.y), abs(dir.z)));
	if(abs(dir.x) == mm)
		return vec3(sign(dir.x), 0, 0);
	if(abs(dir.y) == mm)
		return vec3(0, sign(dir.y), 0);
	return vec3(0, 0, sign(dir.z));
//	return pow(dir, vec3(25));
}

void shootRay(vec3 p, vec3 d, in out hitInfo hit, float bias2, int ignoreMaterial) {
	vec3 pos = p+d*bias2;
	vec3 dir = d;
	for(int i = 0; i < MAX_TESTS; i++){
		isHit(pos, hit);
		bool a = hit.material != ignoreMaterial;
		bool b = max(max(pos.x, pos.y), pos.z) > 1 || min(min(pos.x, pos.y), pos.z) < 0;
		if(a || b) {//Hit!
			if(b)
				break;
			hit.normal = calcNorm(mod(pos/hit.size, 1), hit.size);
			hit.pos = pos;
			return;
		}
		pos += dir*calcDistance(pos, dir, hit.size);
	}
	hit.material = -1;
}

void main() {
	vec3 vPL = vec3(tex_coords*2, -1);
	vec4 vp = (pmi * vec4(vPL, 1));
	vp /= vp.w;
	vp = vmi * vp;
	
	//Raytracing
	vec3 dir = normalize(vp.xyz-cp);
	vec3 pos = vec3(cp+offset.xyz)*offset.w;
	
	{ //Position correction
		vec2 init = bf(pos, dir, 1);
		if(init.y < init.x)
			discard;
		pos += dir*(init.x+bias);
	}
	
	hitInfo hit = hitInfo(0, 0, 0, vec3(0), vec3(0), vec3(0));
	shootRay(pos, dir, hit, bias*2, 0);
	if(hit.material == -1)
		discard;
	//Evaluation
	material mat = materials[hit.material];
	FragColor.rgb = mat.color.rgb;
	FragColor.a = 1;
	//Depth
	vec4 dp = pm * vm * vec4(pos, 1);
	dp.z /= dp.w;
	float depth = dp.z*.5+.5;
	gl_FragDepth = depth;
	//Buffers
	FragPos = vec4(pos, 1);
	FragNormal = vec4(hit.normal, 1);
	FragMaterial = vec4(mat.metallic, mat.roughness, mat.emissive, 1);
}
