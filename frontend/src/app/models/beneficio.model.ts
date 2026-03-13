export interface Beneficio {
  id?: number;
  nome: string;
  descricao?: string;
  valor: number;
  ativo?: boolean;
  version?: number;
}

export interface TransferRequest {
  fromId: number;
  toId: number;
  amount: number;
}

export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  details?: string[];
}
