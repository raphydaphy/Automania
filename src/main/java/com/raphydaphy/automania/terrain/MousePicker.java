package main.java.com.raphydaphy.automania.terrain;

import main.java.com.raphydaphy.automania.render.Camera;
import main.java.com.raphydaphy.automania.util.MathUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.*;

public class MousePicker
{
	private Vector3f currentRay;

	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Camera camera;

	public MousePicker(Camera camera, Matrix4f projectionMatrix)
	{
		this.camera = camera;
		this.projectionMatrix = projectionMatrix;
		this.viewMatrix = MathUtils.createViewMatrix(camera);
	}

	public Vector3f getCurrentRay()
	{
		return currentRay;
	}

	public void update()
	{
		viewMatrix = MathUtils.createViewMatrix(camera);
		currentRay = doRaycast();
	}

	// This does everything required to turn world coordinates into viewport coordinates in reverse
	// The resulting vector is a 3D raycast from the mouse position on the screen
	private Vector3f doRaycast()
	{
		float viewportMouseX = Mouse.getX();
		float viewportMouseY = Mouse.getY();

		Vector2f normalizedMouseCoords = normalizeMouseCoords(viewportMouseX, viewportMouseY);

		Vector4f clipSpace = new Vector4f(normalizedMouseCoords.x, normalizedMouseCoords.y, -1f, 1f);

		Vector4f eyeSpace = clipToEyeSpace(clipSpace);

		Vector3f worldSpace = eyeToWorldSpace(eyeSpace);

		return worldSpace;
	}

	// Instead of the mouse coordinates ranging from 0 to the screen width or height, this converts them from -1 to 1
	private Vector2f normalizeMouseCoords(float mouseX, float mouseY)
	{
		float x = (2f * mouseX) / Display.getWidth() - 1;
		float y = (2f * mouseY) / Display.getHeight() - 1;
		return new Vector2f(x,y);
	}

	// This will convert the position in clip-space to a vector from the viewpoint of the camera
	private Vector4f clipToEyeSpace(Vector4f clipSpace)
	{
		Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
		Vector4f eyeSpace = Matrix4f.transform(invertedProjection, clipSpace, null);
		return new Vector4f(eyeSpace.x, eyeSpace.y, -1f, 0f);
	}

	// This converts the camera-space vector into something that shares the same coordinate system as all other objects in the world
	private Vector3f eyeToWorldSpace(Vector4f eyeSpace)
	{
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		Vector4f raycast = Matrix4f.transform(invertedView, eyeSpace, null);
		Vector3f ray3D = new Vector3f(raycast.x, raycast.y, raycast.z);
		ray3D.normalise();
		return ray3D;
	}
}
