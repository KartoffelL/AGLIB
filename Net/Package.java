package Kartoffel.Licht.Net;

import Kartoffel.Licht.Res.SerializationUtils;

public abstract class Package {
	
	public static final int SubPackageHeaderSize = 13; //Type(long)+Size(integer)+..+tb(byte)
	
	public long type = 0;
	public boolean isCorrupt = true;
	
	public byte[] Nserialize() {
		byte[] serialized = serialize();
		byte[] b = new byte[SubPackageHeaderSize+serialized.length];
		b[b.length-1] = 60;
		System.arraycopy(serialized, 0, b, 12, serialized.length);
		System.arraycopy(SerializationUtils.from(type), 0, b, 0, 8);
		System.arraycopy(SerializationUtils.from(serialized.length), 0, b, 8, 4);
		isCorrupt = false;
		return b;
	}
	public Package Ndeserialize(byte[] data) {
		int size = SerializationUtils.toInteger(data, 8);
		type = SerializationUtils.toLong(data, 0);
		isCorrupt = true;
		if(data.length-SubPackageHeaderSize < size)
			throw new CorruptPackageException("Package smaller than expected! " + data.length + " < " + (size+SubPackageHeaderSize));
		if(data.length-SubPackageHeaderSize > size && (data.length != Harbor.PACKAGE_SIZE))
			throw new CorruptPackageException("Package bigger than expected! " + data.length + " > " + (size+SubPackageHeaderSize));
		if(data[size+SubPackageHeaderSize-1] != Harbor.TRAP_BYTE)
			throw new CorruptPackageException("Package corrupt! " + (size+SubPackageHeaderSize) + ": " + data[size+SubPackageHeaderSize] + " '" + (char)data[size+SubPackageHeaderSize] + "'");
		isCorrupt = false;
		byte[] serialized = new byte[size];
		System.arraycopy(data, SubPackageHeaderSize-1, serialized, 0, size);
		return deserialize(serialized);
	}
	
	final public boolean isFine() {
		return !isCorrupt;
	}
	
	protected abstract byte[] serialize();
	public abstract Package deserialize(byte[] bytes);

}
