#define HIGHP

uniform sampler2D u_texture;

uniform float u_alpha;

in vec2 v_texCoords;

out vec4 fragColor;

void main() {
	vec2 T = v_texCoords.xy;
	vec4 color = texture(u_texture, T);
	color.a *= u_alpha;

	fragColor = color;
}
