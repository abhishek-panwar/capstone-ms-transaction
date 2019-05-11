package project.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import project.local.LocalNode;
import project.local.MultiSigTransaction;
import project.local.OverlayBlock;
import project.local.Transaction;
import project.overlay.OverlayNode;

public class Project {

	//private static final boolean String = false;
	private HashMap<OverlayNode, ArrayList<OverlayNode>> map = new HashMap<OverlayNode, ArrayList<OverlayNode>>();
	private HashSet<OverlayNode> overlayNodes = new HashSet<OverlayNode>();
	
	
		
	public static void main(String[] args) {
		
		Project obj = new Project();
		obj.init();
				
		
		
		
		Scanner scan = new Scanner(System.in);
		while(true)
		{
			System.out.println("Enter a command {Acess (OBM), add, print, reputation, find}");
			
			String input = scan.next();
			
			if(input.equals("access"))
			{
				System.out.println("Enter OBM ID: ");
				input = scan.next();
				if(obj.checkID(input))
					obj.getNode(input).access();
				
				System.out.println("back in home");
			}
			else if(input.equals("add"))
			{
				System.out.println("Enter new OBM ID: ");
				input = scan.next();
				if(!obj.map.containsKey(input))
				{
					System.out.println("which nodes to connect (write all nodes space separated): ");
					scan = new Scanner(System.in);
					String connections = scan.nextLine();
					
					String[] neighbours = connections.split(" ");
					for(String neighbour : neighbours)
					{
						if(!obj.map.containsKey(obj.getNode(neighbour.trim())))
						{
							System.out.println("incorrect input");
						}
					}
					obj.addNode(input);
					
					for(String neighbour : neighbours) 
					{ 
						obj.addEdge(input, neighbour); 
					}
					System.out.println("overlay node "+input+" added");
				}
			}
			else if(input.equalsIgnoreCase("print"))
			{
				obj.printGraph();
			}
			else if(input.equalsIgnoreCase("reputation"))
			{
				obj.findReputation();
			}
			else if(input.equalsIgnoreCase("find"))
			{
				System.out.println("node ID: ");
				String id = scan.next();
				LocalNode node = obj.findLocalNodeWithID(id);
				if(node!=null) 
					node.access();
				else
				{
					OverlayNode n = obj.findOverlayNodeWithID(id);
					if(n!=null)
						n.access();
				}
			}
			else if(input.equalsIgnoreCase("exit"))
			{
				System.out.println("program stopped");
				return;
			}
			
		}
		
	}
	
	/**
	 * finds reputation of a node
	 */
	private void findReputation() {
		// TODO Auto-generated method stub
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter node ID: ");
		String id = scan.next();
		OverlayNode node = this.findOverlayNodeWithID(id);
		if(node!=null)
			System.out.println("Reputation: "+node.getReputation()+" %      [S: "+node.getSuccessCount()+"  F: "+node.getFailureCount()+" ]");
		else
		{
			LocalNode n = this.findLocalNodeWithID(id);
			if(n!=null)
				System.out.println("Reputation: "+n.getReputation()+" %      [S: "+n.getSuccessCount()+"  F: "+n.getFailureCount()+" ]");
			else
				System.out.println("Invalid node for reputation");
		}
		
		
	}
	/**
	 * redirects control to local node using node id
	 * @param id
	 * @return
	 */
	public LocalNode findLocalNodeWithID(String id) 
	{
		
		for(OverlayNode n : this.map.keySet())
		{
			if(n.getLocalNodes().containsKey(id))
				return n.getLocalNodes().get(id);
		}
		return null;
	}

	/**
	 * redirects control to local node using device id
	 * @param id
	 * @return
	 */
	public LocalNode findLocalNodeWithDeviceID(String id)
	{
		for(OverlayNode n : this.map.keySet())
		{
			for (String ln : n.getLocalNodes().keySet())
			{
				if(n.getLocalNodes().get(ln).getDevicesRegistered().contains(id))
				{
					return n.getLocalNodes().get(ln);
				}
			}
		}
		return null;
	}
	
	/**
	 * redirects control to overlay node using node id
	 * @param id
	 * @return
	 */
	public OverlayNode findOverlayNodeWithID(String id)
	{
		for(OverlayNode n : this.map.keySet())
		{
			if(n.getId().equals(id))
				return n;
		}
		return null;
	}
	
	/**
	 * redirects control to overlay node using device id
	 * @param id
	 * @return
	 */
	public OverlayNode findOverlayNodeWithDeviceID(String id)
	{
		for(OverlayNode n : this.map.keySet())
		{
			for (String ln : n.getLocalNodes().keySet())
			{
				if(n.getLocalNodes().get(ln).getDevicesRegistered().contains(id))
				{
					return n;
				}
			}
		}
		return null;
	}
	
	
	private boolean checkID(String id)
	{
		for(OverlayNode n : this.map.keySet())
		{
			if(n.getId().equals(id))
				return true;
		}
		return false;
	}
	
	/*
	 * public void takeCareOfThisBlock(OverlayBlock block, OverlayNode obm) {
	 * this.broadcastGlobally(block, obm); }
	 */
	public void broadcastGlobally(MultiSigTransaction transaction, OverlayNode obm)
	{
		for (OverlayNode node: this.overlayNodes) 
		{
			if(node!=obm)
				node.verifyAndAddTransaction(transaction);
		}
	}
	
	public void broadcastThisTransaction(MultiSigTransaction transaction, OverlayNode obm)
	{
		this.broadcastGlobally(transaction, obm);
	}
	
	
	public boolean takeCareOfThisTransaction(MultiSigTransaction transaction)
	{
		return this.broadcastGlobally(transaction);
	}
	
	public boolean broadcastGlobally(MultiSigTransaction transaction)
	{
		boolean verified=false;

		for (OverlayNode node: this.overlayNodes) 
		{
			verified |= node.broadcastLocally(transaction);
		}
 
		return verified;
	}
	
	private OverlayNode getNode(String id)
	{
		for(OverlayNode n : this.map.keySet())
		{
			if(n.getId().equals(id))
				return n;
		}
		return null;
	}
	
	public void addNode(String id)
	{
		if(!this.checkID(id)) 
		{
			OverlayNode newNode = new OverlayNode(id, this);
			this.map.put(newNode, new ArrayList<OverlayNode>());
			this.overlayNodes.add(newNode);
		}
	}
	
	public void addEdge(String id1, String id2)
	{
		if(this.checkID(id1) && this.checkID(id2))
		{
			this.map.get(this.getNode(id1)).add(this.getNode(id2));
			this.map.get(this.getNode(id2)).add(this.getNode(id1));
		}
	}
	
	public void printGraph()
	{

		for (OverlayNode key : this.map.keySet()) 
		{
			System.out.print(key.getId()+ " :  ");
			for (OverlayNode value : this.map.get(this.getNode(key.getId()))) 
			{
				System.out.print(value.getId() + " ");
			}
			System.out.println();
		}		 
		
		/*
		 * System.out.println("set   :  "); for(OverlayNode n : this.overlayNodes) {
		 * System.out.print(n.id+"  "); }
		 * 
		 */		

	}
	
	/**
	 * initial topology of the network
	 */
	public void init()
	{
		this.addNode("1");
		this.addNode("2");
		this.addNode("3");
		this.addNode("4");
		
		this.addEdge("1", "2");
		this.addEdge("1", "3");
		this.addEdge("1", "4");
		this.addEdge("2", "4");
		this.addEdge("3", "4");
		
		this.getNode("1").add("11");
		this.getNode("1").add("12");
		this.getNode("1").add("13");
		
		this.getNode("2").add("21");
		this.getNode("2").add("22");
		this.getNode("2").add("23");
		
		this.getNode("3").add("31");
		this.getNode("3").add("32");
		this.getNode("3").add("33");
		
		this.getNode("4").add("41");
		this.getNode("4").add("42");
		this.getNode("4").add("43");
		
		
		this.getNode("1").getNode("11").addPolicy("sensor_123", "access", "smart_bulb_332", "allow");
		this.getNode("1").getNode("11").addPolicy("smart_bulb_332", "access", "local_data_store", "allow");
		
		this.getNode("1").getNode("11").addDevice("sensor_123");
		this.getNode("1").getNode("11").addDevice("smart_bulb_332");
		this.getNode("1").getNode("11").addDevice("local_data_store");
		
		this.getNode("1").getNode("12").addPolicy("camera_212", "access", "thermostat_112", "allow");
		this.getNode("1").getNode("12").addPolicy("camera_212", "access", "smart_tv", "allow");
		
		this.getNode("1").getNode("12").addDevice("camera_212");
		this.getNode("1").getNode("12").addDevice("thermostat_112");
		this.getNode("1").getNode("12").addDevice("smart_tv");
		
		this.getNode("2").getNode("21").addPolicy("21_device_1", "access", "21_device_2", "allow");
		this.getNode("2").getNode("21").addPolicy("21_device_2", "access", "21_device_3", "allow");
		
		this.getNode("2").getNode("21").addDevice("21_device_1");
		this.getNode("2").getNode("21").addDevice("21_device_2");
		this.getNode("2").getNode("21").addDevice("21_device_3");
		
		
		this.getNode("3").getNode("31").addPolicy("31_device_1", "access", "31_device_2", "allow");
		this.getNode("3").getNode("31").addPolicy("31_device_2", "access", "31_device_3", "allow");
		
		this.getNode("3").getNode("31").addDevice("31_device_1");
		this.getNode("3").getNode("31").addDevice("31_device_2");
		this.getNode("3").getNode("31").addDevice("31_device_3");
		
		
		this.getNode("4").getNode("41").addPolicy("41_device_1", "access", "41_device_2", "allow");
		this.getNode("4").getNode("41").addPolicy("41_device_2", "access", "41_device_3", "allow");
		
		this.getNode("4").getNode("41").addDevice("41_device_1");
		this.getNode("4").getNode("41").addDevice("41_device_2");
		this.getNode("4").getNode("41").addDevice("41_device_3");

	}

	

	
	
}
