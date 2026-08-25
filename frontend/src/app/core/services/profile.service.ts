import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private apiUrl = `${environment.apiUrl}/profiles`;

  constructor(private http: HttpClient) {}

  getArtistProfile(): Observable<any> {
    return this.http.get(`${this.apiUrl}/artist`);
  }

  updateArtistProfile(profile: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/artist`, profile);
  }

  getOrganizerProfile(): Observable<any> {
    return this.http.get(`${this.apiUrl}/organizer`);
  }

  updateOrganizerProfile(profile: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/organizer`, profile);
  }
}
