package Kartoffel.Licht.Rendering.Text;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import Kartoffel.Licht.Input.Formatting;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Rendering.Model;
import Kartoffel.Licht.Rendering.Shapes.SBox2D;
import Kartoffel.Licht.Tools.Tools;



/**
 * A ready Text. Has to be rendered by a TextShader.<br>
 * Example:<br>
 * <br>
 *Font f = new Font("fonts/font1.fnt", "fonts/font1.png");<br>
	Text t =  new Text(font, f);<br>
	t.setSize(1);<br>
	t.setColor(Color.YELLOW);<br>	
	t.setPosition(new Vector3f(-1, -0.9f, 0)); //Screen Space Coordinates<br>
	t.setOrigin(Text.ORIGIN_LEFT);<br>
	t.setText("TEST");<br>
	t.setName("CHAT");<br>
 * 
 * 
 * @author Kartoffel_Licht
 *
 */
final public class Text extends GEntity{
	
	
	public static final int ORIGIN_LEFT = 1;
	public static final int ORIGIN_CENTER = 0;
	public static final int ORIGIN_RIGHT = -1;
	
	public static int renderedChars = 0;
	public static int totalChars = 0;
	
	public static double resetStats() {
		double specs = (double)renderedChars/totalChars;
		renderedChars = 0;
		totalChars = 0;
		return specs;
	}
	
	private int selected_a = -1, selected_b = -1;
	
	private FontProvider font;
	private String text = "";
	
	private float size = 1;
	private float spacing_modifier = 1f;
	private float padding = 1;
	private float paddingY = 1;
	
	private float selectorHeight = 0.25f;
	private float selectorWidth = 0;
	
	
	private Vector3f color = new Vector3f(0, 0, 0);
	private float width = 0.66666f;
	private float edge = 256;
	
	private int originX = 0;
	private int originY = 2;
	
	private float xoff = 100, yoffC = 0, yoff = 0, lowest = 1000, highest = 1000;
	
	private Vector4f low = new Vector4f(1), high = new Vector4f(1);
	private int lowIndA = 0, lowIndB = 0;
	
	private Vector4f clip = new Vector4f(-1, -1, 1, 1);
	
	private boolean is2D = true;
	private boolean newline = false;
	private int amountNewLines = 0;
	private float newLineJump = 10;
	
	private List<ModeledChar> models = new ArrayList<ModeledChar>();
	private ModeledChar static_overdraw_model = new ModeledChar(null, new SChar(Formatting.OVERDRAW), null, null, 0, null, 0);

	/**
	 * A ready Text. Has to be rendered by a TextShader.<br>
	 * Example:<br>
	 * <br>
	 *Font f = new Font("fonts/font1.fnt", "fonts/font1.png");<br>
		Text t =  new Text(font, f);<br>
		t.setSize(1);<br>
		t.setColor(Color.YELLOW);<br>	
		t.setPosition(new Vector3f(-1, -0.9f, 0)); //Screen Space Coordinates<br>
		t.setOrigin(Text.ORIGIN_LEFT);<br>
		t.setText("TEST");<br>
		t.setName("CHAT");<br>
	 * 
	 * 
	 * @author Kartoffel_Licht
	 *
	 */	
	public Text(FontProvider font) {
		super(null, generateChar());
		this.font = font;
//		paddingY = font.getMaxHeight();
	}
	
	String old_text = "";
	public Text setText(String text) {
		if(text == null)
			throw new NullPointerException("Text can´t be null");
		this.text = text;
		return this;
	}
	public Text setText(String text, boolean update) {
		if(text == null)
			throw new NullPointerException("Text can´t be null");
		this.text = text;
		if(update)
			NsetText(text);
		return this;
	}
	private void NsetText(String text) {
		if(old_text.contentEquals(text))
			return;
		old_text = text;
		this.text = text;
		int[] codepoints = text.codePoints().toArray();
		models.clear();
		int font = 0;
		for(int i = 0; i < codepoints.length; i++) {
			int codepoint = codepoints[i];
			if(codepoint == Formatting.FONT_CHANGE) {
				i++;
				if(i < codepoints.length)
					font = codepoints[i];
			}
			else 
				addChar(codepoint, font);
		}
	}
	
	public String getText() {
		return text;
	}
	
	public Text addOverdrawCharacter() {
		models.add(static_overdraw_model);
		return this;
	}
	
	public Text addChar(int codepoint, int FLAGS) {
		SChar c = font.getChar(codepoint, FLAGS);
		if(c == null)
			return this;
		models.add(getModeledChar(c, FLAGS));
		return this;
	}
	
	public Text addChars(int FLAGS, int... codepoints) {
		for(int i : codepoints)
			addChar(i, FLAGS);
		return this;
	}
	
	public Text addChar(SChar codepoint, int FLAGS) {
		models.add(getModeledChar(codepoint, FLAGS));
		return this;
	}
	
	public Text addChars(int FLAGS, SChar... codepoints) {
		for(SChar i : codepoints)
			addChar(i, FLAGS);
		return this;
	}
	
	private ModeledChar getModeledChar(SChar c, int FLAGS) {
		for(ModeledChar cs : models) {
			if(cs.getSchar() == c)
				return cs;
		}
		Vector4f bounds = new Vector4f(
				c.x/font.getTexture(FLAGS).getWidth(),
				c.y/font.getTexture(FLAGS).getHeight(),
				c.width/font.getTexture(FLAGS).getWidth(),
				c.height/font.getTexture(FLAGS).getHeight());
		Vector2f modelBounds = new Vector2f(
				((c.width+padding)),
				((c.height+padding)));
		return new ModeledChar(this.mod, c, bounds, modelBounds, FLAGS, font.getTexture(FLAGS), 1.0f/font.getHeight());
	}
	
