package main.java.com.raphydaphy.automania.render;

import main.java.com.raphydaphy.automania.entity.Player;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera
{
    private static final float speed = 0.5f;

    private Vector3f position = new Vector3f(0, 5, 0);
    private float pitch, yaw, roll;

    private float distanceFromPlayer = 15;
    private float angleAroundPlayer = 0;

    private Player player;

    public Camera(Player player)
    {
        this.player = player;
        pitch = 5;
    }

    public void move()
    {
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();

        float horizontalDist = (float)Math.cos(Math.toRadians(pitch)) * distanceFromPlayer;
        float verticalDist = (float)Math.sin(Math.toRadians(pitch)) * distanceFromPlayer;

        calculatePosition(horizontalDist, verticalDist);
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public float getPitch()
    {
        return pitch;
    }

    public float getYaw()
    {
        return yaw;
    }

    public float getRoll()
    {
        return roll;
    }

    private void calculateZoom()
    {
        float zoomLevel = Mouse.getDWheel() * 0.02f;
        distanceFromPlayer -= zoomLevel;

        if (distanceFromPlayer < 2)
        {
            distanceFromPlayer = 2;
        }
        else if (distanceFromPlayer > 25)
        {
            distanceFromPlayer = 25;
        }
    }

    private void calculatePitch()
    {
        if (Mouse.isButtonDown(1))
        {
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;

            if (pitch < 5)
            {
                pitch = 5;
            }

            if (pitch > 90)
            {
                pitch = 90;
            }
        }
    }

    private void calculateAngleAroundPlayer()
    {
        if (Mouse.isButtonDown(1))
        {
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer -= angleChange;
        }
    }

    private void calculatePosition(float horizontalDist, float verticalDist)
    {
        Vector3f playerPos = player.data.getTransform().getPosition();

        position.y = playerPos.y + verticalDist + 4f;

        float cameraAngle = player.data.getTransform().getRotY() + angleAroundPlayer;

        yaw = 180 - cameraAngle;

        float xDist = (float)Math.sin(Math.toRadians(cameraAngle)) * horizontalDist;
        float zDist = (float)Math.cos(Math.toRadians(cameraAngle)) * horizontalDist;

        position.x = playerPos.x - xDist;
        position.z = playerPos.z - zDist;
    }
}
