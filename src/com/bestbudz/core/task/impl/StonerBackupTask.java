package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class StonerBackupTask extends Task {

  public StonerBackupTask() {
    super(12000, true, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CHARACTER_BACKUP);
  }

  public static void main(String[] args) {
    Thread t =
        new Thread(
            new Runnable() {
              @Override
              public void run() {
                backup();
                System.out.println("done.");
              }
            });
    try {
      t.start();
      t.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

	public static void backup() {
		String charDir = "." + File.separator + "data" + File.separator + "database";
		String backupsBase = System.getProperty("user.home") + File.separator + "BestBudz-backups";
		String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
		String zipPath = backupsBase + File.separator + "backup_" + timestamp + ".zip";

		File backupDir = new File(backupsBase);
		backupDir.mkdirs();

		try (FileOutputStream fos = new FileOutputStream(zipPath);
			 ZipOutputStream zos = new ZipOutputStream(fos)) {
			zipDirectory(new File(charDir), "", zos);
		} catch (IOException e) {
			e.printStackTrace();
		}

		cleanupOldBackups(backupDir, 5);

		System.out.println("Compressed backup created at: " + zipPath);
	}

	private static void zipDirectory(File folder, String parentPath, ZipOutputStream zos) throws IOException {
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				zipDirectory(file, parentPath + file.getName() + "/", zos);
				continue;
			}

			try (FileInputStream fis = new FileInputStream(file)) {
				ZipEntry zipEntry = new ZipEntry(parentPath + file.getName());
				zos.putNextEntry(zipEntry);

				byte[] buffer = new byte[4096];
				int len;
				while ((len = fis.read(buffer)) != -1) {
					zos.write(buffer, 0, len);
				}
			}
		}
	}

	private static void cleanupOldBackups(File backupDir, int maxBackups) {
		File[] files = backupDir.listFiles((dir, name) -> name.endsWith(".zip"));
		if (files == null || files.length <= maxBackups) return;

		Arrays.sort(files, Comparator.comparingLong(File::lastModified));

		for (int i = 0; i < files.length - maxBackups; i++) {
			files[i].delete();
		}
	}


	public static void copyFile(File sourceFile, File destFile) throws IOException {
    Files.copy(
        Paths.get(sourceFile.getPath()),
        Paths.get(destFile.getPath()),
        StandardCopyOption.COPY_ATTRIBUTES);
  }

	@Override
	public void execute() {
		new Thread(() -> {
			backup();
			System.out.println("Anti-Rollback Backup Task completed.");
		}, "BackupThread").start();
	}


  @Override
  public void onStop() {}
}