	private static Model generateChar() {
		Model mod = new Model(new SBox2D(
				0, 0, 
				1, 1,
				0, 0, 1, 1
				));
		mod.setFaceCulling(false);
		return mod;
	}
	
	public float getIndicatorOffset(int index, float offset) {
		float off = 0;
		for(int i = 0; i < Math.min(models.size(), index); i++) {
			off += models.get(i).getSchar().xadvance*spacing_modifier;
		}
		return off;
	}
	
	public Text resizeText(float scale) {
		size = scale;
		NsetText(text);
		return this;
	}
	
	/**
	 * -1 = left  
	 * 0 = centered  
	 * 1 = right  
	 */
	public Text setOriginX(int position) {
		this.originX = position;
		return this;
	}
	/**
	 * -1 = left  
	 * 0 = centered  
	 * 1 = right  
	 */
	public Text setOriginY(int position) {
		this.originY = position;
		return this;
	}
	public int getOriginX() {
		return originX;
	}
	public int getOriginY() {
		return originY;
	}
	public Text setSize(float size) {
		this.size = size;
		return this;
	}
	public Text setWidth(float width) {
		this.width = width;
		return this;
	}
	public Text setEdge(float edge) {
		this.edge = edge;
		return this;
	}
	
	public Text setColor(Vector3f color) {
		this.color = color;
		return this;
	}
	public Text setColor(Color color) {
		this.color.x = color.getRed();
		this.color.y = color.getGreen();
		this.color.z = color.getBlue();
		return this;
	}
	
	public static Color randomColor() {
		return Color.getHSBColor(Tools.RANDOM.nextFloat(), 0.4f, 1f);
	}

	public FontProvider getFont() {
		return font;
	}

	public Text setFont(Font font) {
		this.font = font;
		return this;
	}

	public float getSpacing_modifier() {
		return spacing_modifier;
	}

	public Text setSpacing_modifier(float spacing_modifier) {
		this.spacing_modifier = spacing_modifier;
		return this;
	}

	public float getPadding() {
		return padding;
	}

	public Text setPadding(float padding) {
		this.padding = padding;
		return this;
	}

	public float getXOffset() {
		return xoff;
	}

	public Text setXOffset(float total_width) {
		this.xoff = total_width;
		return this;
	}
	
	public float getYOffsetFinal() {
		return yoffC;
	}
	public Text setYOffsetFinal(float total_height) {
		this.yoffC = total_height;
		return this;
	}
	public void setYOffset(float yoff) {
		this.yoff = yoff;
	}
	public float getYOffset() {
		return yoff;
	}
	public void setLowest(float lowest) {
		this.lowest = lowest;
	}
	public void setHighest(float highest) {
		this.highest = highest;
	}
	public float getLowest() {
		return lowest;
	}
	public float getHighest() {
		return highest;
	}

	public List<ModeledChar> getModels() {
		return models;
	}

	public Text setModels(List<ModeledChar> models) {
		this.models = models;
		return this;
	}

	public float getSize() {
		return size;
	}

	public Vector3f getColor() {
		return color;
	}

	public float getWidth() {
		return width;
	}

	public float getEdge() {
		return edge;
	}
	
	public Text setSelectedText(int a, int b) {
		selected_a = a;
		selected_b = b;
		return this;
	}
	
	public boolean isCharSelected(int c) {
		return selected_a <= c && c <= selected_b;
	}

	public float getPaddingY() {
		return paddingY;
	}
	
	public boolean isIs2D() {
		return is2D;
	}
	public Text setIs2D(boolean is2d) {
		is2D = is2d;
		return this;
	}
	
	public void setClip(Vector4f clip) {
		this.clip = clip;
	}
	public Vector4f getClip() {
		return clip;
	}
	
	public Vector4f getLow() {
		return low;
	}
	public Vector4f getHigh() {
		return high;
	}
	public void setLowIndA(int lowIndA) {
		this.lowIndA = lowIndA;
	}
	public int getLowIndA() {
		return lowIndA;
	}
	public void setLowIndB(int lowIndB) {
		this.lowIndB = lowIndB;
	}
	public int getLowIndB() {
		return lowIndB;
	}
	public Text setNewline(boolean newline) {
		this.newline = newline;
		return this;
	}
	public boolean getNewline() {
		return newline;
	}
	public int[] getSelectedText() {
		return new int[] {selected_a, selected_b};
	}
	public float getSelectorHeight() {
		return selectorHeight;
	}
	public float getSelectorWidth() {
		return selectorWidth;
	}
	public void setSelectorHeight(float selectorHeight) {
		this.selectorHeight = selectorHeight;
	}
	public void setSelectorWidth(float selectorWidth) {
		this.selectorWidth = selectorWidth;
	}
	public int getAmountNewLines() {
		return amountNewLines;
	}
	public void setAmountNewLines(int amountNewLines) {
		this.amountNewLines = amountNewLines;
	}
	public Text setNewLineJump(float newLineJump) {
		this.newLineJump = newLineJump;
		return this;
	}
	public float getNewLineJump() {
		return newLineJump;
	}
	
}
