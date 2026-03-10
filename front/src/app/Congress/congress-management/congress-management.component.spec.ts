import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CongressManagementComponent } from './congress-management.component';

describe('CongressManagementComponent', () => {
  let component: CongressManagementComponent;
  let fixture: ComponentFixture<CongressManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CongressManagementComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CongressManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
