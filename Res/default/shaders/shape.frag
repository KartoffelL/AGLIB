#version 330 core


in vec3 fragposition;
in vec3 cam_pos;

in vec2 tex_coords;

in vec4 ndc;

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

uniform DirLight DirectionalLight;

uniform sampler2D albedo;
uniform sampler2D depth;
uniform sampler2D shadow;

uniform vec3 size;

uniform mat4 shadowpv;
uniform vec3[4] shadowpos;
uniform float[4] shadowsiz;
uniform float shadowBias = 0.00001;

uniform int cascades = 1;

uniform mat4 transformationMatInv;
uniform mat4 pvm;

const float PI = 3.14159265359;

float disSq(vec3 a, vec3 b) {
	vec3 c = a-b;
	c *= c;
	return (c.x+c.y+c.z);
}

vec3 shadowC(vec4 fragPosLightSpace, float cas) {
	// perform perspective divide
    vec3 projCoords = fragPosLightSpace.xyz/fragPosLightSpace.w;
    // transform to [0,1] range
    projCoords = projCoords * 0.5 + 0.5;
    //Return if shadow is out of bounds
    if(projCoords.x > 1 || projCoords.x < 0 || projCoords.y > 1 || projCoords.y < 0 || projCoords.z > 1 || projCoords.z < 0)
    	 return vec3(1.0f, 0, 1);
    // get depth of current fragment from light's perspective
    float currentDepth = projCoords.z;
    //Cascade transform
    projCoords.xy = projCoords.xy*vec2(1.0/cascades, 1)+vec2(cas/cascades, 0);
    //Sample Depth texture
    vec4 a = texture(shadow, projCoords.xy);
    
    float closestDepth = a.r;
    if(closestDepth == 0)
    	return vec3(0.0f, 0, 1);

    return vec3((closestDepth+shadowBias-currentDepth), projCoords.xy);
}

float SDF(vec3 pos)
{
  vec3 p = (transformationMatInv*vec4(pos, 1)).xyz;
  vec3 b = vec3(size);
  vec3 q = abs(p) - b;
  return length(max(q,0.0)) + min(max(q.x,max(q.y,q.z)),0.0);
}

vec2 intersectAABB(vec3 pos, vec3 dir) {
	vec3 rayOrigin = (transformationMatInv*vec4(pos, 1)).xyz;
	vec3 rayTo = (transformationMatInv*vec4(pos+dir, 1)).xyz;
	vec3 rayDir = rayTo-rayOrigin;
	vec3 boxMin = vec3(-size);
	vec3 boxMax = -boxMin;
    vec3 tMin = (boxMin - rayOrigin) / rayDir;
    vec3 tMax = (boxMax - rayOrigin) / rayDir;
    vec3 t1 = min(tMin, tMax);
    vec3 t2 = max(tMin, tMax);
    float tNear = max(max(t1.x, t1.y), t1.z);
    float tFar = min(min(t2.x, t2.y), t2.z);
    return vec2(tNear, tFar);
};

uniform float time = 1;

uniform float stepSize1 = .001;
uniform int maxSamples1 = 512;
//const float maxSamples2 = 32;
//const float stepSize2 = .01;
uniform float stepBias = 0.01;
uniform float increase1 = 0.1;
uniform float increase2 = 0.1;
uniform int colorSampleRate = 4;
uniform float seedInfluence = 0.1;
uniform vec3 colorMul = vec3(1);
uniform vec3 color = vec3(0);
uniform float density = 0.001;
uniform bool infinite = true;
uniform bool effect = true;

const float LightClipBias = 0.00390625;  //1/256

float fogFunc(float x, float ab) {
	float beerslaw = 1-exp(-x * ab);
	return beerslaw;
}

//float calcVis(vec3 position, vec3 dir, float ab) {
//	vec3 pos = position;
//	float dt = 0;
//	for(int i = 0; i < maxSamples2; i++) {
//		float dst = SDF(pos);
//		float tt = dst;
//		if(dst > 0)
//			break;
//		dt += stepSize2;
//		pos += dir*stepSize2;
//		
//	}
//	return 1-fogFunc(dt, ab);
//	return 1;
//}

float invDisSq(vec3 a, vec3 b, float dst) {
	vec3 c = a-b;
	c *= c;
	return 1/(c.x+c.y+c.z+dst*dst);
}

vec3 cLAP(vec3 color, vec3 viewDir, vec3 pos, float absorption, float dist2Cam) {

	int cas = 0;
	for(cas = 0; cas < cascades; cas++)
		if(disSq(shadowpos[cas], pos) < shadowsiz[cas]*shadowsiz[cas])
			break;
	
	vec4 scc = shadowpv * vec4(pos-shadowpos[cas], 1);
	scc.xy /= shadowsiz[cas];
	vec3 smss = shadowC(scc, cas);
	
	vec3 Lo = vec3(0.0);
	
	//Directional Light---------------------------------
//	vec3 lightDir   = normalize(-DirectionalLight.direction);
//	float visib = calcVis(pos, lightDir, absorption);
	float distance   = 1;
	float attenuation = 1;
	if(smss.r >= 0)
		Lo += /*visib**/DirectionalLight.color;
	//Point lights---------------------------------
	for(int i = 0; i < plAm; i++) {
		attenuation = invDisSq(PointedLights[i].position, pos, dist2Cam);
//		if(attenuation > LightClipBias) {
//			lightDir   = normalize(PointedLights[i].position-pos);
//			visib = calcVis(pos, lightDir, absorption);
			Lo += /*visib**/PointedLights[i].color*attenuation;
//		}
	}
	//Spot lights---------------------------------
	for(int i = 0; i < slAm; i++) {
		attenuation = invDisSq(PointedLights[i].position, pos, dist2Cam);
//		if(attenuation > LightClipBias) {
			float v = dot(normalize(SpotLights[i].position-pos), SpotLights[i].direction);
//			visib = calcVis(pos, lightDir, absorption);
			Lo += /*visib**/v*SpotLights[i].color*attenuation;
//		}
	}

	Lo += DirectionalLight.ambient*color;
	return Lo;
}

float getDepth(vec3 worldpos) {
	vec4 a = pvm*vec4(worldpos, 1);
	a /= a.w;
	return a.z;
}

float mod289(float x){return x - floor(x * (1.0 / 289.0)) * 289.0;}
vec4 mod289(vec4 x){return x - floor(x * (1.0 / 289.0)) * 289.0;}
vec4 perm(vec4 x){return mod289(((x * 34.0) + 1.0) * x);}

float noise(vec3 p){
    vec3 a = floor(p);
    vec3 d = p - a;
    d = d * d * (3.0 - 2.0 * d);

    vec4 b = a.xxyy + vec4(0.0, 1.0, 0.0, 1.0);
    vec4 k1 = perm(b.xyxy);
    vec4 k2 = perm(k1.xyxy + b.zzww);

    vec4 c = k2 + a.zzzz;
    vec4 k3 = perm(c);
    vec4 k4 = perm(c + 1.0);

    vec4 o1 = fract(k3 * (1.0 / 41.0));
    vec4 o2 = fract(k4 * (1.0 / 41.0));

    vec4 o3 = o2 * d.z + o1 * (1.0 - d.z);
    vec2 o4 = o3.yw * d.x + o3.xz * (1.0 - d.x);

    return o4.y * d.y + o4.x * (1.0 - d.y);
}

float getDensityDiff(vec3 pos) {
	return effect ? noise(pos*2) : 1;
}

float rand(vec3 co){
    return fract(sin(dot(co, vec3(12.9898, 78.233, -48.462))) * 43758.5453);
}

vec4 calcVisColor(vec3 position, vec3 dir, float ab, vec3 color, float seed) {
	vec3 pos = position;
	float dst = SDF(cam_pos);
	bool outside = dst > 0; //For correct culling
	if(outside)//Move pos to relevant position
		pos += intersectAABB(position, dir).x*dir;
	pos += dir*stepBias;
	float dt = 0; //Distance traveled
	vec3 col = vec3(0); //the resulting color
	vec3 last = vec3(0);
	
	
	
	for(int i = 0; i < maxSamples1; i++) {
		float depth = texture2D(depth, (ndc.xy/ndc.w)*.5+.5).x*2-1;
		if(depth-getDepth(pos) < 0)
			break;
		dst = SDF(pos);
		if(!infinite)
			if(dst > 0)
				break;
		float dtc = ((sqrt((pos.x-position.x)*(pos.x-position.x)+(pos.y-position.y)*(pos.y-position.y)+(pos.z-position.z)*(pos.z-position.z)))*increase1+1)*(i*increase2+1);
		float density = getDensityDiff(pos);
		dt += density*stepSize1*dtc*seed;
		pos += dir*stepSize1*dtc*seed;
		if(mod(i, colorSampleRate) == 0)
			last = cLAP(color, dir, pos, ab, 0);
		col += last*density;
	}
	
	return vec4(col*colorMul, fogFunc(dt, ab));
}


void main() {
	vec3 cam_vec = normalize(fragposition-cam_pos);
	
	vec4 col = calcVisColor(cam_pos, cam_vec, density, color, rand(fragposition)*seedInfluence+1-seedInfluence);
	FragColor = col;
	gl_FragDepth = 0;
	//GBuffer
	FragPos = vec4(fragposition, 1);
	FragNormal = vec4(0, 0, 0, 1);
	FragMaterial = vec4(0, 0, 0, 1);
}