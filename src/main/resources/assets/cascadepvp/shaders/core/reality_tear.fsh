#version 150

uniform float GameTime;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

#define timeMult 1200.0
#define BaseColor vec3(0, 0, 0)

/* discontinuous pseudorandom uniformly distributed in [-0.5, +0.5]^3 */
vec3 random3(vec3 c) {
	float j = 4096.0*sin(dot(c,vec3(17.0, 59.4, 15.0)));
	vec3 r;
	r.z = fract(512.0*j);
	j *= .125;
	r.x = fract(512.0*j);
	j *= .125;
	r.y = fract(512.0*j);
	return r-0.5;
}

/* skew constants for 3d simplex functions */
const float F3 =  0.3333333;
const float G3 =  0.1666667;

/* 3d simplex noise */
float simplex3d(vec3 p) {
	 /* 1. find current tetrahedron T and it's four vertices */
	 /* s, s+i1, s+i2, s+1.0 - absolute skewed (integer) coordinates of T vertices */
	 /* x, x1, x2, x3 - unskewed coordinates of p relative to each of T vertices*/

	 /* calculate s and x */
	 vec3 s = floor(p + dot(p, vec3(F3)));
	 vec3 x = p - s + dot(s, vec3(G3));

	 /* calculate i1 and i2 */
	 vec3 e = step(vec3(0.0), x - x.yzx);
	 vec3 i1 = e*(1.0 - e.zxy);
	 vec3 i2 = 1.0 - e.zxy*(1.0 - e);

	 /* x1, x2, x3 */
	 vec3 x1 = x - i1 + G3;
	 vec3 x2 = x - i2 + 2.0*G3;
	 vec3 x3 = x - 1.0 + 3.0*G3;

	 /* 2. find four surflets and store them in d */
	 vec4 w, d;

	 /* calculate surflet weights */
	 w.x = dot(x, x);
	 w.y = dot(x1, x1);
	 w.z = dot(x2, x2);
	 w.w = dot(x3, x3);

	 /* w fades from 0.6 at the center of the surflet to 0.0 at the margin */
	 w = max(0.6 - w, 0.0);

	 /* calculate surflet components */
	 d.x = dot(random3(s), x);
	 d.y = dot(random3(s + i1), x1);
	 d.z = dot(random3(s + i2), x2);
	 d.w = dot(random3(s + 1.0), x3);

	 /* multiply d by w^4 */
	 w *= w;
	 w *= w;
	 d *= w;

	 /* 3. return the sum of the four surflets */
	 return dot(d, vec4(52.0));
}

/* const matrices for 3d rotation */
const mat3 rot1 = mat3(-0.37, 0.36, 0.85,-0.14,-0.93, 0.34,0.92, 0.01,0.4);
const mat3 rot2 = mat3(-0.55,-0.39, 0.74, 0.33,-0.91,-0.24,0.77, 0.12,0.63);
const mat3 rot3 = mat3(-0.71, 0.52,-0.47,-0.08,-0.72,-0.68,-0.7,-0.45,0.56);

/* directional artifacts can be reduced by rotating each octave */
float simplex3d_fractal(vec3 m) {
    return   0.5333333*simplex3d(m*rot1)
			+0.2666667*simplex3d(2.0*m*rot2)
			+0.1333333*simplex3d(4.0*m*rot3)
			+0.0666667*simplex3d(8.0*m);
}

void main() {
    float iTime = GameTime * timeMult;
    vec2 z = (2.0 * texCoord0 - 1.0) * 1.6;
    vec2 coord = z;
    float alpha = 0.0;

    float TearStyle = vertexColor.a * 4.0;

    if (TearStyle <= 0) { //Zubieta
        vec2 c = vec2(sin(iTime / 3.) * 4., cos(iTime) * 2.) / 10.;
        for (alpha = 0.0; alpha < 100.0; alpha += 1.0) { // FORMULA: z = z^2 + c / z
            z = vec2(z.x * z.x - z.y * z.y, 2.0 * z.x * z.y) + (vec2(dot(c, z), c.y * z.x - c.x * z.y ) / dot(z, z));
            if(dot(z, z) > 65536.0) break;
        }

        //smooth
        float abs_z = z.x*z.x+z.y*z.y;
        if (alpha < 100.0) {
            alpha = alpha + 1.0 - log(log(abs_z))/log(2.0);
        }

        alpha = pow(alpha / 100.0, 0.5) * 1.5; // Divide iterations by maximum for solid fractal
        //if (alpha < 0.4) alpha = 0.0;
        //alpha = pow(alpha*1.75, 3.0);
        alpha = pow(alpha*2.5, 2.5);
    } else if (TearStyle <= 1) { //Lambda
        z = z + vec2(0.45, 0.0);
        vec2 a = vec2(sin(iTime / 10.0) * 1.2, cos(iTime / 10.0));
        for (alpha = 0.0; alpha < 1000.0; alpha += 1.0) { // FORMULA: z = a * z * (1 - z)
            z = vec2(a.x * (z.y * z.y) + 2.0 *  a.y * z.y * z.x - a.y * z.y - a.x * (z.x * z.x) + a.x * z.x, (a.x * z.y - 2.0 * a.x * z.y * z.x - a.y * (z.x * z.x) + a.y * z.x + a.y * (z.y * z.y)));
            if(dot(z, z) > 65536.0) break;
        }
        float abs_z = z.x*z.x+z.y*z.y;
        if (alpha < 1000.0) {
            alpha = alpha + 1.0 - log(log(abs_z))/log(2.0);
        }

        alpha = pow(alpha / 100.0, 0.5) * 1.5;
        alpha = pow(alpha*1.5, 1.5);
    } else if (TearStyle <= 2) { //Phoenix
        vec2 zm, zp, zpt = vec2(0.0);
        vec2 k = vec2(sin(iTime / 2.0 - 10.0) / 2.0, cos(iTime / 3.0) / 2.0);
        vec2 c = vec2(sin(iTime / 2.5 + 5.0) / 2.0, cos(iTime / 1.5 + 20.0) / 2.0);
        for (alpha = 0.0; alpha < 200.0; alpha += 1.0) {
            zp = mat2(k, -k.y, k.x) * zm;
            zpt = mat2(z, -z.y, z.x) * z + c + zp;
            if(dot(z, z) > 65536.0) break;
            zm = z;
            z = zpt;
        }
        alpha = alpha - log2(log2(dot(z,z))) + 4.0;
        if(dot(z, z) < 65536.0) alpha = 200.0;

        alpha = sqrt(alpha/200.0) * 1.5;
        alpha = pow(alpha*1.5, 2.5);
        if (alpha < 0.075) alpha = 0.0;
    } else if (TearStyle <= 3) { //Julia
        z = z * 4.0/3.0;
        for (alpha = 0.0; alpha < 100.0; alpha += 1.0) {
            z = vec2(z.x * z.x - z.y * z.y, 2.0 * z.x * z.y) + vec2(sin(iTime / 3.0), cos(iTime / 2.0));
            if(dot(z, z) > 65536.0) break;
        }

        //smooth
        float abs_z = z.x*z.x+z.y*z.y;
        if (alpha < 100.0) {
            alpha = alpha + 1.0 - log(log(abs_z))/log(2.0);
        }

        alpha = sqrt(alpha/100.0) * 1.4;
    }

    float noise = 0.3 + 0.7*simplex3d_fractal(vec3(coord, iTime*0.25)*8.0+8.0);
    fragColor = vec4(BaseColor + (vertexColor.rgb-BaseColor)*noise, alpha);
}