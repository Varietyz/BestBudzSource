package com.bestbudz.rs2.content.minigames.bloodtrial.waves;

import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class WaveRegistry {
	private static final Map<Integer, WaveDefinition> waves = new HashMap<>();
	private static final String WAVE_PACKAGE = "com.bestbudz.rs2.content.minigames.bloodtrial.waves.impl";

	static {
		discoverAndRegisterWaves();
	}

	private static void discoverAndRegisterWaves() {
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			String path = WAVE_PACKAGE.replace('.', '/');
			Enumeration<URL> resources = classLoader.getResources(path);

			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();

				if (resource.getProtocol().equals("file")) {
					// Running from file system (development)
					discoverFromFileSystem(resource, path);
				} else if (resource.getProtocol().equals("jar")) {
					// Running from JAR (production)
					discoverFromJar(resource, path);
				}
			}

			System.out.println("Registered " + waves.size() + " waves automatically");

		} catch (Exception e) {
			System.err.println("Failed to auto-discover waves: " + e.getMessage());
			e.printStackTrace();
			// Fallback to manual registration
			registerWavesManually();
		}
	}

	private static void discoverFromFileSystem(URL resource, String path) throws Exception {
		File directory = new File(resource.toURI());

		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles((dir, name) ->
				name.startsWith("Wave") && name.endsWith(".class"));

			if (files != null) {
				for (File file : files) {
					String className = file.getName().replace(".class", "");
					registerWaveClass(WAVE_PACKAGE + "." + className);
				}
			}
		}
	}

	private static void discoverFromJar(URL resource, String path) throws Exception {
		String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
		JarFile jar = new JarFile(jarPath);

		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String entryName = entry.getName();

			if (entryName.startsWith(path + "/Wave") && entryName.endsWith(".class")) {
				String className = entryName.replace('/', '.').replace(".class", "");
				registerWaveClass(className);
			}
		}
		jar.close();
	}

	private static void registerWaveClass(String className) {
		try {
			Class<?> clazz = Class.forName(className);

			// Check if it extends WaveDefinition
			if (WaveDefinition.class.isAssignableFrom(clazz)) {
				WaveDefinition wave = (WaveDefinition) clazz.getDeclaredConstructor().newInstance();
				registerWave(wave);
				System.out.println("Auto-registered: " + className);
			}

		} catch (Exception e) {
			System.err.println("Failed to register wave class: " + className + " - " + e.getMessage());
		}
	}

	private static void registerWave(WaveDefinition wave) {
		waves.put(wave.getWaveNumber(), wave);
	}

	// Fallback method in case auto-discovery fails
	private static void registerWavesManually() {
		System.out.println("Falling back to manual wave registration...");

		try {
			// Use reflection to find and register known wave classes
			for (int i = 1; i <= 15; i++) {
				String className = WAVE_PACKAGE + ".Wave" + String.format("%02d", i);
				try {
					Class<?> clazz = Class.forName(className);
					WaveDefinition wave = (WaveDefinition) clazz.getDeclaredConstructor().newInstance();
					registerWave(wave);
				} catch (ClassNotFoundException e) {
					// Wave class doesn't exist, skip
				}
			}
		} catch (Exception e) {
			System.err.println("Manual registration also failed: " + e.getMessage());
		}
	}

	public static WaveDefinition getWave(int waveNumber) {
		WaveDefinition wave = waves.get(waveNumber);
		if (wave == null) {
			throw new IllegalArgumentException("Invalid wave number: " + waveNumber);
		}
		return wave;
	}

	public static int getMaxWaves() {
		return waves.size();
	}

	// Debug method to list all registered waves
	public static void printRegisteredWaves() {
		System.out.println("Registered Waves:");
		waves.entrySet().stream()
			.sorted(Map.Entry.comparingByKey())
			.forEach(entry -> System.out.println("Wave " + (entry.getKey() + 1) + ": " +
				entry.getValue().getClass().getSimpleName()));
	}
}