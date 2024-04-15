package Kartoffel.Licht.Rendering.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import Kartoffel.Licht.Rendering.Texture.Texture;
import Kartoffel.Licht.Res.FileLoader;
import Kartoffel.Licht.Tools.Tools;

public class GlobalFont implements FontProvider{
	
	public static GlobalFont font;
	public static boolean CACHE_FONTS = false;
	public static int[] DEFAULT_FONT_SNIP = new int[] {0, 256};
	
	final public static float DEFAULT_SCALE = 64;
	
	public String name = "Global Fonts";
	
	public static void init(Font...fonts) {
		if(font == null)
			font = new GlobalFont(fonts);
	}
	public static void init(boolean loadDefault) {
		if(font == null) {
			List<FontProvider> fonts = new ArrayList<FontProvider>();
			List<File> files = FontUtils.getSystemFontFiles();
			if(loadDefault)
				for(File f : files)
					try {
						InputStream i = new FileInputStream(f);
						Font fo = new Font(i, DEFAULT_SCALE, DEFAULT_SCALE/64, new int[] {0, 128}, true, f.getName());
						i.close();
						fonts.add(fo);
					} catch (Exception e) {
						Tools.err("Failed to load OS Font: " + f.getName() + "! " + e.getMessage());
					}

			fonts.add(new Font(FileLoader.getFileD("default/fonts/google/Roboto/Roboto-Regular.ttf"), DEFAULT_SCALE, DEFAULT_SCALE/64, DEFAULT_FONT_SNIP, CACHE_FONTS, "Google Roboto R"));
//			fonts.add(new Font(FileLoader.getFileD("default/fonts/google/Roboto/Roboto-Italic.ttf"), DEFAULT_SCALE, DEFAULT_SCALE/64, DEFAULT_FONT_SNIP, CACHE_FONTS, "Google Roboto I"));
//			fonts.add(new Font(FileLoader.getFileD("default/fonts/google/Roboto/Roboto-Bold.ttf"), DEFAULT_SCALE, DEFAULT_SCALE/64, DEFAULT_FONT_SNIP, CACHE_FONTS, "Google Roboto B"));
//			fonts.add(new Font(FileLoader.getFileD("default/fonts/google/Roboto/Roboto-BoldItalic.ttf"), DEFAULT_SCALE, DEFAULT_SCALE/64, DEFAULT_FONT_SNIP, CACHE_FONTS, "Google Roboto BI"));
			
			font = new GlobalFont(fonts.toArray(new FontProvider[fonts.size()]));
		}
	}
	public static void init() {
		init(false);
	}

	public List<FontProvider> fonts = new ArrayList<>();
	public FontProvider latestFont = null;
	
	GlobalFont(FontProvider...fonts) {
		for(FontProvider f : fonts)
			this.fonts.add(f);
		latestFont = this.fonts.get(0);
	}
	
	public FontProvider getFontByName(String name) {
		List<String> names = new ArrayList<String>();
		for(FontProvider f : fonts)
			names.add(f.getName());
		Tools.sortBestResult(names, name);
		for(FontProvider f : fonts)
			if(f.getName().equals(names.get(0)))
				return f;
		return null;
	}
	
	public boolean isCoveredByFont(int codepoint) {
		for(FontProvider f : fonts)
			if(f.getChar(codepoint, 0) != null)
				return true;
		return false;
	}
	
	@Override
	public SChar getChar(int codepoint, int FLAGS) {
		if(!(FLAGS < 0 || FLAGS >= fonts.size())) {
			latestFont = fonts.get(FLAGS);
			if(latestFont != null) {
				SChar c = latestFont.getChar(codepoint, FLAGS);
				if(c != null)
					return c;
				}
		}
		for(FontProvider f : fonts) {
			SChar c = f.getChar(codepoint, FLAGS);
			if(c != null) {
				latestFont = f;
				return c;
			}
		}
		latestFont = fonts.get(0);
		return new SChar(codepoint);
	}

	@Override
	public Texture getTexture(int FLAGS) {
		if(latestFont != null)
			return latestFont.getTexture(FLAGS);
		return null;
	}
	@Override
	public int getHeight() {
		if(latestFont != null)
			return latestFont.getHeight();
		return 64;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}

}
