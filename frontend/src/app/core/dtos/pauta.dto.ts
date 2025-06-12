export interface PautaRequest {
  titulo: string;
  descricao?: string;
}

export interface PautaResponse {
  uuid: string;
  titulo: string;
  descricao?: string;
}