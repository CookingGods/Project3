import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

class BTreeIndex {
    private static final String MAGIC_NUMBER = "4337PRJ3"; // Unique identifier for the file format
    private static final int BLOCK_SIZE = 512; // Fixed size for blocks in the file
    
    private String filename;
    private long rootId; // Block ID of the root node
    private long nextBlock; // ID of the next available block
    private RandomAccessFile file;

    public BTreeIndex() {
        this.filename = null;
        this.rootId = 0;
        this.nextBlock = 1;  // Block 0 is reserved for the header
        this.file = null;
    }

    // Creates a new B-tree file, optionally overwriting if it exists
    public boolean createFile(String filename) throws IOException {
        File f = new File(filename);
        if (f.exists()) {
            System.out.print("File " + filename + " exists. Overwrite? (y/n): ");
            Scanner scanner = new Scanner(System.in);
            String response = scanner.nextLine().toLowerCase();
            if (!response.equals("y")) {
                return false; // Abort if user does not consent to overwrite
            }
        }

        this.filename = filename;
        this.rootId = 0;
        this.nextBlock = 1;
        
        if (file != null) {
            file.close(); // Close existing file handle
        }
        file = new RandomAccessFile(filename, "rw");
        writeHeader(); // Initialize the file with a header block
        return true;
    }

    // Opens an existing B-tree file, verifying the header for compatibility
    public boolean openFile(String filename) throws IOException {
        File f = new File(filename);
        if (!f.exists()) {
            System.out.println("Error: File " + filename + " does not exist");
            return false;
        }

        if (file != null) {
            file.close();
        }
        file = new RandomAccessFile(filename, "rw");

        // Read and verify the header block
        byte[] header = new byte[BLOCK_SIZE];
        file.read(header);
        String magic = new String(header, 0, 8);
        if (!magic.equals(MAGIC_NUMBER)) {
            System.out.println("Error: Invalid file format");
            file.close();
            file = null;
            return false;
        }

        ByteBuffer buffer = ByteBuffer.wrap(header);
        buffer.position(8);  // Skip the magic number
        this.filename = filename;
        this.rootId = buffer.getLong(); // Retrieve the root block ID
        this.nextBlock = buffer.getLong(); // Retrieve the next available block ID
        return true;
    }

