package Kartoffel.Licht.Tools;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import Kartoffel.Licht.Input.CursorCallback;
import Kartoffel.Licht.Input.CursorScrollCallback;
import Kartoffel.Licht.Input.KeyInputCallback;
import Kartoffel.Licht.Rendering.Camera;
import Kartoffel.Licht.Rendering.GraphicWindow;

public class CameraUtils {
	
	public static float speedM = 1f, speedR = 1;
	
	private static Matrix4f mat = new Matrix4f();
	public static Vector4f getRay(float deviceX, float deviceY, Camera cam) {
		Vector4f coords = new Vector4f(deviceX, deviceY, -1, 1);
		//view space
		cam.getProjection().invert(mat).transform(coords);
		//World space
		cam.getViewMatrix().transpose(mat).transform(coords);
		coords.w = 0;
		coords.normalize();
		return coords;
	}
	
	public static Vector4f getRay(Vector4f coords, Camera cam) {
		//World space
		cam.getViewMatrix().transform(coords);
		//view space
		cam.getProjection().transform(coords);
		
		return coords;
	}
	
	public static void update(Camera cam, GraphicWindow window, double delta) {
		update(cam, window.getCallback_Key(), window.getCallback_Cursor(), delta);
	}
	public static void update(Camera cam, KeyInputCallback call, CursorCallback call2, double delta) {

		cam.getRotation().y -= call2.getMotionX()*speedR;
		cam.getRotation().x -= call2.getMotionY()*speedR;
		int rx = 0, ry = 0;
		float speed = 0;
		if(call.isKeyDown("W")) {
			ry += 1;
			speed = speedM;
		}
		if(call.isKeyDown("A")) {
			rx += -1;
			speed = speedM;
		}
		if(call.isKeyDown("S")) {
			ry += -1;
			speed = speedM;
		}
		if(call.isKeyDown("D")) {
			rx += 1;
			speed = speedM;
		}
		if(call.isKeyDown("SPACE")) {
			cam.getPosition().y += speedM*delta;
		}
		if(call.isKeyDown("LEFT_SHIFT")) {
			cam.getPosition().y += -speedM*delta;
		}
		if(call.isKeyDown("LEFT_CONTROL")) {
			speed *= 2;
		}
	
		float relativeDir = (float) (Math.atan2(rx, ry) - Math.PI / 2);
		cam.getPosition().add(  new Vector3f(
				(float)Math.cos(relativeDir + Math.toRadians(cam.getRotation().y))*speed,
				(float)0,
				(float)Math.sin(relativeDir + Math.toRadians(cam.getRotation().y))*speed)
				.mul((float)delta));
	
	}
	
	public static double updateR(Camera cam, GraphicWindow window, double distance, double delta) {
		return updateR(cam, window.getCallback_Key(), window.getCallback_Cursor(), window.getCallback_CursorScroll(), distance, delta);
	}
	
	
	public static double updateR(Camera cam, KeyInputCallback call, CursorCallback call2, CursorScrollCallback call3, double distance, double delta) {
		cam.getRotation().y -= call2.getMotionX()*speedR;
		cam.getRotation().x -= call2.getMotionY()*speedR;
		int rx = 0, ry = 0;
		float speed = 0;
		if(call.isKeyDown("W")) {
			ry += 1;
			speed = speedM;
		}
		if(call.isKeyDown("A")) {
			rx += -1;
			speed = speedM;
		}
		if(call.isKeyDown("S")) {
			ry += -1;
			speed = speedM;
		}
		if(call.isKeyDown("D")) {
			rx += 1;
			speed = speedM;
		}
		if(call.isKeyDown("SPACE")) {
			cam.getPosition().y += speedM*delta;
		}
		if(call.isKeyDown("LEFT_SHIFT")) {
			cam.getPosition().y += -speedM*delta;
		}
		if(call.isKeyDown("LEFT_CONTROL")) {
			speed *= 2;
		}
		float relativeDir = (float) (Math.atan2(rx, ry) - Math.PI / 2);
		cam.getPosition().add(  new Vector3f(
				(float)Math.cos(relativeDir + Math.toRadians(cam.getRotation().y))*speed,
				(float)0,
				(float)Math.sin(relativeDir + Math.toRadians(cam.getRotation().y))*speed)
				.mul((float)delta));
		cam.setDistance((float) distance);
		return distance+call3.MY;
	}



	public static void lockPitch(Camera camera, float fm, float fmm) {
		if(camera.getRotation().x > fm)
			camera.getRotation().x = fm;
		if(camera.getRotation().x < fmm)
			camera.getRotation().x = fmm;
	}



}
