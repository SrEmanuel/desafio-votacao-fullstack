import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { SessaoResponse } from '../../core/dtos/sessao.dto';
import { Router } from '@angular/router';
import { SessaoService } from '../../core/services/sessao.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'card-component',
  standalone: true, // Adicione standalone: true
  imports: [CommonModule, ButtonModule], // Adicione CommonModule
  templateUrl: './card-component.component.html',
  styleUrl: './card-component.component.css'
})
export class CardComponentComponent {

  @Input() sessao: SessaoResponse | undefined;

  constructor(private router: Router, private sessaoService: SessaoService, private messageService: MessageService){
  }

  get formattedDataAbertura(): string {
    if (!this.sessao) return '';
    const date = new Date(this.sessao.dataAbertura);
    return date.toLocaleString('pt-BR', { 
      day: '2-digit', 
      month: '2-digit', 
      year: 'numeric', 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  }

  get formattedDataFechamento(): string {
    if (!this.sessao) return '';
    const date = new Date(this.sessao.dataFechamento);
    return date.toLocaleString('pt-BR', { 
      day: '2-digit', 
      month: '2-digit', 
      year: 'numeric', 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  }

  goToDetails(){
    this.router.navigate(['/sessao', this.sessao?.uuid]);
  }

  
  get isClosed(): boolean {
    if (!this.sessao?.dataFechamento) {
      return false;
    }
    return new Date(this.sessao.dataFechamento) < new Date();
  }

  delete() {
    if (this.sessao?.uuid) {
      this.sessaoService.removerSessao(this.sessao.uuid).subscribe({
          next: () => {
            this.messageService.add({ 
              severity: 'success', 
              summary: 'Sucesso!', 
              detail: 'Sessão removida com sucesso!' 
            });
            window.location.reload();
          },
          error: (err) => {
            console.error('Erro ao remover sessão:', err);
            this.messageService.add({ 
                severity: 'error', 
                summary: 'Erro ao cancelar sessão!!', 
                detail: err.error.mensagem 
            });
          }
      });
    }
  }
}