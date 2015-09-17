package com.shasser.froggy;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity  {

    private GLSurfaceView glSurfaceView; //Виджет отрисовывающий графику

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2); // Ставим версию OpenGL 2.0

        GLSurfaceView.Renderer renderer = new tRender(getApplicationContext());

        glSurfaceView.setRenderer(renderer);

        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY); // Отрисовываем кадры постоянно

        setContentView(glSurfaceView); // Выводим виджет
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    public class tRender implements GLSurfaceView.Renderer {

        Context context;

        private Shader shader;

        private Texture mTexture0;

        private float[] mModelMatrix = new float[16];

        private float[] mViewMatrix = new float[16];

        private float[] mProjectionMatrix = new float[16];

        private float[] mMVPMatrix = new float[16];

        private float xСamera, yCamera, zCamera;

        private FloatBuffer vertexBuffer;

        private final int mPositionOffset = 0;


        public tRender(Context cont) {

            context = cont;


            try {

                float[] vertexArray = new float[] { 0.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 1.0f};

                vertexBuffer = ByteBuffer.allocateDirect(vertexArray.length * 4)
                        .order(ByteOrder.nativeOrder()).asFloatBuffer();
                vertexBuffer.put(vertexArray).position(0);


            } catch (Throwable t) {
                Log.e("lol", "Ошибка создания буфера: " + t.toString());
            }
        }

        @Override
        public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

            GLES20.glClearColor(0, 0.5f, 1, 1);

     //       GLES20.glEnable(GLES20.GL_DEPTH_TEST);

            GLES20.glHint(
                    GLES20.GL_GENERATE_MIPMAP_HINT, GLES20.GL_NICEST);
            xСamera = 0.5f;
            yCamera = 0.5f;
            zCamera = 2f;

            Matrix.setLookAtM(
                    mViewMatrix, 0, xСamera, yCamera, zCamera, 0.5f, 0.5f, 0, 0, 1, 0);

            Matrix.setIdentityM(mModelMatrix, 0);

            String v_shd = "  ";
            String f_shd = " ";
            AssetManager assetManager = getAssets();
            try {
                InputStream ims = assetManager.open("Shader_Vertex.glsl");
                v_shd = Parse_GLSL.convertStreamToString(ims);
                ims = assetManager.open("Shader_Fragment.glsl");
                f_shd = Parse_GLSL.convertStreamToString(ims);
            } catch (Throwable t) {
                Log.e("lol", "Ошибка открытия файла: " + t.toString());
            }

            mTexture0=new Texture(context, R.drawable.frog);

            shader = new Shader(v_shd, f_shd, getApplicationContext());

            shader.on_Vertex();
            shader.on_Color();
            shader.on_MVP();
            shader.on_Camera();
            shader.on_Texture(1);
            shader.linkTexture(mTexture0, 0);
            shader.useProgram();

        }

        @Override
        public void onSurfaceChanged(GL10 glUnused, int width, int height) {
            GLES20.glViewport(0, 0, width, height);

            final float ratio = (float) width / height;
            final float left = -ratio;
            final float right = ratio;
            final float bottom = -1.0f;
            final float top = 1.0f;
            final float near = 1.0f;
            final float far = 10.0f;

            Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top,
                    near, far);
        }

        @Override
        public void onDrawFrame(GL10 glUnused) {

            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT
                    | GLES20.GL_DEPTH_BUFFER_BIT);

            try {
                Matrix.setIdentityM(mModelMatrix, 0);
                drawRectangle(vertexBuffer);
            } catch (Throwable t) {
               Log.e("lol", "Ошибка прорисовки буфера: " + t.toString());
            }

        }

        private void drawRectangle(final FloatBuffer aTriangleBuffer) {
            aTriangleBuffer.position(mPositionOffset);
            shader.linkVertexBuffer(aTriangleBuffer);
            Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix,
                    0);
            shader.linkModelViewProjectionMatrix(mMVPMatrix);
            shader.linkCamera(xСamera, yCamera, zCamera);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }
    }

}