    // Writes the header block to the file
    private void writeHeader() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BLOCK_SIZE);
        buffer.put(MAGIC_NUMBER.getBytes());
        buffer.putLong(rootId); // Root node ID
        buffer.putLong(nextBlock); // Next available block ID
        
        file.seek(0); // Seek to the start of the file
        file.write(buffer.array());
    }

    // Writes a B-tree node to its designated block
    private void writeNode(Node node) throws IOException {
        file.seek(node.blockId * BLOCK_SIZE); // Locate the node's block
        file.write(node.toBytes()); // Serialize and write the node
    }

    // Reads a B-tree node from the file
    private Node readNode(long blockId) throws IOException {
        if (blockId == 0) {
            return null; // Null represents an empty node
        }
        byte[] data = new byte[BLOCK_SIZE];
        file.seek(blockId * BLOCK_SIZE); // Locate the block
        file.read(data); // Read the block's data
        return Node.fromBytes(data); // Deserialize into a Node object
    }

    // Inserts a key-value pair into the B-tree
    public void insert(long key, long value) throws IOException {
        if (search(key) != null) { // Prevent duplicate keys
            System.out.println("Error: Key already exists");
            return;
        }

        if (rootId == 0) { // Initialize root if tree is empty
            Node root = new Node(nextBlock, 0);
            root.keys[0] = key;
            root.values[0] = value;
            root.numKeys = 1;
            writeNode(root);
            rootId = nextBlock++;
            writeHeader();
            return;
        }

        Node root = readNode(rootId);
        if (root.numKeys == Node.MAX_KEYS) { // Handle root splitting
            Node newRoot = new Node(nextBlock++, 0);
            newRoot.children[0] = rootId;
            splitChild(newRoot, 0, root);
            rootId = newRoot.blockId;
            writeHeader();
            insertNonFull(newRoot, key, value);
        } else {
            insertNonFull(root, key, value);
        }
    }

    // Splits a child node to maintain B-tree properties
    private void splitChild(Node parent, int index, Node child) throws IOException {
        Node newNode = new Node(nextBlock++, parent.blockId); // New node for split
        int t = Node.MIN_DEGREE;
        
        // Copy the right half of the child node to the new node
        newNode.numKeys = t - 1;
        for (int i = 0; i < t-1; i++) {
            newNode.keys[i] = child.keys[i+t];
            newNode.values[i] = child.values[i+t];
        }
        
        // Reassign child pointers if node is not a leaf
        if (child.children[0] != 0) {
            for (int i = 0; i < t; i++) {
                newNode.children[i] = child.children[i+t];
                Node childNode = readNode(child.children[i+t]);
                if (childNode != null) {
                    childNode.parentId = newNode.blockId;
                    writeNode(childNode);
                }
            }
        }
        
        // Adjust parent node to accommodate the split
        for (int i = parent.numKeys; i > index; i--) {
            parent.keys[i] = parent.keys[i-1];
            parent.values[i] = parent.values[i-1];
            parent.children[i+1] = parent.children[i];
        }
        
        // Insert the middle key from the child node into the parent
        parent.keys[index] = child.keys[t-1];
        parent.values[index] = child.values[t-1];
        parent.children[index+1] = newNode.blockId;
        parent.numKeys++;
        
        // Update child node
        child.numKeys = t - 1;
        
        writeNode(child);
        writeNode(newNode);
        writeNode(parent);
    }

    // Helper for inserting into a non-full node
    private void insertNonFull(Node node, long key, long value) throws IOException {
        int i = node.numKeys - 1;
        
        if (node.children[0] == 0) {  // Leaf node
            while (i >= 0 && key < node.keys[i]) { // Shift keys to make room
                node.keys[i+1] = node.keys[i];
                node.values[i+1] = node.values[i];
                i--;
            }
            
            node.keys[i+1] = key;
            node.values[i+1] = value;
            node.numKeys++;
            writeNode(node);
        } else { // Internal node
            while (i >= 0 && key < node.keys[i]) {
                i--;
            }
            i++;
            
            Node child = readNode(node.children[i]);
            if (child.numKeys == Node.MAX_KEYS) {
                splitChild(node, i, child);
                if (key > node.keys[i]) { // Re-evaluate child after split
                    i++;
                    child = readNode(node.children[i]);
                }
            }
            insertNonFull(child, key, value);
        }
    }

    // Searches for a key in the B-tree
    public Long[] search(long key) throws IOException {
        if (rootId == 0) { // Tree is empty
            return null;
        }
        
        Node node = readNode(rootId);
        while (node != null) {
            int i = 0;
            while (i < node.numKeys && key > node.keys[i]) {
                i++;
            }
            
            if (i < node.numKeys && key == node.keys[i]) { // Key found
                return new Long[]{node.keys[i], node.values[i]};
            }
            
            if (node.children[i] == 0) { // No further children to search
                return null;
            }
            
            node = readNode(node.children[i]); // Continue search in the child node
        }
        return null;
    }

    // Prints the entire B-tree in order
    public void print() throws IOException {
        printNode(rootId);
    }

    private void printNode(long nodeId) throws IOException {
        if (nodeId == 0) {
            return;
        }
        Node node = readNode(nodeId);
        for (int i = 0; i < node.numKeys; i++) {
            System.out.println(node.keys[i] + "," + node.values[i]);
        }
        for (int i = 0; i < Node.MAX_CHILDREN; i++) {
            if (node.children[i] != 0) {
                printNode(node.children[i]);
            }
        }
    }
