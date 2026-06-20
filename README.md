# GenomeVision

Desktop application for interactive analysis and visualization of large-scale genetic data in CSV format.

![Java](https://img.shields.io/badge/Java-21%2B-orange) ![Build](https://img.shields.io/badge/Build-Maven-blue)

## About

GenomeVision loads large DNA sample datasets from CSV files and provides tools to filter, compare, and visualize genetic similarity. Analysis runs asynchronously to keep the UI responsive even when processing thousands of samples. Results can be explored interactively via a zoomable heatmap and exported in PNG, SVG, or CSV format. All data stays local — nothing is sent to external services.

## Features

- Lazy-loading CSV import: reads sample names first, loads full data in the background
- Responsive progress indicator during file processing
- Sample filtering and multi-selection without writing code
- Heatmap visualization with zoom and pan
- Configurable epsilon parameter for tuning similarity calculations
- Export to PNG, SVG, and CSV

## Requirements

- Java 21+
- Maven 3.x

## Setup

```bash
git clone https://github.com/Firestone82/GenomeVision.git
cd GenomeVision
mvn clean package
java -jar target/GenomeVision-*.jar
```

### Input format

CSV files with sample identifiers in the first row and probe IDs with intensity values in subsequent rows.

## License

MIT License
