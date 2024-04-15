#version 330 core



in Data {
	vec2 tex_coords;
	flat float mat;
	vec3 cam_vec;
	vec3 fragposition;
	vec3 cam_pos;

	vec3 normalR;
	mat3 tbn;
	vec4 debug;
};

layout (location = 0) out vec4 FragColor;
layout (location = 1) out vec4 FragPos;
layout (location = 2) out vec4 FragNormal;
layout (location = 3) out vec4 FragMaterial;

//struct material {
//	vec3 color;
//	float alpha;
//};
//
//struct ray {
//	vec3 origin;
//	vec3 direction;
//};
//
//struct hitInfo {
//	material mat;
//	float dst;
//	int hit;
//};
//
//struct sphere {
//	vec3 pos;
//	float radius;
//	int mat;
//};
//struct box {
//	vec3 pos;
//	vec3 size;
//	int mat;
//};


//Shapes
//uniform int amount_spheres;
//uniform sphere spheres[128];
//uniform int amount_boxes;
//uniform box boxes[128];
//uniform material materials[128];
//
////Clip
//uniform vec2 clip = vec2(0.1, 32768);

//INTERSECTION TEST###########################
//void intersection_sphere(ray r, sphere s, in out hitInfo hi) {
//	vec3 off = r.origin - s.pos;
//	float a = dot(r.direction, r.direction);
//	float b = 2 * dot(off, r.direction);
//	float c = dot(off, off) - s.radius * s.radius;
//	float discr = b * b - 4 * a * c;
//	if(discr >= 0) {
//		float dst = (-b - sqrt(discr)) / (2*a);
//		if(dst > clip.x && dst < hi.dst) {
//			hi.dst = dst;
//			hi.hit++;
//			hi.mat = materials[s.mat];
//		}
//		else {	//Backface
//			dst = (-b + sqrt(discr)) / (2*a);
//			if(dst > clip.x && dst < hi.dst) {
//				hi.dst = dst;
//				hi.hit = 1;
//				hi.mat = materials[s.mat];
//			}
//		}
//	}
//}
//void intersection_box(ray r, box b, in out hitInfo hi) {
//	vec3 ird = 1/r.direction;
//	vec3 n = ird*(r.origin-b.pos);
//	vec3 k = abs(ird)*(b.size);
//	vec3 t1 = -n - k;
//	vec3 t2 = -n + k;
//	float tN = max(max(t1.x, t1.y), t1.z);
//	float tF = min(min(t2.x, t2.y), t2.z);
//	if(tN > tF || tF < 0) return;
////	if(tN < 0) return; //Backface
//	if(tN > clip.x && tN < hi.dst) {
//		hi.mat = materials[b.mat];
//		hi.dst = tN;
//		hi.hit++;
//	}
//}


struct cmat {
    sampler2D albedo;
    sampler2D metallic;
    sampler2D roughness;
    sampler2D emissive;
    
    sampler2D normal;
    sampler2D ao;
};

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

uniform cmat mate;
uniform int materialCount;
uniform int tex_repetetion;

uniform samplerCube skybox;
uniform bool sky_reflection = false;

uniform mat4 shadowpv;

uniform bool shading;
//uniform bool r_shading; //hard-disabled raytraced shading using primitive shapes. moved to Deferred basic shader.
uniform bool normalMapping = false;
uniform bool noNormal;

const float PI = 3.14159265359;

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

