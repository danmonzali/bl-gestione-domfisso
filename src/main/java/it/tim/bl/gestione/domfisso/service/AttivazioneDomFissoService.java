package it.tim.bl.gestione.domfisso.service;

import it.tim.bl.gestione.domfisso.dto.*;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.DatiIntestatario;
import it.tim.bl.gestione.domfisso.dto.AttivazioneRequestDto.RichiestaAttivazioneDomiciliazioneFisso;
import it.tim.bl.gestione.domfisso.entity.DatiLineaFisso;
import it.tim.bl.gestione.domfisso.entity.ProfiloPspSddMandato;
import it.tim.bl.gestione.domfisso.entity.RichiestaAttDomFisso;
import it.tim.bl.gestione.domfisso.exception.BRExceptionReturn;
import it.tim.bl.gestione.domfisso.exception.ISEExceptionReturn;
import it.tim.bl.gestione.domfisso.mapper.AttivazioneDomFissoMapper;
import it.tim.bl.gestione.domfisso.repo.DomiciliazioneFissoRepository;
import it.tim.bl.gestione.domfisso.repo.ProfiloPspSddMandatoRepository;
import it.tim.bl.gestione.domfisso.util.MaskingUtil;
import it.tim.bl.gestione.domfisso.validator.AttivazioneDomFissoValidator;
import it.tim.enc.pojo.DecryptPojo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Orchestratore NON transazionale del servizio bl-attivazione-dom-fisso.
 * Il tracciamento di processo usa transazioni proprie ({@link ProcessoTraceService}), le scritture di business
 * una transazione unica ({@link DomiciliazioneTxService}).
 */
@Service
public class AttivazioneDomFissoService {

	private static final Logger logger = LogManager.getLogger(AttivazioneDomFissoService.class);

	private static final String ESITO_OK = "000";
	private static final String SUBSYS_RISPOSTA = "NBIP";
	private static final int MAX_LEN_NOME = 25;
	private static final int MAX_LEN_COGNOME = 50;
	private static final List<Integer> STATI_ESCLUSI_LINEA = List.of(12010, 12003, 12009, 12002, 12007, 12006, 12004,
			10010, 12005, 12008, 10018, 12000, 12011, 12001);
	private static final ZoneId ITALY_ZONE_ID = ZoneId.of("Europe/Rome");
	private static final DateTimeFormatter FORMAT_DATA_ORA = DateTimeFormatter
			.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	@Autowired
	private ProcessoTraceService processoTraceService;

	@Autowired
	private AttivazioneDomFissoValidator validator;

	@Autowired
	private AttivazioneDomFissoMapper mapper;

	@Autowired
	private DomiciliazioneTxService domiciliazioneTxService;

	@Autowired
	private CryptService cryptService;

	@Autowired
	private DomiciliazioneFissoRepository domiciliazioneFissoRepository;

	@Autowired
	private ProfiloPspSddMandatoRepository profiloPspSddMandatoRepository;

	public AttivazioneResponseDto attivaDomiciliazione(AttivazioneRequestDto request, ContestoRichiesta contesto) {
		Long idRichiesta = null;
		try {
			RichiestaAttDomFisso processo = processoTraceService.inserimentoProcesso(contesto);
			idRichiesta = processo.getIdRichiesta();
			ContestoRichiesta ctx = contesto.conTid(processo.getTidRichiestaAtt());
			logger.info("Attivazione dom fisso - ricezione richiesta - ID_RICHIESTA = {}", idRichiesta);

			RichiestaAttivazioneDomiciliazioneFisso richiesta = request.getRichiestaAttivazioneDomiciliazioneFisso();

			Optional<String> esito = validator.controlliFormali(richiesta);
			logger.info("Attivazione dom fisso - controlli formali - ID_RICHIESTA = {}, esito = {}", idRichiesta,
					esito.orElse(ESITO_OK));
			if (esito.isPresent()) {
				koFunzionale(idRichiesta, esito.get(), "controlli formali");
			}

			normalizza(richiesta);
			boolean lineaAttiva = "Y".equals(richiesta.getUtenzaFissa().getFlagLineaAttiva());
			boolean conMandato = !"03".equals(richiesta.getTipoOperazione());

			String codiceKo = controlliBusiness(richiesta, lineaAttiva);
			String ibanInChiaro = null;
			if (codiceKo == null && "01".equals(richiesta.getTipoOperazione())) {
				ibanInChiaro = decifraIban(richiesta.getDatiContoCorrenteAttivazioneDomiciliazione().getIban());
				if (ibanInChiaro == null) {
					codiceKo = "011";
				}
			}
			logger.info("Attivazione dom fisso - controlli business - ID_RICHIESTA = {}, esito = {}", idRichiesta,
					codiceKo != null ? codiceKo : ESITO_OK);
			if (codiceKo != null) {
				koFunzionale(idRichiesta, codiceKo, "controlli business");
			}

			codiceKo = verificheApplicative(richiesta, lineaAttiva);
			logger.info("Attivazione dom fisso - verifiche applicative - ID_RICHIESTA = {}, esito = {}", idRichiesta,
					codiceKo != null ? codiceKo : ESITO_OK);
			if (codiceKo != null) {
				koFunzionale(idRichiesta, codiceKo, "verifiche applicative");
			}

			RichiestaAttDomFisso dettaglio = mapper.dettaglio(processo, richiesta, ibanInChiaro);
			RichiestaAttDomFisso salvato = processoTraceService.salvaProcesso(dettaglio);
			logger.info("Attivazione dom fisso - cifratura e salvataggio dettaglio - ID_RICHIESTA = {}", idRichiesta);

			DatiLineaFisso linea = lineaAttiva ? mapper.datiLinea(salvato) : null;
			String flagPersFisica = mapper.flagPersFisica(richiesta);
			AttivazioneMandatoRequestDto mandatoRequest = conMandato
					? mapper.richiestaMandato(richiesta, flagPersFisica)
					: null;

			Long idDomiciliazione = domiciliazioneTxService
					.registra(new DatiRegistrazione(ctx, richiesta, linea, idRichiesta, flagPersFisica, mandatoRequest));
			logger.info("Attivazione dom fisso - persistenza - ID_RICHIESTA = {}, ID_DOMICILIAZIONE = {}", idRichiesta,
					idDomiciliazione);

			AttivazioneResponseDto response = new AttivazioneResponseDto();
			response.setTipoOperazione(richiesta.getTipoOperazione());
			response.setEsito(ESITO_OK);
			response.setSubsys(SUBSYS_RISPOSTA);
			response.setDataOraRisposta(LocalDateTime.now(ITALY_ZONE_ID).format(FORMAT_DATA_ORA));
			logger.info("Attivazione dom fisso - conclusione - ID_RICHIESTA = {}, esito = {}", idRichiesta, ESITO_OK);
			return response;
		} catch (BRExceptionReturn | ISEExceptionReturn e) {
			throw e;
		} catch (DataAccessException e) {
			logger.error("Errore nell'esecuzione dell'operazione sul database di GUP - ID_RICHIESTA = {}, businessID = {}: ",
					idRichiesta, contesto.businessId(), e);
			throw ISEExceptionReturn.CODICE_674();
		} catch (Exception e) {
			logger.error("Errore generico durante l'attivazione dom fisso - ID_RICHIESTA = {}, businessID = {}: ",
					idRichiesta, contesto.businessId(), e);
			throw ISEExceptionReturn.CODICE_674();
		}
	}

