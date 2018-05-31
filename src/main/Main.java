package main;

import java.util.ArrayList;
import java.util.Stack;

import project.MerkleTree;

public class Main {

    public static void main(String[] args) {


        MerkleTree m0 = new MerkleTree("data/9.txt");
        String hash = m0.getRoot().getLeft().getRight().getData();
        System.out.println(hash);

        boolean valid = m0.checkAuthenticity("data/9meta.txt");
        System.out.println(valid);

        // The following just is an example for you to see the usage.
        // Although there is none in reality, assume that there are two corrupt chunks in this example.
        ArrayList<Stack<String>> corrupts = m0.findCorruptChunks("data/9meta.txt");
        System.out.println(corrupts.size());
//        System.out.println("Corrupt hash of first corrupt chunk is: " + corrupts.get(0).pop());
//        System.out.println("Corrupt hash of second corrupt chunk is: " + corrupts.get(1).pop());

//		download("secondaryPart/data/download_from_trusted.txt");

    }

    public static void download(String path) {
        // Entry point for the secondary part
    }

}
