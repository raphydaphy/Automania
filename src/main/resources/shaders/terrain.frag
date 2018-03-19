#version 400 core

in vec4 shadow_coords;

flat in vec4 frag_color;
flat in vec3 total_diffuse;
flat in float visibility;

out vec4 out_color;

const int sample_radius = 3;
const float total_pixels = (sample_radius * 2 + 1) * (sample_radius * 2 + 1);

uniform vec3 sky_color;
uniform sampler2D shadow_map;

void main()
{
    float map_size = 2048 * 4; // whatever value we use in shadowmapmasterrenderer
    float pixel_size = 1 / map_size;
    float total = 0;

    for (int x = -sample_radius; x < sample_radius; x++)
    {
        for (int y = -sample_radius; y < sample_radius; y++)
        {
            float object_nearest_light = texture(shadow_map, shadow_coords.xy + vec2(x, y) * pixel_size).r;

             if (shadow_coords.z > object_nearest_light)
            {
                total += 1;
            }
        }
    }

    total /= total_pixels;

    float light_factor = 1 - (total * shadow_coords.w);

    vec3 shadow_diffuse = max(total_diffuse, 0.4)* light_factor;

    out_color = vec4(shadow_diffuse, 1) * frag_color;
    out_color = mix(vec4(sky_color, 1), out_color, visibility);
}
