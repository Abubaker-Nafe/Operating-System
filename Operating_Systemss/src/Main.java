import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.util.*;

public class Main {
	static Scanner sc=new Scanner(System.in);
	static ArrayList<Process> Queue1;
	static ArrayList<Process> Queue2;
	static ArrayList<Process> Queue3;
	static ArrayList<Process> Queue4;
	static ArrayList<Process> QueueIO;

	public static void main(String[] args) throws IOException  {
		int Number_of_process, Max_ArrivalTime, Max_NoCpuBurst, Min_IO, Max_IO, Min_Cpu, Max_Cpu;

		System.out.print("Enter the Number of Processes: ");
		Number_of_process = sc.nextInt();
		System.out.print("Enter the max Arrival Time: ");
		Max_ArrivalTime = sc.nextInt();
		System.out.print("Enter the Max Number of CPU Burst: ");
		Max_NoCpuBurst = sc.nextInt();
		System.out.print("Enter the Min IO burst: ");
		Min_IO = sc.nextInt();
		System.out.print("Enter the Max IO burst: ");
		Max_IO = sc.nextInt();
		System.out.print("Enter the  Min Cpu burst: ");
		Min_Cpu = sc.nextInt();
		System.out.print("Enter the Max cpu burst: ");
		Max_Cpu = sc.nextInt();

		Random randomv = new Random();
		ArrayList<Process> processes = new ArrayList<>();
		Queue1 = new ArrayList<>();
		Queue2 = new ArrayList<>();
		Queue3 = new ArrayList<>();
		Queue4 = new ArrayList<>();
		QueueIO = new ArrayList<>();

		FileWriter fileWrite = new FileWriter("Workload.txt");
		for (int i = 0; i < Number_of_process; i++) //from 0 to nofp
		{
			int arrTime = randomv.nextInt(Max_ArrivalTime + 1);//can be 0  , 0<=t<=Maxarrivaltime
			int cpubursts = 1 + randomv.nextInt(Max_NoCpuBurst);     //1<ncburst
			ArrayList<Integer> cbursts = new ArrayList<>();
			ArrayList<Integer> iobursts = new ArrayList<>();
			//System.out.print(i+"\t"+arrTime+"\t");
			//to generate bursts
			for (int j = 0; j < cpubursts; j++) //starts from 0; //genrates cpu io bursts
			{
				int cpub = Min_Cpu + randomv.nextInt(Max_Cpu - Min_Cpu + 1);
				//System.out.print(cpub+"\t");
				cbursts.add(cpub);
				if (j < cpubursts - 1) //to not insert io burst at end ( ends with cpuburst)
				{
					int iob = Min_IO + randomv.nextInt(Max_IO - Min_IO + 1);
					//System.out.print(iob+"\t");
					iobursts.add(iob);
				}
			}
			//System.out.println();
			processes.add(new Process(i, arrTime, cbursts, iobursts));
		}
		//Collections.sort(processes);

		System.out.println("******************************");

		for (Process p : processes) {
			System.out.print(p.pid + "\t" + p.arrivalTime + "\t");
			fileWrite.write(p.pid + "\t" + p.arrivalTime + "\t");
			for (int i = 0; i < p.cpuBursts.size(); i++) {
				System.out.print(p.cpuBursts.get(i) + "\t");
				fileWrite.write(p.cpuBursts.get(i) + "\t");
				if (i < p.ioBursts.size()) {
					System.out.print(p.ioBursts.get(i) + "\t");
					fileWrite.write(p.ioBursts.get(i) + "\t");
				}
			}
			System.out.println();
			fileWrite.write("\n");
		}
		fileWrite.close();

		//read from file******************************************
		String filename = "Workload.txt";

		ArrayList<Process> Newprocesses = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split("\t");
				int processId = Integer.parseInt(values[0]);
				int arrivalTime = Integer.parseInt(values[1]);
				ArrayList<Integer> cpuBursts = new ArrayList<>();
				ArrayList<Integer> ioBursts = new ArrayList<>();
				for (int i = 2; i < values.length; i++) {
					if (i % 2 == 0) {
						cpuBursts.add(Integer.parseInt(values[i]));
					} else {
						ioBursts.add(Integer.parseInt(values[i]));
					}
				}
				Newprocesses.add(new Process(processId, arrivalTime, cpuBursts, ioBursts));

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.print("Sorted BY ArrivalTime : \n");
		Collections.sort(Newprocesses);

		for (Process p : Newprocesses) {
			System.out.print(p.pid + "\t" + p.arrivalTime + "\t");
			for (int i = 0; i < p.cpuBursts.size(); i++) {
				System.out.print(p.cpuBursts.get(i) + "\t");
				if (i < p.ioBursts.size()) {
					System.out.print(p.ioBursts.get(i) + "\t");
				}
			}
			System.out.println();
		}

		//*******************************************************************Simulator.

		for(Process p : Newprocesses) //storing processes sorted based on arrivl time
		{
			Queue1.add(p);
		}

		System.out.print("Please Enter The Quantum Q1: ");
		int q1 = sc.nextInt();

		System.out.print("Please Enter The Quantum Q2: ");
		int q2 = sc.nextInt();

		System.out.print("Enter the value of Alpha: ");
		double alpha =sc.nextDouble();
		int timeline = 0;


		System.out.println("**************************");

		while( !Queue1.isEmpty() || !Queue3.isEmpty() || !Queue2.isEmpty() || !Queue4.isEmpty()){
			while( !Queue1.isEmpty()){
				Process p = Queue1.remove(0);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if(p.remainingTime>q1){
					timeline=timeline+q1;
					p.remainingTime=p.remainingTime-q1;
					p.counter++;
					if(p.counter==10){
						System.out.println("PID_"+p.pid+" :Exceded it's 10 times Q1,and moved to Queue2! Remaining time :"+p.remainingTime);
						p.counter=0;
						Queue2.add(p);
					}else
					{
						Queue1.add(p);
					}
				}else{
					timeline=timeline+p.remainingTime;
					p.cpuBursts.remove(0); //remove the process cpu burst so we can move to the next cpu burst if exists
					if(p.cpuBursts.isEmpty()) //if there still exist cpu bursts for the process
					{
						System.out.println("PID_"+p.pid+ " Finished at time: "+ timeline+ " At Queue 1");
						p.tunrAroundTime=timeline-p.arrivalTime;
						p.waitingTime=p.tunrAroundTime-p.sumBursts;
					}
					else {

						System.out.println("PID_" + p.pid + ": Moved to IO at time : " + timeline);
						p.remainingTime = p.cpuBursts.get(0);
						timeline += p.ioBursts.get(0);
						System.out.println("PID_" + p.pid + ": Finished to IO at time : " + timeline);
						p.ioBursts.remove(0);	
						p.counter++;
						Queue1.add(p);
					}
				}
			}

			while(!Queue2.isEmpty()){ //Round Robin 
				Process p = Queue2.remove(0);

				if(p.remainingTime>q2){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					timeline=timeline+q1;
					p.remainingTime=p.remainingTime-q1;
					p.counter++;
					if(p.counter==10){
						System.out.println("PID_"+p.pid+" :Exceded it's 10 times Q2,and moved to Queue3! "+p.remainingTime);
						p.counter=0;
						Queue3.add(p);
					}else
					{
						Queue2.add(p);
					}
				}else{
					timeline=timeline+p.remainingTime;
					p.cpuBursts.remove(0);
					if(p.cpuBursts.isEmpty())
					{
						System.out.println("PID_"+p.pid+ " Finished at time: "+ timeline+ " At Queue 2");
						p.tunrAroundTime=timeline-p.arrivalTime;                                                                                                  
						p.waitingTime=p.tunrAroundTime-p.sumBursts;

					}else {
						System.out.println("PID_" + p.pid + ": Moved to IO at time : " + timeline);
						p.remainingTime = p.cpuBursts.get(0);
						timeline += p.ioBursts.get(0);
						System.out.println("PID_" + p.pid + ": Finished to IO at time : " + timeline);
						p.ioBursts.remove(0);	
						p.counter++;
						Queue2.add(p);
					}
				}
			}

			int TimeInCpu=0;
			while(!Queue3.isEmpty()){ //shortest remaining time first

				Collections.sort(Queue3, new Comparator<Process>() {
					public int compare(Process o1, Process o2) {
						return o1.remainingTime-o2.remainingTime;
					}});

				Process p=Queue3.remove(0);
				TimeInCpu = (int)((1- alpha) * TimeInCpu + alpha * p.cpuBursts.get(0) );
				timeline=timeline+TimeInCpu;
				p.remainingTime=p.remainingTime-TimeInCpu;
				if(p.remainingTime > 0){
					p.preemptionTimes++;
					if(p.preemptionTimes==3){
						Queue4.add(p);
						System.out.println("PID_"+p.pid+" was Prempted 3 times,and moved to Queue4!");
					}else
					{
						Queue3.add(p);
					}
				}else {
					p.cpuBursts.remove(0);
					if(p.cpuBursts.isEmpty())
					{
						System.out.println("PID_"+p.pid+" Finished At time "+timeline+" In Queue 3");
						p.tunrAroundTime=timeline-p.arrivalTime;
						p.waitingTime=p.tunrAroundTime-p.sumBursts;
					}
					else {
						System.out.println("PID_" + p.pid + ": Moved to IO at time : " + timeline);
						p.remainingTime = p.cpuBursts.get(0);
						timeline += p.ioBursts.get(0);
						System.out.println("PID_" + p.pid + ": Finished to IO at time : " + timeline);
						p.ioBursts.remove(0);						
						Queue3.add(p);
					}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 	
				}
			}

			while(!Queue4.isEmpty()){ //first come first serve
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Process p=Queue4.remove(0);

				p.cpuBursts.remove(0);


				if(p.cpuBursts.isEmpty())
				{
					timeline = timeline + p.remainingTime ;
				}else {
					p.remainingTime = p.remainingTime + p.sumArrayList(p.cpuBursts);
					timeline=timeline+p.remainingTime;

					if(!p.ioBursts.isEmpty())
					{
						int io = p.sumArrayList(p.ioBursts);
						timeline = timeline + io;
					}
				}
				p.remainingTime=0;
				System.out.println("PID_"+p.pid+" Finished At time "+timeline+" In Queue 4");
				p.tunrAroundTime=timeline-p.arrivalTime;
				p.waitingTime=p.tunrAroundTime-p.sumBursts;
			}

		}

		int sumWatingTime=0;
		for(Process p :Newprocesses){
			sumWatingTime=sumWatingTime+p.waitingTime;
		}
		double avg= sumWatingTime/Number_of_process;
		System.out.println("The Avg Waiting Time is: "+avg);

		System.out.println("");

	}
}