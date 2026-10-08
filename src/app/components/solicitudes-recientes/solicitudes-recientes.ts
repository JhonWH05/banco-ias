import { Component, OnInit, signal } from '@angular/core';
import { SolicitudUsoService } from '../../services/solicitud-uso.service';
import { SolicitudUsoResponse } from '../../models/solicitud-uso-response';

@Component({
  selector: 'app-solicitudes-recientes',
  standalone: true,
  imports: [],
  templateUrl: './solicitudes-recientes.html',
  styleUrl: './solicitudes-recientes.css'
})
export class SolicitudesRecientes implements OnInit {

  solicitudes = signal<SolicitudUsoResponse[]>([]);
  cargando = signal(false);
  mensajeError = signal('');

  constructor(
    private solicitudUsoService: SolicitudUsoService
  ) {}

  ngOnInit(): void {
    this.cargarSolicitudes();
  }

  cargarSolicitudes(): void {

    this.cargando.set(true);
    this.mensajeError.set('');

    this.solicitudUsoService
      .consultarRecientes()
      .subscribe({

        next: (response) => {
          this.solicitudes.set(response);
          this.cargando.set(false);
        },

        error: () => {
          this.mensajeError.set(
            'No fue posible consultar las solicitudes recientes'
          );

          this.cargando.set(false);
        }
      });
  }
}