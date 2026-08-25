import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuditionService {
  private apiUrl = `${environment.apiUrl}/auditions`;

  constructor(private http: HttpClient) {}

  getActiveAuditions(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getAuditionDetails(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  getMyAuditions(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/my`);
  }

  createAudition(audition: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, audition);
  }

  updateAudition(id: number, audition: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, audition);
  }

  deleteAudition(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }
}
