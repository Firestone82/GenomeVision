# GenomeVision

Desktop application for interactive analysis and visualization of large-scale genetic data in CSV format.

![Java](https://img.shields.io/badge/Java-21%2B-orange) ![Maven](https://img.shields.io/badge/Build-Maven-blue)

## About

GenomeVision loads large DNA sample datasets from CSV files and provides tools to filter, compare, and visualize genetic similarity. File import runs asynchronously with a progress indicator so the UI stays responsive even for large datasets. Results are displayed as an interactive heatmap with zoom and pan, and can be exported locally in multiple formats. No data leaves your machine.

## Features

- Lazy-loading CSV import: reads sample names first, processes full data in the background
- Responsive progress indicator during file loading
- Sample filtering and multi-selection without writing code
- Heatmap visualization with zoom and pan
- Configurable epsilon parameter for similarity threshold tuning
- Export to PNG, SVG, and CSV

## Requirements

- Java 21+
- Maven 3.x

## Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/Firestone82/GenomeVision.git
   cd GenomeVision
   ```

2. Build the project:
   ```bash
   mvn clean package -DskipTests
   ```

3. Run the application:
   ```bash
   java -jar target/GenomeVision-*.jar
   ```

### Input data format

CSV with sample identifiers in the first row and probe IDs with intensity values in subsequent rows.

## License

MIT License
