import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApplicationService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  applyToAudition(auditionId: number, message: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/auditions/${auditionId}/applications`, { message });
  }

  getMyApplications(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/applications/my`);
  }

  getApplicationById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/applications/${id}`);
  }

  getApplicationsForAudition(auditionId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/auditions/${auditionId}/applications`);
  }

  updateApplicationStatus(applicationId: number, status: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/applications/${applicationId}/status`, { status });
  }
}
