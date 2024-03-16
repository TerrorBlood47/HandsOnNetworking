import matplotlib.pyplot as plt

total_data_sent = [0, 4, 8, 12, 16, 20, 24, 28]
delay = [51, 2, 1, 4, 3, 2, 2, 2]

plt.plot(total_data_sent, delay, marker='o', linestyle='-', color='b')

plt.xlabel('Total Data Sent (byte)')
plt.ylabel('Delay (ms)')
plt.title('Total Data Sent vs Delay')
plt.show()
