package Kartoffel.Licht.Tools;

import Kartoffel.Licht.Rendering.Texture.Renderable;

public interface JGLRenderPipeline {

	public Renderable[] render(Renderable[] textures);

}
