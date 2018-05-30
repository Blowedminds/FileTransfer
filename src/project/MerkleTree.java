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

    public ArrayList<Stack<String>> findCorruptChunks2(String metaFile) {
        Queue<String> hashMeta = FileHandler.readAndParseLines(metaFile);
        Queue<Node> searchQueue = new LinkedList<>();
        Queue<Node> startDetector = new LinkedList<>();

        ArrayList<Stack<String>> corruptChunks = new ArrayList<>();
        ArrayList<Integer> searchMetaIndexes = new ArrayList<>();

        searchMetaIndexes.add(0);

        if(!this.checkAuthenticity(metaFile)) {

            Stack<String> first = new Stack<>();
            first.add(this.root.getData());
            corruptChunks.add(first);

            if(this.root.getLeft() != null) {
                searchQueue.add(this.root.getLeft());
                searchMetaIndexes.add(1);
            }
            if(this.root.getRight() != null) {
                searchQueue.add(this.root.getRight());
                searchMetaIndexes.add(2);
            }
        } else {
            return corruptChunks;
        }


        int elementTraversed = 1, metaSize = hashMeta.size(), levelElementTraversed = 0, level = 1;

        while(searchQueue.size() > 0 && hashMeta.size() > 0) {
            Node node = searchQueue.poll();

            int currentIndex = searchMetaIndexes.get(elementTraversed);

            while(hashMeta.size() + currentIndex > metaSize && hashMeta.size() > 0)
                hashMeta.poll();
            String polled = hashMeta.poll();
            if(!node.getData().equals(polled)) {
                Stack<String> chunk = corruptChunks.get(levelElementTraversed/2);
                //Right Node
                if(chunk.size() > level) {
                    Stack<String> copy = (Stack) chunk.clone();

                    copy.pop();
                    copy.add(node.getData());

                    corruptChunks.add(levelElementTraversed / 2 + 1, copy);

                    levelElementTraversed += 2;
                } else {
                    chunk.add(node.getData());
                }

                int power = (int) Math.pow(2, level);
                int nodeNumber = currentIndex - (power - 1);
                int childNodeNumber = currentIndex + (power - 1 - nodeNumber) + nodeNumber * 2 + 1;

                if(node.getLeft() != null) {
                    searchMetaIndexes.add(childNodeNumber);
                    startDetector.add(node.getLeft());
                }

                if(node.getRight() != null) {
                    searchMetaIndexes.add(childNodeNumber + 1);
                    startDetector.add(node.getRight());
                }
            }

            elementTraversed++;
            levelElementTraversed++;
            //a new level start detecting
            if (searchQueue.isEmpty() && !startDetector.isEmpty()) {
                searchQueue = startDetector;
                startDetector = new LinkedList<>();
                level++;
                levelElementTraversed = 0;
            }
        }
        return corruptChunks;
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

        Queue<Node> aggregatedNodes;

        do {
            aggregatedNodes = new LinkedList<>();

            while (hashNodes.size() > 0) {

                Node firstNode = hashNodes.poll();

                Node secondNode = hashNodes.poll();

                Node parentNode = new Node(this.concatHashNodes(firstNode, secondNode));

                aggregatedNodes.add(parentNode);

                parentNode.setChildren(firstNode, secondNode);
            }

            hashNodes = aggregatedNodes;

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
