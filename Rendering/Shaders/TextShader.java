package Kartoffel.Licht.Rendering.Shaders;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import Kartoffel.Licht.Input.Formatting;
import Kartoffel.Licht.Rendering.Camera;
import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Rendering.Text.ModeledChar;
import Kartoffel.Licht.Rendering.Text.Text;
import Kartoffel.Licht.Res.FileLoader;
import Kartoffel.Licht.Tools.Timer;
import Kartoffel.Licht.Tools.Tools;




/**
 * Shaders are programs that can be uploaded to the GPU.
 * They can be bound if needed.
 * The Class auto-compiles everything.
 * If needed, Int, Float, Bool, Matrix4f and Vector3f can be set as an Uniform variable, accesable in the
 * Shader with the definition 
 * 'uniform', exmp.
 * 
 * uniform Vector3f color;
 * 
 * Names have to be exact and case-sensitive
 * 
 * 
 * @author PC
 *
 */
//Object that can render.
public class TextShader extends Shader{
	
	private Camera cam;
	
	public TextShader(Camera cam) {
		super(FileLoader.readFileD("default/shaders/text.vert"), FileLoader.readFileD("default/shaders/text.frag"), "2D TextShader");
		this.cam = cam;
	}
	
	static int chr, off;
	static float lowest, highest, offy;
	static Matrix4f mat = new Matrix4f();
	static Vector2f vec = new Vector2f();
	
