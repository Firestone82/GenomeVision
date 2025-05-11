# GenomeVision

**Author:** Pavel Mikula

A standalone Java (21+) desktop application built with Swing for interactive analysis and visualization of large-scale genetic data in CSV format. GenomeVision lets you load, filter and compare DNA sample matrices, compute similarity metrics, and render the results as an interactive heatmap or exportable graphic.

> **Note:** All processing is done locally—no genetic data leaves your machine. This tool is provided “as-is” for private or institutional use; adapt as needed. No warranty is provided.

## 🚀 Features
- **Lazy-Loading CSV Import**
  — Fast initial load by reading only record names, full data streams in background
- **Responsive Progress Indicator**
  — Asynchronous progress bar keeps the GUI fluid even for hundred-thousand-row files
- **Interactive Sample Filtering**
  — Search, select and subset your DNA samples in the GUI without writing any code
- **Custom Similarity Parameter (ε)**
  — Adjust epsilon to tune your similarity/distance calculations on the fly
- **Dynamic Heatmap Generation**
  — Zoomable, pannable matrix view with tooltips showing exact values
- **Exportable Results**
  — Save your final matrix as PNG, SVG or CSV for presentations and downstream analysis

## 🧰 Prerequisites

### Software
- **Java 21+**
- **Maven 3.x**
- Desktop OS: Windows, macOS or Linux with a GUI environment

### Data
- One or more **CSV files** containing DNA sample intensities
    - First row: sample identifiers
    - Subsequent rows: probe IDs + intensity values
- Typical file sizes: tens of thousands to millions of probes

## 🛠 Installation & Setup
1. **Clone the repository**
   ```bash
   git clone https://github.com/your-org/GenomeVision.git
   cd GenomeVision

2. **Build the project**
   ```bash
   mvn clean package
   ```

3. **Run the application**
   ```bash
    java -jar target/GenomeVision-*.jar
    ```

## 📜 Logs
Logs are stored in logs/ directory
- Daily rotation with compression to conserve space
