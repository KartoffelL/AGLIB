package Kartoffel.Licht.Net.callbacks;

import java.net.InetSocketAddress;

import Kartoffel.Licht.Net.CorruptPackageException;
import Kartoffel.Licht.Net.Package;
import Kartoffel.Licht.Net.PackageCallback;

public class PrintPacketCallback extends PackageCallback{

	@Override
	public void accept(byte[] data, InetSocketAddress address, Throwable t) {
		System.err.println("[" + address.toString() + "]: Error:" + t.getMessage());
		if(!(t instanceof CorruptPackageException)) {
			t.printStackTrace();
			System.err.println("Data: '" + new String(data) + "'");
		}
	}

	@Override
	public void accept(Package pack, InetSocketAddress address) {
		System.out.println("[" + address.toString() + "]: " + "'"+pack.toString()+"'");
	}


}
