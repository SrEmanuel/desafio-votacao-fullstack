import { Routes } from '@angular/router';
import { PautaComponent } from './pages/pauta/pauta.component';
import { SessaoComponent } from './pages/sessao/sessao.component';
import { SessaoVotacaoComponent } from './pages/sessao-votacao/sessao-votacao.component';

export const routes: Routes = [
    { 
        path: '', 
        component: PautaComponent 
    },
    { 
        path: 'pauta/:uuid/sessoes', 
        component: SessaoComponent 
    },
    { 
        path: 'sessao/:uuid', 
        component: SessaoVotacaoComponent 
    }
];