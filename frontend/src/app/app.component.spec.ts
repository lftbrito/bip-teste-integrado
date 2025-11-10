import { TestBed, ComponentFixture } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { RouterTestingModule } from '@angular/router/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AppComponent,
        RouterTestingModule,
        BrowserAnimationsModule
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should have correct title', () => {
    expect(component.title).toEqual('BIP - Gerenciamento de Benefícios');
  });

  describe('Theme Management', () => {
    it('should initialize with light theme by default', () => {
      fixture.detectChanges();
      expect(component.isDarkTheme).toBeFalse();
    });

    it('should load saved theme from localStorage', () => {
      localStorage.setItem('theme', 'dark');
      fixture.detectChanges();
      expect(component.isDarkTheme).toBeTrue();
    });

    it('should toggle theme', () => {
      fixture.detectChanges();
      const initialTheme = component.isDarkTheme;
      
      component.toggleTheme();
      
      expect(component.isDarkTheme).toBe(!initialTheme);
    });

    it('should save theme preference to localStorage', () => {
      fixture.detectChanges();
      
      component.toggleTheme();
      
      const savedTheme = localStorage.getItem('theme');
      expect(savedTheme).toBe(component.isDarkTheme ? 'dark' : 'light');
    });

    it('should apply theme when toggled', () => {
      fixture.detectChanges();
      spyOn<any>(component, 'applyTheme');
      
      component.toggleTheme();
      
      expect((component as any).applyTheme).toHaveBeenCalled();
    });
  });

  describe('Rendering', () => {
    it('should render router outlet', () => {
      fixture.detectChanges();
      const compiled = fixture.nativeElement as HTMLElement;
      const routerOutlet = compiled.querySelector('router-outlet');
      expect(routerOutlet).toBeTruthy();
    });
  });

  describe('ngOnInit', () => {
    it('should check localStorage for saved theme', () => {
      spyOn(localStorage, 'getItem').and.returnValue('dark');
      
      component.ngOnInit();
      
      expect(localStorage.getItem).toHaveBeenCalledWith('theme');
      expect(component.isDarkTheme).toBeTrue();
    });

    it('should apply theme on initialization', () => {
      spyOn<any>(component, 'applyTheme');
      
      component.ngOnInit();
      
      expect((component as any).applyTheme).toHaveBeenCalled();
    });
  });
});
