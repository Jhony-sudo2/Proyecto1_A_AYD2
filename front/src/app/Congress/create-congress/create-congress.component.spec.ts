import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateCongressComponent } from './create-congress.component';

describe('CreateCongressComponent', () => {
  let component: CreateCongressComponent;
  let fixture: ComponentFixture<CreateCongressComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateCongressComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateCongressComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
