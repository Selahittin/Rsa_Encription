
public class main {

	public static void main(String[] args) throws Exception{
		
		RsaCrypt rsaObject = new RsaCrypt();
		
		/*E�er anahtar de�erleri daha �nce olu�turulmad�ysa �retir.*/
		if (!rsaObject.areKeyPresent())
			rsaObject.generateKey();
		
		/*txt dosyas�n�n bulundu�u klas�re gider ve dosyay� okur.*/
		String plainText = rsaObject.readData("C:\\Users\\USER\\Desktop\\Projeler\\RsaCryptTxt\\CrytoFiles\\plainText.txt");
		System.out.println("Sifrelenecek Metin: " + plainText);
		
		/*Dosyadan okunan mesaj� �ifreleme i�lemine sokar ve �ifreli mesaj� ekrana yazd�r�r.*/
		String result = rsaObject.encrypt("C:\\Users\\USER\\Desktop\\Projeler\\RsaCryptTxt\\CrytoFiles\\plainText.txt", "C:\\Users\\USER\\Desktop\\Projeler\\RsaCryptTxt\\CrytoFiles\\cipherText.txt");
		System.out.println(result);
		
		/*�ifreli mesaj� �ifre ��zme i�lemine sokar ve ��z�lm�� mesaj� ekrana yazd�r�r.*/
		result = rsaObject.decrypt("C:\\Users\\USER\\Desktop\\Projeler\\RsaCryptTxt\\CrytoFiles\\cipherText.txt", "C:\\Users\\USER\\Desktop\\Projeler\\RsaCryptTxt\\CrytoFiles\\plainTextAgain.txt");
		System.out.println(result);
	}

}
