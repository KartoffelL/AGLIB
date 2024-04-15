#version 330 core

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


uniform sampler2D sampler0; //Color
uniform sampler2D sampler1; //Bloom map
uniform sampler2D sampler2;	//Shadow sampler
uniform sampler2D sampler3; //Frag position
uniform sampler2D sampler4; //Frag Normal
uniform sampler2D sampler5; //Material properties
uniform sampler2D sampler6; //AO

uniform mat4 shadowpv;
uniform vec3[4] shadowpos;
uniform float[4] shadowsiz;
uniform float shadowMapRes;
uniform int cascades = 1;
uniform vec2 shadowFilter = vec2(4, 2);
uniform int shadowSamples = 16;
uniform float shadowBias = 0.00001;


uniform int debug = 0;
uniform int tonemapper = 0;

uniform bool shading = true;
uniform bool bloom = false;
uniform bool shadows = false;
uniform bool r_shading = true;
uniform bool ao = false;


#define NR_POINT_LIGHTS 128
uniform PointLight PointedLights[NR_POINT_LIGHTS];
uniform SpotLight SpotLights[NR_POINT_LIGHTS];
uniform int slAm = 0;
uniform int plAm = 0;
uniform float lightClip = .2;

uniform DirLight DirectionalLight;

in vec2 tex_coords;
in vec3 shadowVec;
in vec3 cp;

const float PI = 3.14159265359;

/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////

