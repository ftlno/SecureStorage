package no.ftl.secureandroid;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;

public class Storage {

    private final String KEYPAIR_ALIAS = "KEYPAIR_ALIAS";
    private final String KEYPAIR_EXIST = "KEYPAIR_EXIST";
    private final String ENCRYPTION = "RSA/ECB/PKCS1Padding";
    private final Context context;
    private KeyStore keyStore;

    public Storage(final Context context) {
        this.context = context;
        loadKeyStore();
    }

    public String get(final String key) {
        String encrypted = PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
        return decrypt(encrypted);
    }

    public void set(final String key, final String value) {
        String encrypted = encrypt(value);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, encrypted).apply();
    }

    private KeyStore loadKeyStore() {
        try {
            if (keyStore == null) {
                generateKeyPair();
                keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                return keyStore;
            } else {
                return keyStore;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void generateKeyPair() throws Exception {

        Boolean keyPairExists = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEYPAIR_EXIST, false);

        if (!keyPairExists) {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
            KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                    KEYPAIR_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setKeySize(2048)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .build();
            keyPairGenerator.initialize(spec);
            keyPairGenerator.generateKeyPair();

            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(KEYPAIR_EXIST, true).apply();
        }
    }

    private String encrypt(final String clearText) {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEYPAIR_ALIAS, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();
            Cipher input = Cipher.getInstance(ENCRYPTION, "AndroidOpenSSL");
            input.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, input);
            cipherOutputStream.write(clearText.getBytes("UTF-8"));
            cipherOutputStream.close();
            byte[] vals = outputStream.toByteArray();

            return Base64.encodeToString(vals, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String decrypt(final String cipherText) {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEYPAIR_ALIAS, null);

            Cipher cipher = Cipher.getInstance(ENCRYPTION);
            cipher.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
            CipherInputStream cipherInputStream = new CipherInputStream(new ByteArrayInputStream(Base64.decode(cipherText, Base64.DEFAULT)), cipher);

            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte) nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i);
            }

            return new String(bytes, 0, bytes.length, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}