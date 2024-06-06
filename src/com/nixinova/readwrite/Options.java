package com.nixinova.readwrite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Options {
	public static final String MINEO_FOLDER = ".mineo";
	public static final String OPTIONS_FILE = "options.txt";
	public static final int OPTIONS_VERSION = 4;
	
	public class DEFAULT_OPTIONS {
		public static int renderDistance = 5000;
		public static int skyHeight = 144;
		public static int groundHeight = 16;
		public static double gravity = 0.04;
		public static double rotationSpeed = 0.03;
		public static double moveSpeed = 1.5;
		public static double walkSpeed = 0.5;
		public static double sprintSpeed = 3.0;
		public static double jumpHeight = 0.5;
	}

	public static int fileVersion = OPTIONS_VERSION;
	public static int renderDistance = DEFAULT_OPTIONS.renderDistance;
	public static int skyHeight = DEFAULT_OPTIONS.skyHeight;
	public static int groundHeight = DEFAULT_OPTIONS.groundHeight;
	public static double gravity = DEFAULT_OPTIONS.gravity;
	public static double rotationSpeed = DEFAULT_OPTIONS.rotationSpeed;
	public static double moveSpeed = DEFAULT_OPTIONS.moveSpeed;
	public static double sprintSpeed = DEFAULT_OPTIONS.sprintSpeed;
	public static double walkSpeed = DEFAULT_OPTIONS.walkSpeed;
	public static double jumpHeight = DEFAULT_OPTIONS.jumpHeight;

	public static String texturesFolder = "/textures/";

	public static String WriteValue(String id, String value) {
		String br = System.getProperty("line.separator");

		return String.valueOf(id) + ":" + br + value + br;
	}
	public static String WriteValue(String id, double value) {
		return WriteValue(id, String.valueOf(value));
	}

	public static void CreateOptions() {
		boolean fileIsNew = false;

		String rootFolder = String.valueOf(System.getenv("APPDATA")) + "/" + MINEO_FOLDER;
		String optionsFilePath = String.valueOf(rootFolder) + "/" + OPTIONS_FILE;

		File dir = new File(rootFolder);
		dir.mkdir();

		File optionsFile = new File(optionsFilePath);

		try {
			if (optionsFile.createNewFile()) {
				System.out.println("options.txt created: " + optionsFile.getName());
				fileIsNew = true;
			} else {
				System.out.println("options.txt already exists.");
			}
			System.out.println("options.txt path: " + optionsFile.getAbsolutePath());
		} catch (Exception err) {
			err.printStackTrace();
		}

		if (fileIsNew) {
			try {
				FileWriter writer = new FileWriter(optionsFilePath);

				writer.write(WriteValue("fileVersion", OPTIONS_VERSION));

				writer.write(WriteValue("renderDistance", DEFAULT_OPTIONS.renderDistance));
				writer.write(WriteValue("skyHeight", DEFAULT_OPTIONS.skyHeight));
				writer.write(WriteValue("groundHeight", DEFAULT_OPTIONS.groundHeight));
				writer.write(WriteValue("gravity", DEFAULT_OPTIONS.gravity));
				writer.write(WriteValue("rotationSpeed", DEFAULT_OPTIONS.rotationSpeed));
				writer.write(WriteValue("moveSpeed", DEFAULT_OPTIONS.moveSpeed));
				writer.write(WriteValue("walkSpeed", DEFAULT_OPTIONS.walkSpeed));
				writer.write(WriteValue("sprintSpeed", DEFAULT_OPTIONS.sprintSpeed));
				writer.write(WriteValue("jumpHeight", DEFAULT_OPTIONS.jumpHeight));

				writer.close();

				System.out.println("Successfully wrote to the file.");
			} catch (IOException err) {
				System.out.println("An error occurred.");
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
			System.out.println("An error occurred.");
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
			if (optionsDataNames[i] != null && optionsDataNames[i].contains("gravity")) {
				gravity = Double.parseDouble(optionsDataValues[i]);
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
