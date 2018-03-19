#version 400 core

in vec2 position;
in vec2 uvs;

out vec2 frag_uvs;

uniform vec2 translation;

void main(void)
{
    gl_Position = vec4(position + translation * vec2(2,-2), 0.0, 1.0);
    frag_uvs = uvs;
}