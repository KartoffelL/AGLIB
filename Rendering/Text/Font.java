package Kartoffel.Licht.Rendering.Text;

import static Kartoffel.Licht.Tools.Tools.font;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

import Kartoffel.Licht.Input.Formatting;
import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Java.ImageIO;
import Kartoffel.Licht.Java.namable;
import Kartoffel.Licht.Rendering.Texture.Texture;
import Kartoffel.Licht.Res.Cache;
import Kartoffel.Licht.Res.SerializationUtils;
import Kartoffel.Licht.Tools.Tools;
/**
 * A Font is a.. Font...<br>
 * The Font class includes the Font Texture and the Char-placing informations.<br>
 * Used by the Text class.
 * 
 * 
 * 
 * 
 * @param path
 * @param texturePath
 * @throws FileNotFoundException
 */
public class Font implements namable, FontProvider{
	
	public static boolean createPNGs = false;
	private String name = "";
	
	private HashMap<Integer, SChar> chars = new HashMap<Integer, SChar>();
	private Texture texture;
	private int pixelHeight = 1;
/**
 * A Font is a.. Font...<br>
 * The Font class includes the Font Texture and the Char-placing informations.<br>
 * Used by the Text class.
 * 
 * 
 * 
 * 
 * @param path
 * @param texturePath
 * @throws FileNotFoundException
 */
	public Font(String fontFile, BufferedImage image) {
		this.texture = new Texture(6403, 9728, image);
		this.pixelHeight = 94;
		int nameStart = fontFile.indexOf("face=")+5;
		nameStart = fontFile.indexOf('"', nameStart);
		int nameEnd = fontFile.indexOf('"', nameStart);
		name = fontFile.substring(nameStart, nameEnd);
		String[] lines = fontFile.split("\n");
		
		for(String s : lines) {				//for every line in file
			if(s.startsWith("char ")) {				//if line is char
				SChar cha = new SChar();
				String[] args = s.split(" ");
				for(String arg : args) {				//for every argument
					String[] t = arg.split("=");
					if(t.length == 2) {					//if argument is valid
						int value = Integer.parseInt(t[1]);
						
							switch (t[0]) {
								case "id":
									cha.id = value;
									break;
								case "x":
									cha.x = value;				
									break;
								case "y":
									cha.y = value;
									break;
								case "width":
									cha.width = value;
									break;
								case "height":
									cha.height = value;
									break;
								case "xoffset":
									cha.xoffset = value;
									break;
								case "yoffset":
									cha.yoffset = value;
									break;
								case "xadvance":
									cha.xadvance = value;
									break;
								default:
									break;
							}	
					}
				}
				font("Loaded new Char with code: " + cha.id);
				chars.put(cha.id, cha);				//put char into map
			}
		}
		loadCustomChars();
		
		
		
		
	}			//Method end
	
	
	public Font(InputStream is, float size, float step, int[] characters, boolean cache, String name){
		this.name = name;
		this.pixelHeight = (int) size;
		if(cache) {
			byte[] cach = Cache.readFromCache("fonts/"+name+".CTTF");
			byte[] image = Cache.readFromCache("fonts/"+name+".png");
			int headerSize = 12;
			if(cach != null && image != null) {
				int amountCharacters = SerializationUtils.toInteger(cach, 0);
				for(int i = 0; i < amountCharacters; i++) {
					SChar ch = new SChar();
					ch.id = SerializationUtils.toInteger(cach, (i*8+0)*4+headerSize);
					ch.width = SerializationUtils.toFloat(cach, (i*8+1)*4+headerSize);
					ch.height = SerializationUtils.toFloat(cach, (i*8+2)*4+headerSize);
					ch.x = SerializationUtils.toFloat(cach, (i*8+3)*4+headerSize);
					ch.y = SerializationUtils.toFloat(cach, (i*8+4)*4+headerSize);
					ch.xoffset = SerializationUtils.toFloat(cach, (i*8+5)*4+headerSize);
					ch.yoffset = SerializationUtils.toFloat(cach, (i*8+6)*4+headerSize);
					ch.xadvance = SerializationUtils.toFloat(cach, (i*8+7)*4+headerSize);
					chars.put(ch.id, ch);
				}
				try {
					InputStream ims = new ByteArrayInputStream(image);
					this.texture = new Texture(6403, 9729, ImageIO.read(ims)); //9729 Linear //9728 Nearest
					ims.close();
				} catch (IOException e) {
					throw new RuntimeException("Cant open Font Image!");
				}
				loadCustomChars();
				Tools.Ressource("Loaded Font from Cache!");
				return;
			}
		}
		try {
			Tools.Ressource("Loading Font " + name + "...");
			if(is == null)
				throw new Exception("Inputstream == null");
			byte[] b = is.readAllBytes();
			ByteBuffer buff = MemoryUtil.memAlloc(b.length);
			buff.put(b);
			buff.flip();
			STBTTFontinfo info = STBTTFontinfo.create();
			if(!STBTruetype.stbtt_InitFont(info, buff))
				throw new Exception("Failed To Load Font!");
			
			//Removing multiple of the same characters or characters that aren't visible/there
			List<Integer> cps = new ArrayList<>();
			if(characters.length == 2) {
				int start = characters[0];
				int end = characters[1];
				for(int codepoint = start; codepoint < end; codepoint++)
					if(!cps.contains(codepoint)) {
						int glyph = STBTruetype.stbtt_FindGlyphIndex(info, codepoint);
						if(glyph != 0) //Don´t add if no glyph was found
							if(!STBTruetype.stbtt_IsGlyphEmpty(info, glyph)) //Don´t add if glyph is not 'visible'
								cps.add(codepoint);
				}
			}
			else {
				for(int codepoint : characters)
					if(!cps.contains(codepoint)) {
						int glyph = STBTruetype.stbtt_FindGlyphIndex(info, codepoint);
						if(glyph != 0) //Don´t add if no glyph was found
							if(!STBTruetype.stbtt_IsGlyphEmpty(info, glyph)) //Don´t add if glyph is not 'visible'
								cps.add(codepoint);
				}
			}
			Integer[] codepoints = (Integer[]) cps.toArray(new Integer[cps.size()]);
			float scale =  STBTruetype.stbtt_ScaleForPixelHeight(info, size);
			
			int[] sx = new int[codepoints.length];
			int[] sy = new int[codepoints.length];
			int[] sxo = new int[codepoints.length];
			int[] syo = new int[codepoints.length];
			boolean[] mask = new boolean[codepoints.length];
			int[] cp = new int[codepoints.length];
			int[] tsx = new int[1];
			int[] tsy = new int[1];
			int[] xoff = new int[1];
			int[] yoff = new int[1];
			ByteBuffer[] data = new ByteBuffer[codepoints.length];
			for(int i = 0; i < codepoints.length; i++) {
				data[i] = STBTruetype.stbtt_GetCodepointSDF(info, scale, codepoints[i], (int) (8*step), (byte) (128), (128)/8/step, tsx, tsy, xoff, yoff);
				sx[i] = tsx[0];
				sy[i] = tsy[0];
				sxo[i] = xoff[0];
				syo[i] = yoff[0];
				mask[i] = data[i] != null;
				cp[i] = codepoints[i];
			}
			//Removing missing characters
			int amount = 0;
			for(boolean bo : mask)
				if(bo)
					amount++;
			
			if(amount == 0)
				return;
			sx = mask(sx, mask, amount);
			sy = mask(sy, mask, amount);
			sxo = mask(sxo, mask, amount);
			syo = mask(syo, mask, amount);
			data = mask(data, mask, amount);
			cp = mask(cp, mask, amount);
			
			int[] px = new int[amount];
			int[] py = new int[amount];
			
			Tools.Ressource("Packing Font...");
			int[] dimensions = Packer.blockPack(sx.length, sx, sy, px, py);
			ByteBuffer image = MemoryUtil.memAlloc(dimensions[0]*dimensions[1]);
			int[] xbearing = new int[1];
			int[] xadvance = new int[1];
			for(int i = 0; i < amount; i++) { //Character Processing
				draw(image, dimensions[0], data[i], sx[i], sy[i], px[i], py[i]);
				STBTruetype.stbtt_FreeSDF(data[i]);
				SChar character = new SChar();
				character.id = cp[i];
				character.x = px[i];
				character.y = py[i];
				character.width = sx[i];
				character.height = sy[i];
				STBTruetype.stbtt_GetCodepointHMetrics(info, cp[i], xadvance, xbearing);
				character.xoffset = sxo[i];
				character.yoffset = syo[i];
				character.xadvance = scale*xadvance[0];
				chars.put(cp[i], character);
			}
			for(int x = 0; x < dimensions[0]*dimensions[1]; x++) {
				int val = 128+image.get(x);
				if(val < 128)
					val = 255+val;
				val /= 1.5; //Compression
				image.put(x, (byte) val);
			}
			this.texture = new Texture(image, dimensions[0], dimensions[1], 1);
			this.texture.setFiltering(false, false);
			if(createPNGs || cache) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ImageIO.writePNG(out, new BufferedImage(image, dimensions[0], dimensions[1]));
				Cache.writeToCache(out.toByteArray(), "fonts/"+name+".PNG");
				out.close();
			}
			
			
			if(cache) {
				Collection<SChar> chs = chars.values();
				int headerSize = 12;
				byte[] ch = new byte[headerSize+chs.size()*8*4+image.capacity()];
				int i = 0;
				for(SChar c : chs) {
					System.arraycopy(SerializationUtils.from(c.id), 0, ch, (i*8+0)*4+headerSize, 4);
					System.arraycopy(SerializationUtils.from(c.width), 0, ch, (i*8+1)*4+headerSize, 4);
					System.arraycopy(SerializationUtils.from(c.height), 0, ch, (i*8+2)*4+headerSize, 4);
					System.arraycopy(SerializationUtils.from(c.x), 0, ch, (i*8+3)*4+headerSize, 4);
					System.arraycopy(SerializationUtils.from(c.y), 0, ch, (i*8+4)*4+headerSize, 4);
					System.arraycopy(SerializationUtils.from(c.xoffset), 0, ch, (i*8+5)*4+headerSize, 4);
					System.arraycopy(SerializationUtils.from(c.yoffset), 0, ch, (i*8+6)*4+headerSize, 4);
					System.arraycopy(SerializationUtils.from(c.xadvance), 0, ch, (i*8+7)*4+headerSize, 4);
					i++;
				}
				System.arraycopy(image.capacity(), 0, ch, chs.size()*8*4+headerSize, image.capacity());
				System.arraycopy(SerializationUtils.from(chs.size()), 0, ch, 0, 4);
				System.arraycopy(SerializationUtils.from(dimensions[0]), 0, ch, 4, 4);
				System.arraycopy(SerializationUtils.from(dimensions[1]), 0, ch, 8, 4);
				Cache.writeToCache(ch, "fonts/"+name+".CTTF");
				
			}
			
			
			MemoryUtil.memFree(image);
			MemoryUtil.memFree(buff);
			Tools.Ressource("Loaded Font!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		loadCustomChars();
	}
	
	public Font(byte[] imageFile, byte[] characterFile) {
		byte[] cach = characterFile;
		byte[] image = imageFile;
		int headerSize = 12;
		if(cach != null && image != null) {
			int amountCharacters = SerializationUtils.toInteger(cach, 0);
			for(int i = 0; i < amountCharacters; i++) {
				SChar ch = new SChar();
				ch.id = SerializationUtils.toInteger(cach, (i*8+0)*4+headerSize);
				ch.width = SerializationUtils.toFloat(cach, (i*8+1)*4+headerSize);
				ch.height = SerializationUtils.toFloat(cach, (i*8+2)*4+headerSize);
				ch.x = SerializationUtils.toFloat(cach, (i*8+3)*4+headerSize);
				ch.y = SerializationUtils.toFloat(cach, (i*8+4)*4+headerSize);
				ch.xoffset = SerializationUtils.toFloat(cach, (i*8+5)*4+headerSize);
				ch.yoffset = SerializationUtils.toFloat(cach, (i*8+6)*4+headerSize);
				ch.xadvance = SerializationUtils.toFloat(cach, (i*8+7)*4+headerSize);
				chars.put(ch.id, ch);
			}
			try {
				InputStream ims = new ByteArrayInputStream(image);
				this.texture = new Texture(6403, 9729, ImageIO.read(ims)); //9729 Linear //9728 Nearest
				ims.close();
			} catch (IOException e) {
				throw new RuntimeException("Cant open Font Image!");
			}
			loadCustomChars();
			Tools.Ressource("Loaded Font from Cache!");
			return;
		}
	
	}
	
	
	private int[] mask(int[] a, boolean[] mask, int amount) {
		int[] o = new int[amount];
		int c = 0;
		for(int i = 0; i < a.length; i++) {
			if(mask[i]) {
				o[c] = a[i];
				c++;
			}
		}
		return o;
	}
	
	private ByteBuffer[] mask(ByteBuffer[] a, boolean[] mask, int amount) {
		ByteBuffer[] o = new ByteBuffer[amount];
		int c = 0;
		for(int i = 0; i < a.length; i++) {
			if(mask[i]) {
				o[c] = a[i];
				c++;
			}
		}
		return o;
	}
	
	private void draw(ByteBuffer dest, int destWidth, ByteBuffer src, int srcWidth, int srcHeight, int x, int y) {
		for(int xx = 0; xx < srcWidth; xx++) {
			for(int yy = 0; yy < srcHeight; yy++) {
				int destIndex = (xx+x)+(yy+y)*destWidth;
				int srcIndex = xx+yy*srcWidth;
				dest.put(destIndex,
						src.get(srcIndex));
			}
		}
	}
	
	
	private void loadCustomChars() {
		SChar space = new SChar();
		space.id = 32;
		space.x = 0;
		space.y = 0;
		space.width = 0;
		space.height = 0;
		space.xoffset = 0;
		space.yoffset = 0;
		space.xadvance = 15;
		chars.put(32, space);
		
		SChar newLine = new SChar();
		newLine.id = 10;
		newLine.x = 0;
		newLine.y = 0;
		newLine.width = 0;
		newLine.height = 0;
		newLine.xoffset = 0;
		newLine.yoffset = 0;
		newLine.xadvance = 0;
		chars.put(10, newLine);
		
		SChar verTab = new SChar();
		newLine.id = 9;
		newLine.x = 0;
		newLine.y = 0;
		newLine.width = 0;
		newLine.height = 0;
		newLine.xoffset = 0;
		newLine.yoffset = 0;
		newLine.xadvance = 15*5;
		chars.put(9, verTab);
		
		SChar colorChange = new SChar();
		colorChange.id = Formatting.COLOR_CHANGE;
		colorChange.x = 0;
		colorChange.y = 0;
		colorChange.width = 0;
		colorChange.height = 0;
		colorChange.xoffset = 0;
		colorChange.yoffset = 0;
		colorChange.xadvance = 0;
		chars.put((int) Formatting.COLOR_CHANGE, colorChange);
		
		SChar overdraw = new SChar();
		colorChange.id = Formatting.OVERDRAW;
		colorChange.x = 0;
		colorChange.y = 0;
		colorChange.width = 0;
		colorChange.height = 0;
		colorChange.xoffset = 0;
		colorChange.yoffset = 0;
		colorChange.xadvance = 0;
		chars.put((int) Formatting.OVERDRAW, overdraw);
		
	}
	
	public Texture getTexture(int FLAGS) {
		return texture;
	}
	
	public SChar getChar(int code, int FLAGS) {
		return chars.get(code);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public int getHeight() {
		return pixelHeight;
	}
	

}
