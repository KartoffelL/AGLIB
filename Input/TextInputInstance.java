package Kartoffel.Licht.Input;

import java.util.ArrayList;
import java.util.List;

public class TextInputInstance {

	public final int MAX_CHARS = 6000;
	
	private boolean numeric = false;
	
	private int select_1 = 0, select_2 = 0;
	
	public boolean isWriting = false;
	private List<Integer> codepoints = new ArrayList<Integer>();
	
	void invoke(int codepoint) {
		if(isWriting)
			if(codepoints.size() < MAX_CHARS) {
				if(numeric) {
					if((codepoint > 47 && codepoint < 58))
						setSelectedText(new String(new char[] {(char) codepoint}));
					if(codepoint == 46) {
						codepoints.remove((Integer)46);
						setSelectedText(new String(new char[] {(char) codepoint}));
					}
					if(codepoint == 43 || codepoint == 45) {
						codepoints.remove((Integer)43);
						codepoints.remove((Integer)45);
						codepoints.add(0, codepoint);
						select_1++;
						select_2++;
					}
				}else
					setSelectedText(new String(new char[] {(char) codepoint}));
			}
				
	}
	
	public List<Integer> getCodepoints() {
		return codepoints;
	}
	
	public void write(String s) {
		setSelectedText(s);
	}
	
	public void write(int cp) {
		setSelectedText(new String(new int[] {cp}, 0, 1));
	}
	
	public void setSelectedText(CharSequence t) {
		clearSelectedText();
		injectText(select_1, t);
		select_1 += t.length();
		select_2 += t.length();
	}
	public void setText(String t) {
		select_1 = 0;
		select_2 = codepoints.size();
		clearSelectedText();
		injectText(0, t);
	}
	public void clearSelectedText() {
		for(int i = 0; i < select_2-select_1; i++) {
			if(codepoints.size() > select_1)
				codepoints.remove(select_1);
		}
		select_2 = select_1;
	}
	public void injectText(int l, CharSequence t) {
		if(l < 0)
			return;
		l = Math.min(codepoints.size(), l);
		int index = 0;
		for(int codepoint : t.codePoints().toArray()) {
			codepoints.add(l+index++, codepoint);
		}
	}
	
	
	public String getText() {
		int[] cps = new int[codepoints.size()];
		for(int i = 0; i < codepoints.size(); i++)
			cps[i] = codepoints.get(i).intValue();
		return new String(cps, 0, cps.length);
	}
	public String getSelectedText() {
		if(codepoints.size() == 0)
			return "";
		int[] cps = new int[select_2-select_1];
		
		for(int i = select_1; i < Math.min(select_2, codepoints.size()); i++)
			cps[i] = codepoints.get(i+select_1).intValue();
		return new String(cps, 0, cps.length);
	}
	
	
	public void deleteLatest() {
		do {
			select_1 = Math.max(0, select_1-1);
			clearSelectedText();
		} while(isSpecial(codepoints.size()-1));
	}
	
	public boolean isSpecial(int index) {
		if(index < 0)
			return false;
		if(Formatting.isSpecial(codepoints.get(index)))
			return true;
		if(codepoints.size() > 1 && index > 0)
			if(Formatting.isSpecial(codepoints.get(index-1)))
				return true;
		return false;
	}
	
	
	
	public void setSelection(int a, int b) {
		select_1 = a;
		select_2 = b;
	}
	
	public void startWriting() {
		isWriting = true;
	}
	
	public String stopWriting() {
		isWriting = false;
		char[] bytes = new char[codepoints.size()];
		for(int i = 0; i < codepoints.size(); i++)
			bytes[i] = (char) codepoints.get(i).intValue();
		return new String(bytes);
	}
	
	
	public List<Integer> getChars() {
		return codepoints;
	}
	
	public int getLength() {
		return codepoints.size();
	}
	
	public int[] getSelection() {
		return new int[] {select_1, select_2};
	}
	public int getSelect_1() {
		return select_1;
	}
	public int getSelect_2() {
		return select_2;
	}
	
	public void setNumeric(boolean numeric) {
		this.numeric = numeric;
	}
	
	public boolean isNumeric() {
		return numeric;
	}
	
	public boolean shouldDrawIndicator() {
		return select_1 == select_2;
	}
}

