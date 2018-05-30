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

    private void createMT() {

        Queue<String> chunksPaths = FileHandler.readAndParseLines(this.chunksPath);

        Queue<Node> hashedChunks = FileHandler.hashFiles(chunksPaths);

        this.root = this.aggregateNodes(hashedChunks);
    }

    public Node getRoot() {
        return this.root;
    }

    public boolean checkAuthenticity(String trustedSource) {

        return this.root.getData().equals(FileHandler.readAndParseLines(trustedSource).poll());
    }

    public ArrayList<Stack<String>> findCorruptChunks(String metaFile) {
        Queue<String> hashMeta = FileHandler.readAndParseLines(metaFile);

        Queue<Node> hashQueue = new LinkedList<>();
        Queue<Node> startDetector = new LinkedList<>();

        hashQueue.add(this.root);

        ArrayList<Stack<String>> corruptChunks = new ArrayList<>();

        int currentElement = 0, metaSize = hashMeta.size(), level = 0;

        while (!hashQueue.isEmpty()) {

            while(hashMeta.size() + currentElement <= metaSize) {
                hashMeta.poll();
            }
            Node node = hashQueue.poll();
            String compateString = hashMeta.poll();

            if (!node.getData().equals(compateString)) {

                if (corruptChunks.get(0) != null) {

                    corruptChunks.get(0).add(node.getData());
                } else {
                    Stack<String> addStack = new Stack<>();

                    addStack.add(node.getData());

                    corruptChunks.add(0, addStack);
                }

                if (node.getLeft() != null) {
                    startDetector.add(node.getLeft());
                }
                if (node.getRight() != null) {
                    startDetector.add(node.getRight());
                }

            }


            if (hashQueue.isEmpty() && !startDetector.isEmpty()) {
                hashQueue = new LinkedList<>(startDetector);
                startDetector.clear();
            }
        }

        return corruptChunks;
    }

    private Node aggregateNodes(Queue<Node> hashNodes) {

        Queue<Node> aggregatedNodes = new LinkedList<>();

        do {
            aggregatedNodes.clear();

            while (hashNodes.size() > 0) {

                Node firstNode = hashNodes.poll();

                Node secondNode = hashNodes.poll();

                Node parentNode = new Node(this.concatHashNodes(firstNode, secondNode));

                aggregatedNodes.add(parentNode);

                parentNode.setChildren(firstNode, secondNode);
            }

            hashNodes = new LinkedList<>(aggregatedNodes);

        }
        while (aggregatedNodes.size() > 1);

        return aggregatedNodes.poll();
    }

    private String concatHashNodes(Node firstHash, Node secondHash) {

        try {
            String firstString = (firstHash != null) ? firstHash.getData() : "";

            String secondString = (secondHash != null) ? secondHash.getData() : "";

            return HashGeneration.generateSHA256(firstString + secondString);
        } catch (NoSuchAlgorithmException e) {
            System.out.print("NoSuchAlgorithmException");
        } catch (UnsupportedEncodingException e) {
            System.out.print("UnsupportedEncodingException");
        }

        return "";
    }

//    private Node convertQueueToBFS(Queue<String> queue) {
//
//        Node parent = new Node(queue.poll());
//
//        Queue<Node> convertQueue = new LinkedList<>();
//
//        convertQueue.add(parent);
//
//        while(!convertQueue.isEmpty()) {
//            Node node = convertQueue.poll();
//
//            String string = queue.poll();
//
//            if(string != null) {
//                node.setChildren(new Node(string), new Node(queue.poll()));
//            }
//
//            if(node.getLeft() != null) {
//                convertQueue.add(node.getLeft());
//            }
//
//            if(node.getRight() != null) {
//                convertQueue.add(node.getRight());
//            }
//        }
//
//        return parent;
//    }
}
