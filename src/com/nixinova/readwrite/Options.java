package com.nixinova.readwrite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Options {
	public static int fileVersion;

	public static int renderDistance;
	public static int skyHeight;
	public static int groundHeight;
	public static double rotationSpeed;
	public static double moveSpeed;
	public static double sprintSpeed;
	public static double walkSpeed;
	public static double jumpHeight;

	public static String texturesFolder = "/textures/";

	public static String WriteValue(String id, String value) {
		String br = System.getProperty("line.separator");

		return String.valueOf(id) + ":" + br + value + br;
	}

	public static void CreateOptions() {
		boolean fileIsNew = false;

		String rootFolder = String.valueOf(System.getenv("APPDATA")) + "/.mineo";
		String optionsFilePath = String.valueOf(rootFolder) + "/options.txt";

		File dir = new File(rootFolder);
		dir.mkdir();

		File optionsFile = new File(optionsFilePath);

		try {
			if (optionsFile.createNewFile()) {
				System.out.println("(Options:42) options.txt created: " + optionsFile.getName());
				fileIsNew = true;
			} else {
				System.out.println("(Options:45) options.txt already exists.");
			}
			System.out.println("(Options:47) options.txt path: " + optionsFile.getAbsolutePath());
		} catch (Exception err) {
			err.printStackTrace();
		}

		if (fileIsNew) {
			try {
				FileWriter writer = new FileWriter(optionsFilePath);

				writer.write(WriteValue("fileVersion", String.valueOf(4)));

				writer.write(WriteValue("renderDistance", "5000"));
				writer.write(WriteValue("skyHeight", "144"));
				writer.write(WriteValue("groundHeight", "16"));
				writer.write(WriteValue("rotationSpeed", "0.03"));
				writer.write(WriteValue("moveSpeed", "1.5"));
				writer.write(WriteValue("walkSpeed", "0.5"));
				writer.write(WriteValue("sprintSpeed", "3.0"));
				writer.write(WriteValue("jumpHeight", "0.25"));

				writer.close();

				System.out.println("(Options:69) Successfully wrote to the file.");
			} catch (IOException err) {
				System.out.println("(Options:71) An error occurred.");
				err.printStackTrace();
			}
		}
		int optionsDataCount = 20;
		String[] optionsDataNames = new String[optionsDataCount];
		String[] optionsDataValues = new String[optionsDataCount];

		try {
			Scanner scanner = new Scanner(optionsFile);
			int j = 0;

			while (scanner.hasNextLine()) {
				String dataName = scanner.nextLine();
				optionsDataNames[j] = dataName;
				String dataValue = scanner.nextLine();
				optionsDataValues[j] = dataValue;

				j += 2;
			}

			scanner.close();
		} catch (FileNotFoundException err) {
			System.out.println("(Options:94) An error occurred.");
			err.printStackTrace();
		}

		for (int i = 0; i < optionsDataNames.length; i++) {
			if (optionsDataNames[i] != null && optionsDataNames[i].contains("fileVersion")) {
				fileVersion = Integer.parseInt(optionsDataValues[i]);
			}
			if (optionsDataNames[i] != null && optionsDataNames[i].contains("renderDistance")) {
				renderDistance = Integer.parseInt(optionsDataValues[i]);
			}
			if (optionsDataNames[i] != null && optionsDataNames[i].contains("skyHeight")) {
				skyHeight = Integer.parseInt(optionsDataValues[i]);
			}
			if (optionsDataNames[i] != null && optionsDataNames[i].contains("groundHeight")) {
				groundHeight = Integer.parseInt(optionsDataValues[i]);
			}
			if (optionsDataNames[i] != null && optionsDataNames[i].contains("rotationSpeed")) {
				rotationSpeed = Double.parseDouble(optionsDataValues[i]);
			}
			if (optionsDataNames[i] != null && optionsDataNames[i].contains("moveSpeed")) {
				moveSpeed = Double.parseDouble(optionsDataValues[i]);
			}
			if (optionsDataNames[i] != null && optionsDataNames[i].contains("walkSpeed")) {
				walkSpeed = Double.parseDouble(optionsDataValues[i]);
			}
			if (optionsDataNames[i] != null && optionsDataNames[i].contains("sprintSpeed")) {
				sprintSpeed = Double.parseDouble(optionsDataValues[i]);
			}
			if (optionsDataNames[i] != null && optionsDataNames[i].contains("jumpHeight")) {
				jumpHeight = Double.parseDouble(optionsDataValues[i]);
			}
			if (optionsDataNames[i] != null && optionsDataNames[i].contains("texturesFolder")) {
				if (optionsDataValues[i].contains("default")) {
					texturesFolder = "/textures/";
				} else {
					texturesFolder = String.valueOf(optionsDataValues[i]) + "/textures/";
				}
			}
		}
	}
}
