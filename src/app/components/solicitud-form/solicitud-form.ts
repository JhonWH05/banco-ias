import { Component, EventEmitter, Output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import { SolicitudUsoService } from '../../services/solicitud-uso.service';
import { SolicitudUsoRequest } from '../../models/solicitud-uso-request';
import { SolicitudUsoResponse } from '../../models/solicitud-uso-response';
import { ErrorResponse } from '../../models/error-response';

@Component({
  selector: 'app-solicitud-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './solicitud-form.html',
  styleUrl: './solicitud-form.css'
})
export class SolicitudForm {

  solicitud: SolicitudUsoRequest = {
    referenciaSolicitud: '',
    idPreaprobado: '',
    idCliente: '',
    monto: 0
  };

  resultado = signal<SolicitudUsoResponse | null>(null);

  mensajeError = signal('');

  procesando = signal(false);

  @Output()
  solicitudProcesada = new EventEmitter<void>();

  constructor(
    private solicitudUsoService: SolicitudUsoService
  ) {}

  procesar(): void {

    this.resultado.set(null);
    this.mensajeError.set('');
    this.procesando.set(true);

    this.solicitudUsoService
      .procesarSolicitud(this.solicitud)
      .subscribe({

        next: (response) => {

          this.resultado.set(response);

          this.procesando.set(false);

          // Notifica al componente padre que se procesó
          // una nueva solicitud.
          this.solicitudProcesada.emit();
        },

        error: (error: HttpErrorResponse) => {

          const respuesta = error.error as ErrorResponse;

          this.mensajeError.set(
            respuesta?.error?.mensaje ??
            'Ocurrió un error al procesar la solicitud'
          );

          this.procesando.set(false);
        }

      });
  }
}