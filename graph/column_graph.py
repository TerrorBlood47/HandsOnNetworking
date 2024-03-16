import matplotlib.pyplot as plt

# Throughput values
reno_throughput = 22  # bytes/ms
newreno_throughput = 282  # bytes/ms

# Categories for the bar plot
categories = ['Reno', 'NewReno']
values = [reno_throughput, newreno_throughput]

# Create bar plot
plt.bar(categories, values, color=['blue', 'green'])

# Add labels and title
plt.xlabel('Congestion Control Algorithm')
plt.ylabel('Throughput (bytes/ms)')
plt.title('Comparison of Reno and NewReno Throughput')

# Show the plot
plt.show()
