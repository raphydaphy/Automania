#version 400 core

in vec2 frag_tex_coords;
in vec3 frag_surface_normal;
in vec3 frag_light_vector[4];
in vec3 frag_camera_vector;
in float visibility;

out vec4 out_color;

uniform sampler2D sampler;
uniform vec3 light_color[4];
uniform vec3 light_attenuation[4];
uniform vec3 sky_color;

void main()
{
    vec3 unit_normal = normalize(frag_surface_normal);
    vec3 unit_camera_vector = normalize(frag_camera_vector);

    vec3 total_diffuse = vec3(0);

    for (int i = 0; i < 4; i++)
    {
        float distance = length(frag_light_vector[i]);
        float attFactor = light_attenuation[i].x + (light_attenuation[i].y * distance) + (light_attenuation[i].z * distance * distance);
        vec3 unit_light_vector = normalize(frag_light_vector[i]);

        float light_angle = dot(unit_normal, unit_light_vector);
        float brightness = max(light_angle, 0);

        total_diffuse = total_diffuse + (brightness * light_color[i]) / attFactor;
    }

    total_diffuse = max(total_diffuse, 0.2);

    vec4 texture_color = texture(sampler, frag_tex_coords);

    if (texture_color.a < 0.5)
    {
        discard;
    }

    out_color = vec4(total_diffuse, 1) * texture_color;
    out_color = mix(vec4(sky_color, 1), out_color, visibility);
}
