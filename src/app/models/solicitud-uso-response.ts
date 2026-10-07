export interface SolicitudUsoResponse {
  referenciaSolicitud: string;
  idPreaprobado: string;
  idCliente: string;
  monto: number;
  estado: string;
  motivo: string;
  fechaProcesamiento: string;
}