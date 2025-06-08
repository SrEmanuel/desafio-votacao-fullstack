package dev.emanuelm.votacao.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessao_votacao")
public class SessaoVotacao extends BaseDomain {

  @ManyToOne
  @JoinColumn(name = "pauta_id")
  private Pauta pauta;

  @Column(name = "data_abertura", nullable = false)
  private LocalDateTime dataAbertura;

  @Column(name = "data_fechamento", nullable = false)
  private LocalDateTime dataFechamento;

  public Pauta getPauta() {
    return pauta;
  }

  public void setPauta(Pauta pauta) {
    this.pauta = pauta;
  }

  public LocalDateTime getDataAbertura() {
    return dataAbertura;
  }

  public void setDataAbertura(LocalDateTime dataAbertura) {
    this.dataAbertura = dataAbertura;
  }

  public LocalDateTime getDataFechamento() {
    return dataFechamento;
  }

  public void setDataFechamento(LocalDateTime dataFechamento) {
    this.dataFechamento = dataFechamento;
  }
}
