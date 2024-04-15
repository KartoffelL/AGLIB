package Kartoffel.Licht.Net.packages;

import Kartoffel.Licht.Net.Package;
import Kartoffel.Licht.Res.SerializationUtils;

public class PMessage extends Package{
	
	public String message;
	
	public PMessage(String message) {
		this.message = message;
		this.type = 1;
	}


	@Override
	public byte[] serialize() {
		byte[] bytes = message.getBytes();
		byte[] b = new byte[4+bytes.length];
		System.arraycopy(SerializationUtils.from(bytes.length), 0, b, 0, 4);
		System.arraycopy(bytes, 0, b, 4, bytes.length);
		return b;
	}


	@Override
	public Package deserialize(byte[] bytes) {
		int size = SerializationUtils.toInteger(bytes);
		message = new String(bytes, 4, size);
		return this;
	}

	@Override
	public String toString() {
		return message;
	}
}
