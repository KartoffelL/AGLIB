package Kartoffel.Licht.Rendering.Text;

import java.util.Arrays;
import java.util.Comparator;

public class Packer {
	
//	final public static int MAX_CHUNKS = 18; //8 is a good number. Set to 1 for raw Best-Fit packing (very slow with high quantities)
//	final public static int CHUNK_PADDING = 0;
//	public static boolean CONSOLE_DEBUG = false;
//	
//	public static int[] pack(int[] sx, int[] sy, int[] outposx, int[] outposy, int cw, int ch) {
//		return pack(sx.length, sx, sy, outposx, outposy, cw, ch, null, null, 0);
//	}
//	public static int[] pack(int amount, int[] sx, int[] sy, int[] outposx, int[] outposy, int cw, int ch, long[] debug, int[][] bbdebug, int maxChunks) {
//		if(amount != sx.length && amount != sy.length && amount != outposx.length && amount != outposy.length)
//			throw new RuntimeException("Arrays are not the same size!");
//		if(debug != null)
//			if(debug.length < 3)
//				throw new RuntimeException("Debug Array has to be at least of size 4!");
//		long time = Timer.getTime();
//		Integer[] indices = new Integer[amount]; //Simple sorting
//		for(int i = 0; i < amount; i++)
//			indices[i] = i;
//		Arrays.sort(indices, new Comparator<Integer>() {
//			@Override
//			public int compare(Integer o1, Integer o2) {
//				double vo2 = sx[o2]+sy[o2];
//				double vo1 = sx[o1]+sy[o1];
//				
//				return (int) -Math.signum(vo1-vo2);
//			}
//		});
//		if(debug != null)
//			debug[0] = Timer.getTime()-time;
//		time = Timer.getTime();
//		apply(sx, indices);
//		apply(sy, indices);
//		apply(outposx, indices);
//		apply(outposy, indices);
//		if(debug != null)
//			debug[1] = Timer.getTime()-time;
//		time = Timer.getTime();
//		
//		
//		int CHUNKS = maxChunks;
//		if(maxChunks == 0)
//			if(amount < 64)
//				CHUNKS = 1;
//			else if(amount < 128)
//				CHUNKS = 2;
//			else if(amount < 256)
//				CHUNKS = 4;
//			else if(amount < 512)
//				CHUNKS = 6;
//			else if(amount < 1024)
//				CHUNKS = 8;
//			else if(amount < 2048)
//				CHUNKS = 10;
//			else if(amount < 4096)
//				CHUNKS = 12;
//			else if(amount < 8192)
//				CHUNKS = 14;
//			else if(amount < 16384)
//				CHUNKS = 16;
//			else
//				CHUNKS = 18;
//		
//		
//		int[][] bb = new int[CHUNKS*CHUNKS][4];
//		int[] initSize = new int[CHUNKS*CHUNKS];
//		int finished = 0;
//		for(int x = 0; x < CHUNKS; x++) {
//			for(int y = 0; y < CHUNKS; y++) {
//				int i = x+y*CHUNKS;
//				
//				initSize[i] = cw/CHUNKS;
//				
//				while(packi(amount/(CHUNKS*CHUNKS), amount/(CHUNKS*CHUNKS)*i, //Boxes
//						sx, sy, outposx, outposy, //Data
//						initSize[i], initSize[i], //Canvas Size ch/CHUNK_SIZE
//						cw/CHUNKS*(i/CHUNKS), ch/CHUNKS*(i%CHUNKS), //Canvas Offset
//						4, 2, bb[i] //MISC
//						))
//					initSize[i]+=16;
//				
//				if(CONSOLE_DEBUG)
//					System.out.println("Finished Chunk! [" + (finished++) + "/"+CHUNKS*CHUNKS+"]");
//			}
//		}
//		if(debug != null)
//			debug[2] = Timer.getTime()-time;
//		time = Timer.getTime();
//		
//		int[][] bbold = new int[CHUNKS*CHUNKS][4];
//		for(int i = 0; i < CHUNKS*CHUNKS; i++)
//			for(int l = 0; l < 4; l++)
//				bbold[i][l] = bb[i][l];
//		int cww = cw;
//		int chh = ch;
//		if(CONSOLE_DEBUG)
//			System.out.println("Fitting Chunks...");
//		while(packl(bb, cww, chh, 8, 8, CHUNKS)) {
//			cww+=4;
//			chh+=4;
//		}
//		if(CONSOLE_DEBUG)
//			System.out.println("Applying Chunks!");
//		for(int i = 0; i < CHUNKS*CHUNKS; i++) {
//			int offset = amount/(CHUNKS*CHUNKS)*i;
//			int xoff = bb[i][0]-bbold[i][0];
//			int yoff = bb[i][1]-bbold[i][1];
//			for(int l = 0; l < amount/(CHUNKS*CHUNKS); l++) {
//				outposx[l+offset] += xoff;
//				outposy[l+offset] += yoff;
//			}
//		}
//		if(CONSOLE_DEBUG)
//			System.out.println("Finish!");
//		if(debug != null)
//			debug[3] = Timer.getTime()-time;
//		
//		if(bbdebug != null) 
//			System.arraycopy(bb, 0, bbdebug, 0, CHUNKS*CHUNKS);
//		
//		//Reverse sorting
//		applyI(sx, indices);
//		applyI(sy, indices);
//		applyI(outposx, indices);
//		applyI(outposy, indices);
//		
//		return new int[] {cww, chh};
//		
//	}
//	private static boolean packi(int amount, int offset, int[] sx, int[] sy, int[] outposx, int[] outposy, int bw, int bh, int offX, int offY, int incrX, int incrY, int[] boundingBox) {
//		boolean needsMoreSpace = false;
//		boundingBox[0] = Integer.MAX_VALUE;
//		boundingBox[1] = Integer.MAX_VALUE;
//		boundingBox[2] = 0;
//		boundingBox[3] = 0;
//		int x, y;
//		for(int i = offset; i < amount+offset; i++) { //Loop 1
//			boolean col = false;
//			int width = bw-sx[i];
//			int height = bh-sy[i];
//			for(x = 0; x < width; x+=incrX) { //Loop 2
//				outposx[i] = x+offX;
//				for(y = 0; y < height; y+=incrY) { //Loop 3
//					outposy[i] = y+offY;
//					
//					col = false;
//					for(int l = offset; l < i; l++)  { //Loop 4
//						if(AABB.intersects2D(outposx[i], outposy[i], outposx[i]+sx[i], outposy[i]+sy[i], outposx[l], outposy[l], outposx[l]+sx[l], outposy[l]+sy[l])) {
//							col = true;
//							y += sy[l];
//							break;
//						}
//					}
//					if(!col)
//						break;
//				}
//				if(!col)
//					break;
//			}
//			if(col)
//				needsMoreSpace = true;
//			boundingBox[0] = Math.min(boundingBox[0], outposx[i]);//X1
//			boundingBox[1] = Math.min(boundingBox[1], outposy[i]);//Y1
//			
//			boundingBox[2] = Math.max(boundingBox[2], outposx[i]+sx[i]);//X2
//			boundingBox[3] = Math.max(boundingBox[3], outposy[i]+sy[i]);//Y2
//			}
//		return needsMoreSpace;
//	}
//	private static boolean packl(int[][] bb, int bw, int bh, int incrX, int incrY, int CHUNKS) {
//		boolean needsMoreSpace = false;
//		for(int i = 0; i < CHUNKS*CHUNKS; i++) { //Loop 1
//			boolean col = false;
//			int width = bb[i][2]-bb[i][0];
//			int height = bb[i][3]-bb[i][1];
//			for(int x = 0; x < bw-width; x+=incrX) { //Loop 2
//				bb[i][0] = x;
//				bb[i][2] = x+width;
//				for(int y = 0; y < bh-height; y+=incrY) { //Loop 3
//					bb[i][1] = y;
//					bb[i][3] = y+height;
//					
//					col = false;
//					for(int l = 0; l < i; l++)  { //Loop 4
//						if(AABB.intersects2D(bb[i][0]-CHUNK_PADDING, bb[i][1]-CHUNK_PADDING, bb[i][2]+CHUNK_PADDING, bb[i][3]+CHUNK_PADDING, bb[l][0], bb[l][1], bb[l][2], bb[l][3])) {
//							col = true;
//							y += bb[l][3]-bb[l][1];
//							break;
//						}
//					}
//					if(!col)
//						break;
//				}
//				if(!col)
//					break;
//			}
//			if(col)
//				needsMoreSpace = true;
//			if(width > bw || height > bh)
//				needsMoreSpace = true;
//			}
//		return needsMoreSpace;
//	}
	private static void apply(int[] object, Integer[] indices) {
		int[] dest = new int[object.length];
		for(int i = 0; i < dest.length; i++)
			dest[i] = object[indices[i]];
		System.arraycopy(dest, 0, object, 0, dest.length);
		
	}
	private static void applyI(int[] object, Integer[] indices) {
		int[] dest = new int[object.length];
		for(int i = 0; i < dest.length; i++)
			dest[indices[i]] = object[i];
		System.arraycopy(dest, 0, object, 0, dest.length);
		
	}
	