vec3 calculateLight(float distance, vec3 diffuse, vec3 color, float metallic, float roughness, float emissive, vec3 lightDir, vec3 viewDir, vec3 halfwayDir, vec3 normal) {

	vec3 shin = vec3(metallic, emissive, roughness);

    float attenuation = 1.0 / (distance * distance);
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

void main() {
	//Texture
	vec2 cords = vec2(((tex_coords.x)/materialCount)+((mat)/materialCount), tex_coords.y);
	vec4 fcolor = texture2D(mate.albedo, cords);
	if(fcolor.a == 0)
		discard;
	vec3 color = fcolor.rgb;
	FragColor.a = fcolor.a;
	float metallic = texture2D(mate.metallic, cords).r;
	float roughness = texture2D(mate.roughness, cords).r;
	float emissive = texture2D(mate.emissive, cords).r;
	
	vec3 normal = normalize(normalR);
	if(normalMapping) {
		vec3 noff = texture2D(mate.normal, cords).xyz*2-1;
		normal = tbn * ((noff.x == 0 && noff.y == 0 && noff.z == 0) ? vec3(0, 0, 1) : noff);
	}
	normal = normalize(normal);
	//Lights
	vec3 viewDir    = normalize(cam_pos - fragposition);
	if(shading) {
		vec3 Lo = vec3(0.0);
		//Directional Light---------------------------------
		float distance = 1;
		vec3 lightDir   = normalize(-DirectionalLight.direction);
		vec3 halfwayDir = normalize(lightDir + viewDir);
//		if(r_shading) {
//			hitInfo hi = hitInfo(material(vec3(0), 0), clip.y, 0);
//			ray r = ray(fragposition, lightDir);
//			for(int i = 0; i < amount_spheres; i++) {
//				intersection_sphere(r, spheres[i], hi);
//			}
//			for(int i = 0; i < amount_boxes; i++) {
//				intersection_box(r, boxes[i], hi);
//			}
//			if(!(hi.hit != 0 && distance > hi.dst)) //Then Light is blocked
//				Lo += calculateLight(distance, color, DirectionalLight.color, metallic, roughness, emissive, lightDir, viewDir, halfwayDir, noNormal ? lightDir : normal);
//		}
//		else
			Lo += calculateLight(distance, color, DirectionalLight.color, metallic, roughness, emissive, lightDir, viewDir, halfwayDir, noNormal ? lightDir : normal);
		//Point lights---------------------------------
		for(int i = 0; i < plAm; i++) {
			if(PointedLights[i].color.r+PointedLights[i].color.g+PointedLights[i].color.b != 0) {
				lightDir   = normalize(PointedLights[i].position-fragposition);
				distance   = length(PointedLights[i].position - fragposition);
				//Raytraced Shadows
//				if(r_shading) {
//					hitInfo hi = hitInfo(material(vec3(0), 0), clip.y, 0);
//					ray r = ray(fragposition, lightDir);
//					for(int i = 0; i < amount_spheres; i++) {
//						intersection_sphere(r, spheres[i], hi);
//					}
//					for(int i = 0; i < amount_boxes; i++) {
//						intersection_box(r, boxes[i], hi);
//					}
//					if(hi.hit != 0) //Then Light is blocked
//						if(distance > hi.dst)
//							continue;
//				}
				halfwayDir = normalize(lightDir + viewDir);
				Lo += clamp(calculateLight(distance, color, PointedLights[i].color, metallic, roughness, emissive, lightDir, viewDir, halfwayDir, noNormal ? lightDir : normal), 0, 2);
			}
		}
		//Spot lights---------------------------------
		for(int i = 0; i < slAm; i++) {
			if(SpotLights[i].color.r+SpotLights[i].color.g+SpotLights[i].color.b != 0) {
				lightDir   = normalize(SpotLights[i].position-fragposition);
				distance   = length(SpotLights[i].position - fragposition);
				//Raytraced Shadows
//				if(r_shading) {
//					hitInfo hi = hitInfo(material(vec3(0), 0), clip.y, 0);
//					ray r = ray(fragposition, lightDir);
//					for(int i = 0; i < amount_spheres; i++) {
//						intersection_sphere(r, spheres[i], hi);
//					}
//					for(int i = 0; i < amount_boxes; i++) {
//						intersection_box(r, boxes[i], hi);
//					}
//					if(hi.hit != 0) //Then Light is blocked
//						if(distance > hi.dst)
//							continue;
//				}
				halfwayDir = normalize(lightDir + viewDir);
				vec3 a = calculateLight(distance, color, SpotLights[i].color, metallic, roughness, emissive, lightDir, viewDir, halfwayDir, noNormal ? lightDir : normal);
				float v = dot(lightDir, SpotLights[i].direction);
				Lo += a*clamp(pow(v+SpotLights[i].edge, 1/SpotLights[i].cutoff), 0, 2);
			}
		}
		Lo += DirectionalLight.ambient*color;
		color = vec3(/*0.03*/emissive) * color + Lo;
	
	}
	FragColor.rgb = color;
	
//	if(metallic != 1 && sky_reflection) { //throws a error, but works fine otherwise
//		vec3 rv = -reflect(viewDir, normal);
//		vec3 rc = vec3(0);
//		int am = int(mix(128, 1, roughness));
//		float d = 0.1f*(1-roughness);
//		for(int i = 0; i < am; i++) {
//			rc += textureCube(skybox, normalize(rv+(vec3(fract(i*1.1486148), fract(i*2.4485148), fract(i*3.1666148))*2-1)*d)).rgb;
//		}
//		FragColor.rgb *= mix(rc/am, vec3(1), metallic);
//	}
	//GBuffer
	FragPos = vec4(fragposition, 1);
	FragNormal = vec4(noNormal ? vec3(10, 0, 0) : normal, 1);
	FragMaterial = vec4(metallic, roughness, emissive, 1);
}
