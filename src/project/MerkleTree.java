package project;

import util.HashGeneration;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
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

        Queue<Node> hashedChunks = new LinkedList<>();

        for (String path : chunksPaths) {
            hashedChunks.add(new Node(this.hashFile(path)));
        }

        this.root = this.aggregateNodes(hashedChunks);
    }

    private Node aggregateNodes(Queue<Node> hashNodes) {

        Queue<Node> aggregatedNodes = new LinkedList<>();

        do {
            aggregatedNodes.clear();

            while(hashNodes.size() > 0) {

                Node firstNode = hashNodes.poll();

                Node secondNode = hashNodes.poll();

                Node parentNode = new Node(this.concatenateHashNodes(firstNode, secondNode));

                aggregatedNodes.add(parentNode);

                parentNode.setChildren(firstNode, secondNode);
            }

            hashNodes = new LinkedList<>(aggregatedNodes);

        }
        while (aggregatedNodes.size() > 1);

        return aggregatedNodes.poll();
    }

    private String concatenateHashNodes(Node firstHash, Node secondHash) {

        try {
            String firstString = (firstHash != null) ? firstHash.getData() : "";

            String secondString = (secondHash != null) ? secondHash.getData() : "";

            return HashGeneration.generateSHA256(firstString + secondString);
        }
        catch (NoSuchAlgorithmException e) {
            System.out.print("NoSuchAlgorithmException");
        }
        catch (UnsupportedEncodingException e) {
            System.out.print("UnsupportedEncodingException");
        }

        return "";
    }

    private ArrayList<String> readAndParsePaths() {

        BufferedReader reader = this.readFile(this.chunksPath);

        ArrayList<String> paths = new ArrayList<>();

        if(reader == null) {
            return paths;
        }

        try {
            for(String line; (line = reader.readLine()) != null;) {
                paths.add(line);
            }
        }

        catch (IOException e) {
            System.out.println("While reading paths, file cannot be read");
        }

        return paths;
    }

    private String hashFile(String path) {

        try {
            return HashGeneration.generateSHA256(new File(path));
        } catch (IOException e) {
            System.out.print("File cannot be read");
        } catch (NoSuchAlgorithmException e) {
            System.out.print("No such algorithm");
        }

        return "";
    }

    private BufferedReader readFile(String path) {
        try {
            return new BufferedReader(new FileReader(path));
        }
        catch (FileNotFoundException e) {
            System.out.print("File not found");
        }

        return null;
    }
}
