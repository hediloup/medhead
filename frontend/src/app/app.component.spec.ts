import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { HospitalAllocationComponent } from './components/hospital-allocation.component';
import { AllocationService } from './services/allocation.service';
import { GeocodingService } from './services/geocoding.service';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AppComponent, HospitalAllocationComponent],
      imports: [HttpClientTestingModule, ReactiveFormsModule],
      providers: [AllocationService, GeocodingService]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it(`should have as title 'MedHead Frontend'`, () => {
    expect(component.title).toEqual('MedHead Frontend');
  });

  it('should render hospital allocation component', () => {
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    const hospitalAllocationComponent = compiled.querySelector('app-hospital-allocation');
    expect(hospitalAllocationComponent).toBeTruthy();
  });

  it('should have correct component structure', () => {
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    // In Angular tests, fixture.nativeElement is the actual DOM element (usually div)
    // We should check that the component exists and has the right content
    expect(compiled).toBeTruthy();
    expect(compiled.querySelector('app-hospital-allocation')).toBeTruthy();
  });
});
