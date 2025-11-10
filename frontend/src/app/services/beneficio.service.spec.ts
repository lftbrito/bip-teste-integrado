import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BeneficioService } from './beneficio.service';
import { Beneficio, TransferenciaRequest, TransferenciaResponse } from '../models/beneficio.model';

describe('BeneficioService', () => {
  let service: BeneficioService;
  let httpMock: HttpTestingController;
  const API_URL = 'http://localhost:8080/api/beneficios';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BeneficioService]
    });
    service = TestBed.inject(BeneficioService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('listarBeneficios', () => {
    it('should return an array of beneficios', () => {
      const mockBeneficios: Beneficio[] = [
        { id: 1, nome: 'Benefício 1', descricao: 'Desc 1', valor: 1000, ativo: true },
        { id: 2, nome: 'Benefício 2', descricao: 'Desc 2', valor: 2000, ativo: true }
      ];

      service.listarBeneficios().subscribe(beneficios => {
        expect(beneficios.length).toBe(2);
        expect(beneficios).toEqual(mockBeneficios);
      });

      const req = httpMock.expectOne(API_URL);
      expect(req.request.method).toBe('GET');
      req.flush(mockBeneficios);
    });

    it('should handle error when listing beneficios fails', () => {
      const errorMessage = 'Erro ao listar benefícios';

      service.listarBeneficios().subscribe({
        next: () => fail('should have failed with error'),
        error: (error) => {
          expect(error.message).toContain('500');
        }
      });

      const req = httpMock.expectOne(API_URL);
      req.flush({ message: errorMessage }, { status: 500, statusText: 'Server Error' });
    });
  });

  describe('listarBeneficiosAtivos', () => {
    it('should return only active beneficios', () => {
      const mockBeneficios: Beneficio[] = [
        { id: 1, nome: 'Benefício 1', descricao: 'Desc 1', valor: 1000, ativo: true },
        { id: 2, nome: 'Benefício 2', descricao: 'Desc 2', valor: 2000, ativo: true }
      ];

      service.listarBeneficiosAtivos().subscribe(beneficios => {
        expect(beneficios.length).toBe(2);
        expect(beneficios.every(b => b.ativo)).toBeTruthy();
      });

      const req = httpMock.expectOne(`${API_URL}/ativos`);
      expect(req.request.method).toBe('GET');
      req.flush(mockBeneficios);
    });
  });

  describe('buscarPorId', () => {
    it('should return a beneficio by id', () => {
      const mockBeneficio: Beneficio = {
        id: 1,
        nome: 'Benefício 1',
        descricao: 'Desc 1',
        valor: 1000,
        ativo: true
      };

      service.buscarPorId(1).subscribe(beneficio => {
        expect(beneficio).toEqual(mockBeneficio);
        expect(beneficio.id).toBe(1);
      });

      const req = httpMock.expectOne(`${API_URL}/1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockBeneficio);
    });

    it('should handle error when beneficio not found', () => {
      service.buscarPorId(999).subscribe({
        next: () => fail('should have failed with 404'),
        error: (error) => {
          expect(error.message).toContain('404');
        }
      });

      const req = httpMock.expectOne(`${API_URL}/999`);
      req.flush({ message: 'Benefício não encontrado' }, { status: 404, statusText: 'Not Found' });
    });
  });

  describe('criar', () => {
    it('should create a new beneficio', () => {
      const newBeneficio: Beneficio = {
        nome: 'Novo Benefício',
        descricao: 'Nova Descrição',
        valor: 1500,
        ativo: true
      };

      const createdBeneficio: Beneficio = { ...newBeneficio, id: 1 };

      service.criar(newBeneficio).subscribe(beneficio => {
        expect(beneficio).toEqual(createdBeneficio);
        expect(beneficio.id).toBe(1);
      });

      const req = httpMock.expectOne(API_URL);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(newBeneficio);
      req.flush(createdBeneficio);
    });

    it('should handle validation error when creating beneficio', () => {
      const invalidBeneficio: Beneficio = {
        nome: '',
        descricao: '',
        valor: -100,
        ativo: true
      };

      service.criar(invalidBeneficio).subscribe({
        next: () => fail('should have failed with validation error'),
        error: (error) => {
          expect(error.message).toBeTruthy();
        }
      });

      const req = httpMock.expectOne(API_URL);
      req.flush({ message: 'Dados inválidos' }, { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('atualizar', () => {
    it('should update an existing beneficio', () => {
      const updatedBeneficio: Beneficio = {
        id: 1,
        nome: 'Benefício Atualizado',
        descricao: 'Descrição Atualizada',
        valor: 2000,
        ativo: true
      };

      service.atualizar(1, updatedBeneficio).subscribe(beneficio => {
        expect(beneficio).toEqual(updatedBeneficio);
        expect(beneficio.nome).toBe('Benefício Atualizado');
      });

      const req = httpMock.expectOne(`${API_URL}/1`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updatedBeneficio);
      req.flush(updatedBeneficio);
    });

    it('should handle error when updating non-existent beneficio', () => {
      const beneficio: Beneficio = {
        id: 999,
        nome: 'Benefício',
        descricao: 'Desc',
        valor: 1000,
        ativo: true
      };

      service.atualizar(999, beneficio).subscribe({
        next: () => fail('should have failed with 404'),
        error: (error) => {
          expect(error.message).toContain('404');
        }
      });

      const req = httpMock.expectOne(`${API_URL}/999`);
      req.flush({ message: 'Benefício não encontrado' }, { status: 404, statusText: 'Not Found' });
    });
  });

  describe('deletar', () => {
    it('should delete a beneficio', () => {
      service.deletar(1).subscribe(response => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne(`${API_URL}/1`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should handle error when deleting non-existent beneficio', () => {
      service.deletar(999).subscribe({
        next: () => fail('should have failed with 404'),
        error: (error) => {
          expect(error.message).toContain('404');
        }
      });

      const req = httpMock.expectOne(`${API_URL}/999`);
      req.flush({ message: 'Benefício não encontrado' }, { status: 404, statusText: 'Not Found' });
    });
  });

  describe('transferir', () => {
    it('should transfer value between beneficios successfully', () => {
      const transferRequest: TransferenciaRequest = {
        beneficioOrigemId: 1,
        beneficioDestinoId: 2,
        valor: 500
      };

      const transferResponse: TransferenciaResponse = {
        sucesso: true,
        mensagem: 'Transferência realizada com sucesso',
        transacao: {
          origemId: 1,
          destinoId: 2,
          valor: 500,
          dataHora: '2025-11-10T14:30:00'
        },
        origem: {
          id: 1,
          nome: 'Benefício Origem',
          valorAnterior: 1000,
          valorAtual: 500
        },
        destino: {
          id: 2,
          nome: 'Benefício Destino',
          valorAnterior: 2000,
          valorAtual: 2500
        }
      };

      service.transferir(transferRequest).subscribe(response => {
        expect(response).toEqual(transferResponse);
        expect(response.transacao.valor).toBe(500);
        expect(response.mensagem).toContain('sucesso');
      });

      const req = httpMock.expectOne(`${API_URL}/transferir`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(transferRequest);
      req.flush(transferResponse);
    });

    it('should handle error when insufficient balance', () => {
      const transferRequest: TransferenciaRequest = {
        beneficioOrigemId: 1,
        beneficioDestinoId: 2,
        valor: 10000
      };

      service.transferir(transferRequest).subscribe({
        next: () => fail('should have failed with insufficient balance error'),
        error: (error) => {
          expect(error.message).toBeTruthy();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/transferir`);
      req.flush({ message: 'Saldo insuficiente' }, { status: 400, statusText: 'Bad Request' });
    });

    it('should handle error when same origin and destination', () => {
      const transferRequest: TransferenciaRequest = {
        beneficioOrigemId: 1,
        beneficioDestinoId: 1,
        valor: 500
      };

      service.transferir(transferRequest).subscribe({
        next: () => fail('should have failed with same beneficio error'),
        error: (error) => {
          expect(error.message).toBeTruthy();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/transferir`);
      req.flush({ message: 'Origem e destino não podem ser iguais' }, { status: 400, statusText: 'Bad Request' });
    });

    it('should handle error when beneficio not found', () => {
      const transferRequest: TransferenciaRequest = {
        beneficioOrigemId: 999,
        beneficioDestinoId: 2,
        valor: 500
      };

      service.transferir(transferRequest).subscribe({
        next: () => fail('should have failed with not found error'),
        error: (error) => {
          expect(error.message).toContain('404');
        }
      });

      const req = httpMock.expectOne(`${API_URL}/transferir`);
      req.flush({ message: 'Benefício não encontrado' }, { status: 404, statusText: 'Not Found' });
    });

    it('should handle error when negative transfer value', () => {
      const transferRequest: TransferenciaRequest = {
        beneficioOrigemId: 1,
        beneficioDestinoId: 2,
        valor: -100
      };

      service.transferir(transferRequest).subscribe({
        next: () => fail('should have failed with negative value error'),
        error: (error) => {
          expect(error.message).toBeTruthy();
        }
      });

      const req = httpMock.expectOne(`${API_URL}/transferir`);
      req.flush({ message: 'Valor deve ser positivo' }, { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('error handling', () => {
    it('should handle client-side error', () => {
      const errorEvent = new ErrorEvent('Network error', {
        message: 'Erro de rede'
      });

      service.listarBeneficios().subscribe({
        next: () => fail('should have failed with network error'),
        error: (error) => {
          expect(error.message).toContain('Erro');
        }
      });

      const req = httpMock.expectOne(API_URL);
      req.error(errorEvent);
    });

    it('should handle server error with custom message', () => {
      const errorMessage = 'Erro customizado do servidor';

      service.listarBeneficios().subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          // O handleError agora adiciona o código de status
          expect(error.message).toBe(`${errorMessage} (500)`);
        }
      });

      const req = httpMock.expectOne(API_URL);
      req.flush({ message: errorMessage }, { status: 500, statusText: 'Server Error' });
    });
  });
});
