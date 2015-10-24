package test;

import org.junit.Test;

public class LockTest {

	private Object lock = new Object();
	private DummyESOWithEmptyMethod dummyEso;

	private class Job1 implements Runnable {

		@Override
		public void run() {

			synchronized (lock) {

				dummyEso.exec();

			}

		}
	}

	@Test
	public void execute() {
		dummyEso = new DummyESOWithEmptyMethod();
		dummyEso.exec();
		Thread t1 = new Thread(new Job1());
		t1.start();
		synchronized (lock) {
			dummyEso.exec();
		}
		try {
			t1.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
