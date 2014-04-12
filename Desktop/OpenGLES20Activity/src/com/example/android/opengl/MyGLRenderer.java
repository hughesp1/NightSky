/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.opengl;



import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;


public class MyGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
    private Triangle mTriangle;
    private Square   mSquare;
    private star mStar;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private static float[] mViewMatrix = new float[16];
    private static float[] mModelMatrix = new float[16];
    private float mAngle;
	private static float tarAngX, tarAngY, tarAngZ;
	private static float angleX, angleY, angleZ;
	int stars = 200;
	float[] ra = new float[stars];
	float[] dec = new float[stars];
	float[] mag = new float[stars];
	float[] con = new float[stars];
	float[] stco = {
			101.3f,-15.28f,-1.46f,0f,
			95.99f,-51.3f,-0.72f,1f,
			213.9f,19.18f,-0.04f,2f,
			219.9f,-59.16f,-0.01f,3f,
			279.2f,38.78f,0.03f,4f,
			79.17f,46f,0.08f,5f,
			78.63f,-7.798f,0.12f,6f,
			114.8f,5.225f,0.38f,7f,
			24.43f,-56.76f,0.46f,8f,
			88.79f,7.407f,0.5f,6f,
			211f,-59.63f,0.61f,3f,
			297.7f,8.868f,0.77f,9f,
			68.98f,16.51f,0.85f,10f,
			247.4f,-25.57f,0.96f,11f,
			201.3f,-10.84f,0.98f,12f,
			116.3f,28.03f,1.14f,13f,
			344.4f,-28.38f,1.16f,14f,
			191.9f,-58.31f,1.25f,15f,
			310.4f,45.28f,1.25f,16f,
			186.6f,-62.9f,1.33f,15f,
			219.9f,-59.16f,1.33f,3f,
			152.1f,11.97f,1.35f,17f,
			104.7f,-27.03f,1.5f,0f,
			187.8f,-56.89f,1.63f,15f,
			263.4f,-36.9f,1.63f,11f,
			81.28f,6.35f,1.64f,6f,
			81.57f,28.61f,1.65f,10f,
			138.3f,-68.28f,1.68f,1f,
			84.05f,-0.7981f,1.7f,6f,
			186.7f,-62.9f,1.73f,15f,
			332.1f,-45.04f,1.74f,18f,
			193.5f,55.96f,1.77f,-1f,
			122.4f,-46.66f,1.78f,-1f,
			51.08f,49.86f,1.79f,-1f,
			165.9f,61.75f,1.79f,-1f,
			107.1f,-25.61f,1.84f,0f,
			276f,-33.62f,1.85f,-1f,
			125.6f,-58.49f,1.86f,1f,
			206.9f,49.31f,1.86f,-1f,
			264.3f,-41f,1.87f,11f,
			89.88f,44.95f,1.9f,5f,
			252.2f,-68.97f,1.92f,-1f,
			99.43f,16.4f,1.93f,13f,
			306.4f,-55.27f,1.94f,-1f,
			131.2f,-53.29f,1.96f,-1f,
			95.68f,-16.04f,1.98f,0f,
			113.7f,31.89f,1.98f,13f,
			141.9f,-7.341f,1.98f,-1f,
			31.79f,23.46f,2f,-1f,
			239.9f,25.92f,2f,-1f,
			37.95f,89.26f,2.02f,-1f,
			283.8f,-25.7f,2.02f,-1f,
			10.9f,-16.01f,2.04f,-1f,
			85.19f,-0.05722f,2.05f,6f,
			2.097f,29.09f,2.06f,-1f,
			17.43f,35.62f,2.06f,-1f,
			86.94f,-8.33f,2.06f,6f,
			211.7f,-35.63f,2.06f,3f,
			222.7f,74.16f,2.08f,-1f,
			263.7f,12.56f,2.08f,-1f,
			340.7f,-45.12f,2.1f,18f,
			47.04f,40.96f,2.12f,-1f,
			177.3f,14.57f,2.14f,17f,
			190.4f,-47.04f,2.17f,3f,
			305.6f,40.26f,2.2f,16f,
			137f,-42.57f,2.21f,-1f,
			10.13f,56.54f,2.23f,-1f,
			83f,0.2992f,2.23f,6f,
			233.7f,26.71f,2.23f,-1f,
			269.2f,51.49f,2.23f,-1f,
			120.9f,-40f,2.25f,-1f,
			139.3f,-58.72f,2.25f,1f,
			30.98f,42.33f,2.26f,-1f,
			2.295f,59.15f,2.27f,-1f,
			201f,54.93f,2.27f,-1f,
			252.5f,-33.71f,2.29f,11f,
			205f,-52.53f,2.3f,3f,
			220.5f,-46.61f,2.3f,-1f,
			218.9f,-41.84f,2.31f,3f,
			240.1f,-21.38f,2.32f,11f,
			165.5f,56.38f,2.37f,-1f,
			6.571f,-41.69f,2.39f,-1f,
			326f,9.875f,2.39f,-1f,
			265.6f,-38.97f,2.41f,11f,
			345.9f,28.08f,2.42f,-1f,
			257.6f,-14.28f,2.43f,-1f,
			178.5f,53.69f,2.44f,-1f,
			319.6f,62.59f,2.44f,-1f,
			111f,-28.7f,2.45f,0f,
			311.6f,33.97f,2.46f,16f,
			14.18f,60.72f,2.47f,-1f,
			346.2f,15.21f,2.49f,-1f,
			140.5f,-54.99f,2.5f,-1f,
			45.57f,4.09f,2.53f,-1f,
			208.9f,-46.71f,2.55f,3f,
			168.5f,20.52f,2.56f,17f,
			249.3f,-9.433f,2.56f,-1f,
			83.18f,-16.18f,2.58f,-1f,
			184f,-16.46f,2.59f,-1f,
			182.1f,-49.28f,2.6f,3f,
			285.7f,-28.12f,2.6f,-1f,
			155f,19.84f,2.61f,17f,
			229.3f,-8.617f,2.61f,-1f,
			89.93f,37.21f,2.62f,5f,
			241.4f,-18.19f,2.62f,11f,
			28.66f,20.81f,2.64f,-1f,
			84.91f,-33.93f,2.64f,-1f,
			188.6f,-22.6f,2.65f,-1f,
			236.1f,6.426f,2.65f,-1f,
			21.45f,60.24f,2.68f,-1f,
			208.7f,18.4f,2.68f,2f,
			224.6f,-42.87f,2.68f,-1f,
			74.25f,33.17f,2.69f,5f,
			161.7f,-48.58f,2.69f,-1f,
			189.3f,-68.86f,2.69f,-1f,
			262.7f,-36.7f,2.69f,11f,
			109.3f,-36.9f,2.7f,-1f,
			221.2f,27.07f,2.7f,2f,
			275.2f,-28.17f,2.7f,-1f,
			296.6f,10.61f,2.72f,9f,
			243.6f,-2.306f,2.74f,-1f,
			246f,61.51f,2.74f,-1f,
			200.1f,-35.29f,2.75f,3f,
			222.7f,-15.96f,2.75f,-1f,
			160.7f,-63.61f,2.76f,1f,
			83.86f,-4.09f,2.77f,6f,
			247.6f,21.49f,2.77f,-1f,
			265.9f,4.567f,2.77f,-1f,
			233.8f,-40.83f,2.78f,-1f,
			76.96f,-4.914f,2.79f,8f,
			262.6f,52.3f,2.79f,-1f,
			6.438f,-76.75f,2.8f,-1f,
			183.8f,-57.25f,2.8f,15f,
			121.9f,-23.7f,2.81f,-1f,
			250.3f,31.6f,2.81f,-1f,
			277f,-24.58f,2.81f,-1f,
			249f,-27.78f,2.82f,11f,
			3.309f,15.18f,2.83f,-1f,
			195.5f,10.96f,2.83f,12f,
			82.06f,-19.24f,2.84f,-1f,
			58.53f,31.88f,2.85f,-1f,
			238.8f,-62.57f,2.85f,-1f,
			261.3f,-54.47f,2.85f,-1f,
			29.69f,-60.43f,2.86f,-1f,
			334.6f,-59.74f,2.86f,-1f,
			56.87f,24.11f,2.87f,10f,
			296.2f,45.13f,2.87f,16f,
			326.8f,-15.87f,2.87f,-1f,
			95.74f,22.51f,2.88f,13f,
			113.7f,31.89f,2.88f,13f,
			59.46f,40.01f,2.89f,-1f,
			229.7f,-67.32f,2.89f,-1f,
			239.7f,-25.89f,2.89f,11f,
			245.3f,-24.41f,2.89f,11f,
			287.4f,-20.98f,2.89f,-1f,
			111.8f,8.289f,2.9f,7f,
			194f,38.32f,2.9f,-1f,
			322.9f,-4.429f,2.91f,-1f,
			46.2f,53.51f,2.93f,-1f,
			102.5f,-49.39f,2.93f,-1f,
			340.8f,30.22f,2.94f,-1f,
			59.51f,-12.49f,2.95f,8f,
			187.5f,-15.48f,2.95f,-1f,
			263f,-48.12f,2.95f,-1f,
			331.4f,0.3197f,2.96f,-1f,
			101f,25.13f,2.98f,13f,
			146.5f,23.77f,2.98f,17f,
			75.49f,43.82f,2.99f,5f,
			271.5f,-29.58f,2.99f,-1f,
			286.4f,13.86f,2.99f,9f,
			32.39f,34.99f,3f,-1f,
			84.41f,21.14f,3f,10f,
			182.5f,-21.38f,3f,-1f,
			199.7f,-22.83f,3f,-1f,
			55.73f,47.79f,3.01f,-1f,
			146.8f,-64.93f,3.01f,1f,
			167.4f,44.5f,3.01f,-1f,
			328.5f,-36.64f,3.01f,18f,
			95.08f,-29.94f,3.02f,0f,
			105.8f,-22.17f,3.02f,0f,
			218f,38.31f,3.03f,2f,
			266.9f,-39.87f,3.03f,11f,
			34.84f,-1.023f,3.04f,-1f,
			207.4f,-41.53f,3.04f,3f,
			155.6f,41.5f,3.05f,-1f,
			191.6f,-67.89f,3.05f,-1f,
			230.2f,71.83f,3.05f,-1f,
			288.1f,67.66f,3.07f,-1f,
			253f,-37.95f,3.08f,11f,
			292.7f,27.96f,3.08f,16f,
			305.3f,-13.22f,3.08f,-1f,
			133.8f,5.946f,3.11f,-1f,
			162.4f,-15.81f,3.11f,-1f,
			274.4f,-35.24f,3.11f,-1f,
			309.4f,-46.71f,3.11f,-1f,
			87.74f,-34.23f,3.12f,-1f,
			140.3f,34.39f,3.13f,-1f,
			142.8f,-56.97f,3.13f,-1f,
			173.9f,-62.98f,3.13f,3f,
			224.8f,-41.9f,3.13f,3f
	};
	float[][] constCol = new float[18][3];
	public static float[] decAvg = new float[18];// = [asc, dec][#const]
	public static float[] raAvg = new float[18];
	public static int raOffset = 0;//220;
    public static int decOffset = 0;//15;
    public static int raO = 0;//220;
    public static int decO = 0;//15;

    
	@Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		raOffset = 0;//220;
	    decOffset = 0;//15;
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        
        for(int i=0; i<18; i++){
        	for(int j=0; j<3; j++){
        		constCol[i][j] = 0.0f; 
        		if((j%3)-(i%3) == 0){
        			constCol[i][j] = 1.0f; 
        		}
        	}
        }
        constCol[13][0] = 1.0f;
        constCol[13][1] = 1.0f;
        constCol[13][2] = 0.0f;
        constCol[6][0] = 0.0f;
        constCol[6][1] = 1.0f;
        constCol[6][2] = 1.0f;
        constCol[15][0] = 0.0f;
        constCol[15][1] = 1.0f;
        constCol[15][2] = 1.0f;
        constCol[3][0] = 1.0f;
        constCol[3][1] = 0.0f;
        constCol[3][2] = 0.0f;
        for(int i=0; i<stars; i++){
        	ra[i] = stco[(i*4)]+raOffset;
        	if(ra[i]>360){ra[i] -=360;}else if(ra[i]<0){ra[i]+=360;}
        	if(ra[i]>180){
        		dec[i] = stco[(i*4)+1]+decOffset;
        	}else{
        		dec[i] = stco[(i*4)+1]-decOffset;
        	}
        	if(dec[i]>90){
        		dec[i] = 180 - dec[i];
        		if(ra[i]<180){ra[i]+=180;}else if(ra[i]<=180){ra[i]-=180;}
        	}else if(dec[i]< -90){
        		dec[i] = -180 - dec[i];
        		if(ra[i]<180){ra[i]+=180;}else if(ra[i]<=180){ra[i]-=180;}
        	}
        	mag[i] = stco[(i*4)+2];
        	con[i] = stco[(i*4)+3];
        }
        //find the average ra and dec of each constellation
        for(int i=0; i<18; i++){
        	float raSum = 0.0f;
        	float decSum = 0.0f;
        	int count = 0;
        	for(int j=0; j<stars; j++){
        		if((int)con[j] == i){
        			count++;
        			raSum+=ra[j];
        			if((int)dec[j]<0){
        				decSum+=dec[j];
        			}else{decSum+=dec[j];}
        		}
        	}
        	raAvg[i] = (raSum/count);
        	if(raAvg[i]>360){raAvg[i]-=360;}
        	decAvg[i] = decSum/count;//+decOffset;
        	//if((int)decAvg[i]>180){decAvg[i]-=180;}
        	Log.d("Const", "index=" + Integer.toString(i) + "  ra=" + Float.toString(raAvg[i]) + "  dec=" + Float.toString(decAvg[i]));
        }
        
        mTriangle = new Triangle();
        mSquare   = new Square();
		mStar = new star();
		
    }
    float acc[] = new float [3];

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Matrix.setIdentityM(mModelMatrix, 0);
        //Matrix.translateM(mModelMatrix, 0, 0, 0, 0f);
        //Matrix.scaleM(mModelMatrix, 0, 0.1f, 0.1f, 0.1f);
    	float d = 0.1f;
        if(Math.abs(angleX-tarAngX)>1.2){
        if(angleX > tarAngX){angleX-= d+((angleX-tarAngX)/10);}else{angleX+=d+((tarAngX-angleX)/10);}}
        if(Math.abs(angleY-tarAngY)>1.2){
        if(angleY > tarAngY){angleY-=d+((angleY-tarAngY)/10);}else{angleY+=d+((tarAngY-angleY)/10);}}
        if(Math.abs(angleZ-tarAngZ)>1.2){
        if(angleZ > tarAngZ){angleZ-=d*6+((angleZ-tarAngZ)/10);}else{angleZ+=d*6+((tarAngZ-angleZ)/10);}}
        
        //Log.d("Angle", "X=" + Float.toString(angleX) + "  Y=" + Float.toString(angleY) + "  Z=" + Float.toString(angleZ));

        
        Matrix.setIdentityM(mViewMatrix, 0);
        
        float[] RMat= new float[16];
        float[] RVec = new float[3];
        RVec[0] = (float) Math.toRadians(angleX);
        RVec[1] = (float) Math.toRadians(angleY);
        RVec[2] = (float) Math.toRadians(angleZ);
        SensorManager.getRotationMatrixFromVector(RMat, RVec);
        
        Matrix.rotateM(mViewMatrix, 0, angleX+raO, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(mViewMatrix, 0, angleY+decO, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mViewMatrix, 0, angleZ, 0.0f, 0.0f, 1.0f);
        
        
        /*
        Matrix.setIdentityM(mViewMatrix, 0);
        float temp[] = new float[16];
        Matrix.setIdentityM(temp, 0);
        Matrix.rotateM(temp, 0, angleX, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mViewMatrix, 0, temp, 0, mViewMatrix, 0);
        Matrix.setIdentityM(temp, 0);
        Matrix.rotateM(temp, 0, angleY, -1.0f, 0.0f, 0.0f);
        Matrix.multiplyMM(mViewMatrix, 0, temp, 0, mViewMatrix, 0);
        Matrix.setIdentityM(temp, 0);
        Matrix.rotateM(temp, 0, angleZ, 0.0f, 0.0f, 1.0f);
        Matrix.multiplyMM(mViewMatrix, 0, temp, 0, mViewMatrix, 0);
        */
        // Calculate MVP = P * V * M
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix, 0, mModelMatrix, 0);
        
        // Draw square
        //int closest = highlight();
        
        for(int i=0; i<stars; i++){
        	float scale = 0.01f + (0.04f * (1.0f/(mag[i]+2.5f)));
        	Matrix.rotateM(scratch, 0, mMVPMatrix, 0, -ra[i], 1, 0, 0);
        	float rot = dec[i];//+decOffset;
        	if(rot>90){rot-=180;}else if(rot<-90){rot+=180;}
        	Matrix.rotateM(scratch, 0, rot, 0, -1, 0);
        	Matrix.translateM(scratch, 0, 0, 0, 6);
        	Matrix.scaleM(scratch, 0, scale, scale, scale);
        	if((int)con[i]>=0 && (int)con[i]<16){star.setColour(constCol[(int)con[i]][0], constCol[(int)con[i]][1], constCol[(int)con[i]][2]);}
        	
        	mStar.draw(scratch);
        	star.setColour(1.0f, 1.0f, 1.0f);
        }//Matrix.translateM(mMVPMatrix, 0, 0, 0, -0.1f);
        //mStar.draw(mMVPMatrix);
    }
    
    int highlight(){
    	int ret = -1;
    	double closest = 1000;
    	for(int i=0; i<18; i++){
    		double a = Math.sqrt(Math.pow(Math.abs(raAvg[i] - (int)angleX), 2)
        			+ Math.pow(Math.abs(decAvg[i] - (int)angleY), 2));
    		if(a<closest){
    			closest = a;
    			ret = i;
    		}
    	}
    	return ret;
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.orthoM(mProjectionMatrix, 0, -ratio*2, ratio*2, -1*2, 1*2, 0, 100);
        //Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 50);

    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
    * Utility method for debugging OpenGL calls. Provide the name of the call
    * just after making it:
    *
    * <pre>
    * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
    * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
    *
    * If the operation is not successful, the check throws an error.
    *
    * @param glOperation - Name of the OpenGL call to check.
    */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    public float getAngle() {
        return mAngle;
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngle(float angle) {
        mAngle = angle;
    }
    
    
    
    static public void setRotateView(float x, float y, float z){
    	tarAngX = x;
    	tarAngY = y;
    	tarAngZ = z;
    	
    	if(Math.abs(angleX-tarAngX)>170){angleX = tarAngX;}
    	if(Math.abs(angleY-tarAngY)>170){angleY = tarAngY;}
    	if(Math.abs(angleZ-tarAngX)>170){angleZ = tarAngZ;}
        
    }
    
    static public float getX(){
    	return angleX-raO;//+decOffset;
    }
    static public float getY(){
    	return angleY-decO;//+raOffset;
    }
    
    public float decOF(){
    	int[] monthValues = {31,28,31,30,31,30,31,31,30,31,30,31};
        float sidereal = 0.0f;
        Calendar c = Calendar.getInstance(); 
     	int day = c.get(Calendar.DAY_OF_MONTH);
     	int month = c.get(Calendar.MONTH);
     	int year = c.get(Calendar.YEAR);
     	int equinox = 266;
     	if(day>266 && ((year & 3) == 0 && ((year % 25) != 0 || (year & 15) == 0))){day++;}
     	else if(day<267 && (((year-1) & 3) == 0 && (((year-1) % 25) != 0 || ((year-1) & 15) == 0))){day++;}
     	//check if it's a leap year
        if ((year & 3) == 0 && ((year % 25) != 0 || (year & 15) == 0))
        {
            monthValues[1] = 29;//leap year
        }
        //number of day in year
        int days = 0;
        for(int m=0; m<12; m++){
        	if(month == m){
        		days+=day;
        		m+=12;
        	}else{
        		day+=monthValues[m];
        	}
        }
        int diff = days - equinox;
        if(diff<0){
        	diff+=365;
        }
        int offset = diff * 365;
        return 0.0f;
    }

}



