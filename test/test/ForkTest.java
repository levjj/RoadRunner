package test;

public class ForkTest {
	
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
	}
	
	public static void main(String args[]) {
		
		ForkTest test = new ForkTest();
		try {
			test.execute();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
}
