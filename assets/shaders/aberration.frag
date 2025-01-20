uniform sampler2D u_texture;
uniform vec2 u_texsize;
uniform float u_time;
uniform float u_dp;
uniform vec2 u_offset;

varying vec2 v_texCoords;

void main() {
    vec2 tex = v_texCoords.xy;
    vec2 coords = (tex * u_texsize) + u_offset;
    vec4 color = texture2D(u_texture, tex);

    float aberrAmount = 0.0025;

    aberrAmount += sin(u_time * 0.05) * (aberrAmount * 0.25);

    //get the colors at an offset
    color.r = texture2D(u_texture, vec2(tex.x + aberrAmount, tex.y)).r;
    color.g = texture2D(u_texture, tex).g;
    color.b = texture2D(u_texture, vec2(tex.x - aberrAmount, tex.y)).b;

    //I don't remember what this does, I think it's supposed to make the alpha extend over the edges of the aberrated object
    if (color.r + color.b > 0.2 && color.a < 0.5) {
        color.a = 1.0;
    }

    //add lines to it similar to build beams
    color.a *= 1.0 - (0.2 * (step(mod(coords.y / u_dp + u_time / 4.0, 20.0), 18.0)));

    //output finished result
    gl_FragColor = color;
}
