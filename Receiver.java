/*
	STOP AND WAIT ARQ----RECEIVER

	ACK's are not affected by noise
*/

import java.io.*;
import java.net.*;

class DataOfReceiver
{
	static DataInputStream input;
 	static DataOutputStream output;
	static InputStreamReader r;
	static BufferedReader br;
	static BufferedWriter writer;
             
	//Socket s1=new Socket("116.203.252.110",1000);

		ServerSocket s;
		Socket s1;


	DataOfReceiver()throws IOException,InterruptedException
	{
	s=new ServerSocket(1023);
	s1=s.accept();

	r=new InputStreamReader(System.in);
	br=new BufferedReader(r);

	writer = new BufferedWriter(new OutputStreamWriter(s1.getOutputStream()));

	input= new DataInputStream(s1.getInputStream());
	output= new DataOutputStream(s1.getOutputStream());

	

		start();
	}

	void start()throws IOException,InterruptedException
	{
	boolean validFrame;

	int ACK=0;

	String sendACK;

		while(true)
		{

			validFrame=checkFrame(ACK);			


				if(validFrame)
				{
				ACK=(ACK+1)%2;	//since ACK's are either 0 or 1 for modulo 2

		System.out.println("Frame received...sending ACK with ACK no " +ACK); //for Debugging
				
				sendACK=Integer.toString(ACK);

		System.out.println("Press 1 to send ACK");	//Debugging purposes
		int choice=Integer.parseInt(br.readLine());	//Debugging purposes
				
				writer.write(sendACK);
				writer.newLine();
				writer.flush();
				
				}
			
	
		}
	}

	boolean checkFrame(int ACK)throws IOException
	{
	String toReceive;
	int received_no;

		
		toReceive=input.readLine();
		received_no=Integer.parseInt(toReceive);

			if(received_no==ACK)
			return true;

	return false;
	}

	synchronized void waitTime()throws InterruptedException
	{
		wait(1000);
		return;
	}


}

class Receiver
{
	public static void main(String args[])throws IOException,InterruptedException
	{
	DataOfReceiver d=new DataOfReceiver();
	}
}