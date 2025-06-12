import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PautaRequest, PautaResponse } from '../dtos/pauta.dto';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PautaService {
  private readonly apiHost = environment.apiHost;
  private readonly baseUrl = '/api/v1/pautas';

  constructor(private http: HttpClient) { }

  criarPauta(pauta: PautaRequest): Observable<PautaResponse> {
    return this.http.post<PautaResponse>(`${this.apiHost}${this.baseUrl}`, pauta);
  }

  obterPautas(): Observable<PautaResponse[]> {
    return this.http.get<PautaResponse[]>(`${this.apiHost}${this.baseUrl}`);
  }

  removerPauta(pautaUuid: string): Observable<void> {
    return this.http.delete<void>(`${this.apiHost}${this.baseUrl}/${pautaUuid}`);
  }
}