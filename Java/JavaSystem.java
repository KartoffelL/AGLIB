package Kartoffel.Licht.Java;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import Kartoffel.Licht.Tools.Timer;

public class JavaSystem {
	
	public static long getMemoryTotal_Usage() {
		return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed()+ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
	}
	public static long getMemoryHeap_Usage() {
		return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
	}
	public static long getMemoryNonHeap_Usage() {
		return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
	}
	
	public static long getMemoryTotal_Max() {
		return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax()+ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
	}
	public static long getMemoryHeap_Max() {
		return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax();
	}
	public static long getMemoryNonHeap_Max() {
		return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getMax();
	}
	
	public static long getMemoryTotal_Commited() {
		return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted()+ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getCommitted();
	}
	public static long getMemoryHeap_Commited() {
		return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted();
	}
	public static long getMemoryNonHeap_Commited() {
		return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getCommitted();
	}
	
	private static long last_thread = 0;
	private static long last_time = 0;
	public static double getThreadTime() {
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		long time = 0;
		for(Long threadID : threadMXBean.getAllThreadIds()) {
			time += threadMXBean.getThreadCpuTime(threadID);
		}
		double a = (double)(time-last_thread)/(Timer.getTime()-last_time);
		last_thread = time;
		last_time = Timer.getTime();
		return a;
	}
	static double[] l = new double[50];
	public static double getThreadAverageTime() {
		for(int i = 1; i < l.length; i++) {
			l[i-1] = l[i];
		}
		l[l.length-1] = getThreadTime();
		double b = 0;
		double d = 0;
		for(double la : l) {
			if(la != 0) {
				b++;
				d += la;
			}
		}
		return d/b;
	}

}
