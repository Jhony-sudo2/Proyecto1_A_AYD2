import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Activity } from '../../interfaces/Activity';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-activity-card',
  imports: [CommonModule],
  templateUrl: './activity-card.component.html',
  styleUrl: './activity-card.component.css'
})
export class ActivityCardComponent {
  @Input() activity!: Activity;
  @Input() showActions: boolean = false;   
  @Input() showEnroll: boolean = false;    
  @Input() isEditing: boolean = false;

  @Output() onEdit = new EventEmitter<Activity>();
  @Output() onDelete = new EventEmitter<number>();
  @Output() onEnroll = new EventEmitter<number>();
}
