#version 400 core

in float height;

out vec4 out_colour;

const float lower_limit = -20.0;
const float upper_limit = 44.0;

uniform vec3 horizon_color;
uniform vec3 sky_color;

void main(void)
{
    float factor = (height - lower_limit) / (upper_limit - lower_limit);
    factor = clamp(factor, 0.0, 1.0);
    vec3 final_color = mix(horizon_color, sky_color, factor);
	out_colour = vec4(final_color, 1.0);
}
