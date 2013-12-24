import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Main
	{
		public static String serial = "not compatable";

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

		public static void write(String dir)
			{
				if (!serial.equals("not compatable"))
					{
						try
							{

								Element root = new Element("System");
								Document doc = new Document(root);
								String tmp = serial;

								Element CPU = new Element("HDD");
								CPU.setAttribute(new Attribute("ID", "0"));
								CPU.addContent(new Element("Serial").setText(tmp));
								doc.getRootElement().addContent(CPU);

								XMLOutputter xoutput = new XMLOutputter();
								xoutput.setFormat(Format.getPrettyFormat());
								xoutput.output(doc, new FileWriter(dir));

							} catch (IOException io)
							{
								System.out.println(io.getMessage());
							}
					}

			}

		public static void add(String dir) throws JDOMException, IOException
			{
				if (!serial.equals("not compatable"))
					{
						try
							{
								SAXBuilder build = new SAXBuilder();
								File in = new File(dir);
								Document doc = (Document) build.build(in);
								Element rootnode = doc.getRootElement();
								@SuppressWarnings("rawtypes") List idnum = rootnode.getChildren("HDD");
								int tem = idnum.size();
								String num = String.valueOf(tem);
								String tmp = serial;

								Element CPU = new Element("HDD");
								CPU.setAttribute(new Attribute("id", num));
								CPU.addContent(new Element("serial").setText(tmp));

								doc.getRootElement().addContent(CPU);

								XMLOutputter out = new XMLOutputter();

								out.setFormat(Format.getPrettyFormat());
								out.output(doc, new FileWriter(dir));

							} catch (IOException io)
							{
								io.printStackTrace();
							} catch (JDOMException e)
							{
								e.printStackTrace();
							}
					}
			}

		public static void main(String[] args) throws IOException,
				JDOMException
			{
				try
					{
						read("C");
						JFileChooser gui = new JFileChooser();
						Object[] options = { "new", "open", "cancel" };
						int option = JOptionPane.showOptionDialog(null, "select an option", "serial grabber", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
						switch (option)
						{
						case 0:
							{
								int response = gui.showSaveDialog(null);
								if (response == JFileChooser.APPROVE_OPTION)
									{
										write(gui.getSelectedFile().getPath());
									}
								break;
							}
						case 1:
							{
								int response = gui.showOpenDialog(null);
								if (response == JFileChooser.APPROVE_OPTION)
									{
										add(gui.getSelectedFile().getPath());
									}
								break;
							}
						case 2:
							{
								System.exit(0);
							}
							break;
						}

						javax.swing.JOptionPane.showConfirmDialog((java.awt.Component) null, serial, "Serial Number of C:", javax.swing.JOptionPane.DEFAULT_OPTION);
					} catch (IOException io)
					{
						io.printStackTrace();
					} catch (JDOMException e)
					{
						e.printStackTrace();
					}
			}

	}
