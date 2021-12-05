package edu.utils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class Logger {

	public enum LogLevel {
		CRITICAL,
		ERROR,
		INFO;

		@Override
		public String toString() {
			return switch (this) {
				case INFO -> "INFO >> ";
				case CRITICAL -> "CRITICAL >> ";
				case ERROR -> "ERROR >> ";
			};
		}
	}

	private BufferedWriter logBW;
	private static Logger instance;

	private Logger() {
		LocalDateTime currDate = LocalDateTime.now();
		String currDateFormated = currDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		long currPID = ProcessHandle.current().pid();

		String formattedLogFileName = "log-" + currDateFormated + "-" + currPID + ".log";
		String fullPath = getDiskDrive() + File.separator + "Temp" + File.separator;
		try {
			File logFilePath = new File(fullPath);

			// if the dir exists, it is not created again
			logFilePath.mkdirs();
			File logFile = new File(fullPath + formattedLogFileName);

			// if the file exists, it is not created again
			logFile.createNewFile();

			this.logBW = new BufferedWriter(new FileWriter(logFile));
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public static synchronized Logger getLogger() {
		// lazy instantiation
		if (instance == null) {
			instance = new Logger();
		}

		return instance;
	}

	public synchronized void logMessage(LogLevel level, String msg) {
		if (this.logBW == null) {
			System.err.println("Error: Unable to connect to the log file.");
			return;
		}

		LocalDateTime currDate = LocalDateTime.now();
		String currDateFormated = currDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		String loggedMessage = level + currDateFormated + " >> " + msg;

		try {
			logBW.write(loggedMessage);
			System.out.println(loggedMessage);
			logBW.newLine();
			logBW.flush();

			if (level.toString().equals(LogLevel.CRITICAL.toString())) {
				System.exit(1);
			}
		} catch (IOException e) {
			System.err.println("Error: Unable to write in log file.");
		}
	}

	private static String getDiskDrive() {
		String winPath = System.getenv("windir");

		return (winPath.split(Pattern.quote("\\")))[0];
	}

	// Singleton mintához szükséges tiltások, amelyek meggátolják, hogy újabb példány jöjjön létre

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	private void writeObject(ObjectOutputStream out) throws NotSerializableException {
		throw new NotSerializableException();
	}

	private void readObject(ObjectInputStream in) throws NotSerializableException {
		throw new NotSerializableException();
	}

}
