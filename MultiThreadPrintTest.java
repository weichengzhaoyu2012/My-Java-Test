package algorithm;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadPrintTest {
	private static volatile AtomicInteger integer = new AtomicInteger(1);
	public static void main(String[] args) {
		//action();
		//action1();
		//action2();
	}

	/**
	 * one lock contains two conditions
	 */
	private static void action(){
		ReentrantLock lock1 = new ReentrantLock();
		Condition condition1 = lock1.newCondition();
		Condition condition2 = lock1.newCondition();

		new Thread(() -> {
			while(integer.get() < 1001){
				if(integer.get()%2==0){
					lock1.lock();
					try {
						condition2.signal();
						condition1.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						lock1.unlock();
					}
				}
				System.out.println("a" + integer.getAndIncrement());
			}
		}, "ThreadA").start();

		new Thread(() -> {
			while(integer.get() < 1001){
				if(integer.get()%2!=0){
					lock1.lock();
					try {
						condition1.signal();
						condition2.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						lock1.unlock();
					}
				}
				System.out.println("b" + integer.getAndIncrement());
			}
		}, "ThreadB").start();
	}

	/**
	 * two locks which has it's only condition
	 * which can reduce the lock contention of the conditions
	 */
	private static void action1(){
		ReentrantLock lock1 = new ReentrantLock();
		Condition condition1 = lock1.newCondition();
		ReentrantLock lock2 = new ReentrantLock();
		Condition condition2 = lock2.newCondition();

		new Thread(() -> {
			while(integer.get() < 1001){
				if(integer.get()%2==0){
					lock1.lock();
					try {
						condition1.await();
						System.out.println("a" + integer.getAndIncrement());
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						lock1.unlock();
					}
				}
				// if the threadB do some operations which wasting time,
				// in the while cycle the condition2 can keep trying to signal it's await
				try {
					lock2.lock();
					condition2.signal();
				} finally {
					lock2.unlock();
				}
			}
		}, "ThreadA").start();

		new Thread(() -> {
			while(integer.get() < 1001){
				if(integer.get()%2!=0){
					lock2.lock();
					try {
						// simulate the operation which wastes time
						//TimeUnit.MICROSECONDS.sleep(10);
						condition2.await();
						System.out.println("b" + integer.getAndIncrement());
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						lock2.unlock();
					}
				}
				try {
					lock1.lock();
					condition1.signal();
				} finally {
					lock1.unlock();
				}
			}
		}, "ThreadB").start();
	}

	private static void action2(){
		Object o = new Object();
		new Thread(() -> {
			while (integer.get() < 101){
				synchronized (o){
					// odd
					if (integer.get()%2 == 1){
						System.out.println("a" + integer.getAndIncrement());
						o.notifyAll();
					} else {
						try {
							o.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}, "ThreadA").start();

		//线程2-偶数线程
		new Thread(() -> {
			while (integer.get() < 101){
				synchronized (o){
					// even
					if (integer.get()%2 == 1){
						try {
							o.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						System.out.println("b" + integer.getAndIncrement());
						o.notifyAll();
					}
				}
			}
		}, "ThreadB").start();
	}
}
