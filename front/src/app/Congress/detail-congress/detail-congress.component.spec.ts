import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailCongressComponent } from './detail-congress.component';

describe('DetailCongressComponent', () => {
  let component: DetailCongressComponent;
  let fixture: ComponentFixture<DetailCongressComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailCongressComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetailCongressComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
