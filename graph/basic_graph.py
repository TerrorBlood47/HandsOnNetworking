import matplotlib.pyplot as plt

RTT = [550, 590, 658 ]
ERROR = [10,20,30]

plt.plot(ERROR, RTT, marker='o', linestyle='-', color='r')

plt.ylabel('RTT(ms)')
plt.xlabel('Error%')
plt.title('TCP  New Reno Loss Rate vs RTT')
plt.show()
