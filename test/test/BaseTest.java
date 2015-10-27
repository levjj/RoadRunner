package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;

import tools.eso.ESOChecker;

public class BaseTest {

	public BaseTest() {
		super();
	}
	
	protected Thread forkAndAccess(final DummyESO eso) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				eso.exec();
			}
		});
		t.start();
		return t;
	}

	protected void assertNoViolation() {
		assertEquals(ESOChecker.getViolations().size(), 0);
	}

	protected void assertViolation() {
		assertTrue(ESOChecker.getViolations().size() >= 1);
	}
	
	@Before
	public void resetViolations() {
		ESOChecker.resetViolations();
	}
}