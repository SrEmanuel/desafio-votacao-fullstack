import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddCardComponentComponent } from './add-card-component.component';

describe('AddCardComponentComponent', () => {
  let component: AddCardComponentComponent;
  let fixture: ComponentFixture<AddCardComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddCardComponentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddCardComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
