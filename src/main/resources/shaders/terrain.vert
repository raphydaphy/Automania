#version 400 core

in vec3 position;
in vec3 normal;
in vec3 color;

out vec4 shadow_coords;

flat out vec4 frag_color;
flat out vec3 total_diffuse;
flat out float visibility;

const float fog_density = 0.005;
const float fog_gradient = 4;
const float shadow_distance = 100;
const float transition_distance = 10;

uniform mat4 transform;
uniform mat4 projection;
uniform mat4 view;

uniform vec3 light_position[4];
uniform vec3 light_color[4];
uniform vec3 light_attenuation[4];
uniform vec3 sky_color;

uniform mat4 to_shadow_map_space;

void main()
{
    vec4 world_position = transform * vec4(position, 1);
    shadow_coords = to_shadow_map_space * world_position;

    vec4 relative_position = view * world_position;
	gl_Position = projection * relative_position;

	vec3 frag_surface_normal = (transform * vec4(normal, 0)).xyz;

    vec3 frag_light_vector[4];
	for (int i = 0; i < 4; i++)
	{
	    frag_light_vector[i] = light_position[i] - world_position.xyz;
	}
	vec3 frag_camera_vector = (inverse(view) * vec4(0, 0, 0, 1)).xyz - world_position.xyz;

	float camera_distance = length(relative_position.xyz);
    visibility = exp(-pow((camera_distance*fog_density), fog_gradient));
    visibility = clamp(visibility, 0, 1);

    camera_distance = camera_distance - (shadow_distance - transition_distance);
    camera_distance = camera_distance / transition_distance;

    shadow_coords.w = clamp(1 - camera_distance, 0, 1);

    frag_color = vec4(color, 1);

    vec3 unit_normal = normalize(frag_surface_normal);
    vec3 unit_camera_vector = normalize(frag_camera_vector);

    for (int i = 0; i < 4; i++)
    {
        float distance = length(frag_light_vector[i]);
        float attFactor = light_attenuation[i].x + (light_attenuation[i].y * distance) + (light_attenuation[i].z * distance * distance);
        vec3 unit_light_vector = normalize(frag_light_vector[i]);

        float light_angle = dot(unit_normal, unit_light_vector);
        float brightness = max(light_angle, 0);

        total_diffuse = total_diffuse + (brightness * light_color[i]) / attFactor;
    }
}
