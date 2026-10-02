package it.tim.bl.gestione.domfisso.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.tim.bl.gestione.domfisso.entity.ProfiloPspSddMandato;

public interface ProfiloPspSddMandatoRepository extends JpaRepository<ProfiloPspSddMandato, String> {

	List<ProfiloPspSddMandato> findByCfOPiva(String cfOPiva);

}
