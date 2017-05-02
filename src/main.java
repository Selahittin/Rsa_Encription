
public class main {

	public static void main(String[] args) throws Exception{
		
		RsaCrypt rsaObject = new RsaCrypt();
		
		/*Eðer anahtar deðerleri daha önce oluþturulmadýysa üretir.*/
		if (!rsaObject.areKeyPresent())
			rsaObject.generateKey();
		
		/*txt dosyasýnýn bulunduðu klasöre gider ve dosyayý okur.*/
		String plainText = rsaObject.readData("CryptoFiles/plainText.txt");
		System.out.println("Sifrelenecek Metin: " + plainText);
		
		/*Dosyadan okunan mesajý þifreleme iþlemine sokar ve þifreli mesajý ekrana yazdýrýr.*/
		String result = rsaObject.encrypt("CryptoFiles/plainText.txt", "CryptoFiles/cipherText.txt");
		System.out.println(result);
		
		/*Þifreli mesajý þifre çözme iþlemine sokar ve çözülmüþ mesajý ekrana yazdýrýr.*/
		result = rsaObject.decrypt("CryptoFiles/cipherText.txt", "CryptoFiles/plainTextAgain.txt");
		System.out.println(result);
	}

}
