package gasengine.graphics;

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

    public List<MeshData.Verts> getVertices() {return data.getVertices();}
    public List<MeshData.Face> getFaces() {return data.getFaces();}
    public List<MeshData.TextCoord> getTextureCoords() {return data.getTextureCoords();}
    public List<MeshData.Normal> getNormals() {return data.getNormals();}
    public int getFacesDim(){return data.getFacesDim();}


    public void sUnitTest()
    {
        List<MeshData.TextCoord> vertices = this.getTextureCoords();
        List<MeshData.Normal> faces = this.getNormals();
        for(int i = 0; i < vertices.size(); ++i)
            //System.out.println("v " + vertices.get(i).u + " " + vertices.get(i).v);
        System.out.print("\n\n");
        for(int i = 0; i < faces.size(); ++i) {
            System.out.print("f " + faces.get(i).norm);
        }
    }

}