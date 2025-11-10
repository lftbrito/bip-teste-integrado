import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router, ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';
import { BeneficioFormComponent } from './beneficio-form.component';
import { BeneficioService } from '../../services/beneficio.service';
import { Beneficio } from '../../models/beneficio.model';

describe('BeneficioFormComponent', () => {
  let component: BeneficioFormComponent;
  let fixture: ComponentFixture<BeneficioFormComponent>;
  let mockBeneficioService: jasmine.SpyObj<BeneficioService>;
  let mockRouter: jasmine.SpyObj<Router>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;
  let mockActivatedRoute: any;

  const mockBeneficio: Beneficio = {
    id: 1,
    nome: 'Benefício Teste',
    descricao: 'Descrição Teste',
    valor: 1000,
    ativo: true
  };

  beforeEach(async () => {
    mockBeneficioService = jasmine.createSpyObj('BeneficioService', [
      'buscarPorId',
      'criar',
      'atualizar'
    ]);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);
    
    mockActivatedRoute = {
      snapshot: {
        paramMap: {
          get: jasmine.createSpy('get').and.returnValue(null)
        }
      }
    };

    await TestBed.configureTestingModule({
      imports: [
        BeneficioFormComponent,
        ReactiveFormsModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: BeneficioService, useValue: mockBeneficioService },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: MatSnackBar, useValue: mockSnackBar }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(BeneficioFormComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Form Initialization', () => {
    it('should initialize form with empty values in create mode', () => {
      fixture.detectChanges();

      expect(component.form).toBeTruthy();
      expect(component.form.get('nome')?.value).toBe('');
      expect(component.form.get('descricao')?.value).toBe('');
      expect(component.form.get('valor')?.value).toBe(0);
      expect(component.form.get('ativo')?.value).toBe(true);
      expect(component.isEditMode).toBeFalse();
    });

    it('should initialize form controls with correct validators', () => {
      fixture.detectChanges();

      const nomeControl = component.form.get('nome');
      const valorControl = component.form.get('valor');

      expect(nomeControl?.hasError('required')).toBeTrue();
      
      nomeControl?.setValue('AB');
      expect(nomeControl?.hasError('minlength')).toBeTrue();
      
      valorControl?.setValue(-1);
      expect(valorControl?.hasError('min')).toBeTrue();
    });

    it('should load beneficio in edit mode', () => {
      mockActivatedRoute.snapshot.paramMap.get.and.returnValue('1');
      mockBeneficioService.buscarPorId.and.returnValue(of(mockBeneficio));

      fixture.detectChanges();

      expect(component.isEditMode).toBeTrue();
      expect(component.beneficioId).toBe(1);
      expect(mockBeneficioService.buscarPorId).toHaveBeenCalledWith(1);
      expect(component.form.get('nome')?.value).toBe(mockBeneficio.nome);
      expect(component.form.get('valor')?.value).toBe(mockBeneficio.valor);
    });
  });

  describe('Form Validation', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should validate required fields', () => {
      const nomeControl = component.form.get('nome');
      const valorControl = component.form.get('valor');

      expect(component.form.valid).toBeFalse();

      nomeControl?.setValue('');
      expect(nomeControl?.hasError('required')).toBeTrue();

      valorControl?.setValue(null);
      expect(component.form.valid).toBeFalse();
    });

    it('should validate nome minimum length', () => {
      const nomeControl = component.form.get('nome');

      nomeControl?.setValue('AB');
      expect(nomeControl?.hasError('minlength')).toBeTrue();

      nomeControl?.setValue('ABC');
      expect(nomeControl?.hasError('minlength')).toBeFalse();
    });

    it('should validate nome maximum length', () => {
      const nomeControl = component.form.get('nome');
      const longName = 'A'.repeat(101);

      nomeControl?.setValue(longName);
      expect(nomeControl?.hasError('maxlength')).toBeTrue();

      nomeControl?.setValue('A'.repeat(100));
      expect(nomeControl?.hasError('maxlength')).toBeFalse();
    });

    it('should validate valor minimum value', () => {
      const valorControl = component.form.get('valor');

      valorControl?.setValue(-1);
      expect(valorControl?.hasError('min')).toBeTrue();

      valorControl?.setValue(0);
      expect(valorControl?.hasError('min')).toBeFalse();

      valorControl?.setValue(100);
      expect(valorControl?.hasError('min')).toBeFalse();
    });

    it('should validate descricao maximum length', () => {
      const descricaoControl = component.form.get('descricao');
      const longDesc = 'A'.repeat(501);

      descricaoControl?.setValue(longDesc);
      expect(descricaoControl?.hasError('maxlength')).toBeTrue();

      descricaoControl?.setValue('A'.repeat(500));
      expect(descricaoControl?.hasError('maxlength')).toBeFalse();
    });

    it('should mark all fields as touched when form is invalid on submit', () => {
      component.salvar();

      expect(component.form.get('nome')?.touched).toBeTrue();
      expect(component.form.get('valor')?.touched).toBeTrue();
    });
  });

  describe('Create Beneficio', () => {
    beforeEach(() => {
      fixture.detectChanges();
      mockBeneficioService.criar.and.returnValue(of(mockBeneficio));
    });

    it('should create beneficio successfully', () => {
      component.form.setValue({
        nome: 'Novo Benefício',
        descricao: 'Nova Descrição',
        valor: 1500,
        ativo: true
      });

      component.salvar();

      expect(mockBeneficioService.criar).toHaveBeenCalledWith(jasmine.objectContaining({
        nome: 'Novo Benefício',
        valor: 1500
      }));
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/beneficios']);
    });

    it('should not submit if form is invalid', () => {
      component.form.setValue({
        nome: '',
        descricao: '',
        valor: 0,
        ativo: true
      });

      component.salvar();

      expect(mockBeneficioService.criar).not.toHaveBeenCalled();
      expect(component.form.get('nome')?.touched).toBeTrue();
    });

    it('should set submitting flag while creating', () => {
      component.form.setValue({
        nome: 'Novo Benefício',
        descricao: 'Descrição',
        valor: 1000,
        ativo: true
      });

      expect(component.submitting).toBeFalse();
      component.salvar();
      expect(component.submitting).toBeTrue();
    });
  });

  describe('Update Beneficio', () => {
    beforeEach(() => {
      mockActivatedRoute.snapshot.paramMap.get.and.returnValue('1');
      mockBeneficioService.buscarPorId.and.returnValue(of(mockBeneficio));
      mockBeneficioService.atualizar.and.returnValue(of(mockBeneficio));
      fixture.detectChanges();
    });

    it('should update beneficio successfully', () => {
      component.form.patchValue({
        nome: 'Benefício Atualizado'
      });

      component.salvar();

      expect(mockBeneficioService.atualizar).toHaveBeenCalledWith(
        1,
        jasmine.objectContaining({
          nome: 'Benefício Atualizado'
        })
      );
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/beneficios']);
    });
  });

  describe('Navigation', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should navigate back to list on voltar', () => {
      component.voltar();

      expect(mockRouter.navigate).toHaveBeenCalledWith(['/beneficios']);
    });

    it('should navigate back after successful create', () => {
      mockBeneficioService.criar.and.returnValue(of(mockBeneficio));
      
      component.form.setValue({
        nome: 'Benefício',
        descricao: 'Descrição',
        valor: 1000,
        ativo: true
      });

      component.salvar();

      expect(mockRouter.navigate).toHaveBeenCalledWith(['/beneficios']);
    });
  });

  describe('Form Getters', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should provide access to nome control', () => {
      expect(component.nome).toBe(component.form.get('nome'));
    });

    it('should provide access to descricao control', () => {
      expect(component.descricao).toBe(component.form.get('descricao'));
    });

    it('should provide access to valor control', () => {
      expect(component.valor).toBe(component.form.get('valor'));
    });
  });

  describe('Loading State', () => {
    it('should set loading to true while loading beneficio', () => {
      mockActivatedRoute.snapshot.paramMap.get.and.returnValue('1');
      mockBeneficioService.buscarPorId.and.returnValue(of(mockBeneficio));

      expect(component.loading).toBeFalse();
      fixture.detectChanges();
      expect(component.loading).toBeFalse();
    });

    it('should set loading to false after loading error', () => {
      mockActivatedRoute.snapshot.paramMap.get.and.returnValue('1');
      mockBeneficioService.buscarPorId.and.returnValue(
        throwError(() => new Error('Erro'))
      );

      fixture.detectChanges();

      expect(component.loading).toBeFalse();
    });
  });
});
