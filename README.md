### **B-Tree Index File Management Program**

---

#### **Overview**
This is a Java-based program to manage index files using a B-tree structure. It allows users to create, modify, and extract data from index files interactively. The implementation adheres to memory and file format constraints, making it suitable for understanding B-tree operations and file I/O management.

---

#### **Features**
- **Interactive Menu:** text interface for managing index files.
- **File Operations:**  
  - Create new index files with a defined header block.  
  - Open and validate existing index files.  
  - Overwrite files with user confirmation.
- **B-Tree Operations:**  
  - Insert key-value pairs with automatic balancing.  
  - Search for keys in the B-tree.  
  - Print the B-tree in sorted order.
- **Data Extraction:** Export all key-value pairs to a file.

---

#### **Project Structure**
The project is organized into the following files:

1. **`Node.java`**  
   - Defines the structure of a B-tree node.  
   - Manages keys, values, and child pointers within a single block.

2. **`BTreeIndex.java`**  
   - Core logic for B-tree operations like insertion, splitting, and file I/O.  
   - Manages the header and node blocks in the index file.

3. **`Main.java`**  
   - Interactive interface for user input and program execution.  
   - Handles integration with `BTreeIndex` for all operations.

---

#### **Setup and Usage**

1. **Prerequisites**
   - Java Development Kit (JDK) installed on your system.

2. **Compile the Code**
   ```bash
   javac Main.java BTreeIndex.java Node.java
   ```

3. **Run the Program**
   ```bash
   java Main
   ```

4. **Menu Options**
   - `create`: Create a new index file.  
   - `open`: Open an existing index file.  
   - `insert`: Insert a key-value pair into the B-tree.  
   - `search`: Search for a key and display its value.  
   - `load`: Load key-value pairs from a file into the B-tree.  
   - `print`: Print all key-value pairs in sorted order.  
   - `extract`: Export key-value pairs to a file.  
   - `quit`: Exit the program.

---

#### **Notes for TA**
I am not sure if that is how he want my commit log to be answer but I pretty much just answered the question and the template he gived me. I would just log each time I finished a big part of the code which could take me like 2-4 hours to 2 days like the BTreeFiles. 
