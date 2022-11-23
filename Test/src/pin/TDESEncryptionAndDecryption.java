package pin;



import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import cryptix.util.core.Hex;

// import java.nio.charset.StandardCharsets;
// import java.security.InvalidAlgorithmParameterException;
// import java.security.InvalidKeyException;
// import java.security.NoSuchAlgorithmException;
// import java.security.spec.InvalidKeySpecException;
// import java.security.spec.KeySpec;
// import java.util.Base64;
// import javax.crypto.spec.IvParameterSpec;
// import javax.crypto.spec.PBEKeySpec;
// import javax.crypto.spec.SecretKeySpec;
// import javafx.event.ActionEvent;
// import javafx.fxml.FXML;
// import javafx.scene.control.*;

public class TDESEncryptionAndDecryption {

	public String TDESecnryption(String CardNo, String Pin) throws Exception {
		String xOrData = getXORFormat(getCardBlock(CardNo), getPinBlock(Pin));
		String keyTxt = "15EA4CA20131C2FD2315208C9110AD40";
		keyTxt += keyTxt.substring(0, 16);
		byte[] encryptKey = convertHexStringToByteArray(keyTxt);
		byte[] encryptData = convertHexStringToByteArray(xOrData);
		DESedeKeySpec spec = new DESedeKeySpec(encryptKey);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		SecretKey theKey = keyFactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, theKey);
		byte[] encrypted = cipher.doFinal(encryptData);
		return Hex.toString(encrypted);
	}
	
	
	  public String TDESdecrypt(String  encryptData)throws Exception {
		  
		 
			String keyTxt = "15EA4CA20131C2FD2315208C9110AD40";
			keyTxt += keyTxt.substring(0, 16);
			byte[] encryptKey= convertHexStringToByteArray(keyTxt);
			byte[] encryptedData = convertHexStringToByteArray(encryptData);
			DESedeKeySpec spec = new DESedeKeySpec(encryptKey);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
			SecretKey theKey = keyFactory.generateSecret(spec);
			Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, theKey);
			byte[] decrpytkey = cipher.doFinal(encryptedData);
			return Hex.toString(decrpytkey);
		  
		  
		  
	  }

	public static byte[] convertHexStringToByteArray(String hexString) {
		if ((hexString.length() % 2) != 0) {
			throw new IllegalArgumentException("Invalid hex string (length % 2 != 0)");
		}
		byte[] array = new byte[hexString.length() / 2];
		for (int i = 0; i < array.length; i++) {
			String s = hexString.substring(i * 2, i * 2 + 2);
			array[i] = (byte) Integer.parseInt(s, 16);
		}
		return array;
	}

	public static void main(String args[]) {
		TDESEncryptionAndDecryption tdes = new TDESEncryptionAndDecryption();
		try {
			String encr = tdes.TDESecnryption("4591530000000094", "1111");
			System.out.println(encr);
				String dec = tdes.TDESdecrypt(encr);
			
			System.out.println(tdes.getXORFormat(tdes.getCardBlock("4591530000000094"), dec));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception");
		}
	}

	public String getPinBlock(String pin) {
		int pinLength = pin.length();
		String pinBlock = "0" + pinLength + pin;
		return getFormatValue(pinBlock, 16, "right", "F");
	}

	public String getCardBlock(String cardNo) {
		String tempCardNo = cardNo;
		int cardLen = tempCardNo.length();
		if (cardLen > 12) {
			int accountNumIndex = cardLen - 13;
			tempCardNo = tempCardNo.substring(accountNumIndex, tempCardNo.length() - 1);
		}
		return "0000" + tempCardNo.substring(tempCardNo.length() - 12);
	}

	private String getFormatValue(String pinBlock, int length, String direction, String character) {
		int fLength = pinBlock.length();
		int f = length - fLength;
		String z = "";
		for (int i = 0; i < f; i++) {
			z += character;
		}
		if (direction.equals("right")) {
			return pinBlock + z;
		} else {
			return z + pinBlock;
		}
	}

	public String getXORFormat(String pinBlock, String cardBlock) {
		long pin = Long.parseLong(pinBlock, 16);
		long card = Long.parseLong(cardBlock, 16);
		long xorValue = pin ^ card;

		return this.getFormatValue(Long.toHexString(xorValue).toUpperCase(), 16, "left", "0");
	}

}
