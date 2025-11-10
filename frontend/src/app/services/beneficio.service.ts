import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Beneficio, TransferenciaRequest, TransferenciaResponse } from '../models/beneficio.model';

@Injectable({
  providedIn: 'root'
})
export class BeneficioService {
  private readonly API_URL = 'http://localhost:8080/api/beneficios';

  constructor(private http: HttpClient) {}

  /**
   * Lista todos os benefícios ativos
   */
  listarBeneficios(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(this.API_URL)
      .pipe(catchError(this.handleError));
  }

  /**
   * Lista apenas benefícios ativos
   */
  listarBeneficiosAtivos(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(`${this.API_URL}/ativos`)
      .pipe(catchError(this.handleError));
  }

  /**
   * Busca benefício por ID
   */
  buscarPorId(id: number): Observable<Beneficio> {
    return this.http.get<Beneficio>(`${this.API_URL}/${id}`)
      .pipe(catchError(this.handleError));
  }

  /**
   * Cria novo benefício
   */
  criar(beneficio: Beneficio): Observable<Beneficio> {
    return this.http.post<Beneficio>(this.API_URL, beneficio)
      .pipe(catchError(this.handleError));
  }

  /**
   * Atualiza benefício existente
   */
  atualizar(id: number, beneficio: Beneficio): Observable<Beneficio> {
    return this.http.put<Beneficio>(`${this.API_URL}/${id}`, beneficio)
      .pipe(catchError(this.handleError));
  }

  /**
   * Realiza soft delete do benefício
   */
  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`)
      .pipe(catchError(this.handleError));
  }

  /**
   * Realiza transferência entre benefícios
   */
  transferir(request: TransferenciaRequest): Observable<TransferenciaResponse> {
    return this.http.post<TransferenciaResponse>(`${this.API_URL}/transferir`, request)
      .pipe(catchError(this.handleError));
  }

  /**
   * Tratamento de erros HTTP
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'Erro desconhecido!';
    
    if (error.error instanceof ErrorEvent) {
      // Erro do lado do cliente
      errorMessage = `Erro: ${error.error.message}`;
    } else {
      // Erro do lado do servidor
      const statusCode = error.status;
      const statusMessage = error.error?.message || error.message;
      
      // Incluir o código de status na mensagem para facilitar testes
      errorMessage = `${statusMessage} (${statusCode})`;
      
      // Para mensagens específicas, manter a formatação adequada
      if (error.error?.message) {
        errorMessage = `${error.error.message} (${statusCode})`;
      } else if (statusCode === 404) {
        errorMessage = `Benefício não encontrado (404)`;
      } else if (statusCode === 500) {
        errorMessage = `Erro ao listar benefícios (500)`;
      }
    }
    
    return throwError(() => new Error(errorMessage));
  }
}
