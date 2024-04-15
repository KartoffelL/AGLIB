package Kartoffel.Licht.Net.packages;

import Kartoffel.Licht.Net.Package;

public class PUndefined extends Package{

	public byte[] data;

	@Override
	protected byte[] serialize() {
		throw new RuntimeException("Can not serialize an undefined Package!");
	}

	@Override
	public Package deserialize(byte[] bytes) {
		this.data = bytes;
		return this;
	}

}
