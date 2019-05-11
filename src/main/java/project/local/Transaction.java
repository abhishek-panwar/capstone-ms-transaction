package project.local;

import java.util.UUID;

public class Transaction {
	
	private Transaction previous; // not using this rightnow
	private UUID transactionID; //timestamp based
	private String requesterID; //who generates transaction
	private String transactionType;  //access
	private String requesteeID; //who generates transaction
	
	
	
	//private String multisig;
	
	public Transaction(UUID transactionID, String deviceIdFrom, String transactionType, String deviceIdTo) {
		// TODO Auto-generated constructor stub
		this.transactionID = transactionID;
		this.requesterID = deviceIdFrom;
		this.requesteeID = deviceIdTo;
		this.transactionType = transactionType;
	}
	
	public UUID getTransactionID() {
		return transactionID;
	}
	
	public String getDeviceIdFrom() {
		return requesterID;
	}
	
	public String getDeviceIdTo() {
		return requesteeID;
	}
	
	public String getTransactionType() {
		return transactionType;
	}
	
	public void getTransactionObject()
	{
		
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ""+transactionID+"  " +requesterID+"  "+ transactionType+"  "+requesteeID; 
	}
	
	
	
	
	
	

}
