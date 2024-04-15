package Kartoffel.Licht.Rendering.Animation;

import java.util.ArrayList;
import java.util.List;

import Kartoffel.Licht.Java.namable;
import Kartoffel.Licht.Rendering.Shaders.Shader;
import Kartoffel.Licht.Tools.Tools;

/**
 * A class that can interpolate between different animations
 *
 */
public class AnimationManager implements namable, Animation{
	
	private String name = "Animation Manager";

	private List<SkeletalAnimation> animations = new ArrayList<>();
	private int animation = 0;
	private double time;
	
	private double TransTime = 0.1;
	
	
	public AnimationManager(SkeletalAnimation... anims) {
		animations.addAll(List.of(anims));
	}
	
	private int nextAnim = 0;
	private int nextAnim2 = 1;
	private double tt = 0;
	private double ff = 0;
	@Override
	public void getTransformations(Shader sh) {
		if(animation < animations.size()) {
			SkeletalAnimation a = animations.get(nextAnim2);
			SkeletalAnimation b = animations.get(animation);
			
			double t1 = getTime(time, a);
			double t2 = getTime(time, b);
			
			int frame1 = (int)t1;
			int frame2 = (int)t2;
			
			float fract = (float) (t1-frame1);//Same for both
			
			for(int i = 0; i < a.BONES_AMOUNT; i++) {
				sh.setUniformMatrix4f("anim_1["+i+"]", a.FRAMES[(frame1+1) % a.FRAMES.length][i]);
			}
			
			for(int i = 0; i < b.BONES_AMOUNT; i++) {
				sh.setUniformMatrix4f("anim_0["+i+"]", b.FRAMES[frame2][i]);
			}
			//Playing animations
			if(time > 1 && pAnim != -1) {
				this.animation = this.pAnim;
				this.pAnim = -1;
				this.time = 0;
			}
			
			
			if(tt > TransTime) { //When no transition between animations
				if(nextAnim != animation) {	//On transition start
					tt = 0; // set counter to 0
					ff = time; //set begin time
				}
				nextAnim = animation;
				sh.setUniformFloat("anim_time", fract);
			}
			if(tt <= TransTime)  { //When transition between animations
				tt = time-ff; //set counter to be the time passed since transition start
				sh.setUniformFloat("anim_time", (float) (1-tt/TransTime)); //set trans_time to be counter of TransTime
				if(tt > TransTime || nextAnim2 == animation) {//On transition end
					nextAnim2 = nextAnim; //set current animation
					tt = Integer.MAX_VALUE; // set counter to max value
				}
			}
		}
	}
	private double getTime(double time, SkeletalAnimation a) {
		double timeS = time; //How long the animation is in Seconds
		double timeT = timeS*a.TICKS_PER_SECOND; //How long the animation is in Ticks
		return timeT % a.FRAMES.length; //Loop around
	}
	
	private int pAnim = -1;
	/**
	 * switches to the animation corresponding to the given id and returns to playing the previous one.<br>
	 * use 'AnimationManager.setCurrentAnimation(int animation)' to permanently set it
	 */
	public void playAnimation(int index) {
		if(pAnim == -1) {
			this.pAnim = this.animation;
			this.animation = index;
			this.time = 0;
		}
	}
	/**
	 * returns all animations contained in this manager
	 */
	public List<SkeletalAnimation> getAnimations() {
		return animations;
	}
	/*
	 * returns the id of the currently playing animation
	 */
	public int getCurrentAnimation() {
		return animation;
	}
	/**
	 * switches to the animation corresponding to the given id.<br>
	 * use 'AnimationManager.playAnimation(int animation)' to play it once
	 * @param animation
	 */
	public void setCurrentAnimation(int animation) {
		if(pAnim == -1)
			this.animation = animation;
	}
	/**
	 * sets the current time of the animation
	 */
	public void setTime(double time) {
		this.time = Math.abs(time);
	}
	/**
	 * returns the current time of the animation
	 */
	public double getTime() {
		return time;
	}
	
	@Override
	public String toString() {
		return ("["+animation+"/"+animations.size()+"]->"+Tools.format(time)+"f, ") + (animation >= animations.size() ? "-" : animations.get(animation).toString());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

}
