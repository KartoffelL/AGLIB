#version 330 core

uniform sampler2D sampler0;

in vec2 tex_coords;

uniform vec2 textureSize = vec2(1000, 1000);
uniform float alphalock = 0;

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//Copyright (c) 2018-2019 Michele Morrone
//All rights reserved.
//
//https://michelemorrone.eu - https://BrutPitt.com
//
//me@michelemorrone.eu - brutpitt@gmail.com
//twitter: @BrutPitt - github: BrutPitt
//
//https://github.com/BrutPitt/glslSmartDeNoise/
//
//This software is distributed under the terms of the BSD 2-Clause license
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

#define INV_SQRT_OF_2PI 0.39894228040143267793994605993439  // 1.0/SQRT_OF_2PI
#define INV_PI 0.31830988618379067153776752674503

//smartDeNoise - parameters
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//
//sampler2D tex     - sampler image / texture
//vec2 uv           - actual fragment coord
//float sigma  >  0 - sigma Standard Deviation
//float kSigma >= 0 - sigma coefficient 
//  kSigma * sigma  -->  radius of the circular kernel
//float threshold   - edge sharpening threshold 

vec4 smartDeNoise(sampler2D tex, vec2 uv, vec2 size, float sigma, float kSigma, float threshold)
{
float radius = round(kSigma*sigma);
float radQ = radius * radius;

float invSigmaQx2 = .5 / (sigma * sigma);      // 1.0 / (sigma^2 * 2.0)
float invSigmaQx2PI = INV_PI * invSigmaQx2;    // 1/(2 * PI * sigma^2)

float invThresholdSqx2 = .5 / (threshold * threshold);     // 1.0 / (sigma^2 * 2.0)
float invThresholdSqrt2PI = INV_SQRT_OF_2PI / threshold;   // 1.0 / (sqrt(2*PI) * sigma^2)

vec4 centrPx = texture(tex,uv); 

float zBuff = 0.0;
vec4 aBuff = vec4(0.0);

vec2 d;
for (d.x=-radius; d.x <= radius; d.x++) {
    float pt = sqrt(radQ-d.x*d.x);       // pt = yRadius: have circular trend
    for (d.y=-pt; d.y <= pt; d.y++) {
        float blurFactor = exp( -dot(d , d) * invSigmaQx2 ) * invSigmaQx2PI;

        vec4 walkPx =  texture(tex,uv+d/size);
        vec4 dC = walkPx-centrPx;
        float deltaFactor = exp( -dot(dC, dC) * invThresholdSqx2) * invThresholdSqrt2PI * blurFactor;

        zBuff += deltaFactor;
        aBuff += deltaFactor*walkPx;
    }
}
return aBuff/zBuff;
}

void main() {
	gl_FragColor = smartDeNoise(sampler0, tex_coords, textureSize, 4, 2, 0.08);
	if(alphalock != 0)
		gl_FragColor.a = alphalock;
}
