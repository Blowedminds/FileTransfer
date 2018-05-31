package project;

import util.HashGeneration;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class MerkleTree {

    private Node root;

    private int leavesSize;

    public MerkleTree(String path) {
        this(FileHandler.readAndParseLines(path));
    }

    public MerkleTree(Queue<String> chunksPaths) {
        this.createMT(chunksPaths);
    }

    private void createMT(Queue<String> chunksPaths) {

        this.leavesSize = chunksPaths.size();

        LinkedList<Node> leaves = FileHandler.hashFiles(chunksPaths);

        this.root = this.aggregateNodes(leaves);
    }

    public Node getRoot() {
        return this.root;
    }

    public boolean checkAuthenticity(String trustedSource) {

        return this.root.getData().equals(FileHandler.readAndParseLines(trustedSource, 1).poll());
    }

    public ArrayList<Stack<String>> findCorruptChunks(String metaFile) {
        Node metaParent = this.convertLinkedListToBFS(FileHandler.readAndParseLines(metaFile));
        Queue<Node> treeQueue = new LinkedList<>();
        Queue<Node> metaQueue = new LinkedList<>();
        Queue<Node> startQueue = new LinkedList<>();

        ArrayList<Stack<String>> corruptChunks = new ArrayList<>();

        if(this.root.getData().equals(metaParent.getData())) {
           return corruptChunks;
        }
        treeQueue.add(this.root);
        metaQueue.add(metaParent);

        corruptChunks.add(new Stack<>());

        int level = 0, levelElementTraversed = 0;

        while(treeQueue.size() > 0 && metaQueue.size() > 0) {

            Node node = treeQueue.poll();
            Node meta = metaQueue.poll();

            if(!node.getData().equals(meta.getData())) {
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

                if(node.getLeft() != null && meta.getLeft() != null) {
                    startQueue.add(node.getLeft());
                    metaQueue.add(meta.getLeft());
                }
                if(node.getRight() != null && meta.getRight() != null) {
                    startQueue.add(node.getRight());
                    metaQueue.add(meta.getRight());
                }
            }

            levelElementTraversed++;

            if(treeQueue.isEmpty() && !startQueue.isEmpty()) {
                treeQueue = startQueue;
                startQueue = new LinkedList<>();
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
        Queue<Node> startDetector = new LinkedList<>();

        if (this.leavesSize % 2 == 1)
            queue.add(null);

        for (int i = 0; i < this.leavesSize; i++) {
            queue.add(new Node(list.removeLast()));
        }

        while (list.size() > 0) {
            if (queue.size() / 2 % 2 == 1 && list.size() > 1) {
                startDetector.add(null);
            }
            while (queue.size() > 0) {
                Node node = new Node(list.removeLast());

                Node polled = queue.poll();

                node.setChildren(queue.poll(), polled);

                startDetector.add(node);
            }

            queue = startDetector;
            startDetector = new LinkedList<>();
        }

        return queue.poll();
    }
}
