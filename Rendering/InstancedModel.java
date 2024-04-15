package Kartoffel.Licht.Rendering;

import static Kartoffel.Licht.Tools.Tools.ins;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL41;

import Kartoffel.Licht.Rendering.Shapes.ShapeData;
import Kartoffel.Licht.Tools.UUID;

public class InstancedModel extends Model{
	
	/**
	 * Every time a model gets drawn, this will increase by the amount of instances that got drawn
	 */
	volatile public static int InstancedModelDrawings = 0;
	/**
	 * Every time a model gets drawn, this will increase by one
	 */
	volatile public static int InstancedModelDrawCalls = 0;
	
	//Internal stuff###############
	private int InstanceDataLenght = 0;		//Pro float
	private int MaxInstances = 1000;
	
	private int InstanceDrawCount = 1;
	
	private int instance;
	
	private float[] data;
	private List<Integer> instances;
	private int[] attribSizes;
	private int ATTRIBUTE_DATA_TYPE;
	
	//###########################
	/**
	 * Custom Attribute data type
	 */
	public static final byte ADT_CUSTOM = 0;
	/**
	 * Attribute data type with Position XY
	 */
	public static final byte ADT_XY = 1;	
	/**
	 * Attribute data type with Position XYZ
	 */
	public static final byte ADT_XYZ = 2;
	/**
	 * Attribute data type with Position XY and Rotation Roll
	 */
	public static final byte ADT_XY_R = 3;
	/**
	 * Attribute data type with Position XYZ and Rotation Pitch Yaw Roll
	 */
	public static final byte ADT_XYZ_PYR = 4;
	/**
	 * Attribute data type with position XYZ, Rotation Pitch Yaw Roll and Scale Width Height Depth
	 */
	public static final byte ADT_XYZ_PYR_WHD = 5;
	/**
	 * Attribute data type with transformation matrix
	 */
	public static final byte ADT_TRANSMATRIX = 6;
	/**
	 * Attribute data type with a ID and Position XYZ
	 */
	public static final byte ADT_ID_XYZ = 7;

	
	/**
	 * 
	 * @param shape the Model shape.
	 * @param attributes the attributes of the instances
	 * @param maxInstances the maximum amount of instances
	 
	 */
	public InstancedModel(ShapeData shape, int maxInstances, int... attributes) {
		super(shape);
		this.main(maxInstances, attributes);
	}
	
	/**
	 * 
	 * @param shape
	 * @param maxInstances the maximum amount of instances
	 * @param attributes
	 */
	public InstancedModel(ShapeData shape, int maxInstances, byte adt) {
		super(shape);
		ins("Loading instanced model:");
		int[] attributes = new int[0];
		if(adt == ADT_XY)
			attributes = new int[] {2};
		if(adt == ADT_XY_R)
			attributes = new int[] {2, 1};
		if(adt == ADT_XYZ)
			attributes = new int[] {3};
		if(adt == ADT_ID_XYZ)
			attributes = new int[] {1, 3};
		if(adt == ADT_XYZ_PYR)
			attributes = new int[] {3, 3};
		if(adt == ADT_XYZ_PYR_WHD)
			attributes = new int[] {3, 3, 3};
		if(adt == ADT_TRANSMATRIX)
			attributes = new int[] {4, 4, 4, 4};
		this.main(maxInstances, attributes);
	}
	
	
	private void main(int maxInstances, int... attributes) {
		ins("Loading instanced model:");
		//init
		this.instances = new ArrayList<Integer>();
		ATTRIBUTE_DATA_TYPE = InstancedModel.ADT_CUSTOM;
		MaxInstances = maxInstances;
		this.InstanceDrawCount = maxInstances;
		attribSizes = attributes;
		//#Counting total float number per instance
		for(int i = 0; i < attributes.length; i++)
			InstanceDataLenght += attributes[i];
		//#Creating Data elements for Instancing
		//OpenGL Buffer
		instance = genVBO(MaxInstances*InstanceDataLenght);
		glBindBuffer(GL_ARRAY_BUFFER, instance);
		//cache
		data = new float[MaxInstances*InstanceDataLenght];
		//System buffer
		//#Pre-creating instances
		for(int i = 0; i < maxInstances; i++)
			instances.add(UUID.createI());
		
		ins("Generated " + attributes.length + " Attributes with float lenght of " + InstanceDataLenght + ", Total Buffer size of " + maxInstances*InstanceDataLenght + ", maximal instances: " + maxInstances);
		
		bindNullBuffer();
	
	}
	
	
	
	
	
	private int genVBO(int floatCount) {
		int instance = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, instance);
		glBufferData(GL_ARRAY_BUFFER, floatCount * 4, GL_STATIC_DRAW);
		return instance;
	}
	public void bindNullBuffer() {
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	public void bindBuffer() {
		glBindBuffer(GL_ARRAY_BUFFER, instance);
	}
		
	/**
	 * Adds a new Attribute for every instance. May corrupt already set data!<br>
	 * The addition of more attributes can cause problems because of the VBO size being to small.<br>
	 * Instead add Attributes on initialization of the model!
	 * <br>
	 * 
	 * @param vbo The VBO to which the data shall be uploaded. Request ID with Model.getBuffer(String name).<br>
	 * for normal custom instance data use the default instance vbo with InstancedModel.getDefaultInstanceDataVBO()<br><br>
	 * @param attribute the attribute number. should increase with every new attribute<br><br>
	 * @param dataSize the size of the data(1-4).<br><br>
	 * @param instancedDataLenght the length of the data<br><br>
	 * @param offset the offset of the data<br><br>
	 * 
	 * Parameters from the OpenGL Function {@link GL20#glVertexAttribPointer(int index,int size,int type,boolean normalized,int stride,long pointer)}<br>
	 * <br>
     * @param index      the index of the generic vertex attribute to be modified
     * @param size       the number of values per vertex that are stored in the array. The initial value is 4. One of:<br><table><tr><td>1</td><td>2</td><td>3</td><td>4</td><td>{@link GL12#GL_BGRA BGRA}</td></tr></table>
     * @param type       the data type of each component in the array. The initial value is GL_FLOAT. One of:<br><table><tr><td>{@link GL11#GL_BYTE BYTE}</td><td>{@link GL11#GL_UNSIGNED_BYTE UNSIGNED_BYTE}</td><td>{@link GL11#GL_SHORT SHORT}</td><td>{@link GL11#GL_UNSIGNED_SHORT UNSIGNED_SHORT}</td><td>{@link GL11#GL_INT INT}</td><td>{@link GL11#GL_UNSIGNED_INT UNSIGNED_INT}</td><td>{@link GL30#GL_HALF_FLOAT HALF_FLOAT}</td><td>{@link GL11#GL_FLOAT FLOAT}</td></tr><tr><td>{@link GL11#GL_DOUBLE DOUBLE}</td><td>{@link GL12#GL_UNSIGNED_INT_2_10_10_10_REV UNSIGNED_INT_2_10_10_10_REV}</td><td>{@link GL33#GL_INT_2_10_10_10_REV INT_2_10_10_10_REV}</td><td>{@link GL41#GL_FIXED FIXED}</td></tr></table>
     * @param normalized whether fixed-point data values should be normalized or converted directly as fixed-point values when they are accessed
     * @param stride     the byte offset between consecutive generic vertex attributes. If stride is 0, the generic vertex attributes are understood to be tightly packed in
     *                   the array. The initial value is 0.
     * @param pointer    the vertex attribute data or the offset of the first component of the first generic vertex attribute in the array in the data store of the buffer
     *                   currently bound to the {@link GL15#GL_ARRAY_BUFFER ARRAY_BUFFER} target. The initial value is 0.
     * 
     * @see <a target="_blank" href="http://docs.gl/gl4/glVertexAttribPointer">Reference Page</a>
	 */
	public void addInstancedAttribute(int vbo, int attribute, int dataSize, int dataByteSize, int offset) {
		GL15.glBindBuffer(GL_ARRAY_BUFFER, vbo);
		GL30.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, dataByteSize, offset);
		GL33.glVertexAttribDivisor(attribute, 1);
		this.bindNullBuffer();
		//ins("---Added Attribute with float count of " + dataSize + " on place " + offset + "(Attrib: " + attribute + "). Total Instance Data size is now: " + dataByteSize);
		
	}
	/**
	 * Utils for adding extra space for more Instances.<br>
	 * Sets the maximun amount of instances
	 * @param instances
	 * @return the ids of all added instances
	 */
	public int[] setMaxInstances(int instances) {
		if(data == null)
			throw new IllegalStateException("This Model got finalized!");
		this.MaxInstances = instances;
		this.InstanceDrawCount = instances;
		data = resizeFloatArray(data, InstanceDataLenght * instances);
		glBindBuffer(GL_ARRAY_BUFFER, instance);
		glBufferData(GL_ARRAY_BUFFER, InstanceDataLenght * instances * 4, GL_DYNAMIC_DRAW);
		for(int i = 0; i < this.instances.size()-instances; i++) {
			this.instances.remove(instances);
		}
		if(instances-this.instances.size() > 0) {
			int[] ids = new int[instances-this.instances.size()];
			for(int i = 0; i < instances-this.instances.size(); i++) {
				int id = UUID.createI();
				this.instances.add(id);
				ids[i] = id;
			}
			return ids;
		}
		bindNullBuffer();
		return new int[0];
	}
	/**
	 * removes the index. entry of the float array.
	 * (Remove one from Float Array)
	 * @param arr
	 * @param index
	 * @return
	 */
	private float[] rem1fFA(float[] arr, int index) {
		float[] res = new float[arr.length-1];
		int resInd = 0;
		for(int i = 0; i < arr.length-1; i++) {
			res[resInd] = arr[i];
			if(i != index)
				resInd++;
		}
		return res;
	}
	/**
	 * Removes a Instance from the list. this will resize the buffer
	 * @param index
	 */
	private void remInstance(int index) {
		if(MaxInstances-1 < 0)
			return;
		this.MaxInstances--;
		this.InstanceDrawCount--;
		for(int i = 0; i < InstanceDataLenght; i++) {
			data = rem1fFA(data, index*InstanceDataLenght);
		}
		glBindBuffer(GL_ARRAY_BUFFER, instance);
		glBufferData(GL_ARRAY_BUFFER, InstanceDataLenght * MaxInstances * 4, GL_DYNAMIC_DRAW);
		this.bindNullBuffer();
	}
	/**
	 * resizes a float array, cuts of excessive data
	 * @param arr
	 * @param ds
	 * @return
	 */
	private float[] resizeFloatArray(float[] arr, int ds) {
		float[] res = new float[ds];
		for(int i = 0; i < Math.min(res.length, arr.length); i++)
			res[i] = arr[i];
		return res;
	}
	/**
	 * Utils for removing individual instances from space.<br>
	 * removes the instance and compacts the buffer
	 */
	public void removeInstance(int index) {
		if(data == null)
			throw new IllegalStateException("This Model got finalized!");
		remInstance(index);
	}
	/**
	 * Utils for removing individual instances from space.<br>
	 * removes the first instance with this id.
	 * @param data
	 */
	public void removeInstanceID(int id) {
		if(data == null)
			throw new IllegalStateException("This Model got finalized!");
		int ida = instances.indexOf(id);
		if(ida != -1) {
			instances.remove(ida);
			remInstance(ida);
		}
	}
	/**
	 * Utils for removing individual instances from space.<br>
	 * removes all instances using single calls. a stupid way, isint it?
	 * 
	 */
	public void removeAll() {
		if(data == null)
			throw new IllegalStateException("This Model got finalized!");
		for(int i = 0; i < MaxInstances; i++) {
			remInstance(i);
		}
	}
	/**
	 * Utils for adding extra space for more Instances.<br>
	 * Adds one Instance to the maximal amount
	 * 
	 * @return the ID of this instance. delete with {@link InstancedModel#removeInstanceID(int)}
	 */
	public int addInstance() {
		if(data == null)
			throw new IllegalStateException("This Model got finalized!");
		return setMaxInstances(this.MaxInstances+1)[0];	//Length can only be 1
	}
	/**
	 * Utils for adding extra space for more Instances.<br>
	 * Adds one Instance to the maximal amount<br>
	 * Sets the data to the new instance
	 * 
	 * @return the ID of this instance. delete with {@link InstancedModel#removeInstanceID(int)}
	 */
	public int addInstance(float[] data) {
		if(data == null)
			throw new IllegalStateException("This Model got finalized!");
		int id = setMaxInstances(this.MaxInstances+1)[0];
		setInstanceDataID(id, data);
		return id;	//Length can only be 1
	}
	/**
	 * Utils for adding extra space for more Instances.<br>
	 * Adds one Instance to the maximal amount<br>
	 * Sets the Matrix to the new instance
	 * 
	 * @return the ID of this instance. delete with {@link InstancedModel#removeInstanceID(int)}
	 */
	public int addInstance(Matrix4f data) {
		if(data == null)
			throw new IllegalStateException("This Model got finalized!");
		int id = setMaxInstances(this.MaxInstances+1)[0];
		setInstanceDataID(id, data.get(new float[16]));
		return id;	//Length can only be 1
	}
	/**
	 * Utils for adding extra space for more Instances.<br>
	 * Adds inst amount of instance(s) to the maximal amount
	 */
	public int[] addInstance(int inst) {
		if(data == null)
			throw new IllegalStateException("This Model got finalized!");
		return setMaxInstances(this.MaxInstances+inst);
	}
	/**
	 * Utils for removing extra space for less Instances.<br>
	 * Removes 1 amount of instance from the maximal amount
	 */
	public void subInstance() {
		if(data == null)
			throw new IllegalStateException("This Model got finalized!");
		if(this.MaxInstances-1 <= 0)
			return;
		setMaxInstances(this.MaxInstances-1);
		this.instances.remove(this.instances.size()-1);
	}
	/**
	 * Utils for removing extra space for less Instances.<br>
	 * Removes inst amount of instance(s) from the maximal amount
	 */
	public void subInstance(int inst) {
		if(data == null)
			throw new IllegalStateException("This Model got finalized!");
		if(this.MaxInstances-inst <= 0)
			return;
		setMaxInstances(this.MaxInstances-inst);
		for(int i = 0; i < inst; i++)
			this.instances.remove(this.instances.size()-inst);
	}
	/*/**Adds a new Attribute for every instance. May corrupt already set data!<br>
	 * The addition of more attributes can cause problems because of the VBO size being to small.<br>
	 * Instead add Attributes on initialization of the model!
	 * 
	 * 
	 * @param dataSize the datasize(1-4)
	 */
	/*private void addInstancedAttribute(int dataSize) {
		GL15.glBindBuffer(GL_ARRAY_BUFFER, getDefaultInstanceDataVBO());
		GL20.glVertexAttribPointer(Attributes, dataSize, GL11.GL_FLOAT, false, dataSize*4, InstanceDataLenght*4);
		GL33.glVertexAttribDivisor(Attributes, 1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		ins("Added Attribute with float count of " + dataSize + " on place " + InstanceDataLenght);
		Attributes++;
	}*/
	/**
	 * Replaces the complete VBO with the floatbuffer
	 * 
	 * @param vbo
	 * @param data
	 * @param buffer
	 */
	public void updateVBO(int vbo, float[] data) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER , data.length * 4, GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, data);
		bindNullBuffer();
	}
	/**
	 * Replaces the complete VBO with the floatbuffer
	 * 
	 * @param vbo
	 * @param data
	 * @param buffer
	 */
	public void updateVBOSub(int vbo, float[] data, long offset) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset*4, data);
		bindNullBuffer();
	}
	
	
	/**
	 * returns all the attributes of the instance
	 * 
	 * @param instance
	 * @return
	 */
	public float[] getInstanceData(int instance) {
		if(data == null)
			throw new IllegalStateException("This Model got finalized!");
		float[] f = new float[InstanceDataLenght];
		for(int i = 0; i < InstanceDataLenght; i++) {
			f[i] = data[i + (InstanceDataLenght*instance)];
		}
		return f;
		
	}
	/**
	 * returns all the attributes of the first instance with the given ID
	 * @param id
	 * @return
	 */
	public float[] getInstanceDataID(int id) {
		if(data == null)
			throw new IllegalStateException("This Model got finalized!");
		int ida = this.instances.indexOf(id);
		if(ida == -1)
			return new float[0];
		return getInstanceData(ida);
	}
	/**
	 * Sets all the data of the given instance to the given float array
	 * 
	 * @param instance
	 * @param fl
	 */
	public void setInstanceData(int instance, float[] fl) {
		if(data == null)
			throw new IllegalStateException("This Model got finalized!");
		if(instance > MaxInstances)
			throw new IndexOutOfBoundsException("Cant set instance data " + instance + "because it doesnt exist: Max is " + MaxInstances);
		if(fl.length > InstanceDataLenght)
			throw new IndexOutOfBoundsException("Cant set instance data because it is too big: " + fl.length + " (>" + InstanceDataLenght + ")");
		
		for(int i = 0; i < fl.length; i++) {
			data[i + (InstanceDataLenght*instance)] = fl[i];
		}
		
	}
	/**
	 * Sets all the data of the first given instance with the given ID to the given float array<br>
	 * 
	 * <i>a lot of 'given', huh</i>
	 * @param id
	 * @return
	 */
	public void setInstanceDataID(int id, float[] fl) {
		if(data == null)
			throw new IllegalStateException("This Model got finalized!");
		int ida = this.instances.indexOf(id);
		if(ida == -1)
			return;
		setInstanceData(ida, fl);
	}
	/**
	 * Returns the Attribute data from the instance
	 * 
	 * @param instance
	 * @param attrib
	 * @return
	 */
	public float[] getInstanceAttribData(int instance, int attrib) {
		if(data == null)
			throw new IllegalStateException("This Model got finalized!");
		if(instance > MaxInstances)
			throw new IndexOutOfBoundsException("Cant get instance data " + instance + "because it doesnt exist: Max is " + MaxInstances);
		if(attrib > attribSizes.length)
			throw new IndexOutOfBoundsException("There is no Attribute Nr." + attrib);
		float[] d = getInstanceData(instance);
		float[] res = new float[attribSizes[attrib]];
		int off = 0;
		for(int i = 0; i < attrib; i++)
			off += attribSizes[i];
		for(int i = 0; i < attribSizes[attrib]; i++) {
			res[i] = d[off+i];
		}
		return res;
		
	}
	/**
	 * Returns the Attribute data from the instance of the given ID
	 * 
	 * @param instance
	 * @param attrib
	 * @return
	 */
	public float[] getInstanceAttribDataID(int id, int attrib) {
		if(data == null)
			throw new IllegalStateException("This Model got finalized!");
		int ida = this.instances.indexOf(id);
		if(ida == -1)
			return null;
		return getInstanceAttribData(ida, attrib);
		
	}
	/**
	 * Sets the Attribute data of the instance
	 * 
	 * @param instance
	 * @param attrib
	 * @param data
	 */
	public void setInstanceAttribData(int instance, int attrib, float[] data) {
		if(data == null)
			throw new IllegalStateException("This Model got finalized!");
		if(instance > MaxInstances)
			throw new IndexOutOfBoundsException("Cant set instance data " + instance + "because it doesnt exist: Max is " + MaxInstances);
		if(attrib > attribSizes.length-1)
			throw new IndexOutOfBoundsException("Cant set Attribute " + attrib + " because it doesnt exist: Max is " + attribSizes.length);
		if(data.length > attribSizes[attrib])
			throw new IndexOutOfBoundsException("Cant put " + data.length + "-float data into " + attribSizes[attrib] + "-float attribute");
		if(attrib > attribSizes.length)
			throw new IndexOutOfBoundsException("There is no Attribute Nr." + attrib);
		
		float[] d = getInstanceData(instance);
		int off = 0;
		for(int i = 0; i < attrib; i++)
			off += attribSizes[i];
		for(int i = 0; i < data.length; i++) {
			d[off+i] = data[i];
		}
		setInstanceData(instance, d);
		
	}
	/**
	 * Sets the Attribute data of the first given instance with the given ID to the given float array<br>
	 * 
	 * <i>a lot of 'given', huh</i>
	 * @param id the id of the instance
	 * @param attrib the attribute number
	 * @param fl the data to set
	 * @return
	 */
	public void setInstanceAttribDataID(int id, int attrib, float[] fl) {
		int ida = this.instances.indexOf(id);
		if(ida == -1)
			return;
		setInstanceAttribData(ida, attrib, fl);
	}
	/**
	 * Sets the Matrix of the given instance index
	 * @param instance the index of the instance
	 * @param mat the Matrix to set
	 */
	public void setInstanceAttribMatrix(int instance, int attrib, Matrix4f mat) {
		float[] data = new float[4];
		//For each column
		for(int i = 0; i < 4; i++) {
			//Gets data from entire column
			for(int l = 0; l < 4; l++) {
				data[l] = mat.get(i, l);
			}
			setInstanceAttribData(instance, attrib+i, data);
		}
	}
	/**
	 * Sets the Matrix of the given instance with the given ID, by
	 * 
	 * @param ID the ID of the instance
	 * @param mat the Matrix to set
	 */
	public void setInstanceAttribMatrixID(int ID, int attrib, Matrix4f mat) {
		float[] data = new float[4];
		//For each column
		for(int i = 0; i < 4; i++) {
			//Gets data from entire column
			for(int l = 0; l < 4; l++) {
				data[l] = mat.get(l, i);
			}
			setInstanceAttribDataID(ID, attrib+i, data);
		}
	}
	/**
	 * Sets the Matrix of the given instance index
	 * @param instance the index of the instance
	 * @param mat the Matrix to set
	 */
	public Matrix4f getInstanceAttribMatrix(int instance, int attrib, Matrix4f mat) {
		float[] data = new float[4];
		//For each column
		for(int i = 0; i < 4; i++) {
			//Gets data from entire column
			data = getInstanceAttribData(instance, attrib+i);
			for(int l = 0; l < 4; l++) {
				mat.set(i, l, data[l]);
			}
		}
		return mat;
	}
	/**
	 * Sets the Matrix of the given instance index
	 * @param instance the index of the instance
	 * @param mat the Matrix to set
	 */
	public Matrix4f getInstanceAttribMatrixID(int ID, int attrib, Matrix4f mat) {
		float[] data = new float[4];
		//For each column
		for(int i = 0; i < 4; i++) {
			//Gets data from entire column
			data = getInstanceAttribDataID(ID, attrib+i);
			for(int l = 0; l < 4; l++) {
				mat.set(i, l, data[l]);
			}
		}
		return mat;
	}
	/**
	 * Updates all the data. Can have quite a performance impact.<br>
	 * Has to be called whenever the buffer is modified
	 * 
	 */
	public void updateAllData() {
		if(data == null)
			throw new IllegalStateException("This Model got finalized! No need to update VBO anymore");
		updateVBO(getDefaultInstanceDataVBO(), data);
	}
	
	public void updateInstance(int index) {
		if(data == null)
			throw new IllegalStateException("This Model got finalized! No need to update VBO anymore");
		int i = InstanceDataLenght*index;
		float[] d = new float[InstanceDataLenght];
		System.arraycopy(data, i, d, 0, InstanceDataLenght);
		updateVBOSub(getDefaultInstanceDataVBO(), d, i);
	}

	
	public void updateInstanceID(int ID) {
		throw new RuntimeException("Unfinished");
//		if(data == null)
//			throw new IllegalStateException("This Model got finalized! No need to update VBO anymore");
////		int index = getInstanceDataID(index)
//		updateVBO(getDefaultInstanceDataVBO(), data);
	}
	/**
	 * Updates all the data and finalizes the Model. It is then no longer possible to change the data<br>
	 *  Can have quite a performance impact.
	 * 
	 */
	public void set() {
		if(data == null)
			return;
		updateVBO(getDefaultInstanceDataVBO(), data);
		data = null;
		instances = null;
	}
	
	@Override
	public void render() {
		render(InstanceDrawCount);
	}
	
	
	
	/**
	 * renders the scene
	 * 
	 * @param instances
	 */
	private int i;
	public void render(int instances) {
		InstancedModelDrawings += instances;
		InstancedModelDrawCalls++;
		
		if(isFace_Culling_enabled() && !FORCE_DISABLE_FACE_CULLING)
			glEnable(GL_CULL_FACE);
		
		for(i = 0; i < DEFAULT_ATTRIBUTE_COUNT+attribSizes.length; i++)
			glEnableVertexAttribArray(i);
		
		glBindBuffer(GL_ARRAY_BUFFER, get("VERTEX"));
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
				
		glBindBuffer(GL_ARRAY_BUFFER, get("TEXTURE"));
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
				
		glBindBuffer(GL_ARRAY_BUFFER, get("NORMAL"));
		glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
				
		glBindBuffer(GL_ARRAY_BUFFER, get("MATERIAL"));
		glVertexAttribPointer(3, 1, GL_UNSIGNED_INT, false, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, get("A_BONES"));
		GL33.glVertexAttribPointer(4, 4, GL_UNSIGNED_INT, false, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, get("A_WEIGHTS"));
		glVertexAttribPointer(5, 4, GL_FLOAT, false, 0, 0);
				
		glBindBuffer(GL_ARRAY_BUFFER, getDefaultInstanceDataVBO());
		int boff = 0;
		for(int i = 0; i < attribSizes.length; i++) {
			int size = attribSizes[i]*4;
			addInstancedAttribute(instance, i+DEFAULT_ATTRIBUTE_COUNT, attribSizes[i], InstanceDataLenght*4, boff);
			boff += size;
		}
		
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, get("INDICES"));
		
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, FORCE_POLYGON_MODE != 0 ? FORCE_POLYGON_MODE : FILL_MODE);
		glDrawElementsInstanced(DRAW_TYPE, getElementDrawCount(), GL_UNSIGNED_INT, offset, instances);
				
		for(i = 0; i < DEFAULT_ATTRIBUTE_COUNT+attribSizes.length; i++)
			glDisableVertexAttribArray(i);
		
		bindNullBuffer();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		
		glDisable(GL_CULL_FACE);
	}
	
//	public void addInstancedAttribute(int vbo, int attribute, int dataSize, int dataByteSize, int offset) {
//		GL15.glBindBuffer(GL_ARRAY_BUFFER, vbo);
//		GL30.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, dataByteSize, offset);
//		GL33.glVertexAttribDivisor(attribute, 1);
//		this.bindNullBuffer();
//		//ins("---Added Attribute with float count of " + dataSize + " on place " + offset + "(Attrib: " + attribute + "). Total Instance Data size is now: " + dataByteSize);
//		
//	}
	
	public void free() {
		super.free();
		this.data = null;
	}
	
	public int getDefaultInstanceDataVBO() {
		return instance;
	}
	public int getATTRIBUTE_DATA_TYPE() {
		return ATTRIBUTE_DATA_TYPE;
	}
	
	public void setInstanceDrawCount(int drawCount) {
		this.InstanceDrawCount = drawCount;
	}
	public int getInstanceCount() {
		return InstanceDrawCount;
	}
	public float[] getData() {
		return data;
	}
}

