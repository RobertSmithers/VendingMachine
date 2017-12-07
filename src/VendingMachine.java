import java.util.InputMismatchException;

import java.util.Scanner;

import net.codejava.crypto.*;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;

/**
 * @author Robert Smithers and Deven Roychowdhury
 * @date 11/16/17
 * @description Creates a detailed collection of information and statistics on a vending
 * machine using the snacks built into the code and the purchases
 * @version 0.1
 *
 */
public class VendingMachine {

	/**
	 * @param args
	 */
	private ArrayList<Snack>snacks;
	private ArrayList<Snack>drinks;
	final private String invFile = "inventory.txt";
	
	final private String key = "b2Hs0AkwpVme@duW";			//Used for the AES
	final private File accFile = new File("accountDetails.txt");
	private static Scanner input = new Scanner(System.in);
	private PrintWriter acc;

	/**
	 * Constructor for the VendingMachine class
	 * Initializes an accountDetails.txt file if it doesn't exist with Username and Password columns
	 */
	public VendingMachine()
	{
		snacks = new ArrayList<Snack>();
		drinks = new ArrayList<Snack>();
		try {
			
			//Make accountDetails.txt if they don't yet have it
			if(!accFile.exists() && !accFile.isDirectory()) { 
				acc = new PrintWriter(accFile);			//Creates a printwriter with a blank file, only creating the username and password rows
				acc.printf("%8s%25s\n", "Username", "Password", "");
				acc.close();

				//Encrypts the file after modification
				try {
					CryptoUtils.encrypt(key, accFile, accFile);
				} catch (CryptoException ex) {
					System.out.println(ex.getMessage());
					ex.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Creates a brand new account and puts the username and password into the accountDetails text file
	 */
	public void createAccount() {
		System.out.println("\nPlease input your new username: ");
		String newUsername = input.next();
		System.out.println("Please input your new password: ");
		String newPassword = input.next();

		try {
			FileWriter account = new FileWriter(accFile, true);		//Second parameter signals appending, not creating
			PrintWriter accInfo = new PrintWriter(account);

			//Decrypt before modification of the file
			CryptoUtils.decrypt(key, accFile, accFile);

			accInfo.printf("%-15s%18s\n", newUsername, newPassword, "");
			accInfo.close();

			//Encrypt again after modification
			CryptoUtils.encrypt(key, accFile, accFile);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CryptoException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * Creates menu using the text file: inventory.txt
	 */
	public void createMenu() {		
		try {
			boolean snacksNow = false;
			BufferedReader reader = new BufferedReader(new FileReader(invFile));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.equals("Drinks"))
					line = reader.readLine();
				while (!snacksNow) {
					String[] params = line.split(",");
					drinks.add(getSnackFromText(params));
					line = reader.readLine();
					if (line.equals("Snacks"))
						snacksNow = true;
				}
				if (line.equals("Snacks")) {
					line = reader.readLine();
					snacksNow = true;
				}
				String[] params = line.split(",");
				if (params.length == 4)
					snacks.add(getSnackFromText(params));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * Gets what the user wants to do with the vending machine.
	 * @return 1, 2, or 3 based on the user's input. May also be view, modify, or exit
	 */
	public String getManagerChoice() {
		String choice = "0";
		while (!choice.equals("1") && !choice.toLowerCase().equals("view") && !choice.toLowerCase().equals("modify") && !choice.toLowerCase().equals("exit") && !choice.equals("2") && !choice.equals("3") && !choice.equals("4") && !choice.toLowerCase().equals("buy")) {
			System.out.println("\nWould you like to 1) view your inventory, 2) modify your inventory, 3) buy something, or 4) exit");
			try {
				choice = input.next().toLowerCase();
			} catch (InputMismatchException e){
				System.out.println("I'm sorry, but what you entered was not a number from 1-4");
			}
		}
		return choice;
	}
	
	/**
	 * Takes in customer's choice for buying a product
	 */
	private boolean getCustomerChoice() {
		String choice = "0";
		boolean isSnack = false;
		
		while (!choice.equals("1") && !choice.equals("snack") && !choice.equals("2") && !choice.equals("drink") && !choice.equals("3")) {
			System.out.println("Hello Valued Customer!\nWould you like to:\n1. Buy a snack\n2. Buy a drink\n3. Exit");
			 choice = input.next();
		}
		if (choice.equals("1") || choice.equals("snack")) {
			isSnack = true;
			System.out.println("Snacks:"); // Prints menu for Snacks
			System.out.printf("%30s%30s%30s\n","Name:","Price:","Inventory:","");
			for (Snack s : snacks)
				System.out.printf("%30s%30s%30s\n",s.getID(),Double.toString(s.getPrice()),Integer.toString(s.getInventory()));
		}
		else if (choice.equals("2") || choice.equals("drink")){
			System.out.println("Drinks:"); // Prints menu for Drinks
			System.out.printf("%30s%30s%30s\n","Name:","Price:","Inventory:","");
			for (Snack s : drinks)
				System.out.printf("%30s%30s%30s\n",s.getID(),Double.toString(s.getPrice()),Integer.toString(s.getInventory()));
		}
		else {
			return true;
		}
		
		getSales(isSnack);
		return false;		
	}
	
	/**
	 * Allows user to buy snack or drink depending on isSnack.
	 * @param isSnack boolean
	 */
	private boolean getSales(boolean isSnack) {
		String choice = "";
		boolean cont = false;
		int itemIndex = -1;
		
		
		while (!cont) {
			int i = 0;
			int j = 0;
			System.out.println("Which item would you like to buy? Enter \"3\" to go back.");
			choice = input.next();
			if (isSnack) {
				for (Snack s : snacks) {
					if (s.getID().toLowerCase().equals((choice.toLowerCase()))) {
						itemIndex = i; //Determines index of item to be used to find item in array
						cont = true;
					}
					i++;
				}
			}
			else {
				for (Snack s : drinks) {
					if (s.getID().toLowerCase().equals((choice.toLowerCase()))) {
						itemIndex = j;
						cont = true;
					}
					j++;
				}
			}
			if (choice.equals("3"))
				return false;
				
			}
		cont = false;
		double moneyIn = 0;
		boolean valid = true;
		while (!cont) {
			System.out.println("How much money would you like to enter? Enter -1 to go back.");
			valid = true;
			try {
				moneyIn = input.nextDouble();
			} catch (InputMismatchException e) { //If they enter a string we can catch it
				System.out.println("ERROR: Must enter a valid numerical value");
				System.out.println("How much money would you like to enter? Enter -1 to go back.");
				moneyIn = input.nextDouble();
				valid = false;
			}
			if (moneyIn == -1) return false;
			else if (isSnack && valid) { //If the item is a snack
				if (moneyIn < snacks.get(itemIndex).getPrice())
					System.out.println("Please enter more money this item costs: " + snacks.get(itemIndex).getPrice() + ".");
				else if (moneyIn == snacks.get(itemIndex).getPrice()) {
					System.out.println("Thank you for your purchase!");
					snacks.get(itemIndex).sell();
					rewriteInventory();
					return true;
				}
				else {
					System.out.println("Thank you for your purchase! You will recieve $" + (moneyIn-snacks.get(itemIndex).getPrice())  + " in change.");
					rewriteInventory();
					return true;
				}
			}
			else if (!isSnack && valid) { // If the item is a drink
				if (moneyIn < drinks.get(itemIndex).getPrice())
					System.out.println("Please enter more money this item costs: " + drinks.get(itemIndex).getPrice() + ".");
				else if (moneyIn == drinks.get(itemIndex).getPrice()) {
					System.out.println("Thank you for your purchase!");
					drinks.get(itemIndex).sell();
					rewriteInventory();
					return true;
				}
				else {
					System.out.println("Thank you for your purchase! You will recieve $" + (moneyIn-drinks.get(itemIndex).getPrice())  + " in change.");
					rewriteInventory();
					return true;
				}
			}
		}
		rewriteInventory();
		return true;
	}

	/**
	 * Used by createMenu() to create Snack.
	 * @param array params to be used to create a Snack object
	 * @return the created Snack object
	 */
	private Snack getSnackFromText(String[] params) {
		for (String s : params)
			s.replace(",", "");
		Snack sn = new Snack(Integer.parseInt(params[3]), Double.parseDouble(params[2]), Double.parseDouble(params[1]), params[0]);
		return sn;
	}
	
	/**
	 * Used at beginning of program to determine if the person interacting with the program is a customer or a administrator.
	 * @return true if the user is a manager and false if the user is a customer.
	 */
	public boolean intro() {
		System.out.println("Welcome to our Vending Machine program\nVersion number 1.0\n");
		
		String decision = "";
		
		while (!decision.equals("1") || !decision.equals("2") || !decision.toLowerCase().equals("user") || !decision.toLowerCase().equals("manager")) {
			System.out.println("Are you a customer or a manager?");
			decision = input.next();
			if (decision.equals("1") || decision.toLowerCase().equals("customer")) {
				customerIntro();
				return false;
			}
			else if (decision.equals("2") || decision.toLowerCase().equals("manager")) {
				managerIntro();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Intro for a customer to interact with.
	 */
	public void customerIntro() {
		
	}

	/**
	 * Gives the user a UI and handles all account creation and login attempts
	 * @return whether or not the program should continue based on the validity/permissions of the account entered
	 */
	public boolean managerIntro()
	{
		String decision= "login";
		String username = " ";
		String password = " ";
		
		while (!decision.toLowerCase().equals("exit") && !decision.toLowerCase().equals("create") && !isValidAccount(username, password))
		{
			if (decision.toLowerCase().equals("login")) {
				System.out.print("Username: ");
				username = input.next();
				System.out.print("Password: ");
				password = input.next();
			}

			if (isValidAccount(username, password)) {
				System.out.println("Password accepted\n");
				System.out.println("Welcome, "+username+".\n");
				return true;
			}
			else {
				if (decision.equals("login")) System.out.println("I'm sorry, that username and password combination does not exist. ");
				if (!decision.equals("exit")) System.out.println("To login, type \"login\". If you would like, you can make an account by typing \"Create\", Alternatively, you may exit by typing exit.");
				decision = input.next();
			}
		}

		if (decision.equals("exit")) return false;
		else if (decision.toLowerCase().equals("create")) {
			createAccount();
			return true;
		}
		else throw new RuntimeException("Sorry, but something went wrong. Oops");

	}
	
	/**
	 * Determines the validity of the account based on its permissions and existence.
	 * @param username The inputted username from the user
	 * @param password The inputted password from the user
	 * @return true if the account is valid, false if the account is invalid
	 */
	private boolean isValidAccount(String username, String password) {
		try
		{
			//Decrypting before pulling data
			try {
				CryptoUtils.decrypt(key, accFile, accFile);
			} catch (CryptoException ex) {
				System.out.println(ex.getMessage());
				ex.printStackTrace();
			}
			
			BufferedReader reader = new BufferedReader(new FileReader(accFile));
			String line; String[] text;
			while ((line = reader.readLine()) != null && (text = line.split(" ")).length > 17)			//18 is the length of having the username and password strings
			{
				text = line.split(" ");
				for (int i=0; i<text.length; i++) {					//1 and 2 is the Username: "Username" and Password: "Password" which would create a security vulnerability to leave unattended
					while (text[i].equals("") && i<text.length) {
						i++;
					}
					String goodUser = text[i];
					try {
						i++;
						if (text[i] == null) i--;
					} catch (ArrayIndexOutOfBoundsException e) {
						continue;
					}
					while (text[i].equals("") && i<text.length) {
						i++;
					}
					String goodPass = text[i];
					if (username.equals(goodUser) && password.equals(goodPass)) {			//Successful matching of username and password
						reader.close();
						//Encrypt again to protect the data while we are not using it
						try {
							CryptoUtils.encrypt(key, accFile, accFile);
							//CryptoUtils.decrypt(key, encryptedFile, decryptedFile);
						} catch (CryptoException ex) {
							System.out.println(ex.getMessage());
							ex.printStackTrace();
						}
						return true;
					}
				}
			}
			reader.close();
			try {
				CryptoUtils.encrypt(key, accFile, accFile);
				//CryptoUtils.decrypt(key, encryptedFile, decryptedFile);
			} catch (CryptoException ex) {
				System.out.println(ex.getMessage());
				ex.printStackTrace();
			}
			return false;
		}
		catch (Exception e)
		{
			System.err.format("Exception occurred trying to read '%s'.", accFile);
			e.printStackTrace();
			return false;
		}
	}
	
	private ArrayList<String> lookupItem(String name) {
		ArrayList<String> matches = new ArrayList<String>();
		boolean searching = true;
		while (searching) {
			System.out.println("Select the correct category:\n1) Snack\n2) Drink\n3) Go back");
			String choice = input.next();
			if (choice.equals("1")) {
				for (Snack s : snacks) {
		
					if (name.equals(s.getID())) {		//Add vals to the arrayList
						matches.add(Double.toString(s.getCost()));
						matches.add(Double.toString(s.getPrice()));
						matches.add(Integer.toString(s.getInventory()));
						System.out.printf("%1s%30s%30s%30s\n",s.getID(),Double.toString(s.getCost()),Double.toString(s.getPrice()),Integer.toString(s.getInventory()));
						searching=false;
					}
				}
			}
			else if (choice.equals("2")) {
				for (Snack s : drinks) {
					if (name.equals(s.getID())) {		//Add vals to the arrayList
						matches.add(Double.toString(s.getCost()));
						matches.add(Double.toString(s.getPrice()));
						matches.add(Integer.toString(s.getInventory()));
						searching=false;
					}
				}
			}
			else if (choice.equals("3")) return null;
			else System.out.println("Please enter 1, 2, or 3");
		}
		return matches;
	}
	
	/**
	 * User interface for changing inventory, the actual changes are made in changeInventory().
	 */
	private void modifyInventory() {
		System.out.println("What would you like to change? You may:\n1) Update Product Name\n2) Update Product Quantity\n3) Update Product Cost\n4) Update Product Sale Price\n5) Go back");
		String choice = input.next();
		String choice2 = "5";
		boolean choose = true;
		ArrayList<String> a = new ArrayList<String>();
		while (choose) {
			
			if (choice.equals("1") || choice.equals("2") || choice.equals("3")) {
				System.out.println("Which item would you like to change (enter exact name and spelling of item)");
				choice2 = input.next();
				 a = lookupItem(choice2);
				
			}
			if (choice.equals("1")) {
				//Look up item in chart
				if (a != null) {			//Returns an array with the name, cost, price, and stock #
					System.out.println("Please enter the new name of the item");
					String choice3 = input.next();
                    changeInventory(choice2, choice3, -1, -1, -1);
					//Change the item with the new name
					System.out.println();
				}
				
				choose = false;
			}
			else if (choice.equals("2")) {
				if (a != null) {			//Returns an array with the name, cost, price, and stock #
					System.out.println("Please enter the new cost");
					Double choice3 = input.nextDouble();
					changeInventory(choice2, null, -1, -1, choice3);
					//Change the cost of the item
					System.out.println();
					
				}
				choose = false;
			}
			else if (choice.equals("3")) {
				if (a != null) {			//Returns an array with the name, cost, price, and stock #
					System.out.println("Please enter the new sale price");
					Double choice3 = input.nextDouble();
					changeInventory(choice2, null, -1, choice3, -1);
					System.out.println();
				}
				choose = false;
			}
			else if (choice.equals("4")) {
				if (a != null) {			//Returns an array with the name, cost, price, and stock #
					System.out.println("Please enter the new inventory quantity");
					int choice3 = input.nextInt();
					changeInventory(choice2, null, choice3, -1, -1);
					//Change the quantity
					System.out.println();
				}
				choose = false;
			}
			else if (choice.equals("5")) {		//Exit
				choose = false;
			}
			else System.out.println("Please enter a valid number from 1 to 5.");
		}
	}
	
	/**
	 * Changes the inventory for the given snack and if a parameter is -1 or null then it will keep it the same.
	 */
	private void changeInventory(String originalName, String newName, int inv, double price, double cost) {
		Snack s = new Snack(-1, -1, -1, "");
		boolean isSnack = false; 
		
		for (Snack snack : snacks) //Checks if the item is a snack
			if (snack.getID().equals(originalName)) {
				s = snack;
				isSnack = true;
			}
		
		for (Snack snack : drinks)//Checks if the item is a drink
			if (snack.getID().equals(originalName))
				s = snack;
		
		if (newName != null) { // Changes name
			Snack newSnack = new Snack(s.getInventory(), s.getPrice(), s.getCost(), newName);
			
			if (isSnack) {
				snacks.remove(s);
				snacks.add(newSnack);
			}
			else {
				drinks.remove(s);
				snacks.add(newSnack);
			}
			
			System.out.printf("%1s%30s%30s%30s\n",newSnack.getID(),Double.toString(newSnack.getCost()),Double.toString(newSnack.getPrice()),Integer.toString(newSnack.getInventory()));
		}
		
		if (inv != -1) {
			s.setInventory(inv);
			System.out.printf("%1s%30s%30s%30s\n",s.getID(),Double.toString(s.getCost()),Double.toString(s.getPrice()),Integer.toString(s.getInventory()));
		}
		
		if (price != -1) {
			s.setPrice(price);
			System.out.printf("%1s%30s%30s%30s\n",s.getID(),Double.toString(s.getCost()),Double.toString(s.getPrice()),Integer.toString(s.getInventory()));
		}
		
		if (cost != -1) {
			s.setCost(cost);
			System.out.printf("%1s%30s%30s%30s\n",s.getID(),Double.toString(s.getCost()),Double.toString(s.getPrice()),Integer.toString(s.getInventory()));
		}
		
		rewriteInventory();
			
	}
	
	/**
	 * Prints a formatted menu using the stored values for the snacks and drinks.
	 */
	public void printMenu() {
		System.out.println("Drinks:");
		System.out.printf("%30s%30s%30s%30s\n","Name:","Cost:","Price:","Inventory:","");
		for (Snack s : drinks)
			System.out.printf("%30s%30s%30s%30s\n",s.getID(),Double.toString(s.getCost()),Double.toString(s.getPrice()),Integer.toString(s.getInventory()));
		System.out.println("Snacks:");
		System.out.printf("%30s%30s%30s%30s\n","Name:","Cost:","Price:","Inventory:","");
		for (Snack s : snacks)
			System.out.printf("%30s%30s%30s%30s\n",s.getID(),Double.toString(s.getCost()),Double.toString(s.getPrice()),Integer.toString(s.getInventory()));
	}
	
	private void rewriteInventory() {
		try {
			FileWriter write = new FileWriter("inventory.txt");
			write.write("Drinks\n");
			for (Snack d: drinks) {
				write.write(d.getID()+","+d.getCost()+","+Double.toString(d.getPrice())+","+Integer.toString(d.getInventory())+"\n");
			}
				
			write.write("Snacks\n");
			for (Snack s: snacks) {
				write.write(s.getID()+","+s.getCost()+","+Double.toString(s.getPrice())+","+Integer.toString(s.getInventory())+"\n");
			}
			write.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Driver for VendingMachine class. Creates a vending machine and goes through the necessary steps to run it.
	 * @param args
	 */
	public static void main(String[] args) {
		boolean terminate = false;
		VendingMachine machine = new VendingMachine();		//Initializer + login
		if (machine.intro()) {
			while (!terminate) {
				String choice = machine.getManagerChoice();
				machine.createMenu();
				if (choice.equals("exit")||choice.equals("4")) terminate=true;
				else if (choice.equals("view") || choice.equals("1")) {
					machine.printMenu();
				}
				else if (choice.equals("modify") || choice.equals("2")) {
					machine.printMenu();
					machine.modifyInventory();
				}
				else if (choice.equals("buy") || choice.equals("3")) {
					machine.printMenu();
					machine.modifyInventory();
				}
			}
		}
		else {
			while (!terminate) {
				machine.createMenu();
				machine.printMenu();
				terminate = machine.getCustomerChoice();
				
				
			}
			
		}
	}
}
