package Kartoffel.Licht.Rendering.Text;

import Kartoffel.Licht.Rendering.Texture.Texture;

public class MultiFont implements FontProvider{

	private Font normal, italic, bold, bolditalian;
	private String name;
	
	public MultiFont(Font normal, Font italic, Font bold, Font bolditalian, String name) {
		if(normal == null)
			throw new IllegalArgumentException("Normal font has to be not null!");
		this.normal = normal;
		this.italic = italic;
		this.bold = bold;
		this.bolditalian = bolditalian;
		this.name = name;
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
	public SChar getChar(int codepoint, int FLAGS) {
		SChar s = null;
		if(FLAGS == 1)
			if(italic != null)
				s = italic.getChar(codepoint, FLAGS);
		if(FLAGS == 2)
			if(italic != null)
				s = bold.getChar(codepoint, FLAGS);
		if(FLAGS == 3)
			if(italic != null)
				s = bolditalian.getChar(codepoint, FLAGS);
		if(s == null)
			return normal.getChar(codepoint, FLAGS);
		return null;
	}

	@Override
	public Texture getTexture(int FLAGS) {
		Texture s = null;
		if(FLAGS == 1)
			if(italic != null)
				s = italic.getTexture(FLAGS);
		if(FLAGS == 2)
			if(italic != null)
				s = bold.getTexture(FLAGS);
		if(FLAGS == 3)
			if(italic != null)
				s = bolditalian.getTexture(FLAGS);
		if(s == null)
			return normal.getTexture(FLAGS);
		return null;
	}

	@Override
	public int getHeight() {
		return normal.getHeight();
	}

}
