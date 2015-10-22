package test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class TestRunner {

	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(JoinTest.class, SyncTest.class);
		System.out.println(">> Test case has been executed. Appropriate status checks will be added next");
	}
}