	@Override
	public void draw(GEntity gentity) {
		if(!(gentity instanceof Text))
			throw new IllegalArgumentException("Cant render non-Text in a text Shader");
		Text t = (Text)gentity;
		t.setText(t.getText(), true); //Updates the text
		this.bind();
		this.setUniformInt("sampler", 0);
		this.setUniformFloat("width", t.getWidth());
		this.setUniformVec3("color", t.getColor());
		this.setUniformFloat("edge", t.getEdge());
		off = 0;
		offy = 0;
		this.setUniformMatrix4f("transmat", gentity.getTransformationMatrix());
		this.setUniformMatrix4f("camMat", t.isIs2D() || cam == null ? Tools.IDENTITY_MATRIX : cam.getProViewMatrix());
		this.setUniformBool("D3", !t.isIs2D());
		this.setUniformVec4("clip", t.getClip());
		chr = 0;
		lowest = Float.MAX_VALUE;
		highest = Float.MIN_VALUE;
		float ax = Float.MAX_VALUE;
		float bx = Float.MIN_VALUE;
//		float sizeMod = 1;
		
//		float selectorOff = 0;
		boolean drawnSelector = false;
		int newLines = 0;
		boolean nextOverdraw = false;
		Text.totalChars += t.getModels().size();
		for(int index = 0; index < t.getModels().size(); index++) {
			ModeledChar m = t.getModels().get(index);
			if(!m.isVisible())
				continue;
			//In-text properties changes
//			if(m.getSchar().id == Formatting.SIZE_CHANGE)
//				sizeMod = t.getModels().get(index).getSchar().width;
			if(m.getSchar().id == Formatting.COLOR_CHANGE)
				this.setUniformVec3("color", m.getSchar().width, m.getSchar().height, m.getSchar().x);
			if(m.getSchar().id == Formatting.OVERDRAW)
				nextOverdraw = true;
			if(Formatting.isSpecial(m.getSchar().id))
				continue;
			//Early variables
			float scale = t.getSize()*m.getHeight();
			float cdx = (off+m.getSchar().xadvance * t.getSpacing_modifier()-t.getXOffset()*(.5f-t.getOriginX()*.5f))*scale*t.getScale().x;
			float cdxe = (off+m.getSchar().xadvance * t.getSpacing_modifier()-(m.getSchar().width+m.getSchar().xoffset))*scale*t.getScale().x;
			float cdy = ((m.getSchar().yoffset)*scale-t.getYOffsetFinal()-offy)*t.getScale().y;
			//Any position change before culling
			if(m.getSchar().id == '\n' || (t.getNewline() && cdx > t.getClip().z-t.getClip().x)) {
				offy -= t.getNewLineJump()*scale;
				off = 0;
				newLines++;
			}
			//culling
			if(t.isIs2D()) {
				if(Math.abs(cdxe) > t.getClip().z-t.getClip().x) {
					continue;
				}
				if(cdy > t.getClip().w-t.getClip().y) { //Exited bounds
					break;
				}
			}
			Text.renderedChars++;
			if(m.getTexture() != null)
				m.getTexture().bind(0);
			//XPosition
			float uxoff = 0;
			//xPosition
			this.setUniformFloat("Xoff", uxoff = (off+m.getSchar().xoffset-t.getXOffset()*(.5f-t.getOriginX()*.5f))*scale);
			//YPosition
			this.setUniformFloat("Yoff", (m.getSchar().yoffset)*scale-t.getYOffsetFinal()-offy);
			//Selection
			this.setUniformBool("sel", t.isCharSelected(chr));
			//Bounds
			this.setUniformVec4("textMat", m.getTexture_Bounds());	
			this.setUniformVec2("modelMat", m.getModel_Bounds().mul(scale, vec));	
			if(index+1 >= t.getLowIndA() && index+1 <= t.getLowIndB()) {
				ax = Math.min(ax, uxoff);
				bx = Math.max(bx, vec.x+uxoff);
			}
			m.getModel().render();
//			if(true) { //Is underlined
//				this.setUniformBool("selL", true);	
//				this.setUniformVec2("selMat", t.getSelectorWidth(), t.getSelectorHeight());
//				this.setUniformFloat("Xoff", (off+m.getSchar().xoffset-t.getXOffset()*(.5f-t.getOriginX()*.5f))*scale);
//				this.setUniformFloat("Yoff", -t.getYOffsetFinal()-offy);
//				m.getModel().render();
//				this.setUniformBool("selL", false);	
//			
//			}
			if(index == t.getSelectedText()[0]) {
				drawnSelector = true;
//				selectorOff = offy;
				if((Timer.getTimeMilli()/1000.0)%1 < .5) {
					this.setUniformBool("selL", true);	
					this.setUniformVec2("selMat", t.getSelectorWidth(), t.getSelectorHeight());
					this.setUniformFloat("Xoff", (off-t.getXOffset()*(.5f-t.getOriginX()*.5f))*scale);
					this.setUniformFloat("Yoff", -t.getSelectorHeight()-(t.getSelectorHeight())*t.getSize()-t.getYOffsetFinal()-offy);
					m.getModel().render();
					this.setUniformBool("selL", false);	
				}
			}
			if(!nextOverdraw)
				off += m.getSchar().xadvance * t.getSpacing_modifier();
			else
				nextOverdraw = false;
			
			chr++;
			if((m.getSchar().height+m.getSchar().yoffset)*scale > highest)
				highest = (m.getSchar().height+m.getSchar().yoffset)*scale;
			if(m.getSchar().yoffset*scale < lowest)
				lowest = m.getSchar().yoffset*scale;
		}
		if(!drawnSelector) {
//			selectorOff = offy;
			if((Timer.getTimeMilli()/1000.0)%1 < .5) {
				this.setUniformBool("selL", true);	
				this.setUniformVec2("selMat", t.getSelectorWidth(), t.getSelectorHeight());
				this.setUniformFloat("Xoff", (off-t.getXOffset()*(.5f-t.getOriginX()*.5f))*t.getSize());
				this.setUniformFloat("Yoff", -t.getSelectorHeight()-(t.getSelectorHeight())*t.getSize()-t.getYOffsetFinal()-offy);
				t.getMod().render();
				this.setUniformBool("selL", false);	
			}
		}
		t.setAmountNewLines(newLines);
		t.setXOffset(off);
		t.setYOffset(offy);
		t.setLowest(lowest);
		t.setHighest(highest);
		if(t.getOriginY() == 0)
			t.setYOffsetFinal(0);
		else if(t.getOriginY() == 1)
			t.setYOffsetFinal(lowest);
		else if(t.getOriginY() == 2)
			t.setYOffsetFinal((lowest+highest)/4);
		else if(t.getOriginY() == 3)
			t.setYOffsetFinal(highest);
		
		t.getLow().set(ax, -highest+t.getYOffset(), 0, 1).mul(t.getTransformationMatrix());
		t.getHigh().set(bx, -lowest+t.getYOffset(), 0, 1).mul(t.getTransformationMatrix());
	}
	public void setCam(Camera cam) {
		this.cam = cam;
	}
}
