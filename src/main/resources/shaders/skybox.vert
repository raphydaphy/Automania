#version 400 core

layout(location = 0) in vec3 position;

out float height;

uniform mat4 projection_view;
uniform float skybox_size;

const float tiling = 3.3;
const float scrollFactor = 0.461;

void main(void)
{
	vec3 world_position = position * skybox_size;
	world_position.z -= skybox_size * 0.95;
	gl_Position = projection_view * vec4(world_position, 1.0);
	height = world_position.y;
}