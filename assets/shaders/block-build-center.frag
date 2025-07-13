#define HIGHP

uniform sampler2D u_texture;

uniform vec2 u_texsize;
uniform vec2 u_uv;
uniform vec2 u_uv2;
uniform float u_progress;
uniform float u_time;

in vec4 v_color;
in vec2 v_texCoords;

out vec4 fragColor;

bool id(vec2 coords, vec4 base) {
	vec4 target = texture(u_texture, coords);
	return target.a < 0.1 || (coords.x < u_uv.x || coords.y < u_uv.y || coords.x > u_uv2.x || coords.y > u_uv2.y);
}

bool cont(vec2 T, vec2 v) {
	const float step = 3.0;
	vec4 base = texture(u_texture, T);
	return base.a > 0.1 && (id(T + vec2(0, step) * v, base) || id(T + vec2(0, -step) * v, base) || id(T + vec2(step, 0) * v, base) || id(T + vec2(-step, 0) * v, base));
}

void main() {
	vec2 t = v_texCoords.xy;

	vec2 v = vec2(1.0 / u_texsize.x, 1.0 / u_texsize.y);
	vec2 coords = (v_texCoords - u_uv) / v;
	float value = coords.x + coords.y;

	vec4 color = texture(u_texture, t);

	vec2 center = ((u_uv + u_uv2) / 2.0 - u_uv) / v;
	float dst = (abs(center.x - coords.x) + abs(center.y - coords.y)) / 2.0;

	if ((mod(u_time / 1.5 + value, 20.0) < 15.0 && cont(t, v))) {
		fragColor = v_color;
	} else if (dst < (u_progress) * (center.x)) {
		fragColor = color;
	} else if ((dst - 1.0 < (u_progress) * (center.x)) && color.a > 0.1) {
		fragColor = v_color;
	} else {
		fragColor = vec4(0.0);
	}
}