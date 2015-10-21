package test;

public class JoinTest {

	private DummyESOWithEmptyMethod dummyEso;

	private class Job1 implements Runnable {

		@Override
		public void run() {

			dummyEso.exec();

		}
	}

	private void execute() throws InterruptedException {
		dummyEso = new DummyESOWithEmptyMethod();
		dummyEso.exec();
		Thread t1 = new Thread(new Job1());
		t1.start();
		t1.join();
		dummyEso.exec();
	}

	public static void main(String args[]) {

		JoinTest test = new JoinTest();
		try {
			test.execute();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
