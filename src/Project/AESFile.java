package Project;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AESFile {
    public static void encrypt(String username, String pass) throws Exception {

        // file to be encrypted
        FileInputStream inFile = new FileInputStream("database/"+username+"/"+username+".csv");

        // encrypted file
        FileOutputStream outFile = new FileOutputStream("database/"+username+"/"+username+".encrypted");

        byte[] salt = new byte[8];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        FileOutputStream saltOutFile = new FileOutputStream("database/"+username+"/salt.enc");
        saltOutFile.write(salt);
        saltOutFile.close();

        SecretKeyFactory factory = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(pass.toCharArray(), salt, 65536,
                256);
        SecretKey secretKey = factory.generateSecret(keySpec);
        SecretKey secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();

        FileOutputStream ivOutFile = new FileOutputStream("database/"+username+"/iv.enc");
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        ivOutFile.write(iv);
        ivOutFile.close();

        //file encryption
        byte[] input = new byte[64];
        int bytesRead;

        while ((bytesRead = inFile.read(input)) != -1) {
            byte[] output = cipher.update(input, 0, bytesRead);
            if (output != null)
                outFile.write(output);
        }

        byte[] output = cipher.doFinal();
        if (output != null)
            outFile.write(output);

        inFile.close();
        outFile.flush();
        outFile.close();

        File file = new File ("database/"+username+"/"+username+".csv");
        if (file.delete()) {
        }
        else
            System.out.println("Error deleting file");

        System.out.println("File Encrypted.");
    }


    public static String decrypt(String username, String pass) throws Exception {
        String password = pass;

        // reading the salt
        FileInputStream saltFis = new FileInputStream("database/"+username+"/salt.enc");
        byte[] salt = new byte[8];
        saltFis.read(salt);
        saltFis.close();

        // reading the iv
        FileInputStream ivFis = new FileInputStream("database/"+username+"/iv.enc");
        byte[] iv = new byte[16];
        ivFis.read(iv);
        ivFis.close();

        SecretKeyFactory factory = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536,
                256);
        SecretKey tmp = factory.generateSecret(keySpec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        // file decryption
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
        FileInputStream fis = new FileInputStream("database/"+username+"/"+username+".encrypted");
        FileOutputStream fos = new FileOutputStream("database/"+username+"/"+username+".csv");
        byte[] in = new byte[64];
        int read;
        try {
            while ((read = fis.read(in)) != -1) {
                byte[] output = cipher.update(in, 0, read);
                if (output != null)
                    fos.write(output);
            }
            byte[] output = cipher.doFinal();
            if (output != null)
                fos.write(output);
            fis.close();
            fos.flush();
            fos.close();
            System.out.println("File Decrypted.");

        }catch(Exception e) {
            fos.close();
            File myObj = new File("database/"+username+"/"+username+".csv");
            if (myObj.delete()) {
                return "Wrong password";
            } else {
                System.out.println("Failed to delete the file.");
            }
        }
        return "Decrypted";
    }

}