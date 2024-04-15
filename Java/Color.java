package Kartoffel.Licht.Java;

public class Color {
	
	private static final double FACTOR = 0.7;

	public static final Color ALICEBLUE = new Color(0XF0F8FFFF),
	ANTIQUEWHITE = new Color(0XFAEBD7FF),
	AQUA = new Color(0X00FFFFFF),
	AQUAMARINE = new Color(0X7FFFD4FF),
	AZURE = new Color(0XF0FFFFFF),
	BEIGE = new Color(0XF5F5DCFF),
	BISQUE = new Color(0XFFE4C4FF),
	BLACK = new Color(0X000000FF),
	BLANCHEDALMOND = new Color(0XFFEBCDFF),
	BLUE = new Color(0X0000FFFF),
	BLUEVIOLET = new Color(0X8A2BE2FF),
	BROWN = new Color(0XA52A2AFF),
	BURLYWOOD = new Color(0XDEB887FF),
	CADETBLUE = new Color(0X5F9EA0FF),
	CHARTREUSE = new Color(0X7FFF00FF),
	CHOCOLATE = new Color(0XD2691EFF),
	CORAL = new Color(0XFF7F50FF),
	CORNFLOWERBLUE = new Color(0X6495EDFF),
	CORNSILK = new Color(0XFFF8DCFF),
	CRIMSON = new Color(0XDC143CFF),
	CYAN = new Color(0X00FFFFFF),
	DARKBLUE = new Color(0X00008BFF),
	DARKCYAN = new Color(0X008B8BFF),
	DARKGOLDENROD = new Color(0XB8860BFF),
	DARKGRAY = new Color(0XA9A9A9FF),
	DARKGREEN = new Color(0X006400FF),
	DARKKHAKI = new Color(0XBDB76BFF),
	DARKMAGENTA = new Color(0X8B008BFF),
	DARKOLIVEGREEN = new Color(0X556B2FFF),
	DARKORANGE = new Color(0XFF8C00FF),
	DARKORCHID = new Color(0X9932CCFF),
	DARKRED = new Color(0X8B0000FF),
	DARKSALMON = new Color(0XE9967AFF),
	DARKSEAGREEN = new Color(0X8FBC8FFF),
	DARKSLATEBLUE = new Color(0X483D8BFF),
	DARKSLATEGRAY = new Color(0X2F4F4FFF),
	DARKTURQUOISE = new Color(0X00CED1FF),
	DARKVIOLET = new Color(0X9400D3FF),
	DEEPPINK = new Color(0XFF1493FF),
	DEEPSKYBLUE = new Color(0X00BFFFFF),
	DIMGRAY = new Color(0X696969FF),
	DODGERBLUE = new Color(0X1E90FFFF),
	FIREBRICK = new Color(0XB22222FF),
	FLORALWHITE = new Color(0XFFFAF0FF),
	FORESTGREEN = new Color(0X228B22FF),
	FUCHSIA = new Color(0XFF00FFFF),
	GAINSBORO = new Color(0XDCDCDCFF),
	GHOSTWHITE = new Color(0XF8F8FFFF),
	GOLD = new Color(0XFFD700FF),
	GOLDENROD = new Color(0XDAA520FF),
	GRAY = new Color(0X808080FF),
	GREEN = new Color(0X008000FF),
	GREENYELLOW = new Color(0XADFF2FFF),
	HONEYDEW = new Color(0XF0FFF0FF),
	HOTPINK = new Color(0XFF69B4FF),
	INDIANRED = new Color(0XCD5C5CFF),
	INDIGO = new Color(0X4B0082FF),
	IVORY = new Color(0XFFFFF0FF),
	KHAKI = new Color(0XF0E68CFF),
	LAVENDER = new Color(0XE6E6FAFF),
	LAVENDERBLUSH = new Color(0XFFF0F5FF),
	LAWNGREEN = new Color(0X7CFC00FF),
	LEMONCHIFFON = new Color(0XFFFACDFF),
	LIGHTBLUE = new Color(0XADD8E6FF),
	LIGHTCORAL = new Color(0XF08080FF),
	LIGHTCYAN = new Color(0XE0FFFFFF),
	LIGHTGOLDENRODYELLOW = new Color(0XFAFAD2FF),
	LIGHTGREY = new Color(0XD3D3D3FF),
	LIGHTGREEN = new Color(0X90EE90FF),
	LIGHTPINK = new Color(0XFFB6C1FF),
	LIGHTSALMON = new Color(0XFFA07AFF),
	LIGHTSEAGREEN = new Color(0X20B2AAFF),
	LIGHTSKYBLUE = new Color(0X87CEFAFF),
	LIGHTSLATEGRAY = new Color(0X778899FF),
	LIGHTSTEELBLUE = new Color(0XB0C4DEFF),
	LIGHTYELLOW = new Color(0XFFFFE0FF),
	LIME = new Color(0X00FF00FF),
	LIMEGREEN = new Color(0X32CD32FF),
	LINEN = new Color(0XFAF0E6FF),
	MAGENTA = new Color(0XFF00FFFF),
	MAROON = new Color(0X800000FF),
	MEDIUMAQUAMARINE = new Color(0X66CDAAFF),
	MEDIUMBLUE = new Color(0X0000CDFF),
	MEDIUMORCHID = new Color(0XBA55D3FF),
	MEDIUMPURPLE = new Color(0X9370D8FF),
	MEDIUMSEAGREEN = new Color(0X3CB371FF),
	MEDIUMSLATEBLUE = new Color(0X7B68EEFF),
	MEDIUMSPRINGGREEN = new Color(0X00FA9AFF),
	MEDIUMTURQUOISE = new Color(0X48D1CCFF),
	MEDIUMVIOLETRED = new Color(0XC71585FF),
	MIDNIGHTBLUE = new Color(0X191970FF),
	MINTCREAM = new Color(0XF5FFFAFF),
	MISTYROSE = new Color(0XFFE4E1FF),
	MOCCASIN = new Color(0XFFE4B5FF),
	NAVAJOWHITE = new Color(0XFFDEADFF),
	NAVY = new Color(0X000080FF),
	OLDLACE = new Color(0XFDF5E6FF),
	OLIVE = new Color(0X808000FF),
	OLIVEDRAB = new Color(0X6B8E23FF),
	ORANGE = new Color(0XFFA500FF),
	ORANGERED = new Color(0XFF4500FF),
	ORCHID = new Color(0XDA70D6FF),
	PALEGOLDENROD = new Color(0XEEE8AAFF),
	PALEGREEN = new Color(0X98FB98FF),
	PALETURQUOISE = new Color(0XAFEEEEFF),
	PALEVIOLETRED = new Color(0XD87093FF),
	PAPAYAWHIP = new Color(0XFFEFD5FF),
	PEACHPUFF = new Color(0XFFDAB9FF),
	PERU = new Color(0XCD853FFF),
	PINK = new Color(0XFFC0CBFF),
	PLUM = new Color(0XDDA0DDFF),
	POWDERBLUE = new Color(0XB0E0E6FF),
	PURPLE = new Color(0X800080FF),
	RED = new Color(0XFF0000FF),
	ROSYBROWN = new Color(0XBC8F8FFF),
	ROYALBLUE = new Color(0X4169E1FF),
	SADDLEBROWN = new Color(0X8B4513FF),
	SALMON = new Color(0XFA8072FF),
	SANDYBROWN = new Color(0XF4A460FF),
	SEAGREEN = new Color(0X2E8B57FF),
	SEASHELL = new Color(0XFFF5EEFF),
	SIENNA = new Color(0XA0522DFF),
	SILVER = new Color(0XC0C0C0FF),
	SKYBLUE = new Color(0X87CEEBFF),
	SLATEBLUE = new Color(0X6A5ACDFF),
	SLATEGRAY = new Color(0X708090FF),
	SNOW = new Color(0XFFFAFAFF),
	SPRINGGREEN = new Color(0X00FF7FFF),
	STEELBLUE = new Color(0X4682B4FF),
	TAN = new Color(0XD2B48CFF),
	TEAL = new Color(0X008080FF),
	THISTLE = new Color(0XD8BFD8FF),
	TOMATO = new Color(0XFF6347FF),
	TURQUOISE = new Color(0X40E0D0FF),
	VIOLET = new Color(0XEE82EEFF),
	WHEAT = new Color(0XF5DEB3FF),
	WHITE = new Color(0XFFFFFFFF),
	WHITESMOKE = new Color(0XF5F5F5FF),
	YELLOW = new Color(0XFFFF00FF),
	YELLOWGREEN = new Color(0X9ACD32FF);
	
