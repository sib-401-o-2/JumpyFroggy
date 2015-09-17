package com.shasser.froggy;


import java.nio.FloatBuffer;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;
import android.widget.Toast;

public class Shader {

    private int program_Handle;

    int a_vertex_Handle;
    int a_color_Handle;
    int a_normal_Handle;
    int u_modelViewProjectionMatrix_Handle;
    int u_camera_Handle;
    int u_lightPosition_Handle;
    int[] u_texture_Handle;

    int[] texture_id;

    private int mPos = 0;
    private int mPositionOffset;
    private int mColorOffset;
    private int mNormalOffset;

    private boolean b_vertex = false;
    private boolean b_color = false;
    private boolean b_normal = false;

    public Shader(String vertexShader, String fragmentShader, Context context) {

        initShader(vertexShader, fragmentShader, context);
    }

    public void on_Vertex()
    {
        a_vertex_Handle = GLES20.glGetAttribLocation(program_Handle,
                "a_Position");
        mPositionOffset = mPos;
        mPos += 3;
        b_vertex = true;

    }

    public void on_Color()
    {
        a_color_Handle = GLES20.glGetAttribLocation(program_Handle, "a_Color");
        mColorOffset = mPos;
        mPos += 4;
        b_color = true;
    }

    public void on_Normal()
    {
        a_normal_Handle = GLES20.glGetAttribLocation(program_Handle, "a_Normal");
        mNormalOffset = mPos;
        mPos += 3;
        b_normal = true;

    }

    public void on_MVP()
    {
        u_modelViewProjectionMatrix_Handle = GLES20.glGetUniformLocation(
                program_Handle, "u_MVPMatrix");
    }

    public void on_Camera()
    {
        u_camera_Handle = GLES20.glGetUniformLocation(
                program_Handle, "u_Camera");
    }

    public void on_Light()
    {
        u_lightPosition_Handle = GLES20.glGetUniformLocation(
                program_Handle, "u_LightPosition");
    }

    public void on_Texture(int k)
    {
        texture_id = new int[32];
        texture_id[0] = GLES20.GL_TEXTURE0;
        u_texture_Handle = new int[k];
        for (int i = 0; i < k; i++)
        {
            u_texture_Handle[i] = GLES20.glGetUniformLocation(
                    program_Handle, "u_Texture" + k);
        }
    }


    private void initShader(String vertexShader, String fragmentShader, Context context) {
        int vertexShader_Handle = GLES20
                .glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader_Handle, vertexShader);
        GLES20.glCompileShader(vertexShader_Handle);

        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(vertexShader_Handle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0)
        {
            Toast toast = Toast.makeText(context,
                    "Error creating vertex shader.", Toast.LENGTH_SHORT);
            toast.show();
           // throw new RuntimeException("Error creating vertex shader.");
        }

        int fragmentShader_Handle = GLES20
                .glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader_Handle, fragmentShader);
        GLES20.glCompileShader(fragmentShader_Handle);

        GLES20.glGetShaderiv(fragmentShader_Handle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0)
        {
            Toast toast = Toast.makeText(context,
                    "Error creating fragment shader.", Toast.LENGTH_SHORT);
            toast.show();
          //  throw new RuntimeException("Error creating fragment shader.");
        }


        program_Handle = GLES20.glCreateProgram();
        GLES20.glAttachShader(program_Handle, vertexShader_Handle);
        GLES20.glAttachShader(program_Handle, fragmentShader_Handle);
        GLES20.glLinkProgram(program_Handle);

        GLES20.glGetProgramiv(program_Handle, GLES20.GL_LINK_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0)
        {
            Toast toast = Toast.makeText(context,
                    "Error creating program.", Toast.LENGTH_SHORT);
            toast.show();
            //throw new RuntimeException("Error creating program.");
        }
    }



    public void linkVertexBuffer(FloatBuffer vertexBuffer) {

        if(b_vertex){
            vertexBuffer.position(mPositionOffset);
            GLES20.glVertexAttribPointer(a_vertex_Handle, 3, GLES20.GL_FLOAT,
                    false, mPos * 4, vertexBuffer);
            GLES20.glEnableVertexAttribArray(a_vertex_Handle);
        }

        if(b_color){
            vertexBuffer.position(mColorOffset);
            GLES20.glVertexAttribPointer(a_color_Handle, 4, GLES20.GL_FLOAT, false,
                    mPos * 4, vertexBuffer);
            GLES20.glEnableVertexAttribArray(a_color_Handle);
        }

        if(b_normal){
            vertexBuffer.position(mNormalOffset);
            GLES20.glVertexAttribPointer(a_normal_Handle, 3, GLES20.GL_FLOAT, false,
                    mPos * 4, vertexBuffer);
            GLES20.glEnableVertexAttribArray(a_normal_Handle);
        }
    }

    public void linkModelViewProjectionMatrix(float[] modelViewProjectionMatrix) {
        GLES20.glUniformMatrix4fv(u_modelViewProjectionMatrix_Handle, 1, false,
                modelViewProjectionMatrix, 0);
    }

    public void linkCamera (float xCamera, float yCamera, float zCamera){
        GLES20.glUniform3f(u_camera_Handle, xCamera, yCamera, zCamera);
    }

    public void linkLightSource (float xLightPosition, float yLightPosition, float zLightPosition){
        GLES20.glUniform3f(u_lightPosition_Handle, xLightPosition, yLightPosition, zLightPosition);
    }

    public void linkTexture(Texture texture, int k){
        if (texture != null){
            GLES20.glActiveTexture(texture_id[k]);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getName());
            GLES20.glUniform1i(u_texture_Handle[k], k);
        }
    }

    public void useProgram() {
        GLES20.glUseProgram(program_Handle);
    }

}