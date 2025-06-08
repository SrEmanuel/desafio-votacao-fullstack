package dev.emanuelm.votacao.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "voto",
    indexes = {@Index(name = "cpf_associado_idx", columnList = "cpf_associado")},
    uniqueConstraints = {@UniqueConstraint(columnNames = {"cpf_associado", "sessao_votacao_id"})})
public class Voto extends BaseDomain {

  @ManyToOne
  @JoinColumn(name = "sessaovotacao_id", nullable = false)
  private SessaoVotacao sessaoVotacao;

  @Column(name = "cpf_associado", nullable = false)
  private String cpfAssociado;

  @Column(name = "voto_sim", nullable = false)
  private Boolean votoSim;

  public SessaoVotacao getSessaoVotacao() {
    return sessaoVotacao;
  }

  public void setSessaoVotacao(SessaoVotacao sessaoVotacao) {
    this.sessaoVotacao = sessaoVotacao;
  }

  public String getCpfAssociado() {
    return cpfAssociado;
  }

  public void setCpfAssociado(String cpfAssociado) {
    this.cpfAssociado = cpfAssociado;
  }

  public Boolean getVotoSim() {
    return votoSim;
  }

  public void setVotoSim(Boolean votoSim) {
    this.votoSim = votoSim;
  }
}
