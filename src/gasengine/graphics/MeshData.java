package gasengine.graphics;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by Matthew on 11/7/2015.
 */
public class MeshData {
    public static class Verts {
        //float x, y, z;
        public Vector3f vert;
    }
    public static class TextCoord {
        //public float u, v;
        public Vector2f texts;
    }
    public static class Normal {
        //float x, y, z;
        public Vector3f norm;
    }
    public static class Face {
        //int posIndex, textIndex, normIndex;
        public List<Integer> vertexIndices;
        public List<Integer> textureIndices;
        public List<Integer> normalIndices;
    }
    private List<Verts> vertices;
    private List<TextCoord> textureCoords;
    private List<Normal> normals;
    private List<Face> faces;
    private String objFile;
    private int facesDim;

    public MeshData(String filename){
        objFile = filename;
        vertices = new ArrayList<>();
        textureCoords = new ArrayList<>();
        normals = new ArrayList<>();
        faces = new ArrayList<> ();
    }

    public List getVertices()
    {
        return vertices;
    }
    public List getTextureCoords()
    {
        return textureCoords;
    }
    public List getNormals()
    {
        return normals;
    }
    public List getFaces()
    {
        return faces;
    }
    public int getFacesDim(){return facesDim;}

    public void loadMTL() {}
    public void loadOBJ() throws IOException {
        Path path = Paths.get("obj/", objFile);
        try (Stream<String> vertLines = Files.lines(path)
            .filter(s -> s.startsWith("v "))) {
            List<String> vertLine = vertLines.collect(Collectors.toList());
            vertLine.forEach(line -> {
                Verts newVert = new Verts(); newVert.vert = new Vector3f();
                String[] spLine = line.split(" ");
                for(int i = 1; i < spLine.length; ++i) {
                    try{
                        float f = Float.parseFloat(spLine[i]);
                        switch(i){
                            case 1:
                                newVert.vert.x = f;
                                break;
                            case 2:
                                newVert.vert.y = f;
                                break;
                            case 3:
                                newVert.vert.z = f;
                                break;
                            default:
                                break;
                        }
                    }
                    catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
                vertices.add(newVert);
            });
        }
        try (Stream<String> textLines = Files.lines(path)
            .filter(s -> s.startsWith("vt "))) {
            List<String> textLine = textLines.collect(Collectors.toList());
            textLine.forEach(line -> {
                TextCoord coord = new TextCoord(); coord.texts = new Vector2f();
                String[] spLine = line.split(" ");
                for(int i = 1; i < spLine.length; ++i) {
                    float f = Float.parseFloat(spLine[i]);
                    switch(i){
                        case 1:
                            coord.texts.x = f;
                            break;
                        case 2:
                            coord.texts.y = f;
                            break;
                        default:
                            break;
                    }
                }
                textureCoords.add(coord);
            });
        }
        try (Stream<String> normLines = Files.lines(path)
            .filter(s -> s.startsWith("vn "))) {
            List<String> normLine = normLines.collect(Collectors.toList());
            normLine.forEach(line -> {
                Normal norm = new Normal(); norm.norm = new Vector3f();
                String[] spLine = line.split(" ");
                for(int i = 1; i < spLine.length; ++i) {
                    float f = Float.parseFloat(spLine[i]);
                    switch(i){
                        case 1:
                            norm.norm.x = f;
                            break;
                        case 2:
                            norm.norm.y = f;
                            break;
                        case 3:
                            norm.norm.z = f;
                            break;
                        default:
                            break;
                    }
                }
                normals.add(norm);
            });
        }
        try (Stream<String> faceLines = Files.lines(path)
            .filter(s -> s.startsWith("f "))) {
            List<String> faceLine = faceLines.collect(Collectors.toList());
            faceLine.forEach(line -> {
                Face face = new Face();
                    face.vertexIndices = new ArrayList<>();
                    face.textureIndices = new ArrayList<>();
                    face.normalIndices = new ArrayList<>();

                String[] spLine = line.split(" ");
                for(int i = 1; i < spLine.length; ++i) {
                    String[] sspLine = spLine[i].split("/");
                    facesDim = sspLine.length;
                    for(int j = 0; j < sspLine.length; ++j) { // FIXME each entry should be separated (1 2 3 4 5 6 and 1/2 3/4 5/6 are indistinguishable; use only vertex for now)
                        int f = Math.abs(Integer.parseInt(sspLine[j]));

                        switch (j)
                        {
                            case 0:
                                face.vertexIndices.add(f);
                                break;

                            case 1:
                                face.textureIndices.add(f);
                                break;

                            case 2:
                                face.normalIndices.add(f);
                                break;
                        }

                    }
                }
                faces.add(face);
            });
        }
    }
}