	private String controlliBusiness(RichiestaAttivazioneDomiciliazioneFisso r, boolean lineaAttiva) {
		if (domiciliazioneFissoRepository.existsByBid(r.getBid())) {
			return "017";
		}
		if (lineaAttiva && domiciliazioneFissoRepository.countDomiciliazioniInCorsoSuLinea(
				r.getUtenzaFissa().getPrefisso(), r.getUtenzaFissa().getNumero(), STATI_ESCLUSI_LINEA) > 0) {
			return "021";
		}
		return null;
	}

	private String verificheApplicative(RichiestaAttivazioneDomiciliazioneFisso r, boolean lineaAttiva) {
		String cfLinea = AttivazioneDomFissoMapper.cfOPiva(r.getDatiIntestatarioLinea());
		logger.info("Attivazione dom fisso - verifica mandato per cfOPiva = {}", MaskingUtil.mask(cfLinea));
		if ("03".equals(r.getTipoOperazione())) {
			if (!lineaAttiva) {
				return null;
			}
			List<ProfiloPspSddMandato> profili = profiloPspSddMandatoRepository.findByCfOPiva(cfLinea);
			boolean mandatoUtilizzabile = profili.stream().anyMatch(p -> !"3".equals(p.getStatoProfilo()));
			return mandatoUtilizzabile ? null : "013";
		}
		boolean mandatoGiaPresente = profiloPspSddMandatoRepository.findByCfOPiva(cfLinea).stream()
				.anyMatch(p -> !"3".equals(p.getStatoProfilo()));
		return mandatoGiaPresente ? "014" : null;
	}

	/**
	 * Decifra l'IBAN ricevuto con la chiave condivisa con CCC; restituisce null se la decifratura fallisce.
	 */
	private String decifraIban(String ibanCifrato) {
		try {
			DecryptPojo dp = new DecryptPojo();
			dp.setChiperText(ibanCifrato);
			dp.setKeyName("CCC");
			dp.setIv(null);
			String chiaro = cryptService.decrypt(dp);
			return chiaro == null || chiaro.isBlank() ? null : chiaro;
		} catch (Exception e) {
			logger.error("Decifratura IBAN in ingresso non riuscita: {}", e.getClass().getSimpleName());
			return null;
		}
	}

	private void normalizza(RichiestaAttivazioneDomiciliazioneFisso r) {
		tronca(r.getDatiIntestatarioLinea());
		tronca(r.getDatiIntestatarioMandato());
	}

	private void tronca(DatiIntestatario d) {
		if (d == null || d.getPersFisica() == null) {
			return;
		}
		d.getPersFisica().setNome(tronca(d.getPersFisica().getNome(), MAX_LEN_NOME));
		d.getPersFisica().setCognome(tronca(d.getPersFisica().getCognome(), MAX_LEN_COGNOME));
	}

	private String tronca(String valore, int max) {
		return valore != null && valore.length() > max ? valore.substring(0, max) : valore;
	}

	/**
	 * Salva l'esito KO sul processo (transazione propria, nessuna transazione di business aperta) e solleva
	 * l'eccezione con code/message SIF.
	 */
	private void koFunzionale(Long idRichiesta, String esito, String fase) {
		processoTraceService.aggiornaProcesso(idRichiesta, esito);
		logger.error("Attivazione dom fisso - anomalia - ID_RICHIESTA = {}, fase = {}, esito = {}", idRichiesta, fase,
				esito);
		switch (esito) {
		case "002" -> throw BRExceptionReturn.CODE_101();
		case "015" -> throw BRExceptionReturn.CODE_100();
		case "019" -> throw ISEExceptionReturn.CODICE_674();
		default -> throw BRExceptionReturn.CODE_103();
		}
	}

}
