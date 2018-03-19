#version 400 core

in vec2 frag_uvs;

out vec4 out_color;

uniform vec3 color;
uniform sampler2D font;

const float width = 0.5;
const float edge = 0.1;

void main(void)
{
    float distance = 1 - texture(font, frag_uvs).a;
    float alpha = 1 - smoothstep(width, width + edge, distance);

    out_color = vec4(color, alpha);
}