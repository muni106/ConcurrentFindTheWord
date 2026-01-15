# ConcurrentFindTheWord 
A comprehensive comparison of 6 different concurrency approaches in Java for searching words across thousands of PDF files. This project demonstrates practical implementations of threads, virtual threads, ForkJoin, reactive streams, async event loops, and actors.

## Overview
This project explores how different concurrency paradigms handle the computationally intensive task of:
- Recursively traversing directory structures
- Extracting text from PDF files
- Searching for specific words
- Aggregating results with real-time GUI updates

Each implementation uses the Strategy Pattern to provide interchangeable concurrency approaches while maintaining a consistent MVC architecture.

## Architecture
```txt
MVC Pattern + Strategy Pattern
├── SearchView (GUI with start/stop/suspend/resume controls)
├── SearchController (delegates to strategies)
├── SearchModel (shared state with thread-safe counters)
└── Strategies (6 interchangeable implementations)
    ├── Thread Pool (custom monitors + worker threads)
    ├── Virtual Threads (Java 21+ lightweight threads)
    ├── ForkJoin (recursive task decomposition)
    ├── Reactive (RxJava streams)
    ├── Async Event (Vert.x event loops)
    └── Actors (Akka message-passing)
```

## Concurrency strategies

### 1. Thread Pool

- Custom monitor-based synchronization using `ReentrantLock`
- Dynamic thread pool sizing: `N = CPU cores + 1`
- Work partitioning: divides PDFs into equal chunks per thread
- **Best for:** Stable, predictable production workloads


### 2. Virtual Threads

- One virtual thread per PDF file
- Exploits lightweight thread creation (Java 21+)
- Monitor coordination prevents pinning to carrier threads
- **Best for:** High file counts with small PDFs


### 3. ForkJoin Framework

- Hierarchical task decomposition mirroring directory tree
- Work-stealing for load balancing
- Builds complete directory tree upfront, then processes
- **Best for:** Recursive directory structures with imbalanced workloads


### 4. Reactive Programming (RxJava)

- Hot Observable stream with custom emitter
- Backpressure handling (5000-item buffer)
- Reduce operator for result aggregation
- **Best for:** Stream processing scenarios (not blocking I/O)


### 5. Async Event (Vert.x)

- Event loop + worker pool architecture
- Future composition with `Future.all()`
- Non-blocking result handling via callbacks
- **Best for:** Non-blocking I/O operations (not this workload)


### 6. Actor Model (Akka)

- Single analyzer actor processes all files
- Asynchronous message passing
- Requester actor retrieves final count
- **Best for:** Distributed systems with message-based coordination


## Performance benchmarks

Tested on **MacBook M3 Pro (12 cores)** across datasets ranging from 10 to 50,000 PDFs, in the [benchmarksResults.txt] you can see the final results.

##  Benchmark Methodology

- **Warmup:** Pre-execution on <1000 files to minimize JIT effects
- **Iterations:** 7 runs averaged per test
- **Baseline:** Sequential single-threaded execution
- **Datasets:** 8 test scenarios from 10 to 50,000 PDFs (flat + recursive)

### Key Findings

- **ForkJoin dominates** due to efficient work-stealing and cache locality
- **Virtual Threads** excel on tiny datasets but degrade on large workloads
- **Message-passing models** (Reactive/Async/Actor) fail due to 1600-1850ms framework overhead overwhelming blocking I/O operations
- **Thread Pool** provides consistent 2.5-3.7× speedup—ideal for production reliability


## Formal Verification (Model Checking)

The Thread Pool implementation was verified using **Java PathFinder (JPF)** with the PreciseRaceDetector:

```bash
cd jpf-workspace

java -Xmx1024m -jar ./JPF/build/RunJPF.jar +classpath=bin \
  +listener=gov.nasa.jpf.listener.PreciseRaceDetector ThreadPoolSearch
```

**Result:** No race conditions, deadlocks, or synchronization errors detected.

## 🛠️ Project Structure

```
├── src/
│   ├── pcd/ass_single/part1/
│   │   ├── strategies/
│   │   │   ├── thread/          # Thread pool implementation
│   │   │   ├── virtual_threads/ # Virtual threads
│   │   │   ├── task_based/      # ForkJoin
│   │   │   ├── reactive_prog/   # RxJava
│   │   │   ├── async_event/     # Vert.x
│   │   │   └── actors/          # Akka
│   │   ├── SearchModel.java     # Shared state
│   │   ├── SearchView.java      # GUI
│   │   └── SearchController.java # Controller
│   └── benchmarks/               # Performance tests
├── pdfs/                         # Test datasets (10 to 50K files)
├── generator/                    # PDF generation scripts
└── jpf-workspace/                # Java PathFinder setup
```
## 🔧 Requirements

- **Java 21+** (for Virtual Threads support)
- **Java 8** (for JPF verification only)
- **Dependencies:**
    - Apache PDFBox (PDF text extraction)
    - RxJava 3 (Reactive)
    - Vert.x (Async Event)
    - Akka (Actors)


## 🏃 Running the Application

1. **To run the app**

```bash
mvn clean package 

mvn exec:java -Dexec.mainClass="pcd.ass_single.part1.PdfSearchApp"  
```

2.  **To run benchmarks in your machine**
```bash
mvn exec:java -Dexec.mainClass="pcd.ass_single.part1.benchmarks.<benchmark-name>"  
```





