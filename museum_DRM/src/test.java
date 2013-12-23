public class test
	{
		public static void main(String[] args)
			{
				System.out.println(test.class.getProtectionDomain().getCodeSource().getLocation().getFile());
			}
	}
