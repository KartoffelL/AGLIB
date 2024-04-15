#version 330 core

uniform sampler2D sampler0;
in vec2 tex_coords;

uniform vec2 amount = vec2(2000, 2000);

void main()
{
	gl_FragColor = texture2D(sampler0, floor(tex_coords*amount)/amount);
}
