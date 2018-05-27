package project;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Stack;

public class MerkleTree {

    private String chunksPath;

    private Node root;

    public MerkleTree(String path) {

        this.chunksPath = path;

        this.createMT();
    }

    public Node getRoot() {
        return this.root;
    }

    public boolean checkAuthenticity(String trustedSource) {
        return false;
    }

    public ArrayList<Stack<String>> findCorruptChunks(String metaFile) {
        return null;
    }

    private void createMT() {

        ArrayList<String> chunksPaths = this.readAndParsePaths();

        

        for(String path: chunksPaths) {

        }
    }

    private ArrayList<String> readAndParsePaths() {

        return null;
    }

    private BufferedReader readFile(String path) {
        return null;
    }
}
