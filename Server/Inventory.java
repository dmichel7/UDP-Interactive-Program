import java.util.*;
import java.io.*;
import java.net.*;

public class Inventory
{
    String id;
    String name;
    String price;
    String stock;
    
    public Inventory(String itemId, String itemName, String itemPrice, String itemStock)
    {
        id = itemId;
        name = itemName;
        price = itemPrice;
        stock = itemStock;
    }

    public static ArrayList<Inventory> buildDatabase(ArrayList<Inventory> data)
    {
        Inventory item1 =  new Inventory("0001", "Dell Inspiron 15", "499.99", "250");
        data.add(item1);
        Inventory item2 =  new Inventory("0002", "Dell Inspiron 17", "699.99", "126");
        data.add(item2);
        Inventory item3 =  new Inventory("0003", "Dell Inspiron 15R", "549.99", "154");
        data.add(item3);
        Inventory item4 =  new Inventory("0004", "Lenovo ThinkPad", "799.99", "98");
        data.add(item4);
        Inventory item5 =  new Inventory("0005", "MacBook Pro", "1399.99", "74");
        data.add(item5);
        Inventory item6 =  new Inventory("0006", "Dell XPS 15", "1199.99", "85");
        data.add(item6);
        Inventory item7 =  new Inventory("0007", "Razer Blade 15", "1499.99", "45");
        data.add(item7);
        Inventory item8 =  new Inventory("0008", "Microsoft SurfaceBook 4", "1699.99", "122");
        data.add(item8);

        return data;
    }
    public static void main(String[] args) throws IOException, InterruptedException
    {
        ArrayList<Inventory> database = new ArrayList<Inventory>();
        DatagramSocket udpServerSocket = null;
        DatagramPacket udpPacket = null, udpPacket2 = null, udpPacket3 = null;
        String fromClient = null;
        boolean morePackets = true;
        int count = 0, count1 = 0;

        byte[] buf = new byte[256];
        
        buildDatabase(database);

        udpServerSocket = new DatagramSocket(5200);
        
	while(morePackets)
        {
            try
            {
		        int index = -1;
                udpPacket = new DatagramPacket(buf, buf.length);
                udpServerSocket.receive(udpPacket); 

                fromClient = new String(udpPacket.getData(), 0, udpPacket.getLength());
		
		        if(fromClient.length() == 256)
		        {
		            while(count != database.size())
            	    {
		                InetAddress address = udpPacket.getAddress();
                        int port = udpPacket.getPort();
                        byte[] buf2 = database.get(count).id.getBytes();
                        udpPacket = new DatagramPacket(buf2, buf2.length, address, port);
                        udpServerSocket.send(udpPacket);

                        byte[] buf3 = database.get(count).name.getBytes();
                        udpPacket2 = new DatagramPacket(buf3, buf3.length, address, port);
                        udpServerSocket.send(udpPacket2);

                        count++;
                
                        if(count == database.size())
                        {
                            String done = "done";

                            byte[] buf4 = done.getBytes();
                            udpPacket3 = new DatagramPacket(buf4, buf4.length, address, port);
                            udpServerSocket.send(udpPacket3);

                        }
                    }
                    
                    count = 0;
		        }
		        else
		        {
		            for(int i = 0; i<database.size(); i++)
                    {
                        if(database.get(i).id.equals(fromClient))
                        {
                            index = i;
                        }
                    }

                    if(index == -1)
                    {
                        String err = "Err: Item not found. Please retype or try another ID.";
                        InetAddress address = udpPacket.getAddress();
                        int port = udpPacket.getPort();
		                byte[] buf2 = err.getBytes();
                        udpPacket2 = new DatagramPacket(buf2, buf2.length, address, port);
                        udpServerSocket.send(udpPacket2);
                    }
                    else
                    {
                        String id = database.get(index).id;
                        String name = database.get(index).name;
                        String cost = database.get(index).price;
                        String stockCount = database.get(index).stock;

                        InetAddress address = udpPacket.getAddress();
                        int port = udpPacket.getPort();

                        while(count1 != 4)
                        {			
                            if(count1 == 0)
                            {
                                byte[] buf2 = id.getBytes();
                                udpPacket2 = new DatagramPacket(buf2, buf2.length, address, port);
                                udpServerSocket.send(udpPacket2);
                            }
                            else if(count1 == 1)
                            {
                                byte[] buf2 = name.getBytes();
                                udpPacket2 = new DatagramPacket(buf2, buf2.length, address, port);
                                udpServerSocket.send(udpPacket2);
                            }
                            else if(count1 == 2)
                            {
                                byte[] buf2 = cost.getBytes();
                                udpPacket2 = new DatagramPacket(buf2, buf2.length, address, port);
                                udpServerSocket.send(udpPacket2);
                            }
                            else if(count1 == 3)
                            {
                                byte[] buf2 = stockCount.getBytes();
                                udpPacket2 = new DatagramPacket(buf2, buf2.length, address, port);
                                udpServerSocket.send(udpPacket2);
                            }
                            count1++;
                        }
                        
                        count1 = 0;
                    }
               	}	
            }
            catch(IOException e)
            {
                e.printStackTrace();
                morePackets = false;
            }
        }

        udpServerSocket.close();
    }
}
