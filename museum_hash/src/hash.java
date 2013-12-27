import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class hash
	{
		public static void input(String dir)
			{
				SAXBuilder build = new SAXBuilder();
				File xmlf = new File(dir);
				try
					{
						Document doc = (Document) build.build(xmlf);
						Element root = doc.getRootElement();
						@SuppressWarnings("rawtypes") List list = root.getChildren("HDD");
						for (int i = 0; i < list.size(); i++)
							{
								Element node = (Element) list.get(i);
								try
									{
										String in = encoder(node.getChildText("Serial"));
										Element CPU = root.getChild("HDD");
										CPU.getAttribute("ID").setValue(String.valueOf(i));
										Element encode = new Element("checksum").setText(in);
										CPU.addContent(encode);

										XMLOutputter XMLout = new XMLOutputter();
										XMLout.setFormat(Format.getPrettyFormat());
										XMLout.output(doc, new FileWriter(dir));

									} catch (NoSuchAlgorithmException e)
									{
										e.printStackTrace();
									}
							}

					} catch (IOException io)
					{
						System.out.println(io.getMessage());
					} catch (JDOMException jdomex)
					{
						System.out.println(jdomex.getMessage());
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

		public static void main(String[] args)
			{
				JFileChooser gui = new JFileChooser();
				Object[] options = { "encode serials", "cancel" };
				int option = JOptionPane.showOptionDialog(null, "Do you want to encode the serials?", "encoder", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
				switch (option)
				{
				case 0:
					{
						int response = gui.showOpenDialog(null);
						if (response == JFileChooser.APPROVE_OPTION)
							{
								input(gui.getSelectedFile().getPath());
							}
					}
				case 1:
					{
						System.exit(0);
					}
				}
			}
	}
