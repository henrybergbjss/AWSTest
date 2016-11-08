import java.io.BufferedWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SimpleBoomiEquivalent
{
	public static void main(String[] args) throws Exception
	{
		String url = "jdbc:postgresql://ec2-54-146-103-124.compute-1.amazonaws.com/postgres?user=postgres&password=postgres";
		Path filePath = FileSystems.getDefault().getPath("/tmp/temp.txt");
		Connection conn = DriverManager.getConnection(url);
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from test");
		Files.deleteIfExists(filePath);
		Files.createFile(filePath);
		BufferedWriter fileWriter = Files.newBufferedWriter(filePath, StandardOpenOption.APPEND);

		while(rs.next())
		{
			fileWriter.write(String.format("db table row %s\n", rs.getString(1)));
		}
		fileWriter.close();
	}

}
