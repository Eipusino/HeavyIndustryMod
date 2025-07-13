uniform sampler2D u_texture;

in vec2 v_texCoords;

out vec4 fragColor;

void main() {
	fragColor.rgb = texture(u_texture, v_texCoords).rgb;
	fragColor.a = 1.0;
}
