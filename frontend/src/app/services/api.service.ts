import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  //  CONFIGURATION CRITIQUE : withCredentials: true pour les sessions
  private getHttpOptions() {
    return {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      withCredentials: true  //  ESSENTIEL pour envoyer les cookies de session
    };
  }

  // Méthode de login
  login(credentials: { matricule: string, password: string }): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/auth/login`,
      credentials,
      this.getHttpOptions()  //  Avec withCredentials
    );
  }

  // Méthode de logout
  logout(): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/auth/logout`,
      {},
      this.getHttpOptions()
    );
  }

  // Vérifier la session
  checkAuth(): Observable<any> {
    return this.http.get(
      `${this.baseUrl}/auth/check`,
      this.getHttpOptions()
    );
  }

  // GET tous les utilisateurs
  getUsers(): Observable<any> {
    return this.http.get(
      `${this.baseUrl}/users`,
      this.getHttpOptions()
    );
  }

  // GET un utilisateur par ID
  getUserById(id: number): Observable<any> {
    return this.http.get(
      `${this.baseUrl}/users/${id}`,
      this.getHttpOptions()
    );
  }

  // POST créer un utilisateur
  createUser(user: any): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/users`,
      user,
      this.getHttpOptions()
    );
  }

  // POST modifier un utilisateur (POST, pas PUT !)
  updateUser(id: number, user: any): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/users/${id}`,
      user,
      this.getHttpOptions()
    );
  }

  // DELETE supprimer un utilisateur
  deleteUser(id: number): Observable<any> {
    return this.http.delete(
      `${this.baseUrl}/users/${id}`,
      this.getHttpOptions()
    );
  }

  // GET recherche multicritère
  searchUsers(params: any): Observable<any> {
    return this.http.get(
      `${this.baseUrl}/users/search`,
      {
        ...this.getHttpOptions(),
        params
      }
    );
  }
}
