package Kartoffel.Licht.Res;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class SerializationUtils {
	
	final public static ThreadLocal<ByteBuffer> byteBuffer = new ThreadLocal<ByteBuffer>();
	
	final public static byte[] serializeStrings(Charset set, String...strings) {
		byte[][] chars = new byte[strings.length][];
		for(int i = 0; i < strings.length; i++)
			chars[i] = strings[i].getBytes(set);
		int size = 0;
		for(byte[] b : chars)
			size += b.length+4;
		byte[] res = new byte[size];
		int pointer = 0;
		for(byte[] b : chars) {
			from(res, pointer, b.length);
			pointer += 4;
			System.arraycopy(b, 0, res, pointer, b.length);
			pointer += b.length;
		}
		return res;
	}
	
	final public static String combine(char seperator, String...strings) {
		String a = "";
		for(int i = 0; i < strings.length; i++)
			a += strings[i]+(i == strings.length-1 ? "" : seperator);
		return a;
	}
	final public static String combine(char seperator, List<String> strings) {
		String a = "";
		for(int i = 0; i < strings.size(); i++)
			a += strings.get(i)+(i == strings.size()-1 ? "" : seperator);
		return a;
	}
	
	final public static String[] deserializeStrings(Charset set, byte[] data) {
		int count = 0;
		int pointer = 0;
		while(pointer < data.length) {
			pointer += toInteger(data, pointer)+4;
			count++;
		}
		String[] res = new String[count];
		pointer = 0;
		count = 0;
		while(pointer < data.length) {
			int size = toInteger(data, pointer);
			pointer += 4;
			res[count] = new String(data, pointer, size, set);
			pointer += size;
			count++;
		}
		return res;
	}
	
	
	
	final public static byte compose(boolean...bools) {
		BitSet bits = new BitSet(bools.length);
	    for (int i = 0; i < bools.length; i++) {
	        if (bools[i])
	            bits.set(i);
	    }

	    byte[] bytes = bits.toByteArray();
	    if (bytes.length * 8 >= bools.length) {
	        return bytes[0];
	    } else {
	        return Arrays.copyOf(bytes, bools.length / 8 + (bools.length % 8 == 0 ? 0 : 1))[0];
	    }
	}
	
	final public static boolean[] decompose(byte val) {
		BitSet s = BitSet.valueOf(new byte[] {val});
		return new boolean[] {s.get(0), s.get(1), s.get(2), s.get(3), s.get(4), s.get(5), s.get(6), s.get(7)};
	}
	
	final public static byte[] decomposeH(byte val) {
		boolean[] a = decompose(val);
		return new byte[] {compose(a[0], a[1], a[2], a[3], false, false, false, false), compose(a[4], a[5], a[6], a[7], false, false, false, false)};
	}
	
	final public static byte composeH(byte hexA, byte hexB) {
		BitSet a = BitSet.valueOf(new byte[] {hexA});
		BitSet b = BitSet.valueOf(new byte[] {hexB});
		return compose(a.get(0), a.get(1), a.get(2), a.get(3), b.get(0), b.get(1), b.get(2), b.get(3));
	}
	
	final public static byte[] from(int value) {
		return from(new byte[4], 0, value);
	}
	final public static byte[] from(short value) {
		return from(new byte[2], 0, value);
	}
	final public static byte[] from(long value) {
		return from(new byte[8], 0, value);
	}
	final public static byte[] from(float value) {
		return from(new byte[4], 0, value);
	}
	final public static byte[] from(double value) {
		return from(new byte[8], 0, value);
	}
	final public static byte[] from(boolean value) {
	    return new byte[] {(byte) (value ? 1 : 0)};
	}
	final public static byte[] from(char value) {
		return from(new byte[2], 0, value);
	}
	
	final public static byte[] from(byte[] bytes, int off, int value) {
		check();
	    byteBuffer.get().putInt(value);
	    bytes[0+off] = byteBuffer.get().get(0);
	    bytes[1+off] = byteBuffer.get().get(1);
	    bytes[2+off] = byteBuffer.get().get(2);
	    bytes[3+off] = byteBuffer.get().get(3);
	    return bytes;
	}
	final public static byte[] from(byte[] bytes, int off, short value) {
		check();
	    byteBuffer.get().putShort(value);
	    bytes[0+off] = byteBuffer.get().get(0);
	    bytes[1+off] = byteBuffer.get().get(1);
	    return bytes;
	}
	final public static byte[] from(byte[] bytes, int off, long value) {
		check();
	    byteBuffer.get().putLong(value);
	    bytes[0+off] = byteBuffer.get().get(0);
	    bytes[1+off] = byteBuffer.get().get(1);
	    bytes[2+off] = byteBuffer.get().get(2);
	    bytes[3+off] = byteBuffer.get().get(3);
	    bytes[4+off] = byteBuffer.get().get(4);
	    bytes[5+off] = byteBuffer.get().get(5);
	    bytes[6+off] = byteBuffer.get().get(6);
	    bytes[7+off] = byteBuffer.get().get(7);
	    return bytes;
	}
	final public static byte[] from(byte[] bytes, int off, float value) {
		check();
	    byteBuffer.get().putFloat(value);
	    bytes[0+off] = byteBuffer.get().get(0);
	    bytes[1+off] = byteBuffer.get().get(1);
	    bytes[2+off] = byteBuffer.get().get(2);
	    bytes[3+off] = byteBuffer.get().get(3);
	    return bytes;
	}
	final public static byte[] from(byte[] bytes, int off, double value) {
		check();
	    byteBuffer.get().putDouble(value);
	    bytes[0+off] = byteBuffer.get().get(0);
	    bytes[1+off] = byteBuffer.get().get(1);
	    bytes[2+off] = byteBuffer.get().get(2);
	    bytes[3+off] = byteBuffer.get().get(3);
	    bytes[4+off] = byteBuffer.get().get(4);
	    bytes[5+off] = byteBuffer.get().get(5);
	    bytes[6+off] = byteBuffer.get().get(6);
	    bytes[7+off] = byteBuffer.get().get(7);
	    return bytes;
	}
	final public static byte[] from(byte[] bytes, int off, boolean value) {
		bytes[off] = (byte) (value ? 1 : 0);
	    return bytes;
	}
	final public static byte[] from(byte[] bytes, int off, char value) {
		check();
	    byteBuffer.get().putChar(value);
	    bytes[0+off] = byteBuffer.get().get(0);
	    bytes[1+off] = byteBuffer.get().get(1);
	    return bytes;
	}
	
	final public static int toInteger(byte[] bytes) {
		return toInteger(bytes, 0);
	}
	final public static short toShort(byte[] bytes) {
		check();
	   return toShort(bytes, 0);
	}
	final public static long toLong(byte[] bytes) {
		check();
	    return toLong(bytes, 0);
	}
	final public static float toFloat(byte[] bytes) {
		check();
	    return toFloat(bytes, 0);
	}
	final public static double toDouble(byte[] bytes) {
		check();
	   return toDouble(bytes, 0);
	}
	final public static boolean toBoolean(byte[] bytes) {
	    return bytes[0] == 1;
	}
	final public static char toChar(byte[] bytes) {
		check();
		return toChar(bytes, 0);
	}
	
	final public static int toInteger(byte[] bytes, int start) {
		check();
		byteBuffer.get().put(0, bytes[start+0]);
		byteBuffer.get().put(1, bytes[start+1]);
		byteBuffer.get().put(2, bytes[start+2]);
		byteBuffer.get().put(3, bytes[start+3]);
	    return byteBuffer.get().getInt();
	}
	final public static short toShort(byte[] bytes, int start) {
		check();
		byteBuffer.get().put(0, bytes[start+0]);
		byteBuffer.get().put(1, bytes[start+1]);
	    return byteBuffer.get().getShort();
	}
	final public static long toLong(byte[] bytes, int start) {
		check();
		byteBuffer.get().put(0, bytes[start+0]);
		byteBuffer.get().put(1, bytes[start+1]);
		byteBuffer.get().put(2, bytes[start+2]);
		byteBuffer.get().put(3, bytes[start+3]);
		byteBuffer.get().put(4, bytes[start+4]);
		byteBuffer.get().put(5, bytes[start+5]);
		byteBuffer.get().put(6, bytes[start+6]);
		byteBuffer.get().put(7, bytes[start+7]);
	    return byteBuffer.get().getLong();
	}
	final public static float toFloat(byte[] bytes, int start) {
		check();
		byteBuffer.get().put(0, bytes[start+0]);
		byteBuffer.get().put(1, bytes[start+1]);
		byteBuffer.get().put(2, bytes[start+2]);
		byteBuffer.get().put(3, bytes[start+3]);
	    return byteBuffer.get().getFloat();
	}
	final public static double toDouble(byte[] bytes, int start) {
		check();
		byteBuffer.get().put(0, bytes[start+0]);
		byteBuffer.get().put(1, bytes[start+1]);
		byteBuffer.get().put(2, bytes[start+2]);
		byteBuffer.get().put(3, bytes[start+3]);
		byteBuffer.get().put(4, bytes[start+4]);
		byteBuffer.get().put(5, bytes[start+5]);
		byteBuffer.get().put(6, bytes[start+6]);
		byteBuffer.get().put(7, bytes[start+7]);
	    return byteBuffer.get().getDouble();
	}
	final public static boolean toBoolean(byte[] bytes, int start) {
	    return bytes[start] == 1;
	}
	final public static char toChar(byte[] bytes, int start) {
		check();
		byteBuffer.get().put(0, bytes[start+0]);
		byteBuffer.get().put(1, bytes[start+1]);
	    return byteBuffer.get().getChar();
	}
	
	//Extra
	final public static int toInteger3(byte[] bytes, int start) {
		check();
		byteBuffer.get().put(0, bytes[start+0]);
		byteBuffer.get().put(1, bytes[start+1]);
		byteBuffer.get().put(2, bytes[start+2]);
		byteBuffer.get().put(3, (byte) 0);
	    return byteBuffer.get().getInt();
	}
	
	final private static void check() {
		if(byteBuffer.get() == null)
			byteBuffer.set(ByteBuffer.wrap(new byte[8]));
		byteBuffer.get().position(0);
	}

}
