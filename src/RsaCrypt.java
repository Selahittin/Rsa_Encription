import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;


public class RsaCrypt {
	
	public static final String ALGORITHM = "RSA";//Algoritma t�r�
	public static final String PRIVATE_KEY_FILE = "CryptoFiles/private.key";//Gizli anahtar
	public static final String PUBLIC_KEY_FILE = "CryptoFiles/public.key";//A��k anahtar
	public static final int Key = 512;//Olu�turulacak anahtar boyutu
	
	/*Dosya olu�turma metodu*/
	public File createFile(String fileName){
		
		File file = new File(fileName);
		try{
			if (file.getParentFile()!= null){
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
		}catch (IOException e){
			e.printStackTrace();
		}
		return file;
	}
	
	/*��erisine ald��� adresteki dosyay� okur. Dosyan�n i�indekileri String tipinde d�nd�r�r.*/
	public String readData(String fileAddress){
		StringBuilder out = new StringBuilder();
		InputStream in;
		try{
			in = new FileInputStream(fileAddress);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			
			while ((line = reader.readLine())!= null){
				out.append(line);
			}
			reader.close();
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
		return out.toString();
	}
	
	/*Anahtar de�erlerini �retme metodu*/
	public void generateKey(){
		File file;
		KeyPairGenerator keyGen = null;
		try{
			/*Belirtilen bit boyutuna g�re anahtar �ifti �retilir.*/
			keyGen = KeyPairGenerator.getInstance(ALGORITHM);
			keyGen.initialize(Key);
			final KeyPair key = keyGen.generateKeyPair();
			file = createFile(PRIVATE_KEY_FILE);
			/*Private Keyi dosyaya kaydeder.*/
			ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(file));
			privateKeyOS.writeObject(key.getPrivate());
			privateKeyOS.close();
			file = createFile(PUBLIC_KEY_FILE);
			/*Public Keyi dosyaya kaydeder.*/
			ObjectOutputStream publicKeysOS = new ObjectOutputStream(new FileOutputStream(file));
			publicKeysOS.writeObject(key.getPublic());
			publicKeysOS.close();
		}catch (IOException | NoSuchAlgorithmException e){
			e.printStackTrace();
		}
	}
	
	/*Anahtarlar�n mevcut olup olmad���n�n kontrol�*/
	public boolean areKeyPresent(){
		File privateKey = new File(PRIVATE_KEY_FILE);
		File publicKey = new File(PUBLIC_KEY_FILE);
		if (privateKey.exists() && publicKey.exists())
			return true;
		return false;
	}
	
	/*�ifreleme metodu*/
	public String encrypt(String plainTextAddress, String cipherTextAddress){
		/*Metot, �ifrelenecek mesaj�n ve �ifreleme i�lemi sonucu saklanacak �ifreli mesaj�n adres de�erlerini al�r.*/
		try{
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
			final PublicKey pubKey = (PublicKey) inputStream.readObject();
			inputStream.close();
			
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			
			FileInputStream fis = new FileInputStream(plainTextAddress);
			FileOutputStream fos = new FileOutputStream(cipherTextAddress);
			CipherOutputStream cos = new CipherOutputStream(fos, cipher);
			
			byte[] block = new byte[32];
			int i;
			/*While d�ng�s�yle �ifrelenecek mesaj, byte byte okunur ve �ifreli hale getirilir.
			 * �ifreli hale getirilen mesaj hedef dosyaya kaydedilir.
			 * Bu i�lem �ifrelenecek mesaj boyutuna g�re de�i�iklik g�sterir.
			 * Okunan mesaj sonuna gelindi�inde while d�ng�s� sona erer.*/
			while ((i = fis.read(block))!= -1){
				cos.write(block, 0, i);
			}
			cos.close();
			fis.close();
			fos.close();
			
			String cipherText = readData(cipherTextAddress);
			return "Sifreleme Islemi Tamamlandi." + "\n" + "Sifreli Metin: " +
					cipherText;
		}catch (Exception e){
			e.printStackTrace();
			return "Sifreleme Islemi Sirasinda Beklenmedik Bir Hata Olustu.";
		}
	}
	
	/*�ifre ��zme metodu*/
	public String decrypt(String ciphertextAddress, String plainTextAgainAddress){
		/*Metot, i�erisine �ifresi ��z�lecek mesaj�n ve �ifre ��zme i�lemi sonras� saklanacak mesaj�n adres de�erlerini al�r.*/
		try {
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
			final PrivateKey privKey = (PrivateKey) inputStream.readObject();
			inputStream.close();
			
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, privKey);
			
			FileInputStream fis = new FileInputStream(ciphertextAddress);
			FileOutputStream fos = new FileOutputStream(plainTextAgainAddress);
			
			CipherInputStream cis = new CipherInputStream(fis, cipher);
			byte[] block = new byte[32];
			int i;
			/*Buradaki while d�ng�s�nde encrypt metodunun tersi uygulan�r.
			 *�ifreli mesaj� i�eren dosya byte byte okunur, �ifre ��zme i�lemi yap�larak, ��z�len mesaj hedef dosyaya kaydedilir.
			 *�ifreli mesaj boyutuna g�re while d�ng�s� devam eder. �ifreli mesaj� i�eren dosya tamam�yla okunduktan ve
			 *�ifreli mesaj ��z�ld�kten sonra while d�ng�s� sona erer.*/
			while ((i = cis.read(block))!= -1){
				fos.write(block, 0, i);
			}
			fos.close();
			cis.close();
			fis.close();
			
			String plainTextAgain = readData(plainTextAgainAddress);
			return "Sifre Cozme Islemi Tamamlandi." + "\n" + "Cozulen Metin: " + 
					plainTextAgain;
		}catch (Exception ex){
			ex.printStackTrace();
			return "Sifre Cozme Islemi Sirasinda Bir Hata Olustu.";
		}
	}
	
	
}
