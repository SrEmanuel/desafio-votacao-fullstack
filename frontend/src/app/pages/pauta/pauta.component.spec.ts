import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PautaComponent } from './pauta.component';

describe('PautaComponent', () => {
  let component: PautaComponent;
  let fixture: ComponentFixture<PautaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PautaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PautaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
