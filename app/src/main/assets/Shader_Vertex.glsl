uniform mat4 u_MVPMatrix;
attribute vec4 a_Position;
attribute vec4 a_Color;
attribute vec3 a_Normal;
varying vec4 v_Color;
varying vec2 v_texcoord0;

void main()
	{
		v_Color = a_Color;
		v_texcoord0.s=a_Position.x;
		v_texcoord0.t=a_Position.y;
		gl_Position = u_MVPMatrix * a_Position;
	}
