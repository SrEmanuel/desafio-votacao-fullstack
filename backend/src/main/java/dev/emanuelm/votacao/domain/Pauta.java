package dev.emanuelm.votacao.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pauta")
public class Pauta extends BaseDomain {

  @Column(name = "titulo")
  private String titulo;

  @Column(name = "descricao")
  private String descricao;

  public Pauta(){
  }

  public Pauta(String titulo, String descricao) {
    this.titulo = titulo;
    this.descricao = descricao;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

}
