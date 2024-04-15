package Kartoffel.Licht.Engine;

public class CommandManager {
	
	public static boolean syntax(String s, String pattern) {
		return syntax(s, pattern, " ", 0);
	}

	public static boolean syntax(String s, String pattern, String seperator, int optional) { //word1 word2|word3 <Integer>
		String[] sss = s.split(" ");
		String[] pp = pattern.split(" ");
		if(sss.length < pp.length-optional)
			return false;
		try {
			for(int i = 0; i < sss.length; i++) {
				String ss = sss[i].toLowerCase();
				switch (pp[i].toLowerCase()) {
				case "<integer>":
					Integer.parseInt(ss);
					break;
				case "<int>":
					Integer.parseInt(ss);
					break;
				case "<i>":
					Integer.parseInt(ss);
					break;
					
				case "<byte>":
					Byte.parseByte(ss);
					break;
					
				case "<short>":
					Short.parseShort(ss);
					break;
				case "<s>":
					Short.parseShort(ss);
					break;
					
				case "<long>":
					Long.parseLong(ss);
					break;
				case "<l>":
					Long.parseLong(ss);
					break;
					
				case "<float>":
					Float.parseFloat(ss);
					break;
				case "<f>":
					Float.parseFloat(ss);
					break;
					
				case "<double>":
					Double.parseDouble(ss);
					break;
				case "<d>":
					Double.parseDouble(ss);
					break;
					
				case "<char>":
					if(ss.length() != 1)
						throw new Exception("not a single char!");
					break;
				case "<c>":
					if(ss.length() != 1)
						throw new Exception("not a single char!");
					break;
					
				case "<string>":
					break;
				case "<text>":
					break;
					
				case "<boolean>":
					Boolean.parseBoolean(ss);
					break;
				case "<bool>":
					Boolean.parseBoolean(ss);
					break;
				case "<b>":
					Boolean.parseBoolean(ss);
					break;
					
//				case String s when true:
//					int l = pp[i].indexOf(":");
//					if(!syntax(ss, pp[i].substring(l)))
//						throw new Exception("Syntax error");
//					break;
//				case "<syn>":
//					Boolean.parseBoolean(ss);
//					break;
					
				default:
					String[] words = pp[i].split(",");
					for(String w : words)
						if(!w.equalsIgnoreCase(ss))
							throw new Exception("word missmatch: '" + w + "' and '" + ss + "'");
				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
			return false;
		}
		
		return true;
	}

}
