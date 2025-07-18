#define HIGHP
#define ALPHA 1
#define step 2.0

uniform sampler2D u_texture;
uniform vec2 u_texsize;
uniform vec2 u_invsize;
uniform float u_time;
uniform vec2 u_offset;

// uniform float u_alpha;
in vec2 v_texCoords;

out vec4 fragColor;

void main() {
	vec2 ts = v_texCoords.xy;
	vec2 coords = (ts * u_texsize) + u_offset;

	ts += vec2(sin(coords.y / 3.0 + u_time / 20.0), sin(coords.x / 3.0 + u_time / 20.0)) / u_texsize;

	vec4 color = texture(u_texture, ts);
	vec2 v = u_invsize;

	if (color.a == 0.0) {
		fragColor = color;// Set output color
		return;
	}
	// Add frosted glass effect
	color.a = ALPHA;// Set translucency
	//	color.rgb = mix(color.rgb, vec3(0.0), ALPHA);// Set color
	color = HIGHP vec4(color.rgb, color.a);// Set high precision
	fragColor = color;// Set output color
}