// Narkowicz 2015, "ACES Filmic Tone Mapping Curve"
vec3 aces(vec3 x) {
  const float a = 2.51;
  const float b = 0.03;
  const float c = 2.43;
  const float d = 0.59;
  const float e = 0.14;
  return clamp((x * (a * x + b)) / (x * (c * x + d) + e), 0.0, 1.0);
}
vec3 reinhard(vec3 x) {
	return x/(x+1);
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// AMD Cauldron code
//
// Copyright(c) 2018 Advanced Micro Devices, Inc.All rights reserved.
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions :
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

//---->https://github.com/GPUOpen-LibrariesAndSDKs/Cauldron/blob/master/src/VK/shaders/tonemappers.glsl
vec3 Uncharted2TonemapOp(vec3 x)
{
    float A = 0.15;
    float B = 0.50;
    float C = 0.10;
    float D = 0.20;
    float E = 0.02;
    float F = 0.30;

    return ((x*(A*x+C*B)+D*E)/(x*(A*x+B)+D*F))-E/F;
}

vec3 Uncharted2Tonemap(vec3 color)
{
    float W = 11.2;
    return Uncharted2TonemapOp(2.0 * color) / Uncharted2TonemapOp(vec3(W));
}

vec3 DX11DSK(vec3 color)
{
    float  MIDDLE_GRAY = 0.72f;
    float  LUM_WHITE = 1.5f;

    // Tone mapping
    color.rgb *= MIDDLE_GRAY;
    color.rgb *= (1.0f + color/LUM_WHITE);
    color.rgb /= (1.0f + color);

    return color;
}

float ColToneB(float hdrMax, float contrast, float shoulder, float midIn, float midOut)
{
    return
        -((-pow(midIn, contrast) + (midOut*(pow(hdrMax, contrast*shoulder)*pow(midIn, contrast) -
            pow(hdrMax, contrast)*pow(midIn, contrast*shoulder)*midOut)) /
            (pow(hdrMax, contrast*shoulder)*midOut - pow(midIn, contrast*shoulder)*midOut)) /
            (pow(midIn, contrast*shoulder)*midOut));
}

// General tonemapping operator, build 'c' term.
float ColToneC(float hdrMax, float contrast, float shoulder, float midIn, float midOut)
{
    return (pow(hdrMax, contrast*shoulder)*pow(midIn, contrast) - pow(hdrMax, contrast)*pow(midIn, contrast*shoulder)*midOut) /
           (pow(hdrMax, contrast*shoulder)*midOut - pow(midIn, contrast*shoulder)*midOut);
}

// General tonemapping operator, p := {contrast,shoulder,b,c}.
float ColTone(float x, vec4 p)
{
    float z = pow(x, p.r);
    return z / (pow(z, p.g)*p.b + p.a);
}

vec3 AMDTonemapper(vec3 color)
{
    const float hdrMax = 16.0; // How much HDR range before clipping. HDR modes likely need this pushed up to say 25.0.
    const float contrast = 2.0; // Use as a baseline to tune the amount of contrast the tonemapper has.
    const float shoulder = 1.0; // Likely don�t need to mess with this factor, unless matching existing tonemapper is not working well..
    const float midIn = 0.18; // most games will have a {0.0 to 1.0} range for LDR so midIn should be 0.18.
    const float midOut = 0.18; // Use for LDR. For HDR10 10:10:10:2 use maybe 0.18/25.0 to start. For scRGB, I forget what a good starting point is, need to re-calculate.

    float b = ColToneB(hdrMax, contrast, shoulder, midIn, midOut);
    float c = ColToneC(hdrMax, contrast, shoulder, midIn, midOut);

    #define EPS 1e-6f
    float peak = max(color.r, max(color.g, color.b));
    peak = max(EPS, peak);

    vec3 ratio = color / peak;
    peak = ColTone(peak, vec4(contrast, shoulder, b, c) );
    // then process ratio

    // probably want send these pre-computed (so send over saturation/crossSaturation as a constant)
    float crosstalk = 4.0; // controls amount of channel crosstalk
    float saturation = contrast; // full tonal range saturation control
    float crossSaturation = contrast*16.0; // crosstalk saturation

    float white = 1.0;

    // wrap crosstalk in transform
    ratio = pow(abs(ratio), vec3(saturation / crossSaturation));
    ratio = mix(ratio, vec3(white), vec3(pow(peak, crosstalk)));
    ratio = pow(abs(ratio), vec3(crossSaturation));

    // then apply ratio to peak
    color = peak * ratio;
    return color;
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

float DistributionGGX(vec3 N, vec3 H, float roughness)
{
    float a      = roughness*roughness;
    float a2     = a*a;
    float NdotH  = max(dot(N, H), 0.0);
    float NdotH2 = NdotH*NdotH;

    float num   = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;

    return num / denom;
}

float GeometrySchlickGGX(float NdotV, float roughness)
{
    float r = (roughness + 1.0);
    float k = (r*r) / 8.0;

    float num   = NdotV;
    float denom = NdotV * (1.0 - k) + k;

    return num / denom;
}
float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness)
{
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2  = GeometrySchlickGGX(NdotV, roughness);
    float ggx1  = GeometrySchlickGGX(NdotL, roughness);

    return ggx1 * ggx2;
}
vec3 fresnelSchlick(float cosTheta, vec3 F0)
{
    return F0 + (1.0 - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}

vec3 calculateLight(float distanceSq, vec3 diffuse, vec3 color, float metallic, float roughness, float emissive, vec3 lightDir, vec3 viewDir, vec3 halfwayDir, vec3 normal) {

	vec3 shin = vec3(metallic, emissive, roughness);

    float attenuation = 1.0 / distanceSq;
    vec3 radiance     = color * attenuation;

	float NDF = DistributionGGX(normal, halfwayDir, shin.r);
	float G   = GeometrySmith(normal, viewDir, lightDir, shin.r);

	vec3 F0 = vec3(0.04);
	F0      = mix(F0, diffuse, shin.b);
	vec3 F  = fresnelSchlick(max(dot(halfwayDir, viewDir), 0.0), F0);

	vec3 numerator    = NDF * G * F;
	float denominator = 4.0 * max(dot(normal, viewDir), 0.0) * max(dot(normal, lightDir), 0.0)  + 0.0001;
	vec3 specular     = numerator / denominator;

	vec3 kS = F;
	vec3 kD = vec3(1.0) - kS;
	kD *= 1.0 - shin.b;

	float NdotL = max(dot(normal, lightDir), 0.0);
	return (kD * diffuse / PI + specular) * radiance * NdotL;

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
	    vec4 a = texture(sampler2, projCoords.xy);
	    
	    float closestDepth = a.r;
	    if(closestDepth == 0)
	    	return vec3(0.0f, 0, 1);

	    return vec3((closestDepth+shadowBias-currentDepth), projCoords.xy);
}
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////

struct material {
	vec3 color;
	float alpha;
};

struct ray {
	vec3 origin;
	vec3 direction;
};

struct hitInfo {
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

//Shapes
uniform sphere spheres[256];
uniform box boxes[256];
uniform material materials[256];

uniform int amount_spheres;
uniform int amount_boxes;

//Clip
uniform vec2 clip = vec2(0.1, 32768);

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
			hi.dst = dst;
			hi.hit++;
			hi.mat = materials[s.mat];
		}
		else {	//Backface
			dst = (-b + sqrt(discr)) / (2*a);
			if(dst > clip.x && dst < hi.dst) {
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
//	if(tN < 0) return; //Backface
	if(tN > clip.x && tN < hi.dst) {
		hi.mat = materials[b.mat];
		hi.dst = tN;
		hi.hit++;
	}
}

float disSq(vec3 a, vec3 b) {
	vec3 c = a-b;
	c *= c;
	return (c.x+c.y+c.z);
}
float rand(float co){
    return fract(sin(dot(vec2(co, co*co+1), vec2(12.9898, 78.233))) * 43758.5453);
}

vec3 randomColor(float seed) {
	vec3 colorA = vec3(rand(-seed*.6+606), rand(seed*.7-100), rand(seed*.331+45));
	return mix(colorA, vec3(1), .25);
}

/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
void main() {
	//Input Color
	vec4 colorf = texture2D(sampler0, tex_coords);
	vec3 color = colorf.rgb;
	vec4 mat = texture2D(sampler5, tex_coords);
	if(mat.a == 0) //Default material
		mat.rgb = vec3(0, 0, 0);
	if(debug == 8)
		color.rgb = vec3(0.8);
	vec4 p = texture2D(sampler4, tex_coords);
	vec3 normal = normalize(p.xyz);
	
	bool noNormal = normal == vec3(10, 0, 0);
	vec3 pos = texture2D(sampler3, tex_coords).rgb;

	float shad = 1;
	int cas = 0;
	for(cas = 0; cas < cascades; cas++)
		if(disSq(shadowpos[cas], pos) < shadowsiz[cas]*shadowsiz[cas])
			break;
	
	//Shadows
	if(shadows) {
		vec4 scc = shadowpv * vec4(pos-shadowpos[cas], 1);
		scc.xy /= shadowsiz[cas];
		float s = 0; //shadow
   
		for(int i = 0; i < shadowSamples; i++) {
			float distt = fract(sin(dot(vec4(pos, i), vec4(250.423, 732.523, 345.211, 146.986))) * 43758.5453); //Between 0-1
			distt *= shadowFilter.y;
			distt = sqrt(distt);
			distt *= shadowFilter.x;
			vec4 off = vec4(cos(i*PI*2/shadowSamples+distt), sin(i*PI*2/shadowSamples+distt), 0, 0);
			off.xy *= distt/shadowMapRes;
			
			vec3 resu = shadowC(scc+off, cas);
			s += resu.r < 0 ? 0 : 1;
			if(i == 4 && s == 4) {
				s = shadowSamples;
				break;
			}
		}
		s /= shadowSamples;
		shad *= s;
	}
	
	//Shading
	bool hh = !(normal.x == 0 && normal.y == 0 && normal.z == 0);
	if(shading && hh) {
		if(p.a != 0) {
			vec3 viewDir = normalize(cp - pos);
			vec3 Lo = vec3(0.0);
			
			//Directional Light---------------------------------
			float distance = 1;
			vec3 lightDir   = normalize(-DirectionalLight.direction);
			vec3 halfwayDir = normalize(lightDir + viewDir);
			
			if(r_shading) {
				hitInfo hi = hitInfo(material(vec3(0), 0), clip.y, 0);
				ray r = ray(pos, lightDir);
				for(int i = 0; i < amount_spheres; i++) {
					intersection_sphere(r, spheres[i], hi);
				}
				for(int i = 0; i < amount_boxes; i++) {
					intersection_box(r, boxes[i], hi);
				}
				if(!(hi.hit != 0 && distance > hi.dst)) //Then Light is blocked
					Lo += calculateLight(distance, color, DirectionalLight.color, mat.r, mat.g, mat.b, lightDir, viewDir, halfwayDir, noNormal ? lightDir : normal)*shad;
			}
			else
				Lo += calculateLight(distance, color, DirectionalLight.color, mat.r, mat.g, mat.b, lightDir, viewDir, halfwayDir, noNormal ? lightDir : normal)*shad;
			//Point lights---------------------------------
			for(int i = 0; i < plAm; i++) {
				distance   = disSq(PointedLights[i].position, pos);
				if((PointedLights[i].color.r+PointedLights[i].color.g+PointedLights[i].color.b)/distance > lightClip) {
					lightDir   = normalize(PointedLights[i].position-pos);
					//Raytraced Shadows
					if(r_shading) {
						hitInfo hi = hitInfo(material(vec3(0), 0), clip.y, 0);
						ray r = ray(pos, lightDir);
						for(int i = 0; i < amount_spheres; i++) {
							intersection_sphere(r, spheres[i], hi);
						}
						for(int i = 0; i < amount_boxes; i++) {
							intersection_box(r, boxes[i], hi);
						}
						if(hi.hit != 0) //Then Light is blocked
							if(distance > hi.dst)
								continue;
					}
					halfwayDir = normalize(lightDir + viewDir);
					Lo += clamp(calculateLight(distance, color, PointedLights[i].color, mat.r, mat.g, mat.b, lightDir, viewDir, halfwayDir, noNormal ? lightDir : normal), 0, 2);
				}
			}
			//Spot lights---------------------------------
			for(int i = 0; i < slAm; i++) {
				distance   = disSq(PointedLights[i].position, pos);
				if((SpotLights[i].color.r+SpotLights[i].color.g+SpotLights[i].color.b)/distance > lightClip) {
					lightDir   = normalize(SpotLights[i].position-pos);
					//Raytraced Shadows
					if(r_shading) {
						hitInfo hi = hitInfo(material(vec3(0), 0), clip.y, 0);
						ray r = ray(pos, lightDir);
						for(int i = 0; i < amount_spheres; i++) {
							intersection_sphere(r, spheres[i], hi);
						}
						for(int i = 0; i < amount_boxes; i++) {
							intersection_box(r, boxes[i], hi);
						}
						if(hi.hit != 0) //Then Light is blocked
							if(distance > hi.dst)
								continue;
					}
					halfwayDir = normalize(lightDir + viewDir);
					vec3 a = calculateLight(distance, color, SpotLights[i].color, mat.r, mat.g, mat.b, lightDir, viewDir, halfwayDir, noNormal ? lightDir : normal);
					float v = dot(lightDir, SpotLights[i].direction);
					Lo += a*clamp(pow(v+SpotLights[i].edge, 1/SpotLights[i].cutoff), 0, 2);
				}
			}
			Lo += DirectionalLight.ambient*color;
			Lo *= ao ? texture2D(sampler6, tex_coords).rgb : vec3(1);
			color = vec3(mat.b) * color + Lo;
		}
	}
	else if(ao && hh)
		color *= texture2D(sampler6, tex_coords).rgb*(1-mat.b)+mat.b;
	
	//''''''''''
	//Bloom
	if(bloom)
		color = max(texture2D(sampler1, tex_coords).rgb, 0);
	//Aces tone mapping
	if(tonemapper == 1)
		color = aces(color);
	else if(tonemapper == 2)
		color = AMDTonemapper(color);
	else if(tonemapper == 3)
		color = Uncharted2Tonemap(color);
	else if(tonemapper == 4)
		color = Uncharted2TonemapOp(color);
	else if(tonemapper == 5)
		color = DX11DSK(color);

	gl_FragColor = vec4(color, colorf.a);
	if(debug == 1)
		gl_FragColor = vec4(pos.rgb, colorf.a);
	else if(debug == 2)
		gl_FragColor = vec4(vec3(dot(vec3(0, 1, 0), normal)), colorf.a);
	else if(debug == 3)
		gl_FragColor = vec4(normal, colorf.a);
	else if(debug == 4)
		gl_FragColor = vec4(mat.rgb, colorf.a);
	else if(debug == 5)
		gl_FragColor = vec4(texture2D(sampler0, tex_coords).rgb, colorf.a);
	else if(debug == 6)
		gl_FragColor = vec4(texture2D(sampler6, tex_coords).rgb, colorf.a);
	else if(debug == 7)
		gl_FragColor = vec4(texture2D(sampler6, tex_coords).rgb*shad, colorf.a);
}
