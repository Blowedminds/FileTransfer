package project;

import util.HashGeneration;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class MerkleTree {

    private String chunksPath;

    private Node root;

    private int leavesSize;

    public MerkleTree(String path) {

        this.chunksPath = path;

        this.createMT();
    }

    private void createMT() {

        Queue<String> chunksPaths = FileHandler.readAndParseLines(this.chunksPath);

        this.leavesSize = chunksPaths.size();

        LinkedList<Node> leaves = FileHandler.hashFiles(chunksPaths);

        this.root = this.aggregateNodes(leaves);
    }

    public Node getRoot() {
        return this.root;
    }

    public boolean checkAuthenticity(String trustedSource) {

        return this.root.getData().equals(FileHandler.readAndParseLines(trustedSource).poll());
    }

    public ArrayList<Stack<String>> findCorruptChunks(String metaFile) {
        LinkedList<String> hashMeta = FileHandler.readAndParseLines(metaFile);
        Node parent = this.convertLinkedListToBFS(hashMeta);
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

    private Node convertLinkedListToBFS(LinkedList<String> list) {
        int listSize = list.size();
        Queue<Node> queue = new LinkedList<>();
        Queue<Node> track = new LinkedList<>();

        if(listSize % 2 == 1)
            queue.add(null);

        for(int i = 0; i < this.leavesSize; i++) {
            queue.add(new Node(list.removeLast()));
        }

        while(list.size() > 0) {
            if(queue.size() / 2 % 2 == 1 && list.size() > 1) {
                track.add(null);
            }
             while(queue.size() > 0) {
                Node node = new Node(list.removeLast());

                Node polled = queue.poll();

                node.setChildren(queue.poll(), polled);

                track.add(node);
            }

            queue = track;
            track = new LinkedList<>();
        }

        return queue.poll();
    }
}
