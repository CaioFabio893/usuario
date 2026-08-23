package com.caiofabio.usuario.infrastructure.repository;

import com.caio.aprendendospring.infrastructure.entity.Telefone;
import com.caio.aprendendospring.infrastructure.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelefoneRepository extends JpaRepository<Telefone,Long> {
}
