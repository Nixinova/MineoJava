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
		public static double gamma = 4.0;
		public static int skyHeight = 144;
		public static int groundHeight = 16;
		public static double gravity = 0.08;
		public static double rotationSpeed = 0.011;
		public static double walkSpeed = 0.6;
		public static double sprintSpeed = 0.8;
		public static double jumpHeight = 5.0;
		public static double jumpStrength = 0.2;
	}

	public static int fileVersion = OPTIONS_VERSION;
	public static int renderDistance = DEFAULT_OPTIONS.renderDistance;
	public static double gamma = DEFAULT_OPTIONS.gamma;
	public static int skyHeight = DEFAULT_OPTIONS.skyHeight;
	public static int groundHeight = DEFAULT_OPTIONS.groundHeight;
	public static double gravity = DEFAULT_OPTIONS.gravity;
	public static double rotationSpeed = DEFAULT_OPTIONS.rotationSpeed;
	public static double sprintSpeed = DEFAULT_OPTIONS.sprintSpeed;
	public static double walkSpeed = DEFAULT_OPTIONS.walkSpeed;
	public static double jumpHeight = DEFAULT_OPTIONS.jumpHeight;
	public static double jumpStrength = DEFAULT_OPTIONS.jumpStrength;

	public static String texturesFolder = "/textures/";

	public static String writeValue(String id, String value) {
		String br = System.getProperty("line.separator");

		return String.valueOf(id) + ":" + br + value + br;
	}

	public static String writeValue(String id, double value) {
		return writeValue(id, String.valueOf(value));
	}

	public static void createOptions() {
		boolean fileIsNew = false;

		String rootFolder = String.valueOf(System.getenv("APPDATA")) + "/" + MINEO_FOLDER;
		String optionsFilePath = String.valueOf(rootFolder) + "/" + OPTIONS_FILE;

		File dir = new File(rootFolder);
		dir.mkdir();

		File optionsFile = new File(optionsFilePath);

		if (fileIsNew) {
			try {
				FileWriter writer = new FileWriter(optionsFilePath);

				writer.write(writeValue("fileVersion", OPTIONS_VERSION));

				writer.write(writeValue("renderDistance", DEFAULT_OPTIONS.renderDistance));
				writer.write(writeValue("gamma", DEFAULT_OPTIONS.gamma));
				writer.write(writeValue("skyHeight", DEFAULT_OPTIONS.skyHeight));
				writer.write(writeValue("groundHeight", DEFAULT_OPTIONS.groundHeight));
				writer.write(writeValue("gravity", DEFAULT_OPTIONS.gravity));
				writer.write(writeValue("rotationSpeed", DEFAULT_OPTIONS.rotationSpeed));
				writer.write(writeValue("walkSpeed", DEFAULT_OPTIONS.walkSpeed));
				writer.write(writeValue("sprintSpeed", DEFAULT_OPTIONS.sprintSpeed));
				writer.write(writeValue("jumpHeight", DEFAULT_OPTIONS.jumpHeight));
				writer.write(writeValue("jumpStrength", DEFAULT_OPTIONS.jumpStrength));

				writer.close();
			} catch (IOException err) {
				System.err.println("Could not create options file.");
			}
		}
		try {
			Scanner scanner = new Scanner(optionsFile);

			// Load options file values
			while (scanner.hasNextLine()) {
				String dataName = scanner.nextLine();

				// Exit if EOF
				if (!scanner.hasNext())
					break;
				// Skip if not a key
				if (!dataName.contains(":"))
					continue;
				
				String dataValue = scanner.nextLine();

				parseOptionStrs(dataName, dataValue);
			}

			scanner.close();
		} catch (FileNotFoundException err) {
			System.err.println("Options file not found.");
			err.printStackTrace();
		}
	}

	private static void parseOptionStrs(String name, String val) {
		if (name == null)
			return;

		if (name.contains("fileVersion"))
			fileVersion = Integer.parseInt(val);
		if (name.contains("renderDistance"))
			renderDistance = Integer.parseInt(val);
		if (name.contains("gamma"))
			gamma = Double.parseDouble(val);
		if (name.contains("skyHeight"))
			skyHeight = Integer.parseInt(val);
		if (name.contains("groundHeight"))
			groundHeight = Integer.parseInt(val);
		if (name.contains("gravity"))
			gravity = Double.parseDouble(val);
		if (name.contains("rotationSpeed"))
			rotationSpeed = Double.parseDouble(val);
		if (name.contains("walkSpeed"))
			walkSpeed = Double.parseDouble(val);
		if (name.contains("sprintSpeed"))
			sprintSpeed = Double.parseDouble(val);
		if (name.contains("jumpHeight"))
			jumpHeight = Double.parseDouble(val);
		if (name.contains("jumpStrength"))
			jumpStrength = Double.parseDouble(val);
		if (name.contains("texturesFolder"))
			texturesFolder = (val.contains("default") ? "" : String.valueOf(val)) + "/textures/";
	}
}
