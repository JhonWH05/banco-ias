import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SolicitudesRecientes } from './solicitudes-recientes';

describe('SolicitudesRecientes', () => {
  let component: SolicitudesRecientes;
  let fixture: ComponentFixture<SolicitudesRecientes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SolicitudesRecientes],
    }).compileComponents();

    fixture = TestBed.createComponent(SolicitudesRecientes);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
