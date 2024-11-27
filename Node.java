
import java.nio.ByteBuffer;


public class Node {
    static final int BLOCK_SIZE = 512;
    static final int MIN_DEGREE = 10;  // t value for B-tree
    static final int MAX_KEYS = 2 * MIN_DEGREE - 1;  // Maximum number of keys in a node (19)
    static final int MAX_CHILDREN = 2 * MIN_DEGREE;  // Maximum number of children (20)
    long blockId;
    long parentId;
    int numKeys;
    long[] keys;
    long[] values;
    long[] children;

    // conscructor for the node
    public Node(long blockId, long parentId) {
        this.blockId = blockId;
        this.parentId = parentId;
        this.numKeys = 0;
        this.keys = new long[MAX_KEYS];
        this.values = new long[MAX_KEYS];
        this.children = new long[MAX_CHILDREN];
    }

    // writing the information
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(BLOCK_SIZE);
        
        // Write header information
        buffer.putLong(blockId);
        buffer.putLong(parentId);
        buffer.putLong(numKeys);
        
        // Write keys
        for (long key : keys) {
            buffer.putLong(key);
        }
        
        // Write values
        for (long value : values) {
            buffer.putLong(value);
        }
        
        // Write children
        for (long child : children) {
            buffer.putLong(child);
        }
        
        return buffer.array();
    }

    // reading the information
    public static Node fromBytes(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        
        long blockId = buffer.getLong();
        long parentId = buffer.getLong();
        Node node = new Node(blockId, parentId);
        node.numKeys = (int) buffer.getLong();
        
        // Read keys
        for (int i = 0; i < MAX_KEYS; i++) {
            node.keys[i] = buffer.getLong();
        }
        
        // Read values
        for (int i = 0; i < MAX_KEYS; i++) {
            node.values[i] = buffer.getLong();
        }
        
        // Read children
        for (int i = 0; i < MAX_CHILDREN; i++) {
            node.children[i] = buffer.getLong();
        }
        
        return node;
    }
}