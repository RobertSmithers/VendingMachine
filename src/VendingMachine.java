import java.util.InputMismatchException;

import java.util.Scanner;

import net.codejava.crypto.CryptoException;
import net.codejava.crypto.CryptoUtils;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Robert Smithers and Devin Roychowdhury
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
	final private String accFile = "accountDetails.txt";
	final private String key = "b2Hs0AkwpVme@duW";			//Used for the AES
	final private File file = new File("accountDetails.txt");
	final private File inputFile = new File(accFile);
	final private File encryptedFile = new File("accountDetails.encrypted");
	final private File decryptedFile = new File("accountDetails.decrypted");
	//final private File decryptedFile = new File("accountDetails.decrypted");
	private Scanner input = new Scanner(System.in);
	private PrintWriter acc;

	/**
	 * Constructor for the VendingMachine class
	 * Initializes an accountDetails.txt file if it doesn't exist with Username and Password columns
	 */
	public VendingMachine()
	{
		try {
			
			//Make accountDetails.txt
			if(!file.exists() && !file.isDirectory()) { 
				acc = new PrintWriter(accFile);			//Creates a printwriter with a blank file, only creating the username and password rows
				acc.printf("%8s%25s\n", "Username", "Password", "");
				acc.close();

				//Encrypts the file after modification
				try {
					CryptoUtils.encrypt(key, file, encryptedFile);
					CryptoUtils.decrypt(key, encryptedFile, decryptedFile);
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
	 * Gives the user a UI and handles all account creation and login attempts
	 * @return whether or not the program should continue based on the validity/permissions of the account entered
	 */
	public boolean intro()
	{
		String decision= "login";
		String username = " ";
		String password = " ";
		System.out.println("Welcome to our Vending Machine program\nVersion number 1.0");
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
				System.out.println("To login, type \"login\". If you would like, you can make an account by typing \"Create\", Alternatively, you may exit by typing exit.");
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
			CryptoUtils.decrypt(key, encryptedFile, decryptedFile);

			accInfo.printf("%-15s%18s\n", newUsername, newPassword, "");
			accInfo.close();

			//Encrypt again after modification
			//CryptoUtils.encrypt(key, inputFile, file);

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
	 * Determines the validity of the account based on its permissions and existence.
	 * @param username The inputted username from the user
	 * @param password The inputted password from the user
	 * @return true if the account is valid, false if the account is invalid
	 */
	public boolean isValidAccount(String username, String password) {
		try
		{
			//Decrypting before pulling data
			try {

				//CryptoUtils.encrypt(key, inputFile, encryptedFile);
				CryptoUtils.decrypt(key, encryptedFile, decryptedFile);
			} catch (CryptoException ex) {
				System.out.println(ex.getMessage());
				ex.printStackTrace();
			}
			BufferedReader reader = new BufferedReader(new FileReader(accFile));
			String line; String[] text;
			while ((line = reader.readLine()) != null && (text = line.split(" ")).length > 18)			//18 is the length of having the username and password strings
			{
				text = line.split(" ");
				System.out.println(text.length);
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
					if (username.equals(goodUser) && password.equals(goodPass)) {
						reader.close();
						try {
							CryptoUtils.encrypt(key, decryptedFile, file);
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
				CryptoUtils.encrypt(key, inputFile, encryptedFile);
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

	/**
	 * Gets what the user wants to do with the vending machine.
	 * @return 1, 2, or 3 based on the user's input. May also be view, modify, or exit
	 */
	public String getChoice() {
		String choice = "0";
		while (!choice.equals("1") && !choice.toLowerCase().equals("view") && !choice.toLowerCase().equals("modify") && !choice.toLowerCase().equals("exit") && !choice.equals("2") && !choice.equals("3")) {
			System.out.println("\nWould you like to 1) view your inventory, 2) modify your inventory, or 3) exit");
			try {
				choice = input.next().toLowerCase();
			} catch (InputMismatchException e){
				System.out.println("I'm sorry, but what you entered was not a number from 1-3");
			}
		}
		return choice;
	}

	/**
	 * Driver for VendingMachine class. Creates a vending machine and goes through the necessary steps to run it.
	 * @param args
	 */
	public static void main(String[] args) {
		boolean terminate = false;
		VendingMachine machine = new VendingMachine();		//Initialized + logon
		while (!terminate) {
			if (machine.intro()) {
				String choice = machine.getChoice();
				if (choice.equals("exit")||choice.equals("3")) terminate=true;
			}
		}
	}
}
