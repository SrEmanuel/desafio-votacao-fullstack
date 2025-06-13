import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'add-card-component',
  imports: [],
  templateUrl: './add-card-component.component.html',
  styleUrl: './add-card-component.component.css'
})
export class AddCardComponentComponent {
  @Output()
  addClick = new EventEmitter<void>();

  onClick(): void {
    this.addClick.emit();
  }
}
