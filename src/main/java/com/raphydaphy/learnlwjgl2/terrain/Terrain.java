package main.java.com.raphydaphy.learnlwjgl2.terrain;

import main.java.com.raphydaphy.learnlwjgl2.models.RawModel;
import main.java.com.raphydaphy.learnlwjgl2.renderengine.load.Loader;
import main.java.com.raphydaphy.learnlwjgl2.renderengine.shader.Material;

public class Terrain
{
    private static final float SIZE = 800;
    private static final int VERTEX_COUNT = 128;

    private final float x;
    private final float z;

    private RawModel mesh;
    private Material texture;

    public Terrain(int gridX, int gridZ, Loader loader, Material texture)
    {
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.texture = texture;

        mesh = generateMesh(loader);
    }

    public float getX()
    {
        return x;
    }

    public float getZ()
    {
        return z;
    }

    public RawModel getMesh()
    {
        return mesh;
    }

    public Material getTexture()
    {
        return texture;
    }

    private RawModel generateMesh(Loader loader)
    {
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] uvs = new float[count*2];
        int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        int vertexPointer = 0;
        for(int i=0;i<VERTEX_COUNT;i++){
            for(int j=0;j<VERTEX_COUNT;j++){
                vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
                vertices[vertexPointer*3+1] = 0;
                vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
                normals[vertexPointer*3] = 0;
                normals[vertexPointer*3+1] = 1;
                normals[vertexPointer*3+2] = 0;
                uvs[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
                uvs[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int indexPointer = 0;
        for(int gz=0;gz<VERTEX_COUNT-1;gz++){
            for(int gx=0;gx<VERTEX_COUNT-1;gx++){
                int topLeft = (gz*VERTEX_COUNT)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
                int bottomRight = bottomLeft + 1;
                indices[indexPointer++] = topLeft;
                indices[indexPointer++] = bottomLeft;
                indices[indexPointer++] = topRight;
                indices[indexPointer++] = topRight;
                indices[indexPointer++] = bottomLeft;
                indices[indexPointer++] = bottomRight;
            }
        }
        return loader.loadToVAO(vertices, uvs, normals, indices);
    }
}
