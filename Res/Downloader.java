package Kartoffel.Licht.Res;

import java.io.File;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.jme3.bullet.util.NativeLibrary;
import com.jme3.system.NativeLibraryLoader;

import Kartoffel.Licht.Tools.Logger;
import Kartoffel.Licht.Tools.Tools;

public class Downloader {

	public static void downloadNativeLibrary_JBULLET(String version, String OS, int bit, boolean release, boolean doublePer, boolean multithreading) throws Exception {
		String fileType = OS.startsWith("Windows") ? ".dll" : OS.startsWith("MacOSX") ? ".dylib" : ".so";
		String flavor = (doublePer?"Dp":"Sp")+(multithreading?"Mt":"");
		String buildType = (release?"Release":"Debug");
		String name = OS+bit+buildType+flavor+"_bulletjme"+fileType;
		if(!Cache.getFile("LIBBULLETJME/"+version+"/"+name).exists()) {
			URL l = new URL("https://github.com/stephengold/Libbulletjme/releases/download/"+version+"/"+name);
			Cache.writeToCache(l.openStream().readAllBytes(), "LIBBULLETJME/"+version+"/"+name);
			Logger.log("Downloaded '"+l+"' succesfully!");
		}
		NativeLibraryLoader.loadLibbulletjme(true, Cache.getFile("LIBBULLETJME/"+version), buildType, flavor);
		NativeLibrary.setStartupMessageEnabled(false);
	}
	
	public static void downloadNativeLibrary_VLC(String OS) throws Exception {
		if(!OS.startsWith("Windows"))
			return;
		
		String link = "https://vlc.pixelx.de/vlc/3.0.20/win64/vlc-3.0.20-win64.zip";
		String path = "VLC/3.0.20/"+"win32-x86-64"+"/";
		
		File dir = Cache.getFile(path);
		dir.mkdirs();

		if(!Cache.getFile(path+"libvlc.dll").exists()) {
			URL l = new URL(link);
			ZipInputStream iis = new ZipInputStream(l.openStream());
			ZipEntry e = iis.getNextEntry();
			while(e != null) {
				int size = (int) e.getSize(); //~2GB size limit
				if(e.getName().endsWith("libvlc.dll")) {
					byte[] libvlc_dll = new byte[size];
					for(int i = 0; i < size; i++)
						libvlc_dll[i] = (byte) iis.read();
					Cache.writeToCache(libvlc_dll, path+"libvlc.dll");
					Tools.log("Downloaded 'libvlc.dll' succesfully!");
				}
				if(e.getName().endsWith("libvlccore.dll")) {
					byte[] libvlc_dll = new byte[size];
					for(int i = 0; i < size; i++)
						libvlc_dll[i] = (byte) iis.read();
					Cache.writeToCache(libvlc_dll, path+"libvlccore.dll");
					Tools.log("Downloaded 'libvlccore.dll' succesfully!");
				}
				if(e.getName().endsWith("README.txt")) {
					byte[] file = new byte[size];
					for(int i = 0; i < size; i++)
						file[i] = (byte) iis.read();
					Cache.writeToCache(file, path+"README.txt");
					Tools.log("Downloaded 'README' succesfully!");
				}
				if(e.getName().endsWith("THANKS.txt")) {
					byte[] file = new byte[size];
					for(int i = 0; i < size; i++)
						file[i] = (byte) iis.read();
					Cache.writeToCache(file, path+"THANKS.txt");
					Tools.log("Downloaded 'THANKS' succesfully!");
				}
				if(e.getName().endsWith("AUTHORS.txt")) {
					byte[] file = new byte[size];
					for(int i = 0; i < size; i++)
						file[i] = (byte) iis.read();
					Cache.writeToCache(file, path+"AUTHORS.txt");
					Tools.log("Downloaded 'AUTHORS' succesfully!");
				}
				if(e.getName().endsWith("COPYING.txt")) {
					byte[] file = new byte[size];
					for(int i = 0; i < size; i++)
						file[i] = (byte) iis.read();
					Cache.writeToCache(file, path+"COPYING.txt");
					Tools.log("Downloaded 'COPYING' succesfully!");
				}
				if(e.getName().contains("plugins")) {
					byte[] file = new byte[size];
					for(int i = 0; i < size; i++)
						file[i] = (byte) iis.read();
					String name = path+e.getName().substring(e.getName().indexOf("plugins"));
					File f = Cache.getFile(name);
					if(e.isDirectory()) {
						f.mkdirs();
						Tools.log("Downloading '"+e.getName().substring(e.getName().indexOf("plugins"))+"'...");
					}
					else
						Cache.writeToCache(file, name);
				}
				iis.closeEntry();
				e = iis.getNextEntry();
			}
			iis.close();
			Tools.log("Downloaded '"+l+"' succesfully!");
		}
		com.sun.jna.NativeLibrary.addSearchPath("libvlc", dir.getAbsolutePath());
	}
	
//	public static CefApp downloadCEFBrowser(boolean windowlessRendering) {
//		try {
//			//Create a new CefAppBuilder instance
//			CefAppBuilder builder = new CefAppBuilder();
//			//Configure the builder instance
//			builder.setInstallDir(new File("jcef-bundle")); //Default
//			builder.setProgressHandler(new ConsoleProgressHandler() {
//				@Override
//				public void handleProgress(EnumProgress state, float percent) {
//					if(percent != -1)
//						Tools.log("Downloading CEF to " + Cache.getFile("cef") + " ; " + percent + "%");
//				}
//			});
//			builder.setInstallDir(Cache.getFile("cef"));
//			builder.getCefSettings().windowless_rendering_enabled = true; //Default - select OSR mode
//			//Set an app handler. Do not use CefApp.addAppHandler(...), it will break your code on MacOSX!
//			builder.setAppHandler(new MavenCefAppHandlerAdapter(){
//				
//			});
//			builder.getCefSettings().cache_path = Cache.getFile("cef/cache").getAbsolutePath();
//			builder.getCefSettings().log_severity = CefSettings.LogSeverity.LOGSEVERITY_ERROR;
//			builder.getCefSettings().windowless_rendering_enabled = windowlessRendering;
//			//Build a CefApp instance using the configuration above
//			return builder.build();
//		} catch (IOException | UnsupportedPlatformException | InterruptedException | CefInitializationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
//	}

}
