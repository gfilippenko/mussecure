import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class DRM
	{
		static final int BUFFER = 512;
		static final int TOOBIG = 0x6400000; // max size of unzipped data, 100MB
		static final int TOOMANY = 1024; // max number of files
		public static String serial = "not compatable";
		public static String encoded = "89fa17405efdef541ceb487e23cbf90b";

		public static void read(String drive) throws IOException
			{
				try
					{
						File file = File.createTempFile("info", ".vbs");
						String vb = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"
								+ "Set colDrives = objFSO.Drives\n"
								+ "Set objDrive = colDrives.item(\""
								+ drive
								+ "\")\n"
								+ "Wscript.Echo objDrive.SerialNumber";
						file.deleteOnExit();
						FileWriter write = new FileWriter(file);
						write.write(vb);
						write.close();
						Process p = Runtime.getRuntime().exec("cscript //NoLogo "
								+ file.getPath());
						BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
						String line;
						while ((line = input.readLine()) != null)
							{
								serial = line;
							}

					} catch (Exception e)
					{
						e.printStackTrace();
					}
			}

		public static String encoder(String serial)
				throws UnsupportedEncodingException, NoSuchAlgorithmException
			{
				String hash = "";

				MessageDigest md = MessageDigest.getInstance("MD5");
				md.reset();
				md.update(serial.getBytes("UTF-8"));
				byte[] digest = md.digest();
				BigInteger bigint = new BigInteger(1, digest);
				hash = bigint.toString(16);

				return hash;
			}

		public static boolean check(String dir) throws JDOMException,
				IOException, NoSuchAlgorithmException
			{
				start();
				boolean check = false;
				SAXBuilder build = new SAXBuilder();
				File xmf = new File(dir);
				Document doc = (Document) build.build(xmf);
				Element root = doc.getRootElement();
				@SuppressWarnings("rawtypes") List list = root.getChildren("HDD");

				for (int i = 0; i < list.size(); i++)
					{
						Element node = (Element) list.get(i);
						if (encoded.equals(node.getChildText("checksum")))
							{
								check = true;
							}
					}

				return check;
			}

		public static void start() throws IOException, NoSuchAlgorithmException
			{
				read("C");
				encoder(serial);
			}

		private static void extractFile(String name) throws IOException
			{
				ClassLoader cl = DRM.class.getClassLoader();
				File target = new File(name);
				if (target.exists())
					return;

				FileOutputStream out = new FileOutputStream(target);
				InputStream in = cl.getResourceAsStream(name);

				byte[] buf = new byte[8 * 1024];
				int len;
				while ((len = in.read(buf)) != -1)
					{
						out.write(buf, 0, len);
					}
				out.close();
				in.close();
			}

		public static void main(String[] args) throws Exception
			{
				JFileChooser gui = new JFileChooser();
				gui.showOpenDialog(null);
				boolean digest = check(gui.getSelectedFile().getPath());
				if (digest == true)
					{
						extractFile("CSU_HOF.air");
					} else
					{
						JOptionPane.showMessageDialog(null, "System not valid!\n Halting!", "error", JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}
			}
	}
