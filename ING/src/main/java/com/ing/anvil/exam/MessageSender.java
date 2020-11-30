package com.ing.anvil.exam;

import java.io.IOException;

public class MessageSender implements IMessageSender<IMessage> {

	@Override
	public void send(IMessage message) throws IOException {
		System.out.println(message);
		
	}

	
	
}
