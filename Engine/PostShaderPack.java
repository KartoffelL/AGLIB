package Kartoffel.Licht.Engine;

import Kartoffel.Licht.Rendering.Shaders.PostShader;

public class PostShaderPack {

	/**
	 * A simple Blurring Shader.<br>
	 * Texture input:<br>
	 * 0: color<br>
	 * 1: depth<br>
	 * Uniform variables:<br>
	 * <code>float weight[5]</code>: Weights used for blurring. Should amount to 0.5.<br>
	 * <code>vec2 tex_offset</code>: The offset for texture sampling. Could be 1/textureSize<br>
	 * <code>float alphalock</code>: if alphalock isin´t 0, alpha will be locked at this value<br>
	 * <code>bool bokeh</code>: if to use a bokeh blurr<br>
	 * <code>float Quality</code>: quality of blurr<br>
	 * <code>float Directions</code>: amount of directions<br>
	 * <code>float Size</code>: blurr radius (pixels)<br>
	 * 
	 */
	public PostShader PostShader_Blurr;
	/**
	 * A simple Inversion shader.<br>
	 * Texture input:<br>
	 * 0: color<br>
	 * Uniform variables:<br>
	 * <code>vec4 mask</code>: a mask of the channels to be inverted.<br>
	 */
	public PostShader Postshader_Invert;
	/**
	 * Basic shading shader used for deferred shading.<br>
	 * This shader makes use of lights, which are handled the same globally.
	 * Texture input:<br>
	 * 0: color<br>
	 * 1: bloom<br>
	 * 2: shadow<br>
	 * 3: worldposition<br>
	 * 4: normal<br>
	 * 5: material<br>
	 * 6: ambient occlusion<br>
	 * Uniform variables:<br>
	 * <code>mat4 cm</code>: camera matrix<br>
	 * <code>int debug</code>: debug mode.<br>
	 * 0: disabled<br>
	 * 1: worldposition<br>
	 * 2: dot between normal and up ([0, 1, 0])<br>
	 * 3: normal<br>
	 * 4: material<br>
	 * 5: albedo color<br>
	 * <code>int tonemapper</code>: tonemapper to use.<br>
	 * 0: none<br>
	 * 1: aces<br>
	 * 2: AMDTonemapper<br>
	 * 3: Uncharted2Tonemap<br>
	 * 4: Uncharted2TonemapOp<br>
	 * 5: DX11DSK<br>
	 * <code>bool shading</code>: if shading should be applied<br>
	 * <code>bool bloom</code>: if bloom should be applied<br>
	 * <code>bool shadows</code>: if shadow mapping should be applied<br>
	 * <code>vec2 shadowMapRes</code>: Resolution of the shadow map<br>
	 * <code>mat4 shadowpv</code>: shadow camera proview matrix<br>
	 * <code>vec3[4] shadowpos</code>: shadow cascades position<br>
	 * <code>float[4] shadowsiz</code>: shadow cascades planesize (radius of covered area)<br>
	 * <code>int cascades</code>: amount of shadow cascades (max 4)<br>
	 * <code>vec2 shadowFilter</code>: filter settings for shadows.<br>
	 * <code>int shadowSamples</code>: samples for shadow filtering<br>
	 * <code>float shadowBias</code>: shadow mapping bias<br>
	 * <code>boolean r_shading</code>: if raytraced shading should be applied<br>
	 * <code>bool ao</code>: if ambient occlusion should be applied<br>
	 * <code>sphere spheres[256]</code>: spheres for raytracing<br>
	 * <blockquote><code>struct sphere {<br>...vec3 pos;<br>...float radius;<br>...int mat;<br>};</code><br></blockquote>
	 * <code>box boxes[256]</code>: boxes for raytracing<br>
	 * <blockquote><code>struct box {<br>...vec3 pos;<br>...vec3 size;<br>...int mat;<br>};</code></blockquote>
	 * <code>material materials[256]</code>: materials assigned by either the boxes or spheres by their 'mat' index<br>
	 * <blockquote><code>struct material {<br>...vec3 color;<br>...float alpha;<br>};</code><br></blockquote>
	 * <code>int amount_spheres</code>: amount of spheres that should be considered for raytracing<br>
	 * <code>int amount_boxes</code>: amount of boxes that should be considered for raytracing<br>
	 * <code>vec2 clip</code>: the near and far (respectively) clip distances for raytracing<br>
	 */
	public PostShader PostShader_Tonemapping;
	/**
	 * Denoising Shader.<br>
	 * Texture input:<br>
	 * 0: color A<br>
	 * 1: color B<br>
	 * Uniform variables:<br>
	 * <code>vec2 textureSize</code>: the size of the image to be denoised<br>
	 * <code>float alphalock</code>: if not 0, locks the alpha to be 'alphalock'<br>
	 */
	public PostShader PostShader_DNoise;
	/**
	 * Mixing Shader.<br>
	 * Uniform variables:<br>
	 * <code>vec4 a</code>: amount of the first image to be mixed in.<br>
	 * <code>vec4 b</code>: amount of the second image to be mixed in.<br>
	 * <code>bool mix</code>: if alpha should be considered with blending<br>
	 */
	public PostShader PostShader_Mix;
	/**
	 * Multiplication Shader.<br>
	 * Texture input:<br>
	 * 0: color A<br>
	 * 1: color B<br>
	 * Uniform variables:<br>
	 * -has no uniforms
	 */
	public PostShader PostShader_Mul;
	/**
	 * Ambient Occlusion Shader.<br>
	 * Texture input:<br>
	 * 0: position<br>
	 * 1: normal<br>
	 * 2: depth<br>
	 * 3: color<br>
	 * Uniform variables:<br>
	 * <code>float radius</code>: radius of the sampling kernel<br>
	 * <code>int SampleKernelSize</code>: amount of samples<br>
	 * <code>int power</code>: intensity<br>
	 * <code>float smoothf</code>: smoothness <br>
	 * <code>float clampV</code>: divides by clamV and clamps between 0-1<br>
	 * <code>vec3 random</code>: random constant<br>
	 * <code>mat4 ProViewMatrix</code>: projection x view matrix<br>
	 * <code>vec2 clip</code>: clip distances (near, far) of the projection matrix<br>
	 */
	public PostShader PostShader_AO;
	/**
	 * Bloom Shader.<br>
	 * Texture input:<br>
	 * 0: color<br>
	 * 1: material<br>
	 * Uniform variables:<br>
	 * <code>float threshhold</code>: threshhold for bloom<br>
	 * <code>float cla</code>: max per color channel value<br>
	 * <code>float boost</code>: multiplier for blooming colors<br>
	 * <code>float em_boost</code>: multiplier for testing colors<br>
	 */
	public PostShader PostShader_Bloom;
	/**
	 * Downscaling Shader.<br>
	 * Texture input:<br>
	 * 0: color<br>
	 * Uniform variables:<br>
	 * <code>vec2 amount</code>: amount of downscaling<br>
	 */
	public PostShader PostShader_Downscale;
	/**
	 * Simple FXAA.<br>
	 * Texture input:<br>
	 * 0: color<br>
	 * Uniform variables:<br>
	 * <code>vec2 windowSize</code>: size of the image<br>
	 */
	public PostShader PostShader_fxaa;
	/**
	 * Fog Shader.<br>
	 * Texture input:<br>
	 * 0: color<br>
	 * 1: worldposition<br>
	 * Uniform variables:<br>
	 * <code>float a</code>: divisor<br>
	 * <code>float b</code>: exponent<br>
	 * <code>vec4 color</code>: color of the fog<br>
	 */
	public PostShader PostShader_fog;
	/**
	 * Shader that simply passes an image.<br>
	 * Texture input:<br>
	 * 0: color<br>
	 * Uniform variables:<br>
	 * -has no uniforms
	 */
	public PostShader PostShader_pass;
	/**
	 * Depth of field Shader (WIP).<br>
	 */
	public PostShader PostShader_dof; //WIP
	
	
	public PostShaderPack() {
		PostShader_Blurr = new PostShader(PostShader.POST_SHADER_BLURR);
		Postshader_Invert = new PostShader(PostShader.POST_SHADER_INVERT);
		PostShader_Tonemapping = new PostShader(PostShader.POST_SHADER_BASIC);
		PostShader_Mix = new PostShader(PostShader.POST_SHADER_MIX);
		PostShader_Mul = new PostShader(PostShader.POST_SHADER_MUL);
		PostShader_AO = new PostShader(PostShader.POST_SHADER_AO);
		PostShader_Bloom = new PostShader(PostShader.POST_SHADER_BLOOM);
		PostShader_Downscale = new PostShader(PostShader.POST_SHADER_DOWNSCALE);
		PostShader_DNoise = new PostShader(PostShader.POST_SHADER_DNOISE);
		PostShader_fxaa = new PostShader(PostShader.POST_SHADER_FXAA);
		PostShader_fog = new PostShader(PostShader.POST_SHADER_FOG);
		PostShader_pass = new PostShader(PostShader.POST_SHADER_PASS);
//		PostShader_dof = new PostShader(PostShader.POST_SHADER_DOF);
	}

}
