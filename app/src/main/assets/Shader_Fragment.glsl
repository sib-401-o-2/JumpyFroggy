precision mediump float;
varying vec4 v_Color;
uniform sampler2D u_texture0;
varying vec2 v_texcoord0;

void main()
	{
		vec4 textureColor0=texture2D(u_texture0,1.0-v_texcoord0);
		gl_FragColor = textureColor0;
	}