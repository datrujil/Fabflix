import os
import re

# Function to parse log lines and calculate totals and counts for a specific regex
def parse_log(log_lines, regex):
    total = 0
    count = 0

    for line in log_lines:
        match = regex.search(line)
        if match:
            value = int(match.group(1))
            total += value
            count += 1

    return total, count

# Get the directory path of the current Python file
log_file_path = '/Users/derektrujillo/Developer/ICS122B/Project 5/Project5ConnectionPooling/2023-fall-cs122b-coffee/Logs/Scaled-No-Pooling-HTTP-10-Thread-Log.txt'

with open(log_file_path, 'r') as file:
    log_data = file.readlines()

# Regular expressions to match log lines for TS (servlet) and TJ (JDBC)
ts_regex = re.compile(r'search\s+servlet\s+total\s+execution\s+time\s+\(TS\):\s+(\d+)\s+nanoseconds')
tj_regex = re.compile(r'JDBC\s+execution\s+time\s+\(TJ\):\s+(\d+)\s+nanoseconds')

# Parse TS and TJ data
ts_total, ts_count = parse_log(log_data, ts_regex)
tj_total, tj_count = parse_log(log_data, tj_regex)

# Calculate averages in milliseconds
avg_ts = (ts_total / ts_count) / 1_000_000 if ts_count > 0 else 0
avg_tj = (tj_total / tj_count) / 1_000_000 if tj_count > 0 else 0

print(f'Average Search servlet total execution time (TS): {avg_ts} ms')
print(f'Average JDBC execution time (TJ): {avg_tj} ms')