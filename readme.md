# File Manager
- final year project for NITA CSE'25.
- limited to only .txt files
___


## How to Add Features:
- Make separate packages and class for each major feature.
- Implement the logic in them.
- add the class object and use the public funcs in FileManager class.
- Add options in Main.Application.Init()  func.
- Test the whole working aspect before pushing changes to main branch.


---

## TODO:

### 1. Access Control for files:
- create user auth system
- make access control calls before every file operation.
- make ACL for managing them.

### 2. Searching for files:
- search for all files starting with given string in the lower
directories or global search.
- implement Efficient string searching dsa.


### 3. Compressor:
- implement some string compression algorithm to compress files.
- make a new .txt file with suffix "-compressed" for the compressed text.

### 4. Encryptor:
- make file encryptor using crypto funcs.
- use multithreading here.

### 5. File Versioning:
- Allow users to create different versions of a file, which can be restored later. You could implement versioning with a branching approach, highlighting storage management concepts.

### 6. Enhance CRUD features:
- Add CRUD for directories
- use concurrency control in file read writes.
- make different type of operations separate (eg. file op., directory op.,user op. etc).

### Extras (if needed):
- Thread pooling and multithreading for whole application.
- Normal UI using ony java.
- converting TCP accessible application.

---

## Tasks Status:

- Access Control: 
- Search:
- Compressor:
- Encryptor:
- File Versioning:
- Enhancing:
