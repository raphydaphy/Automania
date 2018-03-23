package main.java.com.raphydaphy.automania.renderengine.animation;

import main.java.com.raphydaphy.automania.renderengine.DisplayManager;
import org.lwjgl.util.vector.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class Animator
{
	private final AnimatedModel object;

	private Animation currentAnimation;
	private float currentTime = 0;

	public Animator(AnimatedModel object)
	{
		this.object = object;
	}

	public void doAnimation(Animation animation)
	{
		this.currentTime = 0;
		this.currentAnimation = animation;
	}

	public void update()
	{
		if (currentAnimation == null)
		{
			return;
		}

		increaseTime();
		Map<String, Matrix4f> currentPose = calculateCurrentAnimationPose();
		applyPoseToJoints(currentPose, object.root, new Matrix4f());
	}

	private void increaseTime()
	{
		currentTime += DisplayManager.getFrameTimeSeconds();
		if (currentTime > currentAnimation.seconds)
		{
			this.currentTime %= currentAnimation.seconds;
		}
	}

	private Map<String, Matrix4f> calculateCurrentAnimationPose()
	{
		KeyFrame[] frames = getPreviousAndNextFrames();
		float progression = calculateProgression(frames[0], frames[1]);
		return interpolatePoses(frames[0], frames[1], progression);
	}

	private void applyPoseToJoints(Map<String, Matrix4f> currentPose, Joint joint, Matrix4f parentTransform)
	{
		Matrix4f currentLocalTransform = currentPose.get(joint.name);
		Matrix4f currentTransform = Matrix4f.mul(parentTransform, currentLocalTransform, null);
		for (Joint childJoint : joint.children)
		{
			applyPoseToJoints(currentPose, childJoint, currentTransform);
		}
		Matrix4f.mul(currentTransform, joint.getInverseBindTransform(), currentTransform);
		joint.setAnimatedTransform(currentTransform);
	}

	private KeyFrame[] getPreviousAndNextFrames()
	{
		KeyFrame[] allFrames = currentAnimation.keyFrames;
		KeyFrame previousFrame = allFrames[0];
		KeyFrame nextFrame = allFrames[0];
		for (int i = 1; i < allFrames.length; i++)
		{
			nextFrame = allFrames[i];
			if (nextFrame.timeStamp > currentTime)
			{
				break;
			}
			previousFrame = allFrames[i];
		}
		return new KeyFrame[]{previousFrame, nextFrame};
	}

	private float calculateProgression(KeyFrame previousFrame, KeyFrame nextFrame)
	{
		float totalTime = nextFrame.timeStamp - previousFrame.timeStamp;
		float currentTime = this.currentTime - previousFrame.timeStamp;
		return currentTime / totalTime;
	}

	private Map<String, Matrix4f> interpolatePoses(KeyFrame previousFrame, KeyFrame nextFrame, float progression)
	{
		Map<String, Matrix4f> currentPose = new HashMap<String, Matrix4f>();
		for (String jointName : previousFrame.pose.keySet())
		{
			JointTransform previousTransform = previousFrame.pose.get(jointName);
			JointTransform nextTransform = nextFrame.pose.get(jointName);
			JointTransform currentTransform = JointTransform.interpolate(previousTransform, nextTransform, progression);
			currentPose.put(jointName, currentTransform.getLocalTransform());
		}
		return currentPose;
	}
}
