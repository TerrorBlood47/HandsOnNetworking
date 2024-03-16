import matplotlib.pyplot as plt

# Read data from the text file
file_path = 'new_reno.txt'  # Update with the path to your text file
data = []
with open(file_path, 'r') as file:
    for line in file:
        x, y = map(int, line.strip().split())
        data.append((x, y))

# Separate x and y values
x_values = [point[0] for point in data]
y_values = [point[1] for point in data]

# Plot the data
plt.figure(figsize=(10, 6))
plt.plot(x_values, y_values, marker='o', linestyle='-',linewidth=0.5,markersize=3)
plt.title('cwnd vs time')
plt.xlabel('Time(ms)')
plt.ylabel('cwnd')
plt.grid(True)
plt.show()