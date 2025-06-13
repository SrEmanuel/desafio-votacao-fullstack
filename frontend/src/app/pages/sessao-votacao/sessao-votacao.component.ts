import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { CommonModule, Location } from '@angular/common';
import { SessaoResponse, ResultadoResponse } from '../../core/dtos/sessao.dto';
import { SessaoService } from '../../core/services/sessao.service';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';
import { VotoRequest } from '../../core/dtos/voto.dto';

@Component({
  selector: 'app-sessao-votacao',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ButtonModule,
    InputTextModule,
    CardModule,
    TagModule,
  ],
  templateUrl: './sessao-votacao.component.html',
  styleUrls: ['./sessao-votacao.component.css']
})
export class SessaoVotacaoComponent implements OnInit, OnDestroy {

  protected sessao: SessaoResponse | undefined;
  protected resultado: ResultadoResponse | undefined;
  protected uuid: string | null = null;
  
  protected isSessaoFechada: boolean = false;
  protected tempoRestante: string = '';
  private timer: any;

  protected votoForm: FormGroup;

  constructor(
    private form: FormBuilder,
    private sessaoService: SessaoService,
    private messageService: MessageService,
    private route: ActivatedRoute,
    private location: Location
  ) {
    this.votoForm = this.form.group({
      cpf: ['', [Validators.required, Validators.pattern(/^\d{11}$/)]]
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.uuid = params.get('uuid');
      if (this.uuid) {
        this.obterSessao(this.uuid);
      }
    });
  }

  ngOnDestroy(): void {
    if (this.timer) {
      clearInterval(this.timer);
    }
  }

  obterSessao(uuid: string): void {
    this.sessaoService.obterSessao(uuid).subscribe({
      next: (sessao: SessaoResponse) => {
        this.sessao = sessao;
        this.verificarStatusSessao();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: 'Não foi possível carregar os dados da sessão.'
        });
        console.error(err);
      }
    });
  }

  verificarStatusSessao(): void {
    if (!this.sessao) return;

    const dataFechamento = new Date(this.sessao.dataFechamento);
    const agora = new Date();

    if (dataFechamento > agora) {
      this.isSessaoFechada = false;
      this.iniciarContador(dataFechamento);
    } else {
      this.isSessaoFechada = true;
      this.tempoRestante = 'Sessão encerrada';
      if (this.uuid) {
          this.obterResultado(this.uuid);
      }
    }
  }

  iniciarContador(dataFechamento: Date): void {
    this.timer = setInterval(() => {
      const agora = new Date().getTime();
      const distancia = dataFechamento.getTime() - agora;

      if (distancia < 0) {
        clearInterval(this.timer);
        this.tempoRestante = 'Sessão encerrada';
        this.isSessaoFechada = true;
        if (this.uuid) {
            this.obterResultado(this.uuid);
        }
        return;
      }

      const dias = Math.floor(distancia / (1000 * 60 * 60 * 24));
      const horas = Math.floor((distancia % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
      const minutos = Math.floor((distancia % (1000 * 60 * 60)) / (1000 * 60));
      const segundos = Math.floor((distancia % (1000 * 60)) / 1000);

      if (dias > 0) {
        this.tempoRestante = `${dias}d ${horas.toString().padStart(2, '0')}:${minutos.toString().padStart(2, '0')}:${segundos.toString().padStart(2, '0')}`;
      } else {
        this.tempoRestante = `${horas.toString().padStart(2, '0')}:${minutos.toString().padStart(2, '0')}:${segundos.toString().padStart(2, '0')}`;
      }
    }, 1000);
  }

  registrarVoto(votoValor: 'SIM' | 'NAO'): void {
    if (this.votoForm.invalid) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Atenção',
        detail: 'Por favor, insira um CPF válido..'
      });
      this.votoForm.markAllAsTouched();
      return;
    }

    if (!this.uuid) return;

    const voto: VotoRequest = {
      cpfAssociado: this.votoForm.value.cpf,
      votoSim: votoValor == "SIM" ? true : false
    };

    console.log("voto: ", voto);

    this.sessaoService.registrarVoto(this.uuid, voto).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso',
          detail: 'Voto registrado com sucesso!'
        });
        this.votoForm.reset();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro ao votar',
          detail: err.error.mensagem || 'Não foi possível registrar o seu voto.'
        });
      }
    });
  }
  
  obterResultado(uuid: string): void {
    this.sessaoService.obterResultado(uuid).subscribe({
        next: (resultado: ResultadoResponse) => {
            this.resultado = resultado;
        },
        error: (err) => {
            this.messageService.add({
                severity: 'error',
                summary: 'Erro',
                detail: 'Não foi possível carregar os resultados.'
            });
        }
    });
  }

  goBack(): void {
    this.location.back();
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleString('pt-BR', {
        day: '2-digit', month: '2-digit', year: 'numeric',
        hour: '2-digit', minute: '2-digit'
    });
  }
}
