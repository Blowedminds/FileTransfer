package project;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Torrent {

    String trustedSource;
    Queue<String> filesSource;
    String downloadPath;

    public Torrent(String trustedSource, Queue<String> filesSource, String downloadPath) {

        this.trustedSource = trustedSource;
        this.filesSource = filesSource;
        this.downloadPath = downloadPath;
    }

    public boolean processDownload(String altSource) {

        Queue<String> downloadedFiles = this.downloadFromSource(this.filesSource);

        if(downloadedFiles == null) {
            System.out.println("Download failed");
            return false;
        }

        MerkleTree merkleTree = new MerkleTree(downloadedFiles);

        boolean checkAuthenticity = merkleTree.checkAuthenticity(this.trustedSource);

        if(checkAuthenticity) {
            return true;
        }

        ArrayList<Stack<String>> corruptChunks = merkleTree.findCorruptChunks(this.trustedSource);

        for(Stack<String> stack: corruptChunks) {

        }

        return true;
    }

    private Queue<String> downloadFromSource(Queue<String> files) {

        Queue<String> filesPaths = new LinkedList<>();

        while(!files.isEmpty()) {

            String path = URLHandler.downloadFile(files.poll(), this.downloadPath);

            if(path == null)
                return null;
            else
                filesPaths.add(path);
        }

        return filesPaths;
    }
}
