package test;

public class PrimaryTest {

	public DummyESOWithEmptyMethod dummyEso;

	private class Job1 implements Runnable {

		@Override
		public void run() {

			dummyEso.exec();

		}
	}

	private void execute() {
		dummyEso = new DummyESOWithEmptyMethod();
		dummyEso.exec();
		Thread t1 = new Thread(new Job1());
		t1.start();
		try {
			t1.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws Exception {

		PrimaryTest test = new PrimaryTest();
		test.execute();

	}

}
