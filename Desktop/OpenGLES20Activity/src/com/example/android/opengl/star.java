package com.example.android.opengl;


import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class star {
	
    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            // The matrix must be included as a modifier of gl_Position.
            // Note that the uMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            "  gl_Position = uMVPMatrix * vPosition;" +
            "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}";

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[] = {
    	0.0f, 0.0f, -0.0f,
    	0.0f,  1.0f, -0.0f,   // top
    	0.383f, 0.924f, -0.0f,
        0.707f, 0.707f, -0.0f,
        0.924f, 0.383f, -0.0f,
        1.0f, 0.0f, -0.0f, //right
        0.924f, -0.383f, -0.0f,
        0.707f, -0.707f, -0.0f,
        0.383f, -0.924f, -0.0f,
        0.0f,  -1.0f, -0.0f,   //bottom
        -0.383f, -0.924f, -0.0f,
        -0.707f, -0.707f, -0.0f,
        -0.924f, -0.383f, -0.0f,
        -1.0f,  0.0f, -0.0f,   //left
        -0.924f, 0.383f, -0.0f,
        -0.707f, 0.707f, -0.0f,
        -0.383f, 0.924f, -0.0f
    }; 

    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3,
    									0, 3, 4, 0, 4, 5,
    									0, 5, 6, 0, 6, 7,
    									0, 7, 8, 0, 8, 9,
    									0, 9, 10, 0, 10, 11,
    									0, 11, 12, 0, 12, 13,
    									0, 13, 14, 0, 14, 15,
    									0, 15, 16, 0, 16, 1
    									}; // order to draw vertices

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    
    
    static float color[] = { 1.0f, 1.0f, 1.0f, 1.0f};

    public static void setColour(float r, float g, float b){
    	color[0] = r;
    	color[1] = g;
    	color[2] = b;
    }
    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     * @throws FileNotFoundException 
     * @throws Exception 
     */
    int stars = 0;
    
	void Tr(float M1[], float x, float y, float z){
		float M2[] = new float [16];
		Matrix.setIdentityM(M2, 0);
		M2[12] = x;
		M2[13] = y;
		M2[14] = z;
		Matrix.multiplyMM(M1, 0, M1, 0, M2, 0);
	}
    
    public star(){
    	float M[] = new float [16];
    	float star[] = new float [16];
    	Matrix.setIdentityM(M, 0);
    	star = squareCoords;
    	//Matrix.rotateM(M, 0, 90, 1, 0, 0);
    	//Matrix.scaleM(M, 0, 0.01f, 0.01f, 0.01f);
    	//Tr(M, 0, 0, 10.0f);
    	//Matrix.rotateM(M, 0, 90, 1, 0, 0);
    	//Matrix.translateM(M, 0, 0, 0, 10);
    	//Matrix.transposeM(M, 0, M, 0);
    	float[] ra = null, dec = null, mag = null;
    	try {
			parser.parse(ra, dec, mag, stars);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4 * (stars+1));
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        for(int j=0; j<17*3; j+=3){
        	float vec4[] = new float[4];
        	for(int i=0; i<3; i++){vec4[i] = squareCoords[i+j];}
        	vec4[3] = 1;
    		Matrix.multiplyMV(vec4, 0, M, 0, vec4, 0);
    		for(int i=0; i<3; i++){squareCoords[i+j] = vec4[i];
    		//Log.d(Integer.toString(i), Float.toString(vec4[i]));
    		}
    	}
        
    	vertexBuffer.put(squareCoords);
        
        for(int i=0; i<stars; i++){
        	for(int j=0; j<17*3; j+=3){
        		squareCoords[j] += 20.0f;
        	}
        	vertexBuffer.put(squareCoords);
    	}
    	
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2 * (stars+1));
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        
        for(int i=0; i<stars; i++){
        	for(int j=0; j<48; j++){
        		drawOrder[j]+=17;
        	}
        	drawListBuffer.put(drawOrder);
        }
        
        drawListBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
        squareCoords = star;
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length * (stars+1),
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }
    
    

}

