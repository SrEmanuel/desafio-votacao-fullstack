import { PautaResponse } from "./pauta.dto";

export interface SessaoRequest {
  pautaUuid: string;
  duracaoEmMinutos?: number;
}

export interface SessaoResponse {
  uuid: string;
  pautaUuid: string;
  dataAbertura: string;
  dataFechamento: string;
}

export interface ResultadoResponse {
  sessaoUuid: string;
  dataAbertura: string;
  dataFechamento: string;
  pauta: PautaResponse;
  resultado: {
    total: number;
    votosSim: number;
    votosNao: number;
    aprovado: boolean;
  };
}