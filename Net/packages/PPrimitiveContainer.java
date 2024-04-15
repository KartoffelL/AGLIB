package Kartoffel.Licht.Net.packages;

import Kartoffel.Licht.Net.Package;
import Kartoffel.Licht.Res.SerializationUtils;

public class PPrimitiveContainer extends Kartoffel.Licht.Net.Package{
	
	private Object[] primitives;
	private int size;

	public PPrimitiveContainer(Object...p) {
		this.type = 2;
		this.primitives = new Object[p.length+1];
		System.arraycopy(p, 0, this.primitives, 0, p.length);
		size = 4;
		boolean s = true;
		for(int i = 0; i < primitives.length; i++) {
			if(s)
				size++;
			byte type = getType(i);
			if(type == 1)
				size += 4;
			if(type == 2)
				size += 1;
			if(type == 3)
				size += 2;
			if(type == 4)
				size += 2;
			if(type == 5)
				size += 8;
			if(type == 6)
				size += 8;
			if(type == 7)
				size += 4;
			if(type == 8)
				size += 1;
			if(type == 9)
				size += 4+((String)primitives[i]).length();
			s = !s;
		}
	}
	
	public Object getPrimitive(int index) {
		if(index < 0)
			throw new ArrayIndexOutOfBoundsException(index);
		return primitives[index+1];
	}
	
	public void setPrimitive(int index, Object prim) {
		if(index < 0)
			throw new ArrayIndexOutOfBoundsException(index);
		primitives[index+1] = prim;
	}

	@Override
	public byte[] serialize() {
		byte[] a = new byte[size];
		System.arraycopy(SerializationUtils.from(primitives.length-1), 0, a, 0, 4);
		boolean s = true;
		int ii = 4;
		for(int i = 0; i < primitives.length; i++) {
			byte type = getType(i);
			if(s) {
				a[ii] = SerializationUtils.composeH(type, getType(i+1));
				ii++;
			}
			if(type == 1) {
				System.arraycopy(SerializationUtils.from((int)primitives[i]), 0, a, ii, 4);
				ii += 4;
			}else if(type == 2) {
				a[ii] = (byte)primitives[i];
				ii += 1;
			}else if(type == 3) {
				System.arraycopy(SerializationUtils.from((char)primitives[i]), 0, a, ii, 2);
				ii += 2;
			}else if(type == 4) {
				System.arraycopy(SerializationUtils.from((short)primitives[i]), 0, a, ii, 2);
				ii += 2;
			}else if(type == 5) {
				System.arraycopy(SerializationUtils.from((long)primitives[i]), 0, a, ii, 8);
				ii += 8;
			}else if(type == 6) {
				System.arraycopy(SerializationUtils.from((double)primitives[i]), 0, a, ii, 8);
				ii += 8;
			}else if(type == 7) {
				System.arraycopy(SerializationUtils.from((float)primitives[i]), 0, a, ii, 4);
				ii += 4;
			}else if(type == 8) {
				System.arraycopy(SerializationUtils.from((boolean)primitives[i]), 0, a, ii, 1);
				ii += 1;
			}else if(type == 9) {
				String str = (String) primitives[i];
				System.arraycopy(SerializationUtils.from(str.length()), 0, a, ii, 4);
				ii += 4;
				System.arraycopy(str.getBytes(), 0, a, ii, str.length());
				ii += str.length();
			}
			s = !s;
		}
		return a;
	}
	

	@Override
	public Package deserialize(byte[] bytes) {
		int amount = SerializationUtils.toInteger(bytes);
		primitives = new Object[amount];
		boolean s = true;
		int ii = 4;
		byte[] types = null;
		for(int i = 0; i < amount; i++) {
			if(s) {
				types = SerializationUtils.decomposeH((byte) bytes[ii]);
				ii++;
			}
			if(types[s ? 0 : 1] == 0) {
				primitives[i+0] = null;
			}else if(types[s ? 0 : 1] == 1) {
				primitives[i] = SerializationUtils.toInteger(bytes, ii);
				ii+=4;
			}else if(types[s ? 0 : 1] == 2) {
				primitives[i] = bytes[ii];
				ii+=1;
			}else if(types[s ? 0 : 1] == 3) {
				primitives[i] = SerializationUtils.toChar(bytes, ii);
				ii+=2;
			}else if(types[s ? 0 : 1] == 4) {
				primitives[i] = SerializationUtils.toShort(bytes, ii);
				ii+=2;
			}else if(types[s ? 0 : 1] == 5) {
				primitives[i] = SerializationUtils.toLong(bytes, ii);
				ii+=8;
			}else if(types[s ? 0 : 1] == 6) {
				primitives[i] = SerializationUtils.toDouble(bytes, ii);
				ii+=8;
			}else if(types[s ? 0 : 1] == 7) {
				primitives[i] = SerializationUtils.toFloat(bytes, ii);
				ii+=4;
			}else if(types[s ? 0 : 1] == 8) {
				primitives[i] = bytes[ii] == 1;
				ii++;
			}
			else if(types[s ? 0 : 1] == 9) {
				int size = SerializationUtils.toInteger(bytes, ii);
				ii += 4;
				primitives[i] = new String(bytes).substring(ii, ii+size);
				ii+=size;
			}
			s = !s;
		}
		return this;
	}
	
	private byte getType(int index) {
		if(index < 0 || index >= primitives.length)
			return 0;
		Object o = primitives[index];
		if(o == null)
			return 0;
		String name = o.getClass().getSimpleName();
		if(name.equals("Integer")) {
			return 1;
		}
		else if(name.equals("Byte")) {
			return 2;
		}
		else if(name.equals("Character")) {
			return 3;
		}
		else if(name.equals("Short")) {
			return 4;
		}
		else if(name.equals("Long")) {
			return 5;
		}
		else if(name.equals("Double")) {
			return 6;
		}
		else if(name.equals("Float")) {
			return 7;
		}
		else if(name.equals("Boolean")) {
			return 8;
		}
		else if(name.equals("String")) {
			return 9;
		}
		return 0;
	}
	
	@Override
	public String toString() {
		String s = "[";
		for(Object p : primitives)
			s += p+";";
		s = s.substring(0, s.length()-1)+"]";
		return s;
	}

}
