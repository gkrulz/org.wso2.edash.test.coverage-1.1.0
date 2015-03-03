package publish;

/**
 * Created with IntelliJ IDEA.
 * User: anushka
 * Date: 10/21/13
 * Time: 5:52 PM
 * To change this template use File | Settings | File Templates.
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PrintLog {

	public void write(String content) {
		File log = new File("logSupportJira.txt");

		try {
			if (!log.exists()) {
				System.out.println("new file.");
				log.createNewFile();
			}

			FileWriter fileWriter = new FileWriter(log, true);

			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(content);
			bufferedWriter.close();

		} catch (IOException e) {
			System.out.println("COULD NOT LOG!!");
		}
	}

}
