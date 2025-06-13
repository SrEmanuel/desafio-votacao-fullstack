import { Component } from '@angular/core';
import { PautaResponse } from '../../core/dtos/pauta.dto';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { PautaService } from '../../core/services/pauta.service';
import { CommonModule, Location } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { TableModule } from 'primeng/table';
import { ActivatedRoute, Router } from '@angular/router';
import { CardComponentComponent } from "../../components/card-component/card-component.component";
import { AddCardComponentComponent } from "../../components/add-card-component/add-card-component.component";
import { SessaoService } from '../../core/services/sessao.service';
import { SessaoRequest, SessaoResponse } from '../../core/dtos/sessao.dto';

@Component({
  selector: 'app-sessao',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    DialogModule,
    ButtonModule,
    TableModule,
    InputTextModule,
    CardComponentComponent,
    AddCardComponentComponent
],
  templateUrl: './sessao.component.html',
  styleUrl: './sessao.component.css'
})
export class SessaoComponent {
  
  protected displayDialog: boolean = false;
  protected sessaoForm: FormGroup;
  protected sessoes: SessaoResponse[] = [];
  protected uuid?: string | null;
  
  constructor(private fb: FormBuilder, private sessaoService: SessaoService, private messageService: MessageService, private route:ActivatedRoute, private location:Location) {
    this.sessaoForm = this.fb.group({
      duracaoEmMinutos: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.obterSessoes();
  }

  obterSessoes(){
    this.route.paramMap.subscribe(params => {
      this.uuid = params.get('uuid');
      if (this.uuid) {
        this.sessaoService.obterSessoesPorPauta(this.uuid).subscribe({
          next: (sessoes: SessaoResponse[]) => {
            this.sessoes = sessoes;
          },
          error: (err) => {
            console.error('Erro ao remover pauta:', err);
            this.messageService.add({ 
                severity: 'error', 
                summary: 'Erro ao deletar!', 
                detail: err.error.mensagem 
            });

          }
        });
      }
    });
  }

  goBack(){
    this.location.back();
  }

  showDialog() {
    this.sessaoForm.reset();
    this.displayDialog = true;
  }
  
  hideDialog() {
    this.displayDialog = false;
  }

  // deletePauta(pauta: PautaResponse) {
  //   this.pautaService.removerPauta(pauta.uuid).subscribe({
  //     next: () => {
  //       this.messageService.add({ 
  //           severity: 'success', 
  //           summary: 'Sucesso!', 
  //           detail: 'Operação realizada com sucesso.' 
  //       });

  //       this.obterPautas();
  //     },
  //     error: (err) => {
  //       console.error('Erro ao remover pauta:', err);
  //       this.messageService.add({ 
  //           severity: 'error', 
  //           summary: 'Erro ao deletar!', 
  //           detail: err.error.mensagem 
  //       });

  //     }
  //   });

  // }

  saveSessao() {
    if (this.sessaoForm.valid && this.uuid) {
      const novaSessao = this.sessaoForm.value as SessaoRequest;
      novaSessao.pautaUuid = this.uuid;
      console.log(novaSessao);
      this.sessaoService.abrirSessao(novaSessao).subscribe({
      next: () => {
        this.messageService.add({ 
            severity: 'success', 
            summary: 'Sucesso!', 
            detail: 'Operação realizada com sucesso.' 
        });

        this.obterSessoes();
      },
      error: (err) => {
        this.messageService.add({ 
            severity: 'error', 
            summary: 'Erro ao criar pauta!!', 
            detail: err.error.mensagem 
        });

      }
    });

      this.hideDialog();
    } else {
      this.sessaoForm.markAllAsTouched();
    }
  }
}
