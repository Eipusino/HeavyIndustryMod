uniform lowp sampler2D u_texture0;
uniform lowp sampler2D u_texture1;

uniform mat4 convolution;
uniform float conv_len;
uniform vec2 dir;
uniform vec2 size;

in vec2 v_texCoords;

out vec4 fragColor;

void main() {
	vec2 len = dir / size;

	vec4 blur = texture(u_texture0, v_texCoords);
	vec3 color = texture(u_texture1, v_texCoords).rgb;

	if (blur.a != 0) {
		vec3 blurColor = vec3(0);

		float offset = -(conv_len - 1.0) / 2.0;
		//float up = (conv_len - 1.0) / 2.0;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				blurColor += convolution[y][x] * texture(u_texture1, v_texCoords + len * offset).rgb;
				offset += 1.0;
				//if (offset > up) break;
			}
		}

		fragColor.rgb = mix(color, blurColor, blur.a);
	} else {
		fragColor.rgb = color;
	}

	fragColor.a = 1.0;
}