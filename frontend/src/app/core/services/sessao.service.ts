import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SessaoRequest, SessaoResponse, ResultadoResponse } from '../dtos/sessao.dto';
import { VotoRequest } from '../dtos/voto.dto';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SessaoService {
  private readonly apiHost = environment.apiHost;
  private readonly baseUrl = '/api/v1/sessoes';
  private readonly pautaUrl = '/api/v1/pautas';


  constructor(private http: HttpClient) { }

  abrirSessao(sessao: SessaoRequest): Observable<SessaoResponse> {
    return this.http.post<SessaoResponse>(`${this.apiHost}${this.baseUrl}`, sessao);
  }

  obterSessoesPorPauta(pautaUuid: string): Observable<SessaoResponse[]> {
    return this.http.get<SessaoResponse[]>(`${this.apiHost}${this.pautaUrl}/${pautaUuid}/sessoes`);
  }

  obterSessao(sessaoUuid: string): Observable<SessaoResponse> {
    return this.http.get<SessaoResponse>(`${this.apiHost}${this.baseUrl}/${sessaoUuid}`);
  }

  registrarVoto(sessaoUuid: string, voto: VotoRequest): Observable<void> {
    return this.http.post<void>(`${this.apiHost}${this.baseUrl}/${sessaoUuid}/votos`, voto);
  }

  obterResultado(sessaoUuid: string): Observable<ResultadoResponse> {
    return this.http.get<ResultadoResponse>(`${this.apiHost}${this.baseUrl}/${sessaoUuid}/resultado`);
  }

  removerSessao(sessaoUuid: string): Observable<void> {
    return this.http.delete<void>(`${this.apiHost}${this.baseUrl}/${sessaoUuid}`);
  }
}