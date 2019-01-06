package DegreeAuditParser;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This entire class is pretty much just for debug purposes.
 * Hence, everything is static for simplicity.
 */

public class OutWriter {

	private static File outFile = new File("output.txt");
	private static File htmlFile = new File("degAudit.html");
	private static FileWriter outWriter;
	private static FileWriter htmlWriter;

	static {
		try {
			outWriter = new FileWriter(outFile);
		} catch (IOException e) {
			new Alert(Alert.AlertType.ERROR, "Error writing output file!!!!1!").showAndWait();
		}
		try {
			htmlWriter = new FileWriter(htmlFile);
		} catch (IOException e) {
			new Alert(Alert.AlertType.ERROR, "Error writing html file!!!!!1!").showAndWait();
		}
	}

	public static void outputLn(String text) {
		try {
			outWriter.write(text);
			outWriter.write("\n");
		} catch (IOException e) {
			new Alert(Alert.AlertType.ERROR, "Error writing output file!!!!1!").showAndWait();
		}
	}

	public static void output(String text) {
		try {
			outWriter.write(text);
		} catch (IOException e) {
			new Alert(Alert.AlertType.ERROR, "Error writing output file!!!!1!").showAndWait();
		}
	}

	public static void outputHtml(String html) {
		try {
			htmlWriter.write(html);
		} catch (IOException e) {
			new Alert(Alert.AlertType.ERROR, "Error writing html file!!!!1!").showAndWait();
		}
	}

}
