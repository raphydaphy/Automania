#version 150

in vec2 tex_coords;

out vec4 fragColor;

uniform sampler2D sampler;

void main()
{
   fragColor = texture(sampler, tex_coords);
}