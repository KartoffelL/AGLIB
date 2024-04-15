#version 330 core

uniform samplerCube sampler;

in vec3 tex_coords;

layout (location = 0) out vec4 FragColor;
layout (location = 2) out vec4 FragNormal;

struct DirLight {
	vec3 direction;
};
uniform DirLight DirectionalLight;

uniform vec3 e = vec3(1);

uniform bool dynamic = true;
uniform bool cool = false; //Makes the sky a little bit less obnoxious when not using HDR
uniform vec3 up = vec3(0, 1, 0);

uniform vec3 wavelenghts = vec3(pow(vec3(400.0/700, 400.0/530, 400.0/440), vec3(4)));
uniform float tw = 512;
uniform float ss = 4;
uniform float sb = .1;
uniform float fl = 25;
uniform vec3 sun = vec3(4);

uniform vec3 n_stars = vec3(1);
uniform vec3 n_color = vec3(0, 0.004, 0.122)/25;
uniform vec3 n_lines = vec3(0.2);
uniform vec3 n_moon = vec3(0.6);

const float pi = 3.14159265359;

////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
//https://gist.github.com/patriciogonzalezvivo/670c22f3966e662d2f83
//	Classic Perlin 3D Noise
//	by Stefan Gustavson
//
vec4 permute(vec4 x){return mod(((x*34.0)+1.0)*x, 289.0);}
vec4 taylorInvSqrt(vec4 r){return 1.79284291400159 - 0.85373472095314 * r;}
vec3 fade(vec3 t) {return t*t*t*(t*(t*6.0-15.0)+10.0);}

float cnoise(vec3 P){
  vec3 Pi0 = floor(P); // Integer part for indexing
  vec3 Pi1 = Pi0 + vec3(1.0); // Integer part + 1
  Pi0 = mod(Pi0, 289.0);
  Pi1 = mod(Pi1, 289.0);
  vec3 Pf0 = fract(P); // Fractional part for interpolation
  vec3 Pf1 = Pf0 - vec3(1.0); // Fractional part - 1.0
  vec4 ix = vec4(Pi0.x, Pi1.x, Pi0.x, Pi1.x);
  vec4 iy = vec4(Pi0.yy, Pi1.yy);
  vec4 iz0 = Pi0.zzzz;
  vec4 iz1 = Pi1.zzzz;

  vec4 ixy = permute(permute(ix) + iy);
  vec4 ixy0 = permute(ixy + iz0);
  vec4 ixy1 = permute(ixy + iz1);

  vec4 gx0 = ixy0 / 7.0;
  vec4 gy0 = fract(floor(gx0) / 7.0) - 0.5;
  gx0 = fract(gx0);
  vec4 gz0 = vec4(0.5) - abs(gx0) - abs(gy0);
  vec4 sz0 = step(gz0, vec4(0.0));
  gx0 -= sz0 * (step(0.0, gx0) - 0.5);
  gy0 -= sz0 * (step(0.0, gy0) - 0.5);

  vec4 gx1 = ixy1 / 7.0;
  vec4 gy1 = fract(floor(gx1) / 7.0) - 0.5;
  gx1 = fract(gx1);
  vec4 gz1 = vec4(0.5) - abs(gx1) - abs(gy1);
  vec4 sz1 = step(gz1, vec4(0.0));
  gx1 -= sz1 * (step(0.0, gx1) - 0.5);
  gy1 -= sz1 * (step(0.0, gy1) - 0.5);

  vec3 g000 = vec3(gx0.x,gy0.x,gz0.x);
  vec3 g100 = vec3(gx0.y,gy0.y,gz0.y);
  vec3 g010 = vec3(gx0.z,gy0.z,gz0.z);
  vec3 g110 = vec3(gx0.w,gy0.w,gz0.w);
  vec3 g001 = vec3(gx1.x,gy1.x,gz1.x);
  vec3 g101 = vec3(gx1.y,gy1.y,gz1.y);
  vec3 g011 = vec3(gx1.z,gy1.z,gz1.z);
  vec3 g111 = vec3(gx1.w,gy1.w,gz1.w);

  vec4 norm0 = taylorInvSqrt(vec4(dot(g000, g000), dot(g010, g010), dot(g100, g100), dot(g110, g110)));
  g000 *= norm0.x;
  g010 *= norm0.y;
  g100 *= norm0.z;
  g110 *= norm0.w;
  vec4 norm1 = taylorInvSqrt(vec4(dot(g001, g001), dot(g011, g011), dot(g101, g101), dot(g111, g111)));
  g001 *= norm1.x;
  g011 *= norm1.y;
  g101 *= norm1.z;
  g111 *= norm1.w;

  float n000 = dot(g000, Pf0);
  float n100 = dot(g100, vec3(Pf1.x, Pf0.yz));
  float n010 = dot(g010, vec3(Pf0.x, Pf1.y, Pf0.z));
  float n110 = dot(g110, vec3(Pf1.xy, Pf0.z));
  float n001 = dot(g001, vec3(Pf0.xy, Pf1.z));
  float n101 = dot(g101, vec3(Pf1.x, Pf0.y, Pf1.z));
  float n011 = dot(g011, vec3(Pf0.x, Pf1.yz));
  float n111 = dot(g111, Pf1);

  vec3 fade_xyz = fade(Pf0);
  vec4 n_z = mix(vec4(n000, n100, n010, n110), vec4(n001, n101, n011, n111), fade_xyz.z);
  vec2 n_yz = mix(n_z.xy, n_z.zw, fade_xyz.y);
  float n_xyz = mix(n_yz.x, n_yz.y, fade_xyz.x);
  return 2.2 * n_xyz;
}
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
vec3 col(float d, float c, float i) {
	vec3 l = exp(pow(wavelenghts, vec3(d+i)));
	vec3 f = l/(l+1);
	return vec3(cool ? f*f*f : f*f*f*f*f)*(mix(-d, 1, c*2.5));
}
void main() {
	if(dynamic) {
		//Variables
		vec3 dir = normalize(tex_coords);
		vec3 ldir = -normalize(DirectionalLight.direction);
		float dis = length(dir);
		float rot = atan(dir.z/dir.x);
		float height = dot(up, dir);
		float ds = dot(dir, ldir);
		float du = dot(up, ldir);
		float dd = dot(up, dir);
		//Elements
		vec3 day_color = vec3(0);
		vec3 night_Color = n_color;
		//Sky color
		float m = -ds*.5+.5;
		float l = pow(clamp(1-((abs(du)+abs(dd))*m), 0.001, 1), tw)*pow(1-abs(du), ss); //clamp(1-((abs(du)+abs(dd))*m), 0.01, 1) -> clamping new
		float brithness = 1-pow(1-(du*(1-sb)+sb), 4);
		float floor = -min(dd, 0);
		l = mix(l, 0, floor);
		day_color = clamp(col(-l*2+1, brithness, floor*fl), 1, 0);
		day_color *= cool ? max(pow(dd, 0.2)*.1, 0)+1 : max(pow(dd, 0.2)+1, 1);
		if(height > 0)
			day_color = mix(day_color, sun, max(pow(ds, 4096)*2, 0));
		//Night
		if(!(n_stars.r == 0 && n_stars.g == 0 && n_stars.b == 0))
			night_Color += vec3(pow(sin(height*60)*sin(rot*60)*.5+.5, 1024))*n_stars;

		if(!(night_Color.r == 0 && night_Color.g == 0 && night_Color.b == 0))
			night_Color *= cnoise(dir*50);
		//Features (Night)
		if(!(n_lines.r == 0 && n_lines.g == 0 && n_lines.b == 0)) {
			night_Color += pow(cos(rot*10)*.5+.5, 65536)*n_lines;
			night_Color += pow(cos(height*20)*.5+.5, 65536)*n_lines;
		}
		if(height > 0)
			night_Color = mix(night_Color, n_moon, max(pow(-ds, 8192), 0));
		//Night floor
		night_Color = night_Color*(1-brithness)*clamp((1-floor*fl), 0, 1);
		FragColor = vec4((day_color+night_Color)*e, 1);
		FragNormal = vec4(0, 0, 0, 1);
	}
	else {
		FragColor.rgb = texture(sampler, tex_coords).rgb*e;
		FragColor.a = 1;
		FragNormal = vec4(0, 0, 0, 1);
	}
}
