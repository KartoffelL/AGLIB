package Kartoffel.Licht.Res;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;

import Kartoffel.Licht.Tools.Tools;

public class Cache {
	
	final public static String CACHE_LOCATION;
	final public static String JAR_NAME;
	final public static String CACHE_FOLDER_NAME;
	
	static {
		String path = "";
		String name = "";
		try {
			path = URLDecoder.decode(Cache.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
			name = path.substring(path.lastIndexOf("/"));
			path = path.substring(0, path.lastIndexOf("/"));
		} catch (Exception e) {e.printStackTrace();}
		
		CACHE_LOCATION = System.getProperty("user.home")+"/JavaAppCache";
		new File(CACHE_LOCATION).mkdir();
		if(name.length() == 0)
			name = "DEFAULT_CACHE";
		JAR_NAME = name;
		CACHE_FOLDER_NAME = name+"_cache";
		
	}
	public static File writeToCache(byte[] val, String name) {
		if(name.matches(".*[:*?\"<>|].*"))
			throw new RuntimeException("");
		try {
			File folder = new File(CACHE_LOCATION+"/"+CACHE_FOLDER_NAME);
			folder.mkdir();
			if(!folder.exists())
				Tools.err("Cant create new Cache Folder '" + folder.getAbsolutePath() + "'!");
			
			String path = name.replace("\\", "/");
			
			String[] ftg = path.split("/");
			String current = CACHE_LOCATION+"/"+CACHE_FOLDER_NAME;
			for(int i = 0; i < ftg.length-1; i++) {
				current += "/"+ftg[i];
				if(ftg[i].length() != 0)
					new File(current).mkdir();
			}
			
			File v = new File(CACHE_LOCATION+"/"+CACHE_FOLDER_NAME+"/"+name);
			try {
				v.createNewFile();
				FileOutputStream fos = new FileOutputStream(v);
				fos.write(val);
				fos.flush();
				fos.close();
				return v;
			} catch (IOException e) {
				Tools.err("Cant create new File '" + v.getAbsolutePath() + "'!");
				Tools.err(e.getMessage());
				return null;
			}
		} catch (Exception e) {
			Tools.err("Cant create new File '" + name + "'!");
			Tools.err(e.getMessage());
			return null;
		}
	}

	public static byte[] readFromCache(String name) {
		File v = new File(CACHE_LOCATION+"/"+CACHE_FOLDER_NAME+"/"+name);
		try {
			FileInputStream fis = new FileInputStream(v);
			byte[] b = fis.readAllBytes();
			fis.close();
			return b;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static File getFile(String name) {
		return new File(CACHE_LOCATION+"/"+CACHE_FOLDER_NAME+"/"+name);
	}

}
