package com.nixinova.options;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Options {
	public static final String MINEO_FOLDER = ".mineo";
	public static final String OPTIONS_FILE = "options.txt";
	/**
	 * Semantics of version incrementing:
	 * - +1.00: Major: Fundamental file format changes that make older options.txt files unreadable.
	 * - +0.10: Minor: Changes to the implementation of a value.
	 * - +0.01: Micro: Addition of new options, keeps backward compatibility.
	 */
	public static final float OPTIONS_VERSION = 1.20F;

	private static final class DEFAULT_OPTIONS {
		public static long seed = 100L;
		public static short worldSize = 40;
		public static byte groundLevel = 10;
		public static byte buildHeight = 18;
		public static short renderDistance = 20;
		public static float gamma = 1.0f;
		public static float gravity = 0.2f;
		public static float sensitivity = 0.005f;
		public static float walkSpeed = 0.5f;
		public static float sprintSpeed = 0.8f;
		public static float jumpHeight = 1.5f;
	}

	private static final class OPTION_STRINGS {
		public static String fileVersion = "fileVersion";
		public static String seed = "seed";
		public static String worldSize = "worldSize";
		public static String groundLevel = "groundLevel";
		public static String buildHeight = "buildHeight";
		public static String renderDistance = "renderDistance";
		public static String gamma = "gamma";
		public static String gravity = "gravity";
		public static String sensitivity = "sensitivity";
		public static String walkSpeed = "walkSpeed";
		public static String sprintSpeed = "sprintSpeed";
		public static String jumpHeight = "jumpHeight";
	}

	public static float fileVersion = OPTIONS_VERSION;
	public static long seed = DEFAULT_OPTIONS.seed;
	public static short worldSize = DEFAULT_OPTIONS.worldSize;
	public static byte groundLevel = DEFAULT_OPTIONS.groundLevel;
	public static byte buildHeight = DEFAULT_OPTIONS.buildHeight;
	public static short renderDistance = DEFAULT_OPTIONS.renderDistance;
	public static float gamma = DEFAULT_OPTIONS.gamma;
	public static float gravity = DEFAULT_OPTIONS.gravity;
	public static float sensitivity = DEFAULT_OPTIONS.sensitivity;
	public static float sprintSpeed = DEFAULT_OPTIONS.sprintSpeed;
	public static float walkSpeed = DEFAULT_OPTIONS.walkSpeed;
	public static float jumpHeight = DEFAULT_OPTIONS.jumpHeight;

	public static String writeValue(String id, String value) {
		String br = System.getProperty("line.separator");
		return String.valueOf(id) + "=" + value + br;
	}

	public static String writeValue(String id, Object value) {
		return writeValue(id, String.valueOf(value));
	}

	public static void createOptions() {
		String rootFolder = String.valueOf(System.getenv("APPDATA")) + "/" + MINEO_FOLDER;
		String optionsFilePath = String.valueOf(rootFolder) + "/" + OPTIONS_FILE;

		File dir = new File(rootFolder);
		dir.mkdir();

		File optionsFile = new File(optionsFilePath);

		if (!optionsFile.exists()) {
			try {
				FileWriter writer = new FileWriter(optionsFilePath);

				writer.write(writeValue(OPTION_STRINGS.fileVersion, OPTIONS_VERSION));
				writer.write(writeValue(OPTION_STRINGS.seed, DEFAULT_OPTIONS.seed));
				writer.write(writeValue(OPTION_STRINGS.worldSize, DEFAULT_OPTIONS.worldSize));
				writer.write(writeValue(OPTION_STRINGS.groundLevel, DEFAULT_OPTIONS.groundLevel));
				writer.write(writeValue(OPTION_STRINGS.buildHeight, DEFAULT_OPTIONS.buildHeight));
				writer.write(writeValue(OPTION_STRINGS.renderDistance, DEFAULT_OPTIONS.renderDistance));
				writer.write(writeValue(OPTION_STRINGS.gamma, DEFAULT_OPTIONS.gamma));
				writer.write(writeValue(OPTION_STRINGS.gravity, DEFAULT_OPTIONS.gravity));
				writer.write(writeValue(OPTION_STRINGS.sensitivity, DEFAULT_OPTIONS.sensitivity));
				writer.write(writeValue(OPTION_STRINGS.walkSpeed, DEFAULT_OPTIONS.walkSpeed));
				writer.write(writeValue(OPTION_STRINGS.sprintSpeed, DEFAULT_OPTIONS.sprintSpeed));
				writer.write(writeValue(OPTION_STRINGS.jumpHeight, DEFAULT_OPTIONS.jumpHeight));

				writer.close();
			} catch (IOException err) {
				System.err.println("Could not create options file.");
			}
		}
		try {
			Scanner scanner = new Scanner(optionsFile);

			// Load options file values
			while (scanner.hasNextLine()) {
				String data = scanner.nextLine();
				String[] lineParts = data.split("=");

				// Skip if not a key=value pair
				if (lineParts.length != 2)
					continue;

				String dataName = lineParts[0].trim();
				String dataValue = lineParts[1].trim();

				parseOptionStrs(dataName, dataValue);
			}

			scanner.close();
		} catch (FileNotFoundException err) {
			System.err.println("Options file not found.");
		}
	}

	private static void parseOptionStrs(String name, String val) {
		if (name == null)
			return;

		if (name.contains(OPTION_STRINGS.fileVersion))
			fileVersion = Float.parseFloat(val);
		if (name.contains(OPTION_STRINGS.seed))
			seed = Long.parseLong(val);
		if (name.contains(OPTION_STRINGS.worldSize))
			worldSize = Short.parseShort(val);
		if (name.contains(OPTION_STRINGS.groundLevel))
			groundLevel = Byte.parseByte(val);
		if (name.contains(OPTION_STRINGS.buildHeight))
			buildHeight = Byte.parseByte(val);
		if (name.contains(OPTION_STRINGS.renderDistance))
			renderDistance = Short.parseShort(val);
		if (name.contains(OPTION_STRINGS.gamma))
			gamma = Float.parseFloat(val);
		if (name.contains(OPTION_STRINGS.gravity))
			gravity = Float.parseFloat(val);
		if (name.contains(OPTION_STRINGS.sensitivity))
			sensitivity = Float.parseFloat(val);
		if (name.contains(OPTION_STRINGS.walkSpeed))
			walkSpeed = Float.parseFloat(val);
		if (name.contains(OPTION_STRINGS.sprintSpeed))
			sprintSpeed = Float.parseFloat(val);
		if (name.contains(OPTION_STRINGS.jumpHeight))
			jumpHeight = Float.parseFloat(val);
	}
}
