package Kartoffel.Licht.Test;

public class AI {
//
//	public static void main(String[] args) throws Exception {
//		GraphicWindow gw = new GraphicWindow("window");
//		gw.setVisible(true);
//		
////		Webcam cam = Webcam.getDefault();
////		cam.setViewSize(new Dimension(640, 480));
////		cam.open();
//		
////		Texture tex = new Texture(cam.getImageBytes(), cam.getViewSize().width, cam.getViewSize().height, 3);
////		FrameBuffer fbA = new FrameBuffer(gw, cam.getViewSize().width, cam.getViewSize().height, 1);
////		FrameBuffer fbB = new FrameBuffer(gw, cam.getViewSize().width, cam.getViewSize().height, 1);
//		
//		PostShaderPack psp = new PostShaderPack();
//		Vector3f skin = new Vector3f(0.5f, 0.4f, 0.1f);
//		PostShader ps_gray = new PostShader(PostShader.DEFAULT_VERTEX_SHADER, "#version 330 core\r\n"
//				+ "\r\n"
//				+ "uniform sampler2D sampler0;\r\n"
//				+ "\r\n"
//				+ "in vec2 tex_coords;\r\n"
//				+ "\r\n"
//				+ "void main() {\r\n"
//				+ "vec3 color = texture2D(sampler0, tex_coords).rgb;"
//				+ "float mm = max(max(color.r, color.g), color.b)-min(min(color.r, color.g), color.b);"
//				+ "mm = pow(min(mm+0.85, 1), 8);"
//				+ "float lum = dot(color, vec3("+skin.x+","+skin.y+","+skin.z+"))*mm;"
//				+ "lum = clamp(lum, 0, 1);"
//				+ "	gl_FragColor = vec4(lum, lum, lum, 1);\r\n"
//				+ "}\r\n"
//				+ "");
//		
//		
//		
//		System.out.println("created NN");
//		
//		float handPosX = 0;
//		float handPosY = 0;
//		List<ant> ants = new ArrayList<>();
//		for(int i = 0; i < 5; i++)
//			ants.add(new ant());
//		
//		Kartoffel.Licht.Rendering.Debug.init(new Camera(1));
//		
//		while(!gw.WindowShouldClose()) {
//			gw.updateWindow(true);
//			gw.doPollEvents();
////			ByteBuffer color = cam.getImageBytes();
////			tex.upload(color, cam.getViewSize().width, cam.getViewSize().height, GL33.GL_RGB);
//			
////			fbB.clear();
////			ps_gray.render(tex);
//			
//			
////			ByteBuffer bi = gw.takeScreenshotBytes(1);
//			color.flip();
//			for(ant a : ants)
//				a.run(color, cam.getViewSize().width*3, cam.getViewSize().height);
//			
//			gw.bindFrameBuffer(null);
//			psp.PostShader_pass.render(fbB.getTexture());
//			Kartoffel.Licht.Rendering.Debug.COLOR = Color.RED;
//			for(ant a : ants)
//				a.draw();
//			
//			Kartoffel.Licht.Rendering.Debug.COLOR = Color.BLUE;
//			Kartoffel.Licht.Rendering.Debug.drawPoint(handPosX, handPosY, 0);
//			
//			handPosX = (float) Math.cos(Timer.getTimeSecondsF())*.5f;
//			handPosY = (float) Math.sin(Timer.getTimeSecondsF())*.5f;
//		}
//		gw.free();
//		
//	}
//	
//	
//
//}
//
//class ant {
//	float posX, posY;
//	NN brain;
//	public ant() {
//		brain = new NN(8, 2, 2);
//	}
//	public void run(ByteBuffer image, int scanline, int height) {
//		for(int i = 0; i < 8; i++) {
//			brain.ingray[i] = getVal(image, scanline, height);
//			posX = brain.getOut()[0].value;
//			posY = brain.getOut()[1].value;
//		}
//	}
//	private byte getVal(ByteBuffer image, int scanline, int height) {
//		return image.get(Math.min(image.limit()-1, Math.max(0, (int)((posX*.5+.5)*scanline)+(int)((posY*.5+.5)*height)*scanline)));
//	}
//	public void correct(float handPosX, float handPosY) {
//		float dst = (handPosX-posX)*(handPosX-posX)+(handPosX-posY)*(handPosX-posY);
//		if(dst > .5) {
//			brain = new NN(8, 2, 2);
//			posX = 0;
//			posY = 0;
//		} else {
//			
//		}
//	}
//	public void draw() {
//		Kartoffel.Licht.Rendering.Debug.drawPoint(posX, posY, 0);
//	}
//}
//
//class NN {
//
//	public byte[] ingray;
//	private neuron[][] layers;
//	private Thread t;
//	
//	public static class neuron {
//		float value;
//		float sum = 0;
//		byte[] weights;
//		boolean activated = false;
//		public neuron(int size) {
//			weights = new byte[size];
//			float v = 0;
//			for(int i = 0; i < size; i++) {
//				float val = Tools.RANDOM.nextFloat();
//				v += val;
//				weights[i] = (byte) (val*255);
//			}
//			sum = v;
//		}
//	}
//	
//	public NN(int in_size, int outpositions, int layers) {
//		ingray = new byte[in_size];
////		System.out.println("Creating " + (in_size*layers+outpositions) + " Neurons with " + (in_size*layers+outpositions+(layers+1)*in_size*in_size) + " Bytes (Weights)");
//		this.layers = new neuron[layers+1][];
//		for(int i = 0; i < layers; i++) {
//			this.layers[i] = new neuron[in_size];
//			for(int l = 0; l < in_size; l++) {
//				this.layers[i][l] = new neuron(in_size);
//			}
//		}
//		this.layers[layers] = new neuron[outpositions];
//		for(int l = 0; l < outpositions; l++) { //Last layer
//			this.layers[layers][l] = new neuron(in_size);
//		}
//		
//		t = new Thread(()->{while(true)learn();});
//		t.start();
//	}
//	
//	public void learn() {
//		for(int currentLayer = 0; currentLayer < layers.length; currentLayer++) {
//			for(int currentNeuron = 0; currentNeuron < layers[currentLayer].length; currentNeuron++) {
//				neuron n = layers[currentLayer][currentNeuron];
//				float ac = 0;
//				for(int i = 0; i < n.weights.length; i++) {
//					if(currentLayer == 0)
//						ac += ingray[i]/255.0f*n.weights[i]+Math.cos(ac*50000)*.1;
//					else
//						ac += n.weights[i]/n.sum*(layers[currentLayer-1][i].activated ? 1 : 0)+Math.cos(ac*50000)*.1;
//				}
//				n.value = ac;
//				n.activated = activationFunc(ac);
//			}
//		}
//	}
//	
//	public boolean activationFunc(float in) {
//		return Math.tanh(in) > 0.5;
//	}
//	public neuron[] getOut() {
//		return layers[layers.length-1];
//	}

}
