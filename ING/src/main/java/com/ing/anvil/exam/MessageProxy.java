package com.ing.anvil.exam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author raokh
 *
 */
/**
 * @author raokh
 *
 */

class Pair<IMessage, Priority>{

	IMessage message;
	IPriority priority;
	public IMessage getMessage() {
		return message;
	}
	public void setMessage(IMessage message) {
		this.message = message;
	}
	public IPriority getPriority() {
		return priority;
	}
	public void setPriority(IPriority priority) {
		this.priority = priority;
	}
	public Pair(IMessage message, IPriority priority) {
		super();
		this.message = message;
		this.priority = priority;
	}
	
	public String toString() {
		return message.toString();
	}
	
}


public class MessageProxy implements IMessageProxy<IMessage> {

	AtomicBoolean isStop = new AtomicBoolean(false);
	AtomicBoolean isStarted = new AtomicBoolean(false);
	IMessageSender messageSender = null;
	
	PriorityQueue<Pair<IMessage, Priority>> queue = new PriorityQueue<Pair<IMessage, Priority>>(100, (a, b) -> (a == null || b == null) ? 0:a.getPriority().compareTo(b.getPriority()));
	
//	PriorityBlockingQueue<Pair<IMessage, Priority>> queue = new PriorityBlockingQueue<Pair<IMessage, Priority>>(100, (a, b) -> a.getPriority().compareTo(b.getPriority()));
	
	/* (non-Javadoc)
	 * @see com.ing.anvil.exam.IMessageProxy#start()
	 * Start() method must be called before this object will start sending messages.
	 * The object may receive message push requests before start() is called but it will just
	 * queue them up until start() is called.
	 */
	@Override
	public void start() throws Exception{
		// Start consumer to publish messages from PriorityQueue
		if(messageSender == null) {
			throw new Exception("Message Sender not Allocated");
		}
		isStarted.set(true);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		
		Future<String> future = executor.submit(new Callable<String>() {
		    @Override
		    public String call() throws Exception {
		        System.out.println("** Started");
		        while(isStarted.get() && !isStop.get()) {
					Optional<Pair<IMessage, Priority>> ops = Optional.ofNullable(queue.poll());
					if(ops.isPresent()) {
						messageSender.send(ops.get().getMessage());
					}

		        }
		        return "Success";
		    }
		});
		try {
		    future.get(); // raises ExecutionException for any uncaught exception in child
		} catch (ExecutionException e) {
		    System.out.println("** RuntimeException from thread ");
		    e.getCause().printStackTrace(System.out);
		    throw e;
		}
		executor.shutdown();
		System.out.println("** Main START method stopped");
		
	}

	
	/* (non-Javadoc)
	 * @see com.ing.anvil.exam.IMessageProxy#stop()
	 * Stop() method will be called once the proxy is no longer needed.
	 * Any queued (i.e.: not sent yet) message will be returned from the method in a list.
	 * All resources used by the object must be released before the method returns.
	 * 
	 */
	@Override
	public List<IMessage> stop() {
		// Should stop message publishing thread started in start()
		isStop.set(true);
		isStarted.set(false);
		System.out.println("************STOP: "+queue.size());
		List<IMessage> list = new ArrayList<IMessage>();
		while(!queue.isEmpty()) {
			Pair<IMessage, Priority> pair = queue.poll();
			if(pair != null) {
				list.add(pair.getMessage());
			}
		}
		
		return list;
	}
	
	/* (non-Javadoc)
	 * @see com.ing.anvil.exam.IMessageProxy#setMessageSender(com.ing.anvil.exam.IMessageSender)
	 * setMessageSender()
	 * This method must be called before start() is executed.
	 * The IMessageSender passed as an argument will be used for effective message sending.
	 * 
	 */
	@Override
	public void setMessageSender(IMessageSender<IMessage> sender) {
		this.messageSender = sender;
	}

	
	/* (non-Javadoc)
	 * @see com.ing.anvil.exam.IMessageProxy#send(com.ing.anvil.exam.IMessage, com.ing.anvil.exam.IPriority)
	 * Send()
	 * This method will be called from other objects by passing an IMessage and and IPriority.
	 * This method must be implemented in a non-blocking fashion and it must return as soon
	 * as possible.
	 * 
	 */
	@Override
	public void send(IMessage message, IPriority priority) {
		// Producer which will fill in records in an infinite PriorityQueue
		if(message != null && priority != null) {
			queue.add(new Pair(message, priority));
		}
		
	}
	
	public PriorityQueue<Pair<IMessage, Priority>> getQueue() {
		return queue;
	}


}
