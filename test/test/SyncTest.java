package test;

import org.junit.Test;

public class SyncTest {

	private DummyESOWithEmptyMethod dummyEso;

	private class Job1 implements Runnable {

		@Override
		public void run() {

			accessESO();

		}
	}

	public synchronized void accessESO() {
		dummyEso.exec();
	}

	@Test
	public void execute() {
		dummyEso = new DummyESOWithEmptyMethod();
		dummyEso.exec();
		Thread t1 = new Thread(new Job1());
		t1.start();
		accessESO();
		try {
			t1.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
