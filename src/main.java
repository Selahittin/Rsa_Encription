
public class main {

	public static void main(String[] args) throws Exception{
		
		RsaCrypt rsaObject = new RsaCrypt();
		
		/*E�er anahtar de�erleri daha �nce olu�turulmad�ysa �retir.*/
		if (!rsaObject.areKeyPresent())
			rsaObject.generateKey();
		
		/*txt dosyas�n�n bulundu�u klas�re gider ve dosyay� okur.*/
		String plainText = rsaObject.readData("CryptoFiles/plainText.txt");
		System.out.println("Sifrelenecek Metin: " + plainText);
		
		/*Dosyadan okunan mesaj� �ifreleme i�lemine sokar ve �ifreli mesaj� ekrana yazd�r�r.*/
		String result = rsaObject.encrypt("CryptoFiles/plainText.txt", "CryptoFiles/cipherText.txt");
		System.out.println(result);
		
		/*�ifreli mesaj� �ifre ��zme i�lemine sokar ve ��z�lm�� mesaj� ekrana yazd�r�r.*/
		result = rsaObject.decrypt("CryptoFiles/cipherText.txt", "CryptoFiles/plainTextAgain.txt");
		System.out.println(result);
	}

}
