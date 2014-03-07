/******************************************************************************

  Copyright (c) 2010, Cormac Flanagan (University of California, Santa Cruz)
  and Stephen Freund (Williams College) 

  All rights reserved.  

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are met:

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

package rr.profiling;

import java.util.ArrayDeque;
import java.util.Deque;

import rr.annotations.Abbrev;
import rr.event.MethodEvent;
import rr.meta.MethodInfo;
import rr.tool.Tool;
import acme.util.Util;
import acme.util.option.CommandLine;

/**
 * Count the number of events for most common cases.
 */

@Abbrev("PR")
public class MethodProfiler extends Tool {

	Deque<Long> currentTimers = new ArrayDeque<Long>();

	public MethodProfiler(String name, Tool next, CommandLine commandLine) {
		super(name, next, commandLine);
	}

	@Override
	public void enter(MethodEvent me) {
		currentTimers.addFirst(System.currentTimeMillis());
		super.enter(me);
	}

	@Override
	public void exit(MethodEvent me) {
		super.exit(me);
		long time = System.currentTimeMillis() - currentTimers.removeFirst();
		MethodInfo mi = me.getInfo();
		StringBuilder sb = new StringBuilder();
		sb.append(mi.toSimpleName());
		sb.append("(");
		for (Object obj : me.getArguments()) {
			sb.append(obj);
			sb.append(",");
		}
		sb.append(") ");
		sb.append(time);
		Util.log(sb.toString());
	}
}