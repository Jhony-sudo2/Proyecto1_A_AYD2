import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewCongressComponent } from './view-congress.component';

describe('ViewCongressComponent', () => {
  let component: ViewCongressComponent;
  let fixture: ComponentFixture<ViewCongressComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ViewCongressComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewCongressComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
