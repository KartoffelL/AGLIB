package Kartoffel.Licht.Tools;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.nfd.NFDFilterItem;
import org.lwjgl.util.nfd.NFDPathSetEnum;
import org.lwjgl.util.nfd.NativeFileDialog;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import Kartoffel.Licht.Java.Color;

public class FileDialog {
	
	public final static int NOTIFY_INFO = 0,
							NOTIFY_WARNING = 1,
							NOTIFY_ERROR = 2;
	
	volatile private static List<Consumer<?>> ready = new ArrayList<>();
	volatile private static List<Object> data = new ArrayList<>();
	
	private static List<Thread> threads = new ArrayList<>();
	
	public static void openColor(Consumer<Color> c, Color defaultColor) {
		openColor(c, String.format("#%02x%02x%02x", defaultColor.getRed(), defaultColor.getGreen(), defaultColor.getBlue()));
	}
	
	public static void openColor(Consumer<Color> c, String defaultColor) {
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try (MemoryStack stack = MemoryStack.stackPush()) {
					ByteBuffer b = stack.calloc(3);
					String result = TinyFileDialogs.tinyfd_colorChooser(null, defaultColor, null, b);
					data.add(new Color(Integer.parseInt(result, 1, 7, 16)));
					ready.add(c);
				}
			}
		});
		t.start();
		threads.add(t);
	}
	
	public static void openText(Consumer<String> c, String def, String title, String question) {
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try (MemoryStack stack = MemoryStack.stackPush()) {
					data.add(TinyFileDialogs.tinyfd_inputBox(title, question, def));
					ready.add(c);
				}
			}
		});
		t.start();
		threads.add(t);
	}
	public static void notify(int type, String title, String message) {
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				TinyFileDialogs.tinyfd_notifyPopup(title, message, type == NOTIFY_ERROR ? "error" : type == NOTIFY_WARNING ? "warning" : "info");
			}
		});
		t.start();
		threads.add(t);
	}
	
	public static void openSingle(Consumer<String> c, String defaultPath, String[][] filters) { 
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try (MemoryStack stack = MemoryStack.stackPush()) {
					NativeFileDialog.NFD_Init();
					PointerBuffer pb = PointerBuffer.allocateDirect(1);
					org.lwjgl.util.nfd.NFDFilterItem.Buffer buffer = buffer(stack, filters);
					int status = NativeFileDialog.NFD_OpenDialog(pb, buffer, defaultPath);
					data.add((status == NativeFileDialog.NFD_OKAY ? "O*"+pb.getStringASCII() : status == NativeFileDialog.NFD_CANCEL ? "C*-": "E*"+NativeFileDialog.NFD_GetError()));
					ready.add(c);
					if(status == NativeFileDialog.NFD_OKAY)
						NativeFileDialog.NFD_FreePath(pb.get(0));
				}
			}
		});
		t.start();
		threads.add(t);
	}
	
	public static void openMultiple(Consumer<String[]> c, String defaultPath, String[][] filters) { 
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try (MemoryStack stack = MemoryStack.stackPush()) {
					NativeFileDialog.NFD_Init();
					PointerBuffer pb = PointerBuffer.allocateDirect(1);
					org.lwjgl.util.nfd.NFDFilterItem.Buffer buffer = buffer(stack, filters);
					int status = NativeFileDialog.NFD_OpenDialogMultiple(pb, buffer, defaultPath);
					
					data.add((status == NativeFileDialog.NFD_OKAY ? mul(pb, stack) : status == NativeFileDialog.NFD_CANCEL ? new String[] {"C*-"}: new String[] {"E*"+NativeFileDialog.NFD_GetError()}));
					ready.add(c);
					if(status == NativeFileDialog.NFD_OKAY)
						NativeFileDialog.NFD_FreePath(pb.get(0));
				}
			}
		});
		t.start();
		threads.add(t);
	}
	
	private static String[] mul(PointerBuffer pb, MemoryStack stack) {
		long pathset = pb.get(0);
		
		NFDPathSetEnum pse = NFDPathSetEnum.calloc(stack);
		NativeFileDialog.NFD_PathSet_GetEnum(pathset, pse);
		
		 List<String> paths = new ArrayList<String>();
         while (NativeFileDialog.NFD_PathSet_EnumNext(pse, pb) == NativeFileDialog.NFD_OKAY && pb.get(0) != 0) {
             paths.add(pb.getStringUTF8(0));
             NativeFileDialog.NFD_PathSet_FreePath(pb.get(0));
         }

         NativeFileDialog.NFD_PathSet_FreeEnum(pse);
         NativeFileDialog.NFD_PathSet_Free(pathset);
		
		return (String[]) paths.toArray(new String[paths.size()]);
	}
	
	public static void openFolder(Consumer<String> c, String defaultPath) { 
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try (MemoryStack stack = MemoryStack.stackPush()) {
					NativeFileDialog.NFD_Init();
					PointerBuffer pb = PointerBuffer.allocateDirect(1);
					int status = NativeFileDialog.NFD_PickFolder(pb, defaultPath);
					data.add((status == NativeFileDialog.NFD_OKAY ? "O*"+pb.getStringASCII() : status == NativeFileDialog.NFD_CANCEL ? "C*-": "E*"+NativeFileDialog.NFD_GetError()));
					ready.add(c);
					if(status == NativeFileDialog.NFD_OKAY)
						NativeFileDialog.NFD_FreePath(pb.get(0));
				}
			}
		});
		t.start();
		threads.add(t);
	}
	
	public static void openSave(Consumer<String> c, String defaultPath, String defaultName, String[][] filters) { 
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try (MemoryStack stack = MemoryStack.stackPush()) {
					NativeFileDialog.NFD_Init();
					PointerBuffer pb = PointerBuffer.allocateDirect(1);
					org.lwjgl.util.nfd.NFDFilterItem.Buffer buffer = buffer(stack, filters);
					int status = NativeFileDialog.NFD_SaveDialog(pb, buffer, defaultPath, defaultName);
					
					data.add((status == NativeFileDialog.NFD_OKAY ? "O*"+pb.getStringASCII() : status == NativeFileDialog.NFD_CANCEL ? "C*-": "E*"+NativeFileDialog.NFD_GetError()));
					ready.add(c);
					if(status == NativeFileDialog.NFD_OKAY)
						NativeFileDialog.NFD_FreePath(pb.get(0));
				}
			}
		});
		t.start();
		threads.add(t);
	}
	
	public static void update() {
		for(int i = 0; i < ready.size(); i++)
			accept(ready.remove(i), data.remove(i));
	}
	
	@SuppressWarnings("unchecked")
	private static <T> void accept(Consumer<?> consumer, T Object) {
		((Consumer<T>)consumer).accept(Object);
	}
	
	public static void free() {
		for(Thread t : threads)
			t.interrupt();
	}
	
	private static org.lwjgl.util.nfd.NFDFilterItem.Buffer buffer(MemoryStack stack, String[][] f) {
		 NFDFilterItem.Buffer filters = NFDFilterItem.malloc(f.length);
		 for(int i = 0; i < f.length; i++) {
			 if(f[i].length == 0) {
				 Tools.err("Filter has no name! Use: new String[][] {{name1, spec11, spec12}, {name2, spec11, spec12, spec13}}");
				 return null;
			 }
			 String s = "";
			 for(int l = 1; l < f[i].length; l++)
				 s += (l == 1 ? "" : ",") + f[i][l];
         filters.get(i)
             .name(stack.UTF8(f[i][0]))
             .spec(stack.UTF8(s));
		 }
         return filters;
	}

}
