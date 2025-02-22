import csv
import sys
from itertools import zip_longest

def transpose_csv(input_file, output_file):
    """
    Reads a tab-delimited CSV file, transposes its content (rows become columns and vice versa),
    and writes the transposed data to a new CSV file.

    Parameters:
        input_file (str): Path to the input CSV file.
        output_file (str): Path to the output (transposed) CSV file.
    """
    # Read the tab-delimited CSV file.
    with open(input_file, 'r', newline='', encoding='utf-8') as infile:
        reader = csv.reader(infile, delimiter='\t')
        rows = list(reader)

    # Transpose the data. zip_longest handles rows of uneven length by filling missing values with ''.
    transposed = list(zip_longest(*rows, fillvalue=''))

    # Write the transposed data to the output file using tab as the delimiter.
    with open(output_file, 'w', newline='', encoding='utf-8') as outfile:
        writer = csv.writer(outfile, delimiter='\t')
        writer.writerows(transposed)

if __name__ == '__main__':
    if len(sys.argv) != 3:
        print("Usage: python convert.py input_file.tsv output_file.tsv")
        sys.exit(1)

    input_file = sys.argv[1]
    output_file = sys.argv[2]
    transpose_csv(input_file, output_file)
