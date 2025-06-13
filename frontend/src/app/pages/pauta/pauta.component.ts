import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';

// PrimeNG Modules
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { InputTextModule } from 'primeng/inputtext';
import { CommonModule } from '@angular/common';
import { PautaService } from '../../core/services/pauta.service';
import { PautaResponse } from '../../core/dtos/pauta.dto';
import { MessageService } from 'primeng/api';
import { Router } from '@angular/router';

@Component({
  selector: 'app-pautas',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    DialogModule,
    ButtonModule,
    TableModule,
    InputTextModule
  ],
  templateUrl: './pauta.component.html',
  styleUrls: ['./pauta.component.css']
})
export class PautaComponent implements OnInit {

  protected displayCreateDialog: boolean = false;
  protected pautaForm: FormGroup;


  protected pautas: PautaResponse[] = [];
  
  constructor(private fb: FormBuilder, private pautaService: PautaService, private messageService: MessageService, private router: Router) {
    this.pautaForm = this.fb.group({
      titulo: ['', Validators.required],
      descricao: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.obterPautas();
  }

  obterPautas(){
    this.pautaService.obterPautas().subscribe(response => {
      console.log(response);
      this.pautas = response;
    })
  }


  showCreateDialog() {
    this.pautaForm.reset();
    this.displayCreateDialog = true;
  }
  
  hideCreateDialog() {
    this.displayCreateDialog = false;
  }

  deletePauta(pauta: PautaResponse) {
    this.pautaService.removerPauta(pauta.uuid).subscribe({
      next: () => {
        this.messageService.add({ 
            severity: 'success', 
            summary: 'Sucesso!', 
            detail: 'Operação realizada com sucesso.' 
        });

        this.obterPautas();
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

  abrirPauta(pauta: PautaResponse) {
    this.router.navigate(['/pauta', pauta.uuid, 'sessoes']);
  }


  salvarPauta() {
    if (this.pautaForm.valid) {
      const novaPauta = this.pautaForm.value;
      
      console.log(novaPauta);
      this.pautaService.criarPauta({titulo: novaPauta.titulo, descricao: novaPauta.descricao}).subscribe({
      next: () => {
        this.messageService.add({ 
            severity: 'success', 
            summary: 'Sucesso!', 
            detail: 'Operação realizada com sucesso.' 
        });

        this.obterPautas();
      },
      error: (err) => {
        this.messageService.add({ 
            severity: 'error', 
            summary: 'Erro ao criar pauta!!', 
            detail: err.error.mensagem 
        });

      }
    });

      this.hideCreateDialog();
    } else {
      this.pautaForm.markAllAsTouched();
    }
  }
}