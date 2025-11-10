export interface Beneficio {
  id?: number;
  nome: string;
  descricao?: string;
  valor: number;
  ativo?: boolean;
  version?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface TransferenciaRequest {
  beneficioOrigemId: number;
  beneficioDestinoId: number;
  valor: number;
}

export interface TransferenciaResponse {
  sucesso: boolean;
  mensagem: string;
  transacao: {
    origemId: number;
    destinoId: number;
    valor: number;
    dataHora: string;
  };
  origem: {
    id: number;
    nome: string;
    valorAnterior: number;
    valorAtual: number;
  };
  destino: {
    id: number;
    nome: string;
    valorAnterior: number;
    valorAtual: number;
  };
}
