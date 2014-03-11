/*
	STOP AND WAIT ARQ---------SENDER

	ACK's are not affected by noise
*/


import java.io.*;
import java.net.*;
import java.util.*;	//for Random function

class DataOfSender
{
	static InputStreamReader r;
	static BufferedReader br;
	static BufferedWriter writer;
	static DataInputStream input;
 	static DataOutputStream output;

	public static ServerSocket s;
	Socket s1=new Socket("localhost",1023);

	static boolean TimeOut;



	DataOfSender()throws IOException,InterruptedException
	{


	
	r=new InputStreamReader(System.in);
	br=new BufferedReader(r);
	writer = new BufferedWriter(new OutputStreamWriter(s1.getOutputStream()));
	input= new DataInputStream(s1.getInputStream());
	output= new DataOutputStream(s1.getOutputStream());

	TimeOut=false;
	
		start(); 
	}

/*----------------------------------start()---------------------------------------------------------*/

	void start()throws IOException,InterruptedException
	{
	int send_no;
	int sentFrame;
	
	send_no=0;

	

		while(true)
		{

		System.out.println("Sending frame with sequence no "+send_no); //Debugging purposes


			sentFrame=makeFrame(send_no);		

		System.out.println("Sequence no after adding noise "+sentFrame); //Debugging purposes

			sendFrame(sentFrame);

			waitForACK((send_no+1)%2);

				if(TimeOut)
				{
				System.out.println("-------------TIME OUT--------------");
				TimeOut=false;	//Reset the timer
				continue;	//Resend the frame
				}

			send_no=(send_no+1)%2;
			
		}
	}
/*----------------------------------improvedWaitTime---------------------------------------------------------*/

	synchronized void waitTime()throws InterruptedException
	{
		wait(10000);	//wait method waits for 'X'ms...therefore here waits for 10 seconds for ACK
		return;
	}

/*----------------------------------improvedWaitTime---------------------------------------------------------*/

	synchronized void improvedWaitTime()throws IOException,InterruptedException
	{
		for(int i=0;i<10;i++)
			if(input.available()!=0)	//to check if anything is present on buffer
			return;				//if true stop the timer and check if it is a valid
			else				//ACK else wait
			wait(1000);			//available() checks no of bytes waiting in buffer
							//thus if receiver doesnt send ACK for loop will execute
							//and time out will be triggered
	
	/* ---------------------------------------------------------------------------------------------------------
	NOTE:-

		Parameters of for loop and wait() can
		be changed to tune the program

		eg I.
			for(int i=0;i<5;i++)
			if(input.available()!=0)	
			return;				
			else				
			wait(2000);

			this checks the buffer every 2 secs..TimeOut=10 secs..Latency is large cause 2 secs is big
			main time is to wait..for loop execution and if loop checking is negligible

		eg II.
			for(int i=0;i<10;i++)
			if(input.available()!=0)	
			return;				
			else				
			wait(1000);

			this checks the buffer every 1 sec..TimeOut=10 secs..Latency is medium cause 1 secs is okay

		eg III.
			for(int i=0;i<100;i++)
			if(input.available()!=0)	
			return;				
			else				
			wait(100);

			this checks the buffer every 100 ms..TimeOut=10 secs..Latency is negligible cause 1 ms is fast

		Therefore tune the program acc to your choice..
		Experiment!!
		Be Creative!!

		cp[10] 	. .... ... 
		cp[11]	. .... ... 
		
	   ----------------------------------------------------------------------------------------------------------
	*/
							
	}

/*----------------------------------makeFrame----------------------------------------------------------------*/

	int makeFrame(int send_no)
	{
	int sentFrame;
	Random noise=new Random();

	sentFrame=send_no;	//Initially this will be sent
		
		sentFrame^=noise.nextInt(100)%2;	//After adding noise

	return sentFrame;
		
	}

/*-------------------------------------sendFrame---------------------------------------------------------------*/

	void sendFrame(int sentFrame)throws IOException
	{
	String toSend;
		
		
			toSend=Integer.toString(sentFrame);

		System.out.println("Press 1 to send frame"); //Debugging purposes	
		int choice=Integer.parseInt(br.readLine());  //Debugging purposes	

			writer.write(toSend);
			writer.newLine();
			writer.flush();


	}

/*----------------------------------waitForACK-----------------------------------------------------------*/

	void waitForACK(int ack_no)throws IOException,InterruptedException
	{
	int flag;
	String toReceive=new String();

		while(true)
		{
			
			/* waitTime(); */		/*
							------------------NOTE ABOUT waitTime()--------------------
							
							This is obviously a bad choice since it states that the buffer
							be checked directly after 10 seconds and then decide.

							But it is possible that a valid ACK can arrive within 2 seconds
							but then too it has to wait till ten seconds are over.
			
							This introduces unnecessary lag.
							Try it yourself

							Instead improvedWaitTime() tries to even things out by checking
							the buffer and then waiting for 2 seconds.

							It repeats 5 times so that total time waited before time out is
							the same 10 secs but it facilitates checking ACK's arrival 
							simultaneously and services them quickly.

							The order of statements is exactly correct since it first 
							checks the buffer and then waits

							*/

			
			improvedWaitTime();

			if(input.available()==0)	//again check buffer...obviously empty
			{
			TimeOut=true;			//Time out has occured..no need to check for ACK
			return;				//so return to address the issue
			}


		toReceive=input.readLine();
		flag=Integer.parseInt(toReceive);
			
			if(flag==ack_no)
			{
			System.out.println("ACK RECEIVED");
			return;
			}
		
		
		
		}

	}
/*-------------------------------------------------------------------------------------------------------------*/

}

class Sender
{
	public static void main(String args[])throws IOException,InterruptedException
	{
	DataOfSender d=new DataOfSender();
	}
}