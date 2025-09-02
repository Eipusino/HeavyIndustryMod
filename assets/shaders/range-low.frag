#define HIGHP

uniform sampler2D u_texture;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;
uniform float u_stroke;
uniform float u_alpha;
uniform vec4 u_color;

in vec2 v_texCoords;

out vec4 fragColor;

const float threshold = 0.01;
const float PI = 3.14159265359;

void main() {
	vec2 v = u_stroke * (1.0 / u_resolution);

	vec4 base = texture2D(u_texture, v_texCoords);
	vec2 worldCoord = vec2(v_texCoords.x * u_resolution.x + u_campos.x, v_texCoords.y * u_resolution.y + u_campos.y);

	float m = min(min(min(
			texture(u_texture, v_texCoords + vec2(1.0, 0.0) * v).a,
			texture(u_texture, v_texCoords + vec2(0.0, 1.0) * v).a),
			texture(u_texture, v_texCoords + vec2(-1.0, 0.0) * v).a),
			texture(u_texture, v_texCoords + vec2(0.0, -1.0) * v).a);

	float s = length(base.rgb);

	float stepA = step(base.a, threshold);
	float stepM = step(m, threshold);
	float stepS = step(s, threshold);

	float con1 = (1.0 - stepA) * stepM;
	float con2 = (1.0 - stepA) * (1.0 - con1);

	vec4 c1 = vec4(base.rgb, u_alpha);
	vec4 c2 = vec4(0.0);

	fragColor = (u_color * stepS + base * (1.0 - stepS)) * con1
		+ (c2 * stepS + c1 * (1.0 - stepS)) * con2;
}
