package main.java.com.raphydaphy.automania.renderengine.animation;

import java.util.Map;

public class KeyFrame
{
	public final float timeStamp;
	public final Map<String, JointTransform> pose;

	public KeyFrame(float timeStamp, Map<String, JointTransform> jointKeyFrames)
	{
		this.timeStamp = timeStamp;
		this.pose = jointKeyFrames;
	}
}
