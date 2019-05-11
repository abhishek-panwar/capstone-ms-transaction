package project.local;

import java.util.UUID;

public class MultiSigTransaction {
	
	private MultiSigTransaction previous; // not using this rightnow
	private UUID transactionID; //timestamp based
	private String requesterID; //who generates transaction
	private boolean requesterSignature;
	
	//private String transactionType;  //access
	private String requesteeID; //who generates transaction
	private boolean requesteeSignature;
	
	
	
	//private String multisig;
	
	public MultiSigTransaction(UUID transactionID, String requesterID, boolean requesterSignature, String requesteeID, boolean requesteeSignature) {
		// TODO Auto-generated constructor stub
		this.transactionID = transactionID;
		this.requesterID = requesterID;
		this.requesterSignature = requesterSignature;
		this.requesteeID = requesteeID;
		this.requesteeSignature = requesteeSignature;
		//this.transactionType = transactionType;
	}
	
	public UUID getTransactionID() {
		return transactionID;
	}
	
	public String getRequesterID() {
		return requesterID;
	}
	
	public String getRequesteeID() {
		return requesteeID;
	}
	
	public void setRequesteeSignature(boolean requesteeSignature) {
		this.requesteeSignature = requesteeSignature;
	}
	
	public void getTransactionObject()
	{
		
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return  transactionID+" "+requesterID+" "+requesterSignature+" "+requesteeID+" "+requesteeSignature; 
	}
	
	
	
	
	
	

}
