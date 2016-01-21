import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class TimeClient {
	private static String hostUrl = "127.0.0.1";
	private static int PORT = 27780;
	private Double minD;
	private NTPRequest minNTPrequest;
	private Socket socket;
	private int numTries = 10;
	private int msDelayBetweenMeasurements = 300;
	private Random random;
	
	private ArrayList<NTPRequest> pooledRequests;
	
	//used for sending and receiving serialized object across the network
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	public TimeClient() {
		pooledRequests = new ArrayList<NTPRequest>();
		minD = Double.MAX_VALUE;
		random = new Random();
		try {

			System.out.println("Commencing tests in 3...");
			threadSleep(1000);
			System.out.println("2...");
			threadSleep(1000);
			System.out.println("1...");
			threadSleep(1000);
			
			for (int i = 0; i < numTries; i++) {
				socket = new Socket(InetAddress.getByName(hostUrl), PORT);
				oos = new ObjectOutputStream(socket.getOutputStream());
				ois = new ObjectInputStream(socket.getInputStream());
				
				NTPRequest req = new NTPRequest();
				//set client's sending time (T1)
				
				req.setT1(getTimeInMs());
				sendNTPRequest(req);
				
				//accept NTP request from server
				NTPRequest receivedObj = (NTPRequest) ois.readObject();

				//close the socket
				System.out.println("Test #"+(i+1));
				
				//random delay between 10 to 100 ms
				threadSleep(10+random.nextInt(90));
				
				//set client's receiving time (T4)
				receivedObj.setT4(getTimeInMs());
				
				//calculation
				receivedObj.calculateOandD();
				pooledRequests.add(receivedObj);
				
				//delay
				threadSleep(msDelayBetweenMeasurements);
			}
			
			//print the result
			System.out.println("==== Result ====");
			for (int i = 0 ; i < pooledRequests.size() ; i++){
				NTPRequest item = pooledRequests.get(i);
				System.out.println((i+1)+". "+item.toString());
				System.out.println("------");
				
				if (minD > item.getD()){
					minD = item.getD();
					minNTPrequest = item;
				}
			}
			
			/*
			 * 	The selected delay is chosen by choosing the lowest delay in 10 measurements,
			 * 	The estimated offset (o) is calculated by [ o(i) - d(i)/2 <= o <= o(i) + d(i)/2 ]
			 * 	As seen in the NTP slide of TK1
			 */
			
			double o = minNTPrequest.getO();
			
			System.out.println("==== Lowest delay: "+minD+" | Offset: "+o+ "====");
			System.out.println("==== o(i) - d(i)/2 <= o <= o(i) + d(i)/2 ====");
			System.out.println("==== "+o+" - "+minD+"/2 <= o <= "+o+" + "+minD+"/2 ====");
			System.out.println("==== "+ (o - minD/2) +" <= o <= "+(o + minD/2)+" ====");
			

		} catch (UnknownHostException | ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private long getTimeInMs(){
		return Calendar.getInstance().getTimeInMillis();
	}

	private void sendNTPRequest(NTPRequest request) throws IOException {
		oos.writeObject(request);
	}

	private void threadSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TimeClient();
	}

}
