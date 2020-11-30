package com.ing.anvil.exam;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

	static volatile boolean isStoped = false;
	public static void main(String[] args) {
		
		IMessageProxy<IMessage> messageProxy = new MessageProxy();
		IMessageSender<IMessage> messageSender = new MessageSender();
		
		messageProxy.setMessageSender(messageSender);
		// Create producer thread 
        Thread t1 = new Thread(new Runnable() { 
            @Override
            public void run() {
            	
            	int count = 0;
            	Random ra = new Random();
            	while(!isStoped) {
//            	while(true) {
            		Message msg = new Message("message: "+count);
            		Priority priority = new Priority(ra.nextInt(3));
            		try { 
                        messageProxy.send(msg, priority); 
                    } catch (Exception e) { 
                        e.printStackTrace(); 
                    }
            		count++;
            	}
                System.out.println("MAIN STOP");
            } 
        }); 
  
        // Create consumer thread 
        Thread t2 = new Thread(new Runnable() { 
            @Override
            public void run() 
            { 
                try { 
                    messageProxy.start();
                } 
                catch (Exception e) { 
                    e.printStackTrace(); 
                } 
            } 
        }); 
  
        // Start both threads 
        t1.start(); 
        
        try {
        	Thread.sleep(100);
			t2.start();
			Thread.sleep(500);
			isStoped = true;
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
  
        List<IMessage> list = messageProxy.stop();
        System.out.println("*****: " + list.size());
		
	}

}
