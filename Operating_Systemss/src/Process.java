import java.util.ArrayList; //vector stack

public class Process implements Comparable<Process>{ //to use collsort ,needs to have comparable

    int pid;
    int arrivalTime;
    ArrayList<Integer> cpuBursts;
    ArrayList<Integer> ioBursts;
    int remainingTime;
    int counter;

    //int countercpu;
    int waitingTime;
    int tunrAroundTime;
    int preemptionTimes;
    int sumBursts;

    Process(int pid, int arrivalTime, ArrayList<Integer> cpuBursts, ArrayList<Integer> ioBursts) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.cpuBursts = cpuBursts;
        this.ioBursts = ioBursts;
        this.remainingTime=cpuBursts.get(0);

        //this.countercpu=0;
        this.waitingTime=0;
        this.tunrAroundTime=0;
        this.counter=0;
        this.preemptionTimes=0;
        this.sumBursts=sumArrayList(cpuBursts);
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public ArrayList<Integer> getCpuBursts() {
        return cpuBursts;
    }

    public void setCpuBursts(ArrayList<Integer> cpuBursts) {
        this.cpuBursts = cpuBursts;
    }

    public ArrayList<Integer> getIoBursts() {
        return ioBursts;
    }

    public void setIoBursts(ArrayList<Integer> ioBursts) {
        this.ioBursts = ioBursts;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    @Override
    public int compareTo(Process o) {
        return Integer.compare(arrivalTime, o.arrivalTime);
    }

    public  int sumArrayList(ArrayList<Integer> list) {
        int sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i);
        }
        return sum;
    }
}