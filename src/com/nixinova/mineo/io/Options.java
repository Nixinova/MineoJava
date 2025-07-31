package com.nixinova.mineo.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import com.nixinova.mineo.main.Mineo;

public class Options {
	public static final String OPTIONS_FILE = "options.txt";
	/**
	 * Semantics of version incrementing:
	 * - +1.00: Major: Fundamental file format changes that make older options.txt files unreadable.
	 * - +0.10: Minor: Changes to the implementation of a value.
	 * - +0.01: Micro: Addition of new options, keeps backward compatibility.
	 */
	public static final float OPTIONS_VERSION = 1.12F;

	public static final class DEFAULT_OPTIONS {
		public static long seed = 100L;
		public static int worldSize = 500;
		public static int groundLevel = 16;
		public static int buildHeight = 32;
		public static int renderDistance = 20;
		public static double gamma = 1.0;
		public static double gravity = 0.2;
		public static double sensitivity = 0.005;
		public static double walkSpeed = 0.5;
		public static double sprintSpeed = 0.8;
		public static double jumpHeight = 1.6;
		public static double reach = 5.0;
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
		public static String reach = "reach";
	}

	private static final String optionsFilePath = Mineo.rootFolder + "/" + OPTIONS_FILE;

	public static float fileVersion = OPTIONS_VERSION;
	public static long seed = DEFAULT_OPTIONS.seed;
	public static int worldSize = DEFAULT_OPTIONS.worldSize;
	public static int groundLevel = DEFAULT_OPTIONS.groundLevel;
	public static int buildHeight = DEFAULT_OPTIONS.buildHeight;
	public static int renderDistance = DEFAULT_OPTIONS.renderDistance;
	public static double gamma = DEFAULT_OPTIONS.gamma;
	public static double gravity = DEFAULT_OPTIONS.gravity;
	public static double sensitivity = DEFAULT_OPTIONS.sensitivity;
	public static double sprintSpeed = DEFAULT_OPTIONS.sprintSpeed;
	public static double walkSpeed = DEFAULT_OPTIONS.walkSpeed;
	public static double jumpHeight = DEFAULT_OPTIONS.jumpHeight;
	public static double reach = DEFAULT_OPTIONS.reach;

	public static String writeValue(String id, String value) {
		String br = System.getProperty("line.separator");
		return String.valueOf(id) + "=" + value + br;
	}

	public static String writeValue(String id, Object value) {
		return writeValue(id, String.valueOf(value));
	}

	public static void createOptions() {
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
				writer.write(writeValue(OPTION_STRINGS.reach, DEFAULT_OPTIONS.reach));

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
			worldSize = Integer.parseInt(val);
		if (name.contains(OPTION_STRINGS.groundLevel))
			groundLevel = Integer.parseInt(val);
		if (name.contains(OPTION_STRINGS.buildHeight))
			buildHeight = Integer.parseInt(val);
		if (name.contains(OPTION_STRINGS.renderDistance))
			renderDistance = Integer.parseInt(val);
		if (name.contains(OPTION_STRINGS.gamma))
			gamma = Double.parseDouble(val);
		if (name.contains(OPTION_STRINGS.gravity))
			gravity = Double.parseDouble(val);
		if (name.contains(OPTION_STRINGS.sensitivity))
			sensitivity = Double.parseDouble(val);
		if (name.contains(OPTION_STRINGS.walkSpeed))
			walkSpeed = Double.parseDouble(val);
		if (name.contains(OPTION_STRINGS.sprintSpeed))
			sprintSpeed = Double.parseDouble(val);
		if (name.contains(OPTION_STRINGS.jumpHeight))
			jumpHeight = Double.parseDouble(val);
		if (name.contains(OPTION_STRINGS.reach))
			reach = Double.parseDouble(val);
	}
}
