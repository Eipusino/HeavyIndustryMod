uniform sampler2D u_texture;
uniform float u_alpha;

in vec2 v_texCoords;

out vec4 fragColor;

void main() {
	float a = texture(u_texture, v_texCoords).a;
	fragColor = vec4(0.0, 0.0, 0.0, 1.0 * u_alpha * (1.0 - a));
}
