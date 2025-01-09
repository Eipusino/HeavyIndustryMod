#define HIGHP

varying vec2 v_texCoords;

uniform sampler2D u_color;
uniform sampler2D u_depth;

void main() {
    gl_FragColor = texture2D(u_color, v_texCoords);
    gl_FragDepth = texture2D(u_depth, v_texCoords).r;
}
