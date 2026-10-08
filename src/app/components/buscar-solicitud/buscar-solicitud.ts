import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import { SolicitudUsoService } from '../../services/solicitud-uso.service';
import { SolicitudUsoResponse } from '../../models/solicitud-uso-response';
import { ErrorResponse } from '../../models/error-response';

@Component({
  selector: 'app-buscar-solicitud',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './buscar-solicitud.html',
  styleUrl: './buscar-solicitud.css'
})
export class BuscarSolicitud {

  referenciaSolicitud = '';

  resultado = signal<SolicitudUsoResponse | null>(null);
  mensajeError = signal('');
  buscando = signal(false);

  constructor(
    private solicitudUsoService: SolicitudUsoService
  ) {}

  buscar(): void {

    this.resultado.set(null);
    this.mensajeError.set('');

    if (!this.referenciaSolicitud.trim()) {
      this.mensajeError.set(
        'Debe ingresar una referencia de solicitud'
      );
      return;
    }

    this.buscando.set(true);

    this.solicitudUsoService
      .consultarPorReferencia(this.referenciaSolicitud.trim())
      .subscribe({

        next: (response) => {
          this.resultado.set(response);
          this.buscando.set(false);
        },

        error: (error: HttpErrorResponse) => {

          const respuesta = error.error as ErrorResponse;

          this.mensajeError.set(
            respuesta?.error?.mensaje ??
            'No fue posible consultar la solicitud'
          );

          this.buscando.set(false);
        }
      });
  }
}