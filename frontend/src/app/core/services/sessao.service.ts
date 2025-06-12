import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SessaoRequest, SessaoResponse, ResultadoResponse } from '../dtos/sessao.dto';
import { VotoRequest } from '../dtos/voto.dto';

@Injectable({
  providedIn: 'root'
})
export class SessaoService {
  private readonly baseUrl = '/api/v1/sessoes';

  constructor(private http: HttpClient) { }

  abrirSessao(sessao: SessaoRequest): Observable<SessaoResponse> {
    return this.http.post<SessaoResponse>(this.baseUrl, sessao);
  }

  obterSessoesPorPauta(pautaUuid: string): Observable<SessaoResponse[]> {
    return this.http.get<SessaoResponse[]>(`/api/v1/pautas/${pautaUuid}/sessoes`);
  }

  registrarVoto(sessaoUuid: string, voto: VotoRequest): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/${sessaoUuid}/votos`, voto);
  }

  obterResultado(sessaoUuid: string): Observable<ResultadoResponse> {
    return this.http.get<ResultadoResponse>(`${this.baseUrl}/${sessaoUuid}/resultado`);
  }

  removerSessao(sessaoUuid: string): Observable<void> {
    return this.http.delete<void>(`/api/v1/sessions/${sessaoUuid}`);
  }
}