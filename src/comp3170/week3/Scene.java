package comp3170.week3;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL15.glBindBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.GLBuffers;
import comp3170.Shader;
import comp3170.ShaderLibrary;
import static comp3170.Math.*;

public class Scene {

	final private String VERTEX_SHADER = "vertex.glsl";
	final private String FRAGMENT_SHADER = "fragment.glsl";

	private Vector4f[] vertices;
	private int vertexBuffer;
	private int[] indices;
	private int indexBuffer;
	private Vector3f[] colours;
	private int colourBuffer;

	private Shader shader;

	private Matrix4f modelMatrix = new Matrix4f();
	private Matrix4f tMatrix = new Matrix4f();
	private Matrix4f rMatrix = new Matrix4f();
	private Matrix4f sMatrix = new Matrix4f();
	
	final private float offsetX = 0.2f;
	final private float offsetY = 0.0f;
	final private float scaleX = 0.1f;
	final private float scaleY = 0.1f;
	final private float rotation_rate = TAU/12;
	final private int framerate = 60;
	
	public Scene() {

		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		// @formatter:off
			//          (0,1)
			//           /|\
			//          / | \
			//         /  |  \
			//        / (0,0) \
			//       /   / \   \
			//      /  /     \  \
			//     / /         \ \		
			//    //             \\
			//(-1,-1)           (1,-1)
			//
	 		
		vertices = new Vector4f[] {
			new Vector4f( 0, 0, 0, 1),
			new Vector4f( 0, 1, 0, 1),
			new Vector4f(-1,-1, 0, 1),
			new Vector4f( 1,-1, 0, 1),
		};
			
			// @formatter:on
		vertexBuffer = GLBuffers.createBuffer(vertices);

		// @formatter:off
		colours = new Vector3f[] {
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,0),	// RED
			new Vector3f(0,0,1),	// BLUE
		};
			// @formatter:on

		colourBuffer = GLBuffers.createBuffer(colours);

		// @formatter:off
		indices = new int[] {  
			0, 1, 2, // left triangle
			0, 1, 3, // right triangle
			};
			// @formatter:on

		indexBuffer = GLBuffers.createIndexBuffer(indices);
		
//		float offsetX = 0.2f;
//		float offsetY = 0.0f;
//		float scaleX = 0.1f;
//		float scaleY = 0.1f;
//		float rotation = TAU/16;
		//translationMatrix(offsetX,offsetY, tMatrix);
		//scaleMatrix(scaleX,scaleY ,sMatrix); 
		//rotationMatrix(rotation, rMatrix);
		
		//using JOML methods
		//modelMatrix.translate(offsetX, offsetY, 0).rotateZ(rotation).scale(scaleX, scaleY, 0); // T R S order
		
		//modelMatrix.mul(tMatrix).mul(rMatrix).mul(sMatrix);	//trs order
		modelMatrix.translate(0.6f,0f,0f).scale(scaleX,scaleY,0);
		

	}
	
	public void update(float deltaTime){
		float rotation = rotation_rate * deltaTime;
		modelMatrix.rotateZ(rotation);
		modelMatrix.translate(0.0f,0.015f,0.0f);
	}

	public void draw() {
		
		shader.enable();
		// set the attributes
		shader.setAttribute("a_position", vertexBuffer);
		shader.setUniform("u_modelMatrix", modelMatrix);
		
		shader.setAttribute("a_colour", colourBuffer);

		// draw using index buffer
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
		

	}

	/**
	 * Set the destination matrix to a translation matrix. Note the destination
	 * matrix must already be allocated.
	 * 
	 * @param tx   Offset in the x direction
	 * @param ty   Offset in the y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f translationMatrix(float tx, float ty, Matrix4f dest) {
		// clear the matrix to the identity matrix
		dest.identity();

		//     [ 1 0 0 tx ]
		// T = [ 0 1 0 ty ]
	    //     [ 0 0 0 0  ]
		//     [ 0 0 0 1  ]

		// Perform operations on only the x and y values of the T vec. 
		// Leaves the z value alone, as we are only doing 2D transformations.
		
		dest.m30(tx);
		dest.m31(ty);

		return dest;
	}

	/**
	 * Set the destination matrix to a rotation matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param angle Angle of rotation (in radians)
	 * @param dest  Destination matrix to write into
	 * @return
	 */

	public static Matrix4f rotationMatrix(float angle, Matrix4f dest) {
		
		// TODO: Your code here
		dest.identity();
		
		dest.m00((float) (Math.cos(angle)));
		dest.m10((float) (Math.sin(-angle)));
		dest.m01((float) (Math.sin(angle)));
		dest.m11((float) (Math.cos(angle)));
		return dest;
	}

	/**
	 * Set the destination matrix to a scale matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param sx   Scale factor in x direction
	 * @param sy   Scale factor in y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f scaleMatrix(float sx, float sy, Matrix4f dest) {
		dest.identity();
		//     [ sx 0 0 0 ]
		// T = [ 0 xy 0 0 ]
	    //     [ 0 0 0 0  ]
		//     [ 0 0 0 1  ]
		dest.m00(sx);
		dest.m11(sy);


		return dest;
	}

}
