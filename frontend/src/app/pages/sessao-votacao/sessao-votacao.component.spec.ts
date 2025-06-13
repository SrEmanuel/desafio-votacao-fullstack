import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SessaoVotacaoComponent } from './sessao-votacao.component';

describe('SessaoVotacaoComponent', () => {
  let component: SessaoVotacaoComponent;
  let fixture: ComponentFixture<SessaoVotacaoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessaoVotacaoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SessaoVotacaoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
