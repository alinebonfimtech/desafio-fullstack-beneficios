import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Beneficio, TransferRequest } from '../models/beneficio.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BeneficioService {
  private apiUrl = `${environment.apiUrl}/beneficios`;

  constructor(private http: HttpClient) {}

  findAll(activeOnly: boolean = false): Observable<Beneficio[]> {
    const params = new HttpParams().set('activeOnly', activeOnly.toString());
    return this.http.get<Beneficio[]>(this.apiUrl, { params });
  }

  findById(id: number): Observable<Beneficio> {
    return this.http.get<Beneficio>(`${this.apiUrl}/${id}`);
  }

  searchByNome(nome: string): Observable<Beneficio[]> {
    const params = new HttpParams().set('nome', nome);
    return this.http.get<Beneficio[]>(`${this.apiUrl}/search`, { params });
  }

  create(beneficio: Beneficio): Observable<Beneficio> {
    return this.http.post<Beneficio>(this.apiUrl, beneficio);
  }

  update(id: number, beneficio: Beneficio): Observable<Beneficio> {
    return this.http.put<Beneficio>(`${this.apiUrl}/${id}`, beneficio);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  deactivate(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/deactivate`, {});
  }

  transfer(request: TransferRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/transfer`, request);
  }
}
