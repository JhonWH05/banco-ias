import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BuscarSolicitud } from './buscar-solicitud';

describe('BuscarSolicitud', () => {
  let component: BuscarSolicitud;
  let fixture: ComponentFixture<BuscarSolicitud>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BuscarSolicitud],
    }).compileComponents();

    fixture = TestBed.createComponent(BuscarSolicitud);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
