import java.io.*;
import java.net.*;
import java.util.*;

public class Client
{
    public static void main(String[] args) throws IOException
    {
        BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
        String id, name, itemName, cost, stock, fromUser, fromServer;
        String itemId = "ID";
        String description = "Description";
        boolean morePackets = true, goodResponse = false, done = false;

        String dns;
        Scanner keyboard = new Scanner(System.in);
        System.out.print("Enter the DNS of the server to connect to server: ");
        dns = keyboard.nextLine();

        DatagramSocket udpSocket = new DatagramSocket();

        System.out.printf("\n%-8s %8s\n", itemId, description);

        InetAddress address = InetAddress.getByName(dns);
        byte[] blank = new byte[256];
        DatagramPacket emptyPacket = new DatagramPacket(blank, blank.length, address, 5200);
        udpSocket.send(emptyPacket);

        while(morePackets)
        {
            try
            {
                byte[] buf = new byte[256];
                DatagramPacket udpPacket = new DatagramPacket(buf, buf.length);
                udpSocket.receive(udpPacket);

                id = new String(udpPacket.getData(), 0, udpPacket.getLength());

                if(id.equals("done"))
                {
                    break;
                }
            
                byte[] buf2 = new byte[256];
                DatagramPacket udpPacket2 = new DatagramPacket(buf2, buf2.length);
                udpSocket.receive(udpPacket2);

                name = new String(udpPacket2.getData(), 0, udpPacket2.getLength());

                System.out.printf("%-8s %8s\n", id, name);
            }   
            catch(IOException e)
            {
                e.printStackTrace();
                morePackets = false;
            }
        }

        System.out.print("\nEnter an item ID for the product: ");

        while((fromUser = sysIn.readLine()) != null)
        {
            byte[] buf = fromUser.getBytes();
            DatagramPacket udpPacket = new DatagramPacket(buf, buf.length, address, 5200);
            udpSocket.send(udpPacket);

            byte[] buf2 = new byte[256];
            DatagramPacket udpPacket2 = new DatagramPacket(buf2, buf2.length);
            udpSocket.receive(udpPacket2);

            fromServer = new String(udpPacket2.getData(), 0, udpPacket2.getLength());

            if(fromServer.length()>4)
            {
                System.out.println(fromServer);
            }
            else
            {
                long startTime = System.nanoTime();
                byte[] buf3 = new byte[256];
                DatagramPacket udpPacket3 = new DatagramPacket(buf3, buf3.length);
                udpSocket.receive(udpPacket3);
    
                itemName = new String(udpPacket3.getData(), 0, udpPacket3.getLength());

                byte[] buf4 = new byte[256];
                DatagramPacket udpPacket4 = new DatagramPacket(buf4, buf4.length);
                udpSocket.receive(udpPacket4);
    
                cost = new String(udpPacket4.getData(), 0, udpPacket4.getLength());

                byte[] buf5 = new byte[256];
                DatagramPacket udpPacket5 = new DatagramPacket(buf5, buf5.length);
                udpSocket.receive(udpPacket5);
    
                stock = new String(udpPacket5.getData(), 0, udpPacket5.getLength());

                long endTime = System.nanoTime();
                long timeElapsed = (endTime - startTime);

                System.out.printf("\n%-8s %-30s %-15s %-20s %-20s\n", "ID", "Description", "Price", "Stock Count", "RTT of Query (in ns)");
                System.out.printf("%-8s %-30s %-15s %-20s %-20d\n", fromServer, itemName, cost, stock, timeElapsed);

                System.out.println("\nWould you like to continue?");

                while(goodResponse != true)
                {
		            String answer = sysIn.readLine();

                    if(answer.equals("no"))
                    {
                        System.out.println("Exiting...");
                        goodResponse = true;
                        done = true;
                    }
                    else if(answer.equals("yes"))
                    {
                        goodResponse = true;
			            System.out.print("\nEnter an item ID for the product: ");
                    }
                    else
                    {
                        System.out.println("\nUnknown response. 'yes' or 'no' only.");
                    }
                }
                if(done == true)
                {
                    break;
                }

		        goodResponse = false;
            }
        }

        keyboard.close();
        udpSocket.close();
    }
}