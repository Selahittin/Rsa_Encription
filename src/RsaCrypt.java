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
	
	public static final String ALGORITHM = "RSA";//Algoritma türü
	public static final String PRIVATE_KEY_FILE = "CryptoFiles/private.key";//Gizli anahtar
	public static final String PUBLIC_KEY_FILE = "CryptoFiles/public.key";//Açýk anahtar
	public static final int Key = 512;//Oluþturulacak anahtar boyutu
	
	/*Dosya oluþturma metodu*/
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
	
	/*Ýçerisine aldýðý adresteki dosyayý okur. Dosyanýn içindekileri String tipinde döndürür.*/
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
	
	/*Anahtar deðerlerini üretme metodu*/
	public void generateKey(){
		File file;
		KeyPairGenerator keyGen = null;
		try{
			/*Belirtilen bit boyutuna göre anahtar çifti üretilir.*/
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
	
	/*Anahtarlarýn mevcut olup olmadýðýnýn kontrolü*/
	public boolean areKeyPresent(){
		File privateKey = new File(PRIVATE_KEY_FILE);
		File publicKey = new File(PUBLIC_KEY_FILE);
		if (privateKey.exists() && publicKey.exists())
			return true;
		return false;
	}
	
	/*Þifreleme metodu*/
	public String encrypt(String plainTextAddress, String cipherTextAddress){
		/*Metot, þifrelenecek mesajýn ve þifreleme iþlemi sonucu saklanacak þifreli mesajýn adres deðerlerini alýr.*/
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
			/*While döngüsüyle þifrelenecek mesaj, byte byte okunur ve þifreli hale getirilir.
			 * Þifreli hale getirilen mesaj hedef dosyaya kaydedilir.
			 * Bu iþlem þifrelenecek mesaj boyutuna göre deðiþiklik gösterir.
			 * Okunan mesaj sonuna gelindiðinde while döngüsü sona erer.*/
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
	
	/*Þifre çözme metodu*/
	public String decrypt(String ciphertextAddress, String plainTextAgainAddress){
		/*Metot, içerisine þifresi çözülecek mesajýn ve þifre çözme iþlemi sonrasý saklanacak mesajýn adres deðerlerini alýr.*/
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
			/*Buradaki while döngüsünde encrypt metodunun tersi uygulanýr.
			 *Þifreli mesajý içeren dosya byte byte okunur, þifre çözme iþlemi yapýlarak, çözülen mesaj hedef dosyaya kaydedilir.
			 *Þifreli mesaj boyutuna göre while döngüsü devam eder. Þifreli mesajý içeren dosya tamamýyla okunduktan ve
			 *þifreli mesaj çözüldükten sonra while döngüsü sona erer.*/
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
