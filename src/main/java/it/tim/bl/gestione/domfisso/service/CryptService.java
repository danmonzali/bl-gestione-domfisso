package it.tim.bl.gestione.domfisso.service;

import it.tim.enc.DecryptService;
import it.tim.enc.EncryptService;
import it.tim.enc.exception.EncryptDecryptException;
import it.tim.enc.pojo.DecryptPojo;
import it.tim.enc.pojo.EncryptPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CryptService {

	@Autowired
	private DecryptService decryptService;

	@Autowired
	private EncryptService encryptService;


	public String decrypt(DecryptPojo decryptPojo) throws EncryptDecryptException {
		return decryptService.decryptText(decryptPojo);

	}

	public EncryptPojo encrypt(EncryptPojo encryptPojo) throws EncryptDecryptException {
		return encryptService.encryptText(encryptPojo);

	}
}