	protected byte[] components = new byte[4];

	
	public Color(Color c) {
		components[0] = c.getComponents()[0];
		components[1] = c.getComponents()[1];
		components[2] = c.getComponents()[2];
		components[3] = c.getComponents()[3];
	}
	
	public Color(int rgba) {
		components[0] = (byte) ((rgba >> 24) & 0xFF);
		components[1] = (byte) ((rgba >> 16) & 0xFF);
		components[2] = (byte) ((rgba >> 8) & 0xFF);
		components[3] = (byte) ((rgba >> 0) & 0xFF);
	}

	public Color(int r, int g, int b) {
		components[0] = (byte) r;
		components[1] = (byte) g;
		components[2] = (byte) b;
		components[3] = -1;
	}
	
	public Color(int r, int g, int b, int a) {
		components[0] = (byte) r;
		components[1] = (byte) g;
		components[2] = (byte) b;
		components[3] = (byte) a;
	}

	public Color(float r, float g, float b) {
		components[0] = (byte) (r*255);
		components[1] = (byte) (g*255);
		components[2] = (byte) (b*255);
		components[3] = -1;
	}
	
	public Color(float r, float g, float b, float a) {
		components[0] = (byte) (r*255);
		components[1] = (byte) (g*255);
		components[2] = (byte) (b*255);
		components[3] = (byte) (a*255);
	}
	

