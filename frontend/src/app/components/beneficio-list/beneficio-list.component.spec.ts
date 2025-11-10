import { ComponentFixture, TestBed, fakeAsync, tick, flush } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { OverlayModule } from '@angular/cdk/overlay';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError, Observable } from 'rxjs';
import { BeneficioListComponent } from './beneficio-list.component';
import { BeneficioService } from '../../services/beneficio.service';
import { Beneficio } from '../../models/beneficio.model';
import { TransferDialogComponent } from '../transfer-dialog/transfer-dialog.component';

describe('BeneficioListComponent', () => {
  let component: BeneficioListComponent;
  let fixture: ComponentFixture<BeneficioListComponent>;
  let mockBeneficioService: jasmine.SpyObj<BeneficioService>;
  let mockRouter: jasmine.SpyObj<Router>;
  let mockDialog: jasmine.SpyObj<MatDialog>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;

  const mockBeneficios: Beneficio[] = [
    { id: 1, nome: 'Benefício 1', descricao: 'Desc 1', valor: 1000, ativo: true },
    { id: 2, nome: 'Benefício 2', descricao: 'Desc 2', valor: 2000, ativo: true },
    { id: 3, nome: 'Benefício 3', descricao: 'Desc 3', valor: 1500, ativo: false }
  ];

  beforeEach(async () => {
    mockBeneficioService = jasmine.createSpyObj('BeneficioService', [
      'listarBeneficios',
      'deletar'
    ]);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    mockDialog = jasmine.createSpyObj('MatDialog', ['open']);
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);

    await TestBed.configureTestingModule({
      imports: [
        BeneficioListComponent,
        BrowserAnimationsModule,
        OverlayModule
      ],
      providers: [
        { provide: BeneficioService, useValue: mockBeneficioService },
        { provide: Router, useValue: mockRouter },
        { provide: MatDialog, useValue: mockDialog },
        { provide: MatSnackBar, useValue: mockSnackBar }
      ]
    }).compileComponents();

    mockBeneficioService.listarBeneficios.and.returnValue(of(mockBeneficios));
    fixture = TestBed.createComponent(BeneficioListComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load beneficios on init', () => {
      fixture.detectChanges();

      expect(mockBeneficioService.listarBeneficios).toHaveBeenCalled();
      expect(component.beneficios).toEqual(mockBeneficios);
      expect(component.loading).toBeFalse();
    });
  });

  describe('carregarBeneficios', () => {
    it('should set loading to true while loading', () => {
      // Reset para um observable que não completa imediatamente
      let resolveObservable: any;
      mockBeneficioService.listarBeneficios.and.returnValue(
        new Observable(observer => {
          resolveObservable = observer;
        })
      );

      component.carregarBeneficios();
      expect(component.loading).toBeTrue();
      
      // Limpa o observable
      if (resolveObservable) {
        resolveObservable.complete();
      }
    });

    it('should load beneficios successfully', () => {
      component.carregarBeneficios();

      expect(mockBeneficioService.listarBeneficios).toHaveBeenCalled();
      expect(component.beneficios).toEqual(mockBeneficios);
      expect(component.loading).toBeFalse();
    });

    it('should handle error and set loading to false', fakeAsync(() => {
      const errorMessage = 'Erro ao carregar';
      mockBeneficioService.listarBeneficios.and.returnValue(
        throwError(() => new Error(errorMessage))
      );

      component.carregarBeneficios();
      tick(); // Aguarda observables completarem
      flush(); // Limpa todos os timers pendentes
      expect(component.loading).toBeFalse();
    }));
  });

  describe('novoBeneficio', () => {
    it('should navigate to new beneficio form', () => {
      component.novoBeneficio();

      expect(mockRouter.navigate).toHaveBeenCalledWith(['/beneficios/novo']);
    });
  });

  describe('editar', () => {
    it('should navigate to edit beneficio form with id', () => {
      const beneficioId = 1;

      component.editar(beneficioId);

      expect(mockRouter.navigate).toHaveBeenCalledWith(['/beneficios/editar', beneficioId]);
    });
  });

  describe('deletar', () => {
    beforeEach(() => {
      fixture.detectChanges();
      mockBeneficioService.deletar.and.returnValue(of(void 0));
    });

    it('should not delete beneficio if not confirmed', () => {
      spyOn(window, 'confirm').and.returnValue(false);
      const beneficioId = 1;

      component.deletar(beneficioId);

      expect(window.confirm).toHaveBeenCalled();
      expect(mockBeneficioService.deletar).not.toHaveBeenCalled();
    });
  });

  describe('table display', () => {
    it('should display correct columns', () => {
      expect(component.displayedColumns).toEqual([
        'id',
        'nome',
        'descricao',
        'valor',
        'ativo',
        'acoes'
      ]);
    });

    it('should render table with beneficios', () => {
      fixture.detectChanges();
      const compiled = fixture.nativeElement;
      const table = compiled.querySelector('table');

      expect(table).toBeTruthy();
    });
  });

  describe('loading state', () => {
    it('should show loading spinner when loading', () => {
      // Reset para um observable que não completa imediatamente para testar loading=true
      let resolveObservable: any;
      mockBeneficioService.listarBeneficios.and.returnValue(
        new Observable(observer => {
          resolveObservable = observer;
        })
      );
      
      component.carregarBeneficios();
      fixture.detectChanges();
      
      // Loading deve estar true enquanto aguarda resposta
      expect(component.loading).toBeTrue();
      
      // Limpa o observable
      if (resolveObservable) {
        resolveObservable.next(mockBeneficios);
        resolveObservable.complete();
      }
    });

    it('should hide loading spinner when not loading', () => {
      fixture.detectChanges(); // This triggers ngOnInit and loads data
      
      expect(component.loading).toBeFalse();
    });
  });

  describe('empty state', () => {
    it('should handle empty beneficios list', () => {
      mockBeneficioService.listarBeneficios.and.returnValue(of([]));
      
      fixture.detectChanges();

      expect(component.beneficios.length).toBe(0);
    });
  });
});
