uniform lowp sampler2D u_texture0;
uniform lowp sampler2D u_texture1;

uniform mat3 convolution;
uniform vec2 dir;
uniform vec2 size;

in vec2 v_texCoords;

out vec4 fragColor;

void main() {
	vec2 len = dir / size;

	vec4 blur = texture(u_texture0, v_texCoords);
	vec3 color = texture(u_texture1, v_texCoords).rgb;

	if (blur.a > 0.01) {
		vec3 blurColor = vec3(0);

		float offset = -4.0;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++){
				blurColor += convolution[y][x] * texture(u_texture1, v_texCoords + len * offset).rgb;
				offset = offset + 1.0;
			}
		}

		fragColor.rgb = mix(color, blurColor, blur.a);
	} else {
		fragColor.rgb = color;
	}
}