	public int getRGBA() {//F5F5DCFF
		return ((components[0]&0x0ff)<<24)|((components[1]&0x0ff)<<16)|((components[2]&0x0ff)<<8)|(components[3]&0x0ff);
	}
	
	public int getRed() {
		return components[0]&0xFF;
	}
	
	public int getGreen() {
		return components[1]&0xFF;
	}
	
	public int getBlue() {
		return components[2]&0xFF;
	}
	
	public int getAlpha() {
		return components[3]&0xFF;
	}
	
	public byte[] getComponents() {
		byte[] r;
		System.arraycopy(components, 0, r = new byte[components.length], 0, components.length);
		return r;
	}
	public float getComponentFloat(int index) {
		return (components[index]&0x0FF)/255f;
	}
	public Color darker() {
		return new Color(clamp(getRed()*FACTOR), clamp(getGreen()*FACTOR), clamp(getBlue()*FACTOR), getAlpha());
	}
	public Color brighter() {
		return new Color(clamp(getRed()/FACTOR), clamp(getGreen()/FACTOR), clamp(getBlue()/FACTOR), getAlpha());
	}
	public Color darker(float factor) {
		return new Color(clamp(getRed()*factor), clamp(getGreen()*factor), clamp(getBlue()*factor), getAlpha());
	}
	public Color brighter(float factor) {
		return new Color(clamp(getRed()/factor), clamp(getGreen()/factor), clamp(getBlue()/factor), getAlpha());
	}
	
	private static byte clamp(double a) {
		return (byte)Math.min(255, a);
	}

	public static Color getHSBColor(float h, float s, float v) {
		 double      hh, p, q, t, ff;
		    int        i;
		    double r, g, b;

		    if(s <= 0.0) {       // < is bogus, just shuts up warnings
		        r = v;
		        g = v;
		        b = v;
		        return new Color((float)r, (float)g, (float)b);
		    }
		    hh = h;
		    if(hh >= 360.0) hh = 0.0;
		    hh /= 60.0;
		    i = (int) hh;
		    ff = hh - i;
		    p = v * (1.0 - s);
		    q = v * (1.0 - (s * ff));
		    t = v * (1.0 - (s * (1.0 - ff)));

		    switch(i) {
		    case 0:
		        r = v;
		        g = t;
		        b = p;
		        break;
		    case 1:
		        r = q;
		        g = v;
		        b = p;
		        break;
		    case 2:
		        r = p;
		        g = v;
		        b = t;
		        break;

		    case 3:
		        r = p;
		        g = q;
		        b = v;
		        break;
		    case 4:
		        r = t;
		        g = p;
		        b = v;
		        break;
		    case 5:
		    default:
		        r = v;
		        g = p;
		        b = q;
		        break;
		    }
		    return new Color((float)r, (float)g, (float)b);
	}
	
	public static float[] RGBtoHSB(float r, float g, float b) {
		double h, s, v; 
		double      min, max, delta;

	    min = r < g ? r : g;
	    min = min  < b ? min  : b;

	    max = r > g ? r : g;
	    max = max  > b ? max  : b;

	    v = max;                                // v
	    delta = max - min;
	    if (delta < 0.00001)
	    {
	        s = 0;
	        h = 0; // undefined, maybe nan?
	        return new float[] {(float) h, (float) s, (float)v };
	    }
	    if( max > 0.0 ) { // NOTE: if Max is == 0, this divide would cause a crash
	        s = (delta / max);                  // s
	    } else {
	        // if max is 0, then r = g = b = 0              
	        // s = 0, h is undefined
	        s = 0.0;
	        h = Float.NaN;                            // its now undefined
	        return new float[] {(float) h, (float) s, (float)v };
	    }
	    if( r >= max )                           // > is bogus, just keeps compilor happy
	        h = ( g - b ) / delta;        // between yellow & magenta
	    else
	    if( g >= max )
	        h = 2.0 + ( b - r ) / delta;  // between cyan & yellow
	    else
	        h = 4.0 + ( r - g ) / delta;  // between magenta & cyan

	    h *= 60.0;                              // degrees

	    if( h < 0.0 )
	        h += 360.0;

	    return new float[] {(float) h, (float) s, (float)v };
	}
	
	@Override
	public String toString() {
		return "["+(getRed())+"r,"+(getGreen())+"g,"+(getBlue())+"b,"+(getAlpha())+"a]";
	}
	
}
