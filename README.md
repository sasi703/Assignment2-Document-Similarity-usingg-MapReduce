# Assignment 2: Document Similarity using MapReduce  
**Course**: Cloud Computing for Data Analysis (ITCS 6190/8190, Fall 2025)  
**Name:Sasi Vadana Atluri** 
**Student ID:801429678** 

---

## 📌 Project Overview  
This project implements a Hadoop MapReduce application to compute Jaccard Similarity between pairs of documents.

- **Mapper**: Extracts unique words from each document and emits `(word, docID)`  
- **Reducer**: Collects word sets per document and computes Jaccard similarity for all document pairs  
- **Driver**: Configures and runs the MapReduce job  

**Jaccard Similarity**:  
```
J(A, B) = |A ∩ B| / |A ∪ B|
```

---


##  Approach and Implementation  

### Mapper Design  
- **Input Key-Value Pair**:  
  - **Key**: Line offset (LongWritable)  
  - **Value**: A line of text from the input file (Text)  

- **Processing Logic**:  
  - Splits each line into DocumentID and document text.  
  - Normalizes text (lowercase, removes punctuation).  
  - Builds a set of unique words.  
  - Emits `(word, DocumentID)` for each unique word.  

- **Output Key-Value Pair**:  
  - **Key**: Word (Text)  
  - **Value**: DocumentID (Text)  

---

### Reducer Design  
- **Input Key-Value Pair**:  
  - **Key**: Word (Text)  
  - **Value**: List of DocumentIDs containing that word  

- **Processing Logic**:  
  - Builds a map of documents → unique word sets.  
  - In `cleanup()`, generates all document pairs.  
  - Computes Jaccard similarity as `|Intersection| / |Union|`.  

- **Output Key-Value Pair**:  
  - **Key**: Document pair (e.g., `Document1, Document2`)  
  - **Value**: `"Similarity: 0.xx"`  

---

## 📂 Project Structure  

```
Assignment2-Document-Similarity-usingg-MapReduce-main/
│── input/                          # Input datasets
│   ├── dataset_1.txt
│   ├── dataset_2.txt
│   └── dataset_3.txt
│
│── outputs/3nodes/                 # Output results (3-node cluster)
│   ├── output_dataset_1/
│   ├── output_dataset_2/
│   └── output_dataset_3/
│
│── src/main/java/com/example/controller/
│   ├── DocumentSimilarityDriver.java
│   ├── DocumentSimilarityMapper.java
│   └── DocumentSimilarityReducer.java
│
│── target/                         # Built JARs
│── docker-compose.yml              # Hadoop cluster config
│── hadoop.env                      # Hadoop environment variables
│── pom.xml                         # Maven dependencies
│── README.md                       # Project documentation
└── reference screenshots.pdf        # Example screenshots
```

---

## ⚙️ Prerequisites  
- Java 8+  
- Maven 3.6+  
- Docker & Docker Compose (Hadoop 3.2.1 cluster)  

---

## 🚀 Execution Guide  

### 1. Build the JAR  
```bash
mvn clean package
```

### 2. Load Dataset into HDFS  
```bash
docker cp input/dataset_1.txt resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
docker exec -it resourcemanager /bin/bash

hdfs dfs -mkdir -p /input
hdfs dfs -put -f /opt/hadoop-3.2.1/share/hadoop/mapreduce/dataset_1.txt /input/
```

### 3. Run the MapReduce Job  
```bash
hdfs dfs -rm -r /output_dataset_1
hadoop jar /opt/hadoop-3.2.1/share/hadoop/mapreduce/DocumentSimilarity-0.0.1-SNAPSHOT.jar   com.example.controller.DocumentSimilarityDriver   /input/dataset_1.txt /output_dataset_1
```

### 4. View Results  
```bash
hdfs dfs -cat /output_dataset_1/part-r-00000
```

Example output:  
```
Document1, Document2    Similarity: 0.56
Document1, Document3    Similarity: 0.42
Document2, Document3    Similarity: 0.50
```

---

## 📊 Performance Analysis  

### Cluster Configurations
- **3-Node Setup**: 1 NameNode, 3 DataNodes, 1 ResourceManager  
- **1-Node Setup**: 1 NameNode, 1 DataNode, 1 ResourceManager  

### Execution Results  

| Dataset    | 3 Nodes (s) | 1 Node (s) | Improvement |
|------------|-------------|------------|-------------|
| Dataset 1  | 23.10s      | 27.49s     | 16.0% faster |
| Dataset 2  | 20.10s      | 20.67s     | 2.8% faster |
| Dataset 3  | 20.41s      | 21.26s     | 4.0% faster |

---

## ⚠️ Challenges & Solutions  

- **Empty Output Issue**: Fixed by adding `setMapOutputKeyClass` and `setMapOutputValueClass` in the Driver.  
- **ClassNotFoundException**: Resolved by restructuring files into `src/main/java/com/example/controller/`.  
- **FileAlreadyExistsException**: Fixed using `hdfs dfs -rm -r <output>` before re-runs.  
- **Performance Variability**: Documented results from 1-node vs 3-node clusters.  

---

## 📌 Deliverables  
- Input datasets: `input/`  
- Output results: `outputs/3nodes/`  
- Source code: `src/`  
- Execution logs & screenshots: `reference screenshots.pdf`  
- Report: `README.md`  
