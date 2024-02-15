package Task2;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class iterativeDNSServer {
    public static int PORT = 53;

    public static HashMap<String, String> dnsRecords = new HashMap<>();

    public static void main(String[] args) {
        dnsRecords.put("google.ac.bd", "123.345.55.4");
        dnsRecords.put("example.ac.bd", "192.0.2.1");
        dnsRecords.put("openai.ac.bd", "203.0.113.5");

        try {
            DatagramSocket serverSocket = new DatagramSocket(PORT);
            System.out.println("DNS Server Established");

            while (true) {
                byte[] receiveData = new byte[1024 * 4];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String domainName = new String(receivePacket.getData(), 0, receivePacket.getLength());
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                System.out.println("Received DNS request for: " + domainName);

                String ipAddress = handleDNSRequest(domainName);
                byte[] sendData = ipAddress.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                serverSocket.send(sendPacket);

                System.out.println("Sent DNS response: " + ipAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String handleDNSRequest(String domainName) {
        if (dnsRecords.containsKey(domainName)) {
            return dnsRecords.get(domainName);
        } else {
            return "not found";
        }
    }
}
