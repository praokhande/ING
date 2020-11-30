/**
 * 
 */
package com.ing.anvil.exam;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;


/**
 * @author raokh
 *
 */

public class MessageProxyTest {

	MessageProxy mProxy = null;
	MessageSender messageSender = null;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	public  void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterAll
	public void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		mProxy = new MessageProxy();
		messageSender = Mockito.mock(MessageSender.class);
		mProxy.setMessageSender(messageSender);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.ing.anvil.exam.MessageProxy#setMessageSender(com.ing.anvil.exam.IMessageSender)}.
	 */
	//@Test
	public void testSetMessageSender() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.ing.anvil.exam.MessageProxy#send(com.ing.anvil.exam.IMessage, com.ing.anvil.exam.IPriority)}.
	 */
	@Test
	public void testSendQueueAdd() {
		mProxy.send(new Message("Test"), new Priority(new Random().nextInt()));
		 Assert.assertEquals(mProxy.getQueue().size(), 1);
		 
	}
	
	@Test
	public void testSendQueueRemove() {
		mProxy.getQueue().poll();
		 Assert.assertEquals(mProxy.getQueue().size(), 0);
	}

	/**
	 * Test method for {@link com.ing.anvil.exam.MessageProxy#start()}.
	 * @throws Exception 
	 */
	@Test
	public void testStartFullFlow() throws Exception {
		mProxy.send(new Message("Test"), new Priority(new Random().nextInt()));
		ExecutorService executor = Executors.newFixedThreadPool(2);
		List<IMessage> results =  new ArrayList<>();
		CountDownLatch cd = new CountDownLatch(1);
		executor.submit(() -> {
			try {
				mProxy.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		Assert.assertEquals(mProxy.getQueue().size(), 1);
		executor.submit(() -> {
			try {
				while(true) {
					if(mProxy.isStarted.get()) {
						results.addAll(mProxy.stop());
						cd.countDown();
						break;
					}
				}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		cd.await();
		 Assert.assertEquals(results.size(), 0);
		
	}
	
	@Test
	public void testStart() throws Exception {
		mProxy.send(new Message("Test"), new Priority(new Random().nextInt()));
		Thread t = new Thread(() -> {
			try {
				mProxy.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		t.start();
		if(mProxy.isStarted.get()) {
			Assert.assertEquals(mProxy.getQueue().size(), 1);
		}
		
		
	}
	
	@Test
	public void testStop() throws Exception {
		mProxy.stop();
		Assert.assertEquals(false, mProxy.isStarted.get());
		
	}
	
}
