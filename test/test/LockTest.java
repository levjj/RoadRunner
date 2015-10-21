package test;

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

	private void execute() throws InterruptedException {
		dummyEso = new DummyESOWithEmptyMethod();
		dummyEso.exec();
		Thread t1 = new Thread(new Job1());
		t1.start();
		synchronized (lock) {
			dummyEso.exec();
		}
		t1.join();

	}

	public static void main(String args[]) {

		LockTest test = new LockTest();
		try {
			test.execute();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
