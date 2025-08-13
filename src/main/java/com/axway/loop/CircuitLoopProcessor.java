package com.axway.loop;

import com.vordel.circuit.CircuitAbortException;
import com.vordel.circuit.InvocationEngine;
import com.vordel.circuit.Message;
import com.vordel.circuit.MessageProcessor;
import com.vordel.common.Dictionary;
import com.vordel.config.Circuit;
import com.vordel.config.ConfigContext;
import com.vordel.dwe.DelayedESPK;
import com.vordel.el.Selector;
import com.vordel.es.ESPK;
import com.vordel.es.Entity;
import com.vordel.es.EntityStore;
import com.vordel.es.EntityStoreException;
import com.vordel.trace.Trace;

public class CircuitLoopProcessor extends MessageProcessor {
	public static final int LOOPTYPE_WHILE = 1;
	public static final int LOOPTYPE_DOWHILE = 2;

	private Selector<Integer> loopType;
	private Selector<Boolean> loopCondition;
	private Selector<Integer> loopMax;
	private Selector<Integer> loopTimeout;

	private Selector<Boolean> loopErrorCircuit;
	private Selector<Boolean> loopErrorCondition;
	private Selector<Boolean> loopErrorMax;
	private Selector<Boolean> loopErrorTimeout;
	private Selector<Boolean> loopErrorEmpty;

	private Circuit loopCircuit;
	private ESPK loopContext;

	@Override
	public void filterAttached(ConfigContext ctx, Entity entity) throws EntityStoreException {
		super.filterAttached(ctx, entity);

		this.loopType = new Selector<>(entity.getStringValue("loopType"), Integer.class);
		this.loopCondition = new Selector<>(entity.getStringValue("loopCondition"), Boolean.class);
		this.loopMax = new Selector<>(entity.getStringValue("loopMax"), Integer.class);
		this.loopTimeout = new Selector<>(entity.getStringValue("loopTimeout"), Integer.class);

		this.loopErrorCircuit = new Selector<>(entity.getStringValue("loopErrorCircuit"), Boolean.class);
		this.loopErrorCondition = new Selector<>(entity.getStringValue("loopErrorCondition"), Boolean.class);
		this.loopErrorMax = new Selector<>(entity.getStringValue("loopErrorMax"), Boolean.class);
		this.loopErrorTimeout = new Selector<>(entity.getStringValue("loopErrorTimeout"), Boolean.class);
		this.loopErrorEmpty = new Selector<>(entity.getStringValue("loopErrorEmpty"), Boolean.class);

		CircuitLoopFilter filter = (CircuitLoopFilter) getFilter();
		DelayedESPK loopReference = new DelayedESPK(filter.getLoopCircuitPK());
		ESPK loopPK = loopReference.substitute(Dictionary.empty);
		Circuit loopCircuit = null;

		/*
		 * Ensure we have a configured circuit and we do not loop on our parent
		 * policy
		 */
		if ((!EntityStore.ES_NULL_PK.equals(loopPK) && (!entity.getParentPK().equals(loopPK)))) {
			loopCircuit = ctx.getCircuit(loopPK);

			this.loopContext = loopPK;
		}

		this.loopCircuit = loopCircuit;
	}

