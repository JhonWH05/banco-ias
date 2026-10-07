import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SolicitudUsoRequest } from '../models/solicitud-uso-request';
import { SolicitudUsoResponse } from '../models/solicitud-uso-response';

@Injectable({
  providedIn: 'root'
})
export class SolicitudUsoService {

  private readonly apiUrl = 'http://localhost:8080/api/solicitudes';

  constructor(private http: HttpClient) {}

  procesarSolicitud(
    request: SolicitudUsoRequest
  ): Observable<SolicitudUsoResponse> {

    return this.http.post<SolicitudUsoResponse>(
      this.apiUrl,
      request
    );
  }

  consultarPorReferencia(
    referenciaSolicitud: string
  ): Observable<SolicitudUsoResponse> {

    return this.http.get<SolicitudUsoResponse>(
      `${this.apiUrl}/${referenciaSolicitud}`
    );
  }

  consultarRecientes(): Observable<SolicitudUsoResponse[]> {

    return this.http.get<SolicitudUsoResponse[]>(
      this.apiUrl
    );
  }
}