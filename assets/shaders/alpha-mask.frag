uniform sampler2D u_texture;
uniform sampler2D u_mask;

in vec2 v_texCoords;

out vec4 fragColor;

void main() {
	vec4 c = texture(u_texture, v_texCoords);

	fragColor = vec4(c.rgb, texture(u_mask, v_texCoords).a*c.a);
}