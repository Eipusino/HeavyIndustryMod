#define HIGHP

uniform sampler2D u_texture;
uniform sampler2D u_tiletex;
uniform vec2 u_texsize;
uniform vec2 u_offset;
uniform vec2 u_tiletexsize;

in vec2 v_texCoords;

out vec4 fragColor;

void main() {
	vec2 T = v_texCoords.xy;
	vec2 coords = (T * u_texsize) + u_offset;

	vec4 color1 = texture(u_texture, v_texCoords.xy);
	vec4 color2 = texture(u_tiletex, coords / u_tiletexsize);

	color2.a *= color1.a;
	if (color2.a > 0.0) {
		color1.rgb = color2.rgb;
	}

	fragColor = color1;
}