	public static int[] blockPack(int amount, int[] sx, int[] sy, int[] outposx, int[] outposy) {
		Integer[] indices = new Integer[amount]; //Simple sorting by width
		for(int i = 0; i < amount; i++)
			indices[i] = i;
		Arrays.sort(indices, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				double vo2 = sx[o2];
				double vo1 = sx[o1];
				return (int) -Math.signum(vo1-vo2);
			}
		});
		apply(outposy, indices);//Applying the sort
		apply(outposx, indices);
		apply(sx, indices);
		apply(sy, indices);
		
		
		int[] size = new int[2];
		double ratio = 0; //Calculate Average Width/Height ratio
		for(int i = 0; i < amount; i++)
			ratio += (double)sx[i]/sy[i];
		ratio /= amount;
		int w = (int) (Math.sqrt(amount)/ratio)+1;
		int h = (int) (Math.sqrt(amount)*ratio)+1;
		int currentY = 0;
		int i = 0;
		for(int b = 0; b < w; b++) {
			int rowHeight = 0;
			int currentX = 0;
			int extend = 0;
			for(int a = 0; a < h+extend; a++) {
				if(i < amount) {
					outposx[i] = currentX;
					outposy[i] = currentY;
					currentX += sx[i];
					rowHeight = Math.max(sy[i], rowHeight);
					size[0] = Math.max(size[0], currentX);
					if(i+1 < amount && a+1 == h+extend) //If there is a next one and this one is the last
						if(currentX+sx[i+1] < size[0]) //If next one would fit, add it.
							extend++;
					i++;
				}
			}
			currentY += rowHeight;
			currentX = 0;
			size[1] = Math.max(size[1], currentY);
		}
		
		
		applyI(outposy, indices);//Reversing the sort
		applyI(outposx, indices);
		applyI(sx, indices);
		applyI(sy, indices);
		return size;
	}
	
}
