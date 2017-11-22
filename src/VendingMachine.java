import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;

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
	//private File acc;
	private Scanner accReader;
	private Scanner input = new Scanner(System.in);
	private PrintWriter acc;
	
	public VendingMachine()
	{
		File file = new File("accountDetails.txt");
		
		try {
			//accReader = new Scanner(acc);
			//FileWriter accWriter = new FileWriter(accFile);
			//PrintWriter print_line = new PrintWriter(accWriter);
			//acc2.printf("%s"+"%n", "Testing write to a file");
			if(!file.exists() && !file.isDirectory()) { 
				acc = new PrintWriter(accFile);
				acc.printf("%8s%15s\n", "Username", "Password", "");
				acc.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public boolean intro()
	{
		String decision= "";
		String username = "";
		String password = "";
		System.out.println("Welcome to the our Vending Machine program\nVersion number "+null);
		while (!decision.toLowerCase().equals("exit") && !decision.toLowerCase().equals("create") && !isValidAccount(username, password))
		{
			System.out.print("Username: ");
			username = input.next();
			System.out.print("Password: ");
			password = input.next();

			if (isValidAccount(username, password)) {
				System.out.println("Password accepted\n");
				return true;
			}
			else {
				System.out.println("I'm sorry, that username and password combination does not exist. ");
				System.out.println("You can make an account by typing \"Create\", Alternatively, you may exit by typing exit. If you do not type create or exit, you will be prompted to log in again.");
				decision = input.next();
			}
		}
		if (decision.equals("exit")) return false;
		else if (decision.equals("create")) {
			createAccount();
			return true;
		}
		else throw new RuntimeException("Sorry, but something went wrong. Oops");
		
	}
	
	public void createAccount() {
		System.out.println("\nPlease input your new username: ");
		String newUsername = input.next();
		System.out.println("Please input your new password: ");
		String newPassword = input.next();
		acc.printf("%10s%15s", newUsername, newPassword);
		acc.close();
	}
	
	public boolean isValidAccount(String username, String password) {
		return false;
	}
	
	public int getChoice() {
		System.out.println("Would you like to 1) view your inventory, 2) modify your inventory, or 3) exit");
		int choice = input.nextInt();
		return choice;
	}
	
	public static void main(String[] args) {
		VendingMachine first = new VendingMachine();		//Initialized + logon
		if (first.intro()) {
			first.getChoice();
		}
	}
	
}
