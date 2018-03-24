#version 400 core

const int MAX_JOINTS = 50;
const int MAX_WEIGHTS = 3;

in vec3 position;
in vec3 normal;
in vec2 tex_coords;
in ivec3 joint_indices;
in vec3 joint_weights;

out vec3 frag_surface_normal;
out vec2 frag_tex_coords;
out vec3 frag_light_vector[4];
out vec3 frag_camera_vector;
out float visibility;

uniform mat4 joint_transforms[MAX_JOINTS];
uniform mat4 transform;
uniform mat4 projection;
uniform mat4 view;

uniform vec3 light_position[4];

const float fog_density = 0.005;
const float fog_gradient = 4;

void main()
{
    vec4 total_local_pos = vec4(0);
    vec4 total_normal = vec4(0);

    for (int joint = 0; joint < MAX_JOINTS; joint++)
    {
        mat4 joint_transform = joint_transforms[joint_indices[joint]];
        vec4 pose_position = joint_transform * vec4(position, 1);
        total_local_pos += pose_position * joint_weights[joint];

        vec4 world_normal = joint_transform * vec4(normal, 0);
        total_normal += world_normal * joint_weights[joint];
    }

    vec4 world_position = transform * total_local_pos;

    vec4 relative_position = view * world_position;
    gl_Position = projection * relative_position;
    frag_tex_coords = tex_coords;


    frag_surface_normal = (transform * total_normal).xyz;
    for (int i = 0; i < 4; i++)
    {
        frag_light_vector[i] = light_position[i] - world_position.xyz;
    }
    frag_camera_vector = (inverse(view) * vec4(0, 0, 0, 1)).xyz - world_position.xyz;

    float camera_distance = length(relative_position.xyz);
    visibility = exp(-pow((camera_distance*fog_density), fog_gradient));
    visibility = clamp(visibility, 0, 1);

    gl_Position = projection * total_local_pos;
}
