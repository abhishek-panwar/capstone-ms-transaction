package project.local;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.UUID;

import project.overlay.OverlayNode;

public class LocalNode {
	private String id = null; //id of local node (smart home)
	public OverlayNode overlayNode = null; //id of overlay node in which the smart home is 
	private ArrayList<Block> blockchain = new ArrayList<Block>();  //this is local blockchain in every local node (smart home)
	private HashSet<String> devicesRegistered = new HashSet<String>();  //registered devices in smart home
	private Policy policies=new Policy();  //policy details set by owner
	private ArrayList<Transaction> transactions = new ArrayList<Transaction>(); // for storing transactions
	private int previousHash; 
	private int successCount;
	private int failureCount;
	
	
	
	public LocalNode(String id, OverlayNode overlayNode) {
		super();
		this.id = id;
		this.overlayNode = overlayNode;
		this.successCount=0;
		this.failureCount=0;
	}
	public int getPreviousHash() {
		return previousHash;
	}
	
	public String getId() {
		return id;
	}
	public HashSet<String> getDevicesRegistered() {
		return devicesRegistered;
	}

	public int getSuccessCount() {
		return successCount;
	}
	public int getFailureCount() {
		return failureCount;
	}
	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}
	public void setFailureCount(int failureCount) {
		this.failureCount = failureCount;
	}

	/*
	 * public void addPolicies() { //requester, request for, device id, action
	 * policies.addPolicy(new String[] {"device_1", "access", "device_2", "allow"});
	 * policies.addPolicy(new String[] {"device_1", "access", "device_3", "allow"});
	 * }
	 */
	
	
	/*
	 * public Transaction[] registerDevice(String device_1, String device_2, String
	 * device_3) {//int transactionID, String deviceID, String transactionType
	 * 
	 * Transaction[] transactions = { new Transaction(121,"device_1","genesis"), new
	 * Transaction(122, "device_2", "genesis"), new Transaction(123, "device_3",
	 * "genesis") };
	 * 
	 * return transactions; }
	 */
	
	//public void access(String overlayID)
	
	/**
	 * access transactions are taken care here
	 */
	public void access()
	{
		//this.overlayID = overlayID;
		Scanner scan = new Scanner(System.in);
		System.out.println("You are in Local Node "+this.id+"  inside Overlay Node "+overlayNode.getId());
		while(true)
		{
			System.out.println("Enter a command {Acess, Register, Policy, Alter, print}");
			String input = scan.next();
			if(input.equalsIgnoreCase("access"))
			{
				this.accessTransaction();
				
			}
			else if(input.equalsIgnoreCase("register"))
			{
				this.registerTransaction();
			}
		
			else if(input.equalsIgnoreCase("policy"))
			{
				this.setPolicy();
				
			}
			else if(input.equalsIgnoreCase("print"))
			{
				this.printDetails();
				this.printBlockchain();
				
			}
			else if(input.equalsIgnoreCase("alter"))
			{
				this.alterBlock();
			}
			
			else if(input.equalsIgnoreCase("exit"))
			{
				break;
			}
		}
	}

	/**
	 * verifies the blockchain
	 */
	private void verifyBlockchain()
	{
		int hash=0;
		for(Block b : this.blockchain)
		{
			if(hash == b.getPreviousHash())
			{
				hash = b.getBlockHash();
			}
			else
			{
				System.out.println("Blockchain not valid.\n");
				return;
			}
		}
		System.out.println("Blockchain valid.\n");
	}
	
	/**
	 * creates and add block to the blockchain
	 * @param policies
	 * @param transactions
	 */
	private void addBlock(Policy policies, ArrayList<Transaction> transactions)
	{
		if(this.blockchain.size()==0)
			blockchain.add(new Block(0, policies, transactions));
		else
			blockchain.add(new Block(this.previousHash, policies, transactions));
		this.previousHash = this.blockchain.get(this.blockchain.size()-1).getBlockHash();
	}
	
	/**
	 * prints the blockchain
	 */
	private void printBlockchain()
	{
		System.out.println("Block chain for Local Node:  "+this.id);
		for(Block b : this.blockchain)
		{
			System.out.println(b.toString());
		}
	}
	
	/**
	 * creates a transaction object
	 * @param requesterID
	 * @param type
	 * @param requesteeID
	 * @return
	 */
	private Transaction createTransactionObject(String requesterID, String type, String requesteeID)
	{
		//return new Transaction(Math.abs((int)System.currentTimeMillis()),requesterID, type, requesteeID);
		
		UUID id = UUID.nameUUIDFromBytes((System.currentTimeMillis()+requesterID+requesteeID).getBytes());
		return new Transaction(id, requesterID, type, requesteeID);
		
	}

	/**
	 * create and add transaction to the transaction pool
	 * @param requesterID
	 * @param type
	 * @param requesteeID
	 */
	private void createTransaction(String requesterID, String type, String requesteeID)
	{
		if(this.transactions.size()<3)
		{
			this.transactions.add(createTransactionObject(requesterID, type, requesteeID));
			this.devicesRegistered.add(requesterID);
			this.setSuccessCount(this.getSuccessCount()+1);
			//System.out.println(this.transactions.get(this.transactions.size()-1).toString());
		}
		if(this.transactions.size()==3)
		{
			this.addBlock(this.policies, this.transactions); //block added
			this.transactions = new ArrayList<Transaction>();
			this.printBlockchain();
			System.out.println("verifying blockchain......");
			this.verifyBlockchain();
		}
	}
	
	/**
	 * access transactions are taken care here
	 */
	private void accessTransaction()
	{
		Scanner scan = new Scanner(System.in);
		System.out.println("From (device_ID): ");
		String requesterID = scan.next().toLowerCase().trim();
		if(!this.devicesRegistered.contains(requesterID.toLowerCase()))
				return;
		System.out.println("To (device_ID): ");
		String requesteeID = scan.next().toLowerCase().trim();
		//
		//
		//global transaction
		//
		//
		if(!this.devicesRegistered.contains(requesteeID.toLowerCase()))
		{
			if(!this.sendTransactionToOBM(requesterID, requesteeID))
			{
				//this.overlayNode.project.findOverlayNode(deviceIdFrom).failureCount++;
				System.out.println("access denied.");
			}
			else
			{
				System.out.println("access granted.");
			}
			//
			//
			//transaction needs to be added in the overlay I believe
			
			//this.createTransaction(deviceIdFrom, "access", deviceIdTo);
		}
		
		else if(this.policies.checkPolicy(new String[] {requesterID, "access", requesteeID}))
		{
			System.out.println("access granted.");
			this.createTransaction(requesterID, "access", requesteeID);
			//this.overlayNode.project.findLocalNodeWithDeviceID(deviceIdFrom).successCount++;
			
			this.overlayNode.project.findLocalNodeWithDeviceID(requesterID).setSuccessCount(
					this.overlayNode.project.findLocalNodeWithDeviceID(requesterID).getSuccessCount() + 1);
			
		}
		//only if transaction to OBM could not succeed
		else
		{
			//this.overlayNode.project.findLocalNodeWithDeviceID(deviceIdFrom).failureCount++;
			
			this.overlayNode.project.findLocalNodeWithDeviceID(requesterID).setFailureCount(
					this.overlayNode.project.findLocalNodeWithDeviceID(requesterID).getFailureCount() + 1);
			
			System.out.println("access denied.");
			//this.createTransaction(deviceIdFrom, "access", deviceIdTo);
			
		}
		
	}
	
	/**
	 * creates object of multi-sig transaction
	 * @param requesterID
	 * @param requesteeID
	 * @return
	 */
	private MultiSigTransaction createMultiSigTransaction(String requesterID, String requesteeID)
	{
		//return new Transaction(Math.abs((int)System.currentTimeMillis()),deviceIdFrom, type, deviceIdTo);
		
		UUID id = UUID.nameUUIDFromBytes((System.currentTimeMillis()+requesterID+requesteeID).getBytes());
		return new MultiSigTransaction(id, requesterID, true, requesteeID, false);
		
	}

	/**
	 * pass the transaction to OBM for further processing
	 * @param requesterID
	 * @param requesteeID
	 * @return
	 */
	private boolean sendTransactionToOBM(String requesterID, String requesteeID) {
		// TODO Auto-generated method stub
		
		MultiSigTransaction transaction = this.createMultiSigTransaction(requesterID, requesteeID);
		
		return this.overlayNode.takeCareOfThisTransaction(transaction);
	}

	public int getReputation()
	{
		if(this.successCount+this.failureCount==0)
			return 0;
		else 
			return 100*this.successCount/(this.successCount+this.failureCount);
	}

	private void printDetails()
	{
		System.out.println("Overlay node: "+this.overlayNode.getId());
		System.out.println("Local node: "+this.id);
		System.out.println("Reputation: "+this.getReputation()+" %      [S: "+this.successCount+"  F: "+this.failureCount+" ]");
		System.out.print("Devices registered: ");
		for(String id : this.devicesRegistered)
			System.out.print(id+"  ");
		
		System.out.println();
	}
	
	/**
	 * register transactions are taken care here
	 */
	private void registerTransaction()
	{
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter device ID");
		String deviceID = scan.next();
		//System.out.println(input);
		
		if(this.devicesRegistered.contains(deviceID.toLowerCase()))
		{
			System.out.println("Device already registered.");
		}
		else
		{
			this.createTransaction(deviceID, "genesis", "");
		}
	}
	
	/**
	 * policies are set here
	 */
	private void setPolicy()
	{
		Scanner scan = new Scanner(System.in);
		System.out.println("From (device_ID): ");
		String requesterID = scan.next().toLowerCase().trim();
		System.out.println("To (device_ID): ");
		String requesteeID = scan.next().toLowerCase().trim();
		
		System.out.println("Request for(access): ");
		String type = scan.next().toLowerCase().trim();
		System.out.println("Action (allow/deny): ");
		String action = scan.next().toLowerCase().trim();
		
		this.policies.addPolicy(new String[] {requesterID, type, requesteeID, action});
		System.out.println("policy added.");
		
		System.out.println(this.policies.toString());
	}
	
	/**
	 * adds device to the smart home
	 * @param deviceID
	 */
	public void addDevice(String deviceID)
	{
		if(!this.devicesRegistered.contains(deviceID.toLowerCase()))
		{
			this.createTransaction(deviceID, "genesis", "");
		}
	}
	
	/**
	 * adds new policy 
	 * @param requesterID
	 * @param type
	 * @param requesteeID
	 * @param action
	 */
	public void addPolicy(String requesterID, String type, String requesteeID, String action)
	{
		this.policies.addPolicy(new String[] {requesterID, type, requesteeID, action});
	}
	
	
	/**
	 * a block can be altered to check if the blockchain is broken  
	 */
	private void alterBlock()
	{
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter Block number");
		int block = scan.nextInt();
		this.blockchain.get(block-1).alterBlock();
		this.printBlockchain();
		System.out.println("verifying blockchain......");
		this.verifyBlockchain();
	}


	/**
	 * check transaction by finding the device in the network
	 * @param transaction
	 * @return
	 */
	public boolean checkTransaction(MultiSigTransaction transaction) {
		// TODO Auto-generated method stub
		
		//System.out.println("id:  "+id+"  requesterID: "+transaction.getRequesterID());
		if(this.id.equals(transaction.getRequesteeID()))
		{
			this.verifyTransaction(transaction);
			return true;
		}
		
		else if(this.devicesRegistered.contains(transaction.getRequesteeID()))
		{
			//
			//
			//check permission in local blockchain for this transaction
			//
			//
			this.verifyTransaction(transaction);
			
			
			return true;
			
		}
		return false;
	}
	
	/**
	 * verifies transaction
	 * @param transaction
	 */
	private void verifyTransaction(MultiSigTransaction transaction)
	{
		System.out.println("Transaction "+transaction.getTransactionID()+" verified..");
		transaction.setRequesteeSignature(true);
		
		this.overlayNode.project.findLocalNodeWithDeviceID(transaction.getRequesterID()).setSuccessCount(
				this.overlayNode.project.findLocalNodeWithDeviceID(transaction.getRequesterID()).getSuccessCount() + 1);
		
		this.overlayNode.project.findOverlayNodeWithDeviceID(transaction.getRequesterID()).setSuccessCount(
				this.overlayNode.project.findOverlayNodeWithDeviceID(transaction.getRequesterID()).getSuccessCount()
						+ 1);
		//this.overlayNode.project.findOverlayNodeWithDeviceID(transaction.getRequesterID()).successCount++;
	}
	
}
