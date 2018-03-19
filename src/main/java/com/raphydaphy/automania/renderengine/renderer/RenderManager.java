package main.java.com.raphydaphy.automania.renderengine.renderer;

import main.java.com.raphydaphy.automania.models.TexturedModel;
import main.java.com.raphydaphy.automania.render.Camera;
import main.java.com.raphydaphy.automania.render.Light;
import main.java.com.raphydaphy.automania.render.ModelTransform;
import main.java.com.raphydaphy.automania.renderengine.shader.ObjectShader;
import main.java.com.raphydaphy.automania.renderengine.shader.TerrainShader;
import main.java.com.raphydaphy.automania.renderengine.shadow.ShadowMapMasterRenderer;
import main.java.com.raphydaphy.automania.terrain.Terrain;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.util.*;

public class RenderManager
{
    public static final float FOV = 70f;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 1000f;

    private static final Vector3f SKY = new Vector3f(0.5f, 0.5f, 0.5f);

    private ObjectShader objectShader;
    private ObjectRenderer objectRenderer;

    private TerrainShader terrainShader;
    private TerrainRenderer terrainRenderer;

    private ShadowMapMasterRenderer shadowMapRenderer;

    private Matrix4f projection;
    private Map<TexturedModel, List<ModelTransform>> objects = new HashMap<>();
    private List<Terrain> terrains = new ArrayList<>();

    public RenderManager(Camera camera)
    {
        enableCulling();
        initProjection();

        objectShader = new ObjectShader();
        objectRenderer = new ObjectRenderer(objectShader, projection);

        terrainShader = new TerrainShader();
        terrainRenderer = new TerrainRenderer(terrainShader, projection);

        shadowMapRenderer = new ShadowMapMasterRenderer(camera);
    }

    private void initProjection()
    {
        // Aspect ratio of the camera, based on the width and height so that it can scale with screen resolution
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();

        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
        float x_scale = y_scale / aspectRatio;

        // The total z length which this camera can see objects within
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        // Setup the projection with a simple view frustum based on the near plane and far plane distances.
        projection = new Matrix4f();
        projection.m00 = x_scale;
        projection.m11 = y_scale;
        projection.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projection.m23 = -1;
        projection.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projection.m33 = 0;
    }

    public void recalculateProjection()
    {
        initProjection();

        objectShader.bind();
        objectShader.loadProjectionMatrix(projection);
        objectShader.unbind();

        terrainShader.bind();
        terrainShader.loadProjectionMatrix(projection);
        terrainShader.unbind();
    }

    private void prepare()
    {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClearColor(SKY.x, SKY.y, SKY.z, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	    GL13.glActiveTexture(GL13.GL_TEXTURE5);
	    GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
    }

    public void render(List<Light> lights, Camera camera)
    {
        prepare();

        objectShader.bind();

        objectShader.loadSkyColor(SKY);
        objectShader.loadLights(lights);
        objectShader.loadViewMatrix(camera);

        objectRenderer.render(objects);

        objectShader.unbind();

        terrainShader.bind();

        terrainShader.bindShadowMapSampler();
        terrainShader.loadSkyColor(SKY);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);

        terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());

        terrainShader.unbind();

        objects.clear();
        terrains.clear();
    }

    // Adds all the models to the batch, but things will break if the list contains multiple different models!
    public void processSimilarObjects(List<ModelTransform> list)
    {
    	if (list.size() > 0)
	    {
	        TexturedModel model = list.get(0).getModel();
	        List<ModelTransform> batch = objects.get(model);
	        if (batch != null)
	        {
	            batch.addAll(list);
	        }
	        else
	        {
	            batch = new ArrayList<>();
	            batch.addAll(list);
	            objects.put(model, batch);
	        }
	    }
    }

    public void processObject(ModelTransform object)
    {
        TexturedModel model = object.getModel();
        List<ModelTransform> batch = objects.get(model);
        if (batch != null)
        {
            batch.add(object);
        }
        else
        {
            batch = new ArrayList<>();
            batch.add(object);
            objects.put(model, batch);
        }
    }

    public void processTerrain(Terrain terrain)
    {
        terrains.add(terrain);
    }

    public void renderShadowMap(Light sun)
    {
	    shadowMapRenderer.render(objects, sun);
    }

    public void cleanup()
    {
        objectShader.cleanup();
        terrainShader.cleanup();
	    shadowMapRenderer.cleanUp();
    }

    public int getShadowMapTexture()
    {
    	return shadowMapRenderer.getShadowMap();
    }

    public static void enableCulling()
    {
        // This will prevent any triangles with normals that face away from the camera from being rendered
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling()
    {
        // This is useful when rendering transparent models as part of them will not render if culling is enabled
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public Matrix4f getProjectionMatrix()
    {
        return projection;
    }


}
