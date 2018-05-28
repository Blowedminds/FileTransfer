package project;

public class Node {

    private String data;

    private Node left, right;

    public Node(String data) {
        this.data = data;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Node getLeft() {
        return this.left;
    }

    public Node getRight() {
        return this.right;
    }

    public void setChildren(Node leftNode, Node rightNode) {
        this.left = leftNode;
        this.right = rightNode;
    }
}
