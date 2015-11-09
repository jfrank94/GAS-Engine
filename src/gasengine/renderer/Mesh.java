package gasengine.renderer;

import java.io.IOException;
import java.util.List;

/**
 * Created by Matthew on 11/7/2015.
 */
public class Mesh {
    private MeshData data;

    public Mesh() {}
    public Mesh(String objFile){
        data = new MeshData(objFile);
        try
        {
            data.loadOBJ();
        }
        catch(IOException ex)
        {
            System.err.println(ex.toString());
        }
    }

    public List<MeshData.Verts> getVertices()
    {
        return data.getVertices();
    }
    public List<MeshData.Face> getFaces()
    {
        return data.getFaces();
    }


    public void sUnitTest()
    {
        List<MeshData.Verts> vertices = this.getVertices();
        List<MeshData.Face> faces = this.getFaces();
        for(int i = 0; i < vertices.size(); ++i)
            System.out.println("v " + vertices.get(i).vert.x + " " +
                    vertices.get(i).vert.y + " " + vertices.get(i).vert.z);
        System.out.print("\n\n");
        for(int i = 0; i < faces.size(); ++i) {
            System.out.print("f ");
            for (int j = 0; j < faces.get(i).indices.size(); ++j) {
                System.out.print(faces.get(i).indices.get(j) + " ");
            }
            System.out.println();
        }
    }

}
