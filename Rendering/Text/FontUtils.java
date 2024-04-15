package Kartoffel.Licht.Rendering.Text;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import Kartoffel.Licht.Tools.Tools;

public class FontUtils {

	public static List<File> getSystemFontFiles() {
        String[] paths = getSystemFontsPaths();

        ArrayList<File> files = new ArrayList<>();

        for (int i = 0; i < paths.length; i++) {
            File fontDirectory = new File(paths[i]);
            if (!fontDirectory.exists()) break;
            fontDirectory.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					if(pathname.getName().toLowerCase().endsWith("ttf"))
						files.add(pathname);
					return false;
				}
			});
        }

        return files;
    }
	
	 public static String[] getSystemFontsPaths() {
	        String[] result;
	        if (Tools.IS_OS_WINDOWS) {
	            result = new String[1];
	            String path = System.getenv("WINDIR");
	            result[0] = path + "\\" + "Fonts";
	            return result;
	        } else if (Tools.IS_OS_MAC) {
	            result = new String[3];
	            result[0] = System.getProperty("user.home") + File.separator + "Library/Fonts";
	            result[1] = "/Library/Fonts";
	            result[2] = "/System/Library/Fonts";
	            return result;
	        } else if (Tools.IS_OS_LINUX) {
	            String[] pathsToCheck = {
	                    System.getProperty("user.home") + File.separator + ".fonts",
	                    "/usr/share/fonts/truetype",
	                    "/usr/share/fonts/TTF"
	            };
	            ArrayList<String> resultList = new ArrayList<>();

	            for (int i = pathsToCheck.length - 1; i >= 0; i--) {
	                String path = pathsToCheck[i];
	                File tmp = new File(path);
	                if (tmp.exists() && tmp.isDirectory() && tmp.canRead()) {
	                    resultList.add(path);
	                }
	            }

	            if (resultList.isEmpty()) {
	                // TODO: show user warning, TextTool will be crash editor, because system font directories not found
	                result = new String[0];
	            }
	            else {
	                result = new String[resultList.size()];
	                result = resultList.toArray(result);
	            }

	            return result;
	        }

	        return null;
	    }

}