	@Override
	public boolean invoke(Circuit p, Message m) throws CircuitAbortException {
		Integer type = loopType.substitute(m);

		long start = System.currentTimeMillis();
		long timeout = timeout(m);

		if (type == null) {
			throw new CircuitAbortException("Unable to compute loop type " + loopType.getLiteral());
		}

		boolean result = true;
		boolean loop = false;
		int max = max(m);
		int count = 0;

		// Log inicial com configurações
		Trace.info("=== Circuit Loop Processor Starting ===");
		Trace.info("Loop Type: " + type + " (1=while, 2=do-while)");
		Trace.info("Max Iterations: " + max + " (0=unlimited)");
		Trace.info("Timeout: " + timeout + "ms (0=unlimited)");
		Trace.info("Start Time: " + start);

		/*
		 * difference between while and do/while loops is only for the first
		 * round
		 */
		switch (type) {
		case LOOPTYPE_WHILE:
			/* check condition before first round */
			Trace.info("WHILE loop - checking initial condition");
			loop = condition(m);
			Trace.info("Initial condition result: " + loop);

			if (!loop) {
				/* we are going to skip the loop, check for error condition */
				Trace.info("Loop skipped - checking error condition for empty loop");
				result = !isErrorCondition(m, loopErrorEmpty);
				Trace.info("Error condition result: " + result + " (final result: " + result + ")");
			}
			break;
		case LOOPTYPE_DOWHILE:
			/* check condition after first round */
			Trace.info("DO-WHILE loop - starting with loop=true");
			loop = true;
			break;
		default:
			throw new CircuitAbortException("Wrong loop type '" + type + "' (only 1 and 2 permitted)");
		}

		while (loop) {
			/* execute loop (and exit if the loop circuit return false) */
			Trace.info("Loop iteration #" + count + " starting");
			m.put("loopCount", count);
			
			// Executar o loop
			boolean loopResult = executeLoop(p, m);
			Trace.info("Loop execution result: " + loopResult);
			
			loop = loopResult;
			count++;
			
			if (!loop) {
				/*
				 * loop circuit did return an error, check for error condition
				 */
				Trace.info("Loop circuit failed - checking error condition for circuit failure");
				result = !isErrorCondition(m, loopErrorCircuit);
				Trace.info("Error condition result: " + result + " (final result: " + result + ")");
				Trace.info("Loop EXIT: Circuit failure");
			} else if (timeout > 0) {
				/* Check if the current loop has expired */
				long currentTime = System.currentTimeMillis();
				long elapsed = currentTime - start;
				loop = currentTime <= (start + timeout);
				
				Trace.info("Timeout check: elapsed=" + elapsed + "ms, timeout=" + timeout + "ms, loop=" + loop);

				if (!loop) {
					/*
					 * expiration time has exhausted, check for error condition
					 */
					Trace.info("Timeout exceeded - checking error condition for timeout");
					result = !isErrorCondition(m, loopErrorTimeout);
					Trace.info("Error condition result: " + result + " (final result: " + result + ")");
					Trace.info("Loop EXIT: Timeout exceeded");
				} else if (max > 0) {
					/* Check if the maximum iteration count has been reached */
					loop = count < max;
					Trace.info("Max iterations check: count=" + count + ", max=" + max + ", loop=" + loop);

					if (!loop) {
						/*
						 * max iteration count was exhausted, check for error
						 * condition
						 */
						Trace.info("Max iterations reached - checking error condition for max iterations");
						result = !isErrorCondition(m, loopErrorMax);
						Trace.info("Error condition result: " + result + " (final result: " + result + ")");
						Trace.info("Loop EXIT: Max iterations reached");
					} else {
						/* check if we need more iteration */
						Trace.info("Checking loop condition for next iteration");
						loop = condition(m);
						Trace.info("Loop condition result: " + loop);

						if (!loop) {
							/* not looping anymore, check for error condition */
							Trace.info("Loop condition false - checking error condition for condition failure");
							result = !isErrorCondition(m, loopErrorCondition);
							Trace.info("Error condition result: " + result + " (final result: " + result + ")");
							Trace.info("Loop EXIT: Condition false");
						}
					}
				}
			}
		}

		return result;
	}

	private boolean isErrorCondition(Message m, Selector<Boolean> attribute) throws CircuitAbortException {
		Boolean result = attribute.substitute(m);

		if (result == null) {
			throw new CircuitAbortException("Could not evaluate boolean expression " + attribute.getLiteral());
		}

		Trace.debug("isErrorCondition evaluating: " + attribute.getLiteral() + " = " + result);
		return result;
	}

	private boolean condition(Message m) throws CircuitAbortException {
		Boolean result = loopCondition.substitute(m);

		if (result == null) {
			throw new CircuitAbortException("Could not evaluate boolean expression " + loopCondition.getLiteral());
		}

		Trace.debug("Loop condition evaluating: " + loopCondition.getLiteral() + " = " + result);
		return result;
	}

	private int max(Message m) throws CircuitAbortException {
		Integer result = loopMax.substitute(m);

		if (result == null) {
			throw new CircuitAbortException("Could not evaluate maximum loop iteration count " + loopMax.getLiteral());
		}

		if (result < 0) {
			throw new CircuitAbortException("Can't have negative maximum iteration count");
		}

		return result;
	}

	private int timeout(Message m) throws CircuitAbortException {
		Integer result = loopTimeout.substitute(m);

		if (result == null) {
			throw new CircuitAbortException("Could not evaluate loop timeout " + loopMax.getLiteral());
		}

		if (result < 0) {
			throw new CircuitAbortException("Can't have negative maximum loop timeout");
		}

		return result;
	}

	private boolean executeLoop(Circuit p, Message m) throws CircuitAbortException {
		if (loopCircuit == null) {
			Trace.info("executeLoop: loopCircuit is null, returning false");
			return false;
		}
		
		Trace.debug("executeLoop: invoking circuit " + loopCircuit.getName());
		boolean result = InvocationEngine.invokeCircuit(loopCircuit, loopContext, m);
		Trace.debug("executeLoop: circuit result = " + result);
		return result;
	}
}
