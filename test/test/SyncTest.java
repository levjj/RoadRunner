package test;

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

	private void execute() throws InterruptedException {
		dummyEso = new DummyESOWithEmptyMethod();
		dummyEso.exec();
		Thread t1 = new Thread(new Job1());
		t1.start();
		accessESO();
		t1.join();

	}

	public static void main(String args[]) {

		SyncTest test = new SyncTest();
		try {
			test.execute();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
