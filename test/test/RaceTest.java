/******************************************************************************
 
 Copyright (c) 2010, Cormac Flanagan (University of California, Santa Cruz)
 and Stephen Freund (Williams College) 
 
 All rights reserved.  
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are
 met:
 
 * Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 
 * Redistributions in binary form must reproduce the above
 copyright notice, this list of conditions and the following
 disclaimer in the documentation and/or other materials provided
 with the distribution.
 
 * Neither the names of the University of California, Santa Cruz
 and Williams College nor the names of its contributors may be
 used to endorse or promote products derived from this software
 without specific prior written permission.
 
 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 ******************************************************************************/

package test;

import org.junit.Test;

public class RaceTest {

	private DummyESOWithVariable eso;
	final static int ITERS = 2;

	@Test
	public void execute() throws InterruptedException {
		eso = new DummyESOWithVariable();
		Thread[] tasks = new Thread[ITERS];
		for (int i = 0; i < ITERS; i++) {
			tasks[i] = new Thread(new Job());
			tasks[i].start();
		}
		for (int i = 0; i < ITERS; i++) {
			tasks[i].join();

		}

		System.out.println(">> Value is " + ITERS + " ? " + eso.get());
	}

	private class Job implements Runnable {

		@Override
		public void run() {
			eso.inc();

		}
	}

}