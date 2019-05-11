package project.overlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.UUID;

import project.local.Block;
import project.local.LocalNode;
import project.local.MultiSigTransaction;
import project.local.OverlayBlock;
import project.local.Policy;
import project.local.Transaction;
import project.main.Project;

public class OverlayNode {
	private String id = null; //id of overlay node
	public Project project;
	
	private HashMap<String, LocalNode> localNodes = new HashMap<String, LocalNode>();
	private ArrayList<MultiSigTransaction> transactions = new ArrayList<MultiSigTransaction>(); // for storing transactions
	private ArrayList<OverlayBlock> blockchain = new ArrayList<OverlayBlock>();  //this is Global blockchain in every overlay node 
	private int previousHash;
	private int successCount;
	private int failureCount;
	
	
	//logic for blockchain
	
	public String getId() {
		return id;
	}
	
	public HashMap<String, LocalNode> getLocalNodes() {
		return localNodes;
	}
	
	public OverlayNode(String id, Project project) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.project = project;
		this.successCount=0;
		this.failureCount=0;
	}
	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}
	
	public void setFailureCount(int failureCount) {
		this.failureCount = failureCount;
	}
	public int getSuccessCount() {
		return successCount;
	}
	public int getFailureCount() {
		return failureCount;
	}
	
	private boolean checkID(String id)
	{
		if(this.localNodes.containsKey(id))
			return true;
		return false;
	}
	
	public LocalNode getNode(String id)
	{
		if(checkID(id))
			return this.localNodes.get(id);
		return null;
	}
	
	public void access()
	{
		Scanner scan = new Scanner(System.in);
		System.out.println("You are in Overlay Node "+this.id);
		while(true)
		{
			System.out.println("Enter a command: {add (local node), access, print}");
			String input = scan.next();
			//this.add(id);
			if(input.equalsIgnoreCase("access"))
			{
				System.out.println("Enter Local node ID: ");
				input = scan.next();
				if(this.checkID(input))
					this.getNode(input).access();
			}
			else if(input.equalsIgnoreCase("add"))
			{
				System.out.println("Enter Local node ID to add: ");
				input = scan.next();
				if(!this.checkID(input))
				{
					this.add(input);
					System.out.println("local node "+input+" added");
				}
			}
			else if(input.equalsIgnoreCase("print"))
			{
				this.printDetails();
			}
			else if(input.equalsIgnoreCase("exit"))
				return;
		}
		
	}
	
	public void add(String id)
	{
		if(!this.localNodes.containsKey(id))
		{
			this.localNodes.put(id, new LocalNode(id, this));
			UUID uid = UUID.nameUUIDFromBytes((System.currentTimeMillis()+id).getBytes());
			MultiSigTransaction transaction = new MultiSigTransaction(uid, id, true, null, false);
			this.addTransaction(transaction); //genesis transaction
			this.setSuccessCount(this.getSuccessCount()+1);
		}
	}
	
	
	public boolean takeCareOfThisTransaction(MultiSigTransaction transaction)
	{
		boolean verified=false;
		if(verified=this.broadcastLocally(transaction))
		{
			return verified;
		}
		else if(verified=this.project.takeCareOfThisTransaction(transaction))
		{
			return verified;
		}
		else
		{
			
			this.project.findLocalNodeWithDeviceID(transaction.getRequesterID()).setFailureCount(
					this.project.findLocalNodeWithDeviceID(transaction.getRequesterID()).getFailureCount() + 1);
			
			this.project.findOverlayNodeWithDeviceID(transaction.getRequesterID()).setFailureCount(
					this.project.findOverlayNodeWithDeviceID(transaction.getRequesterID()).getFailureCount() + 1);
			
			
			System.out.println("*****************device not found anywhere*****************");
			return verified;
		}
		
	}
	
	public boolean broadcastLocally(MultiSigTransaction transaction)
	{
		boolean verified=false;
		for(String id : this.localNodes.keySet())
		{
			
			verified |= this.localNodes.get(id).checkTransaction(transaction);
		}
		
		if(verified)
		{
			System.out.println("**********Device/node found in cluster with id: "+this.id);
			System.out.println(transaction.toString());
			
			this.addTransaction(transaction);
			//this.printDetails();
		}
		
		return verified;
	}
	
	
	
	private void addTransaction(MultiSigTransaction transaction)
	{
		if(this.transactions.size()<3)
		{
			this.transactions.add(transaction);
			//take care of this transaction
			this.project.broadcastThisTransaction(transaction, this);
		}
		if(this.transactions.size()==3)
		{
			this.addBlock(this.transactions); //block added
			this.transactions = new ArrayList<MultiSigTransaction>();
			this.printBlockchain();
			System.out.println("verifying blockchain......");
			this.verifyBlockchain();
			
		}
			
	}
	
	
	private void verifyBlockchain()
	{
		int hash=0;
		for(OverlayBlock b : this.blockchain)
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
	
	public void verifyBlock(OverlayBlock block) {
		// TODO Auto-generated method stub
		//
		//
		// use some verification logic
		block.setPreviousHash(this.previousHash);
		this.previousHash = block.getBlockHash();
		this.blockchain.add(block);
		//
		//
		//
	}
	
	public void verifyAndAddTransaction(MultiSigTransaction transaction)
	{
		if(this.transactions.size()<3)
		{
			this.transactions.add(transaction);
			
		}
		if(this.transactions.size()==3)
		{
			this.addBlock(this.transactions); //block added
			this.transactions = new ArrayList<MultiSigTransaction>();
			this.printBlockchain();
			System.out.println("verifying blockchain......");
			this.verifyBlockchain();
			
		}
		
		
	}
	
	private void addBlock(ArrayList<MultiSigTransaction> transactions)
	{
		OverlayBlock block = null;
		if(this.blockchain.size()==0)
		{
			block = new OverlayBlock(0, transactions);
			this.blockchain.add(block);
		}
		else
		{
			block = new OverlayBlock(this.previousHash, transactions);
			this.blockchain.add(block);
		}
		this.previousHash = this.blockchain.get(this.blockchain.size()-1).getBlockHash();
		//this.project.takeCareOfThisBlock(block, this);
	}
	
	private void printBlockchain()
	{
		System.out.println("Block chain for Overlay Node:  "+this.id);
		for(OverlayBlock b : this.blockchain)
		{
			System.out.println(b.toString());
		}
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
		System.out.println("Overlay node: "+this.id);
		System.out.println("Reputation: "+this.getReputation()+" %      [S: "+this.successCount+"  F: "+this.failureCount+" ]");
		System.out.print("Nodes in cluster: ");
		for(String id : this.localNodes.keySet())
			System.out.print(id+" ");
		
		System.out.println("Transactions stored.....");
		for(MultiSigTransaction tr : this.transactions)
		{
			System.out.println(tr.toString());
		}
		
		System.out.println();
		this.printBlockchain();
	}

	

}
