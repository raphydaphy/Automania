package main.java.com.raphydaphy.automania.renderengine.animation;

public class Animation
{
	public final float seconds;
	public final KeyFrame[] keyFrames;

	public Animation(float seconds, KeyFrame[] frames)
	{
		this.keyFrames = frames;
		this.seconds = seconds;
	}
}
