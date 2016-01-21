import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Random;

public class TimeServer {
	private static int PORT = 27780;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	
	private NTPRequestHandler reqHandler;
	private Random random;
	
	public TimeServer() {
		try {
			random = new Random();
			serverSocket = new ServerSocket(PORT);
			System.out.println("Server started on port: " + PORT);
			
			while (true){
				clientSocket = serverSocket.accept();
			
				reqHandler = new NTPRequestHandler(clientSocket);
				//start the thread
				new Thread(reqHandler).start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeServer();
		}

	}
	
	private void closeServer(){
		try {
			serverSocket.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void threadSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private long getTimeInMs(){
		return Calendar.getInstance().getTimeInMillis();
	}
	
	public static void main(String[] args) {
		new TimeServer();
	}

	private class NTPRequestHandler implements Runnable {
		private Socket client;

		//receive and send serialized object sent through socket
		private ObjectInputStream ois;
		private ObjectOutputStream oos;
		
		private boolean isRunning;
		
		public NTPRequestHandler(Socket client) throws IOException {
			this.client = client;
			ois = new ObjectInputStream(client.getInputStream());
			oos = new ObjectOutputStream(client.getOutputStream());
			isRunning = true;
		}

		@Override
		public void run() {
			///
			try {
				//read object from ois
				NTPRequest receivedReq = (NTPRequest) ois.readObject();

				//set server's receiving time (T2)
				receivedReq.setT2(getTimeInMs());
				
				//give an artifical 1200ms delay
				threadSleep(1200);
				
				//set server's sending time (T3)
				receivedReq.setT3(getTimeInMs());
				
				//write to output stream
				sendNTPAnswer(receivedReq);
				
				client.close();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e){
				//end of file when reading from client's socket
			}
		}

		private void sendNTPAnswer(NTPRequest request) throws IOException {
			///
			oos.writeObject(request);
		}

	}

}
