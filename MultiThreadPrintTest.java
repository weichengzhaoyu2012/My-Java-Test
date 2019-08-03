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
			while(integer.get()%2 == 1 && integer.get() < 101){
				lock1.lock();
				try {
					condition2.signal();
					System.out.println("a" + integer.getAndIncrement());
					System.out.println("1 await before");
					condition1.await();
					System.out.println("1 await after");
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					lock1.unlock();
				}
			}
		}, "ThreadA").start();

		new Thread(() -> {
			while(integer.get()%2 == 0 && integer.get() < 101){
				lock1.lock();
				try {
					condition1.signal();
					System.out.println("b" + integer.getAndIncrement());
					System.out.println("2 await before");
					condition2.await();
					System.out.println("2 await after");
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					lock1.unlock();
				}
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
			while(integer.get() < 101){
				while(integer.get()%2 == 1){
					System.out.println("1 lock");
					lock1.lock();
					try {
						System.out.println("1 await before");
						condition1.await();
						System.out.println("1 await after");
						// even print a[odd], integer increments to even
						// then condition2 in threadB in awaiting, and condition2 in threadA will try to signal
						System.out.println("a" + integer.getAndIncrement());
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						lock1.unlock();
					}
				}
				// in the while cycle the condition2 will keep trying to signal it's awaiting
				try {
					lock2.lock();
					condition2.signal();
				} finally {
					lock2.unlock();
				}
			}
		}, "ThreadA").start();

		new Thread(() -> {
			while(integer.get() < 101){
				while(integer.get()%2 == 0){
					System.out.println("2 lock");
					lock2.lock();
					try {
						System.out.println("2 await before");
						condition2.await();
						System.out.println("2 await after");
						// even print b[even], integer increments to odd
						// then condition1 in threadA in awaiting, and condition1 in threadB will try to signal
						System.out.println("b" + integer.getAndIncrement());
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						lock2.unlock();
					}
				}
				// in the while cycle the condition1 will keep trying to signal it's awaiting
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
