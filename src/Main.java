import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	public static int globalDepth;
	public static int amountOfRecords;
	public static int amountOfBuckets;
	public static int directorySize;
	public static ArrayList<String> directory;
	ArrayList<Integer> list = new ArrayList<Integer>();
	Bucket root;

	Main() {
		directorySize = 1;
		directory = new ArrayList<String>();
		globalDepth = 0;
		amountOfRecords = 0;
		amountOfBuckets = 1;
		root = new Bucket(0, "", null);
	}

	// find a number
	private void findNumber(int number) {
		System.out.println(root.find(number));
	}

	// insert a number
	private void insertNumber(int number, String name) {
		Record newRec = new Record(number, name);
		++amountOfRecords;
		root.insert(newRec);
	}

	// delete a number
	private void deleteNumber(int number) {
		--amountOfRecords;
		root.delete(number, "");
	}

	// print information for the directory
	private void printAll() {
		System.out.println("Number of Buckets: " + amountOfBuckets);
		System.out.println("Number of Records: " + amountOfRecords);
		System.out.println("Size of directory: " + directorySize);
	}

	// print information for a single bucket
	public void PrintBucket(Bucket bucket, String bucketCode) {
		// base case
		if (bucket == null) {
			return;
		}

		if (bucket.left == null && bucket.right == null && bucket.code.equals(bucketCode)) {
			System.out.print("[");
			System.out.print("Bucket: " + bucket.code + ", Local Depth: " + bucket.localDepth);
			System.out.print(" , Records \"");
			for (int i = 0; i < bucket.amountOfRecords; i++) {
				System.out.print(bucket.records[i].id);
				if ((i + 1) > 1 || bucket.records[i + 1] == null)
					System.out.print("\"]");
				else
					System.out.print(",");

			}

		}
		PrintBucket(bucket.left, bucketCode);
		PrintBucket(bucket.right, bucketCode);
	}

	/*
	 * This took FOREVER to code
	 * It prints all the buckets with their records
	 */
	public void PrintStructure(Bucket bucket) {
		// base case
		if (bucket == null) {
			return;
		}

		if (bucket.left == null && bucket.right == null && bucket.amountOfRecords == 0)
			return;
		else if (bucket.left == null && bucket.right == null) {
			System.out.print("[");
			System.out.print("Bucket: " + bucket.code + ", Local Depth: " + bucket.localDepth);
			System.out.print(" , Records \"");
			for (int i = 0; i < bucket.amountOfRecords; i++) {
				System.out.print(bucket.records[i].id);
				if ((i + 1) > 1 || bucket.records[i + 1] == null)
					System.out.print("\"]");
				else
					System.out.print(",");
			}
		}
		PrintStructure(bucket.left);
		PrintStructure(bucket.right);
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Main directory = new Main();

		// wait for user input, then parse and run it
		boolean enterLoop = true;
		while (enterLoop) {
			String scannerinput = sc.nextLine();
			
			// quit the scanner
			if (scannerinput.equals("q")) {
				enterLoop = false;
			} else {

				// see if input is only a number
				try {

					int number = Integer.parseInt(scannerinput);
					directory.findNumber(number);

					// input is not a number. parse the command
				} catch (Exception e) {
					try {
						
						// see if the first word is "info"
						if (scannerinput.substring(0, 4).equals("info")) {
							String command = scannerinput.substring(5);

							// if second word is "all", print info for directory
							if (command.equals("all")) {
								directory.printAll();
								continue;
							} 
							
							// print command
						} else if (scannerinput.substring(0, 5).equals("print")) {
							directory.PrintStructure(directory.root);
							System.out.println("");
							
							// insert command
						} else if (scannerinput.substring(0, 6).equals("insert")) {

							// used to get the right number to insert since a number can be any length
							String text = scannerinput.substring(7);
							int cut = 0;
							int number = 0;
							for (int i = 0; i < text.length(); i++) {
								if (text.charAt(i) == ' ')
									cut = i;
							}
							number = Integer.parseInt(text.substring(0, cut));
							directory.insertNumber(number, scannerinput.substring(9));
							
							// delete command
						} else if (scannerinput.substring(0, 6).equals("delete")) {
							directory.deleteNumber(Integer.parseInt(scannerinput.substring(7)));
							
							// bucket command
						} else if (scannerinput.substring(0, 6).equals("bucket")) {
							directory.PrintBucket(directory.root, scannerinput.substring(7));
							System.out.println("");
						} else {
							System.out.println("***You have entered an incorrect command***");
						}

					} catch (Exception e1) {
						System.out.println("***You have entered an incorrect command***");
					}
				}
			}
		}
	}
}
