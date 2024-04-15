package Kartoffel.Licht.Net;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;

import Kartoffel.Licht.Net.packages.PMessage;
import Kartoffel.Licht.Net.packages.PPrimitiveContainer;
import Kartoffel.Licht.Net.packages.PUndefined;
import Kartoffel.Licht.Res.SerializationUtils;

public abstract class PackageCallback {
	
	private int chunkSize = Harbor.PACKAGE_SIZE-Harbor.FRAGMENTED_HEADER_SIZE;
	
	private HashMap<Short, byte[]> fragmented = new HashMap<>();
	private HashMap<Short, Integer> remaining = new HashMap<>();
	
	private int complete = 0;
	final public void parse(byte[] data, InetSocketAddress address) {
		try {
			if(data.length < Package.SubPackageHeaderSize)
				throw new Exception("Not an Package");
			if(Arrays.equals(Harbor.FRAGMENTED_HEADER, new byte[] {data[0], data[1], data[2], data[3]})) {
				if(data.length < 12)
					throw new Exception("Fragmented Package Header to small!");
				short ID = SerializationUtils.toShort(new byte[] {data[4], data[5]});
				short index = SerializationUtils.toShort(new byte[] {data[6], data[7]});
				int totalSize = SerializationUtils.toInteger(new byte[] {data[8], data[9], data[10], data[11]});
				if(index == 0) {
					fragmented.put(ID, new byte[totalSize]);
					remaining.put(ID, totalSize/chunkSize+1);
				}
				if(fragmented.containsKey(ID)) {
					int size = Math.min(totalSize-index*(chunkSize), chunkSize);
					remaining.put(ID, remaining.get(ID)-1);
					System.arraycopy(data, 12, fragmented.get(ID), index*(chunkSize), size);
					if((index+1)*chunkSize >= totalSize) {
						complete = remaining.remove(ID);
						parse(fragmented.remove(ID), address);
					}
				}
				return;
			}
			long packID = SerializationUtils.toLong(new byte[] {data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7]});
			Package p = null;
			if(complete != 0) {
				int a = complete;
				complete = 0;
				throw new CorruptPackageException("Fragmented Package incomplete! " + a + " missing!");
			}
			if(packID == 1)
				p = new PMessage("").Ndeserialize(data);
			else if(packID == 2)
				p = new PPrimitiveContainer().Ndeserialize(data);
			else
				p = new PUndefined().Ndeserialize(data);
			accept(p, address);
		}
		catch(Exception t) {
			accept(data, address, t);
		}
	}
	
	public void accept(byte[] data, InetSocketAddress address, Throwable cause) {};
	
	public abstract void accept(Package pack, InetSocketAddress address);

}
