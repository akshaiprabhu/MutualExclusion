import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Process in vector time stamp algorithm
 * 
 * @author Akshai Prabhu
 *
 */
class Process {
	int balance; // balance money in the process
	int lamportTime; // vector times of each process as integer array
	int count; // to count the replies from other processes
	String status;
	List<String> IPs;
	int id;

	/**
	 * Constructor
	 */
	Process() {
		balance = 1000;
		lamportTime = 0;
		count = 0;
		status = new String();
		IPs = new ArrayList<String>();
		try {
			if (InetAddress.getLocalHost().getHostName().equals("glados")) {
				id = 1;
			} else if (InetAddress.getLocalHost().getHostName().equals("kansas")) {
				id = 2;
			} else if (InetAddress.getLocalHost().getHostName().equals("newyork")) {
				id = 3;
			} else if (InetAddress.getLocalHost().getHostName().equals("glados")) {
				id = 4;
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Updates balance of the process according to transaction performed
	 * 
	 * @param lamportTime
	 * @param money
	 * @param event
	 */
	public void changeBalance(int lamportTime, int money, int event) {
		synchronized (this) { // synchronized block
			if (event == 0) { // Deposit money
				System.out.println("Deposit amount: $" + money);
				System.out.println("Before deposit: $" + balance);
				this.balance += money;
				System.out.println("After deposit: $" + balance);
				this.lamportTime = lamportTime + 1;
				System.out.println("Lamport time: (" + lamportTime + ")");
			} else if (event == 3) { // Received money from other process to be
										// added to this process
				System.out.println("Received amount: $" + money);
				System.out.println("Before receiving deposit: $" + balance);
				this.balance += money;
				this.lamportTime = Math.max(this.lamportTime, lamportTime) + 1;
				System.out.println("After receiving deposit: $" + balance);
				System.out.println("Lamport time: (" + lamportTime + ")");
			} else if (event == 1) { // withdraw money
				System.out.println("Withdraw amount: $" + money);
				System.out.println("Before withdraw: $" + balance);
				this.balance -= money;
				this.lamportTime = lamportTime + 1;
				System.out.println("After withdraw: $" + balance);
				System.out.println("Lamport time: (" + lamportTime + ")");
			} else if (event == 2) { // send money to different process
				System.out.println("Send amount: $" + money);
				System.out.println("Before sending: $" + balance);
				this.balance -= money;
				this.lamportTime = lamportTime + 1;
				sendMoney("" + money);
				System.out.println("After sending: $" + balance);
				System.out.println("Lamport time: (" + lamportTime + ")");
			}
		}

	}

	public void increaseCounter() {
		synchronized (this) {
			this.count++;
			if (count == 3) {
				status = "HELD";
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				status = "RELEASED";
				count = 0;
				if (IPs.size() != 0) {
					for (String str : IPs) {
						sendReply(str);
					}
					IPs.clear();
				}
			}
		}
	}

	private void sendReply(String IP) {
		try {
			if (InetAddress.getLocalHost().getHostName().equals("glados")) {
				if (IP.equals("129.21.37.18")) {
					send("129.21.37.18", 45000, "OKAY");
				} else if (IP.equals("129.21.37.16")) {
					send("129.21.37.16", 55000, "OKAY");
				} else if (IP.equals("129.21.37.18")) {
					send("129.21.37.15", 65000, "OKAY");
				}
			} else if (InetAddress.getLocalHost().getHostName().equals("kansas")) {
				if (IP.equals("129.21.37.16")) {
					send("129.21.37.16", 45000, "OKAY");
				} else if (IP.equals("129.21.37.15")) {
					send("129.21.37.15", 55000, "OKAY");
				} else if (IP.equals("129.21.22.196")) {
					send("129.21.22.196", 65000, "OKAY");
				}
			} else if (InetAddress.getLocalHost().getHostName().equals("newyork")) {
				if (IP.equals("129.21.37.15")) {
					send("129.21.22.15", 45000, "OKAY");
				} else if (IP.equals("129.21.22.196")) {
					send("129.21.22.196", 55000, "OKAY");
				} else if (IP.equals("129.21.37.18")) {
					send("129.21.37.18", 65000, "OKAY");

				}
			} else if (InetAddress.getLocalHost().getHostName().equals("arizonas")) {
				if (IP.equals("129.21.22.196")) {
					send("129.21.22.196", 45000, "OKAY");
				} else if (IP.equals("129.21.37.18")) {
					send("129.21.22.18", 55000, "OKAY");
				} else {
					send("129.21.37.16", 65000, "OKAY");
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

	/**
	 * To send money to a different process at random
	 * 
	 * @param money
	 */
	private void sendMoney(String money) {
		Random random = new Random();
		int server = random.nextInt(3);
		try {
			if (InetAddress.getLocalHost().getHostName().equals("glados")) {
				if (server == 0) {
					send("129.21.37.18", 40000, money);
				} else if (server == 1) {
					send("129.21.37.16", 50000, money);
				} else {
					send("129.21.37.15", 60000, money);
				}
			} else if (InetAddress.getLocalHost().getHostName().equals("kansas")) {
				if (server == 0) {
					send("129.21.37.16", 40000, money);
				} else if (server == 1) {
					send("129.21.37.15", 50000, money);
				} else {
					send("129.21.22.196", 50000, money);
				}
			} else if (InetAddress.getLocalHost().getHostName().equals("newyork")) {
				if (server == 0) {
					send("129.21.22.15", 40000, money);
				} else if (server == 1) {
					send("129.21.22.196", 40000, money);
				} else {
					send("129.21.37.18", 50000, money);
				}
			} else if (InetAddress.getLocalHost().getHostName().equals("arizonas")) {
				if (server == 0) {
					send("129.21.22.196", 40000, money);
				} else if (server == 1) {
					send("129.21.22.18", 40000, money);
				} else {
					send("129.21.37.16", 50000, money);
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * To send money as a client
	 * 
	 * @param IP
	 * @param port
	 * @param money
	 */
	private void send(String IP, int port, String money) {
		Socket socket;
		try {
			socket = new Socket(IP, port);
			OutputStream outToServer = socket.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			// send vector time and money to other process
			out.writeUTF(lamportTime + "," + money);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void validate(String[] msg) {

		if (status.equals("HELD")) {
			IPs.add(msg[0]);
		} else if (status.equals("WANTED") && lamportTime < Integer.parseInt(msg[1])) {
			IPs.add(msg[0]);
		} else if (status.equals("WANTED") && lamportTime == Integer.parseInt(msg[1])) {
			if (id < Integer.parseInt(msg[2])) {
				IPs.add(msg[0]);
			} else {
				sendReply(msg[0]);
			}
		} else if (status.equals("RELEASED")) {
			sendReply(msg[0]);
		} else if (status.equals("WANTED") && lamportTime < Integer.parseInt(msg[1])) {
			sendReply(msg[0]);
		}
	}
}

/**
 * Event in the process
 * 
 * @author Akshai Prabhu
 *
 */
class Event extends Thread {
	Process p;

	/**
	 * Constructor
	 * 
	 * @param p
	 */
	public Event(Process p) {
		this.p = p;
	}

	/**
	 * Thread run method
	 */
	public void run() {
		while (true) {
			Random random = new Random();
			int event = random.nextInt(3);
			int money = random.nextInt(100);
			// update balance according to the event and als update vector time
			p.changeBalance(p.lamportTime, money, event);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

/**
 * To receive money from one other process
 * 
 * @author Akshai Prabhu
 *
 */
class ReceiveMoneyOne extends Thread {
	Process p;
	ServerSocket serverSocket;

	/**
	 * Constructor
	 * 
	 * @param p
	 */
	public ReceiveMoneyOne(Process p) {
		this.p = p;
	}

	/**
	 * Thread run method
	 */
	public void run() {
		while (true) {
			Socket socket;
			String message = new String();
			try {
				serverSocket = new ServerSocket(40000);
				socket = serverSocket.accept();
				DataInputStream in = new DataInputStream(socket.getInputStream());
				message = in.readUTF();
				socket.close();
				serverSocket.close();
				String msg[] = message.split(",");
				// update vector time using vector time of other process
				p.changeBalance(Integer.parseInt(msg[0]), Integer.parseInt(msg[1]), 3);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

/**
 * To receive money from one other process
 * 
 * @author Akshai Prabhu
 *
 */
class ReceiveMoneyTwo extends Thread {
	Process p;
	ServerSocket serverSocket;

	/**
	 * Constructor
	 * 
	 * @param p
	 */
	public ReceiveMoneyTwo(Process p) {
		this.p = p;

	}

	/**
	 * Thread run method
	 */
	public void run() {
		while (true) {
			Socket socket;
			String message = new String();
			try {
				serverSocket = new ServerSocket(50000);
				socket = serverSocket.accept();
				DataInputStream in = new DataInputStream(socket.getInputStream());
				message = in.readUTF();
				socket.close();
				serverSocket.close();
				String msg[] = message.split(",");
				// update vector time using vector time of other process
				p.changeBalance(Integer.parseInt(msg[0]), Integer.parseInt(msg[1]), 3);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class ReceiveMoneyThree extends Thread {
	Process p;
	ServerSocket serverSocket;

	/**
	 * Constructor
	 * 
	 * @param p
	 */
	public ReceiveMoneyThree(Process p) {
		this.p = p;
	}

	/**
	 * Thread run method
	 */
	public void run() {
		while (true) {
			Socket socket;
			String message = new String();
			try {
				serverSocket = new ServerSocket(60000);
				socket = serverSocket.accept();
				DataInputStream in = new DataInputStream(socket.getInputStream());
				message = in.readUTF();
				socket.close();
				serverSocket.close();
				String msg[] = message.split(",");
				// update vector time using vector time of other process
				p.changeBalance(Integer.parseInt(msg[0]), Integer.parseInt(msg[1]), 3);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class ReceiveReplyOne extends Thread {
	Process p;
	ServerSocket serverSocket;

	/**
	 * Constructor
	 * 
	 * @param p
	 */
	public ReceiveReplyOne(Process p) {
		this.p = p;
	}

	/**
	 * Thread run method
	 */
	public void run() {
		while (true) {
			Socket socket;
			String message = new String();
			try {
				serverSocket = new ServerSocket(45000);
				socket = serverSocket.accept();
				DataInputStream in = new DataInputStream(socket.getInputStream());
				message = in.readUTF();
				socket.close();
				serverSocket.close();
				if (message.equals("OKAY")) {
					p.increaseCounter();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class ReceiveReplyTwo extends Thread {
	Process p;
	ServerSocket serverSocket;

	/**
	 * Constructor
	 * 
	 * @param p
	 */
	public ReceiveReplyTwo(Process p) {
		this.p = p;
	}

	/**
	 * Thread run method
	 */
	public void run() {
		while (true) {
			Socket socket;
			String message = new String();
			try {
				serverSocket = new ServerSocket(55000);
				socket = serverSocket.accept();
				DataInputStream in = new DataInputStream(socket.getInputStream());
				message = in.readUTF();
				socket.close();
				serverSocket.close();
				if (message.equals("OKAY")) {
					p.increaseCounter();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class ReceiveReplyThree extends Thread {
	Process p;
	ServerSocket serverSocket;

	/**
	 * Constructor
	 * 
	 * @param p
	 */
	public ReceiveReplyThree(Process p) {
		this.p = p;
	}

	/**
	 * Thread run method
	 */
	public void run() {
		while (true) {
			Socket socket;
			String message = new String();
			try {
				serverSocket = new ServerSocket(65000);
				socket = serverSocket.accept();
				DataInputStream in = new DataInputStream(socket.getInputStream());
				message = in.readUTF();
				socket.close();
				serverSocket.close();
				if (message.equals("OKAY")) {
					p.increaseCounter();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class ReceiveReqOne extends Thread {
	Process p;
	ServerSocket serverSocket;

	/**
	 * Constructor
	 * 
	 * @param p
	 */
	public ReceiveReqOne(Process p) {
		this.p = p;
	}

	/**
	 * Thread run method
	 */
	public void run() {
		while (true) {
			Socket socket;
			String message = new String();
			try {
				serverSocket = new ServerSocket(42000);
				socket = serverSocket.accept();
				DataInputStream in = new DataInputStream(socket.getInputStream());
				message = in.readUTF();
				String msg[] = message.split(",");
				p.validate(msg);
				socket.close();
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class ReceiveReqTwo extends Thread {
	Process p;
	ServerSocket serverSocket;

	/**
	 * Constructor
	 * 
	 * @param p
	 */
	public ReceiveReqTwo(Process p) {
		this.p = p;
	}

	/**
	 * Thread run method
	 */
	public void run() {
		while (true) {
			Socket socket;
			String message = new String();
			try {
				serverSocket = new ServerSocket(52000);
				socket = serverSocket.accept();
				DataInputStream in = new DataInputStream(socket.getInputStream());
				message = in.readUTF();
				String msg[] = message.split(",");
				p.validate(msg);
				socket.close();
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class ReceiveReqThree extends Thread {
	Process p;
	ServerSocket serverSocket;

	/**
	 * Constructor
	 * 
	 * @param p
	 */
	public ReceiveReqThree(Process p) {
		this.p = p;
	}

	/**
	 * Thread run method
	 */
	public void run() {
		while (true) {
			Socket socket;
			String message = new String();
			try {
				serverSocket = new ServerSocket(62000);
				socket = serverSocket.accept();
				DataInputStream in = new DataInputStream(socket.getInputStream());
				message = in.readUTF();
				String msg[] = message.split(",");
				p.validate(msg);
				socket.close();
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class SendRequest extends Thread {
	Process p;

	/**
	 * Constructor
	 * 
	 * @param p
	 */
	public SendRequest(Process p) {
		this.p = p;
	}

	/**
	 * Thread run method
	 */
	public void run() {
		while (true) {
			Scanner sc = new Scanner(System.in);
			System.out.println("Press Enter to acces critical section");
			String in = sc.nextLine();
			sendReq();
		}
	}

	private void sendReq() {

		String myIP = new String();
		try {
			myIP = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String message = myIP + "," + p.lamportTime + "," + p.id;
		p.status = "WANTED";
		if (myIP.equals("glados")) {

			send("129.21.37.18", 42000, message);

			send("129.21.37.16", 52000, message);

			send("129.21.37.15", 62000, message);

		} else if (myIP.equals("kansas")) {

			send("129.21.37.16", 42000, message);

			send("129.21.37.15", 52000, message);

			send("129.21.22.196", 62000, message);

		} else if (myIP.equals("newyork")) {

			send("129.21.22.15", 42000, message);

			send("129.21.22.196", 52000, message);

			send("129.21.37.18", 62000, message);

		} else if (myIP.equals("arizona")) {

			send("129.21.22.196", 42000, message);

			send("129.21.22.18", 52000, message);

			send("129.21.37.16", 62000, message);

		}
	}

	/**
	 * To send money as a client
	 * 
	 * @param IP
	 * @param port
	 * @param money
	 */
	private void send(String IP, int port, String message) {
		Socket socket;
		try {
			socket = new Socket(IP, port);
			OutputStream outToServer = socket.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			// send vector time and money to other process
			out.writeUTF(message);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

/**
 * Vector time main class that intiates all other threads
 * 
 * @author Akshai Prabhu
 *
 */
public class MutualExclusion {
	public static void main(String args[]) {
		Process p = new Process();
		Event e = new Event(p);
		ReceiveMoneyOne rmo = new ReceiveMoneyOne(p);
		ReceiveMoneyTwo rmt = new ReceiveMoneyTwo(p);
		ReceiveMoneyThree rmth = new ReceiveMoneyThree(p);
		rmo.start();
		rmt.start();
		rmth.start();
		ReceiveReplyOne rro = new ReceiveReplyOne(p);
		ReceiveReplyTwo rrt = new ReceiveReplyTwo(p);
		ReceiveReplyThree rrth = new ReceiveReplyThree(p);
		rro.start();
		rrt.start();
		rrth.start();

		Scanner sc = new Scanner(System.in);
		System.out.println("Press Enter to start timer");
		String in = sc.nextLine();
		sc.close();
		e.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		SendRequest sr = new SendRequest(p);
		sr.start();

	}
}
