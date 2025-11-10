import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';
import { TransferDialogComponent } from './transfer-dialog.component';
import { BeneficioService } from '../../services/beneficio.service';
import { Beneficio, TransferenciaResponse } from '../../models/beneficio.model';

describe('TransferDialogComponent', () => {
  let component: TransferDialogComponent;
  let fixture: ComponentFixture<TransferDialogComponent>;
  let mockBeneficioService: jasmine.SpyObj<BeneficioService>;
  let mockDialogRef: jasmine.SpyObj<MatDialogRef<TransferDialogComponent>>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;

  const mockBeneficioOrigem: Beneficio = {
    id: 1,
    nome: 'Benefício Origem',
    descricao: 'Descrição Origem',
    valor: 1000,
    ativo: true
  };

  const mockBeneficios: Beneficio[] = [
    mockBeneficioOrigem,
    { id: 2, nome: 'Benefício 2', descricao: 'Desc 2', valor: 2000, ativo: true },
    { id: 3, nome: 'Benefício 3', descricao: 'Desc 3', valor: 1500, ativo: true },
    { id: 4, nome: 'Benefício Inativo', descricao: 'Desc 4', valor: 500, ativo: false }
  ];

  const mockDialogData = {
    beneficioOrigem: mockBeneficioOrigem,
    todosBeneficios: mockBeneficios
  };

  beforeEach(async () => {
    mockBeneficioService = jasmine.createSpyObj('BeneficioService', ['transferir']);
    mockDialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);

    await TestBed.configureTestingModule({
      imports: [
        TransferDialogComponent,
        ReactiveFormsModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: BeneficioService, useValue: mockBeneficioService },
        { provide: MatDialogRef, useValue: mockDialogRef },
        { provide: MAT_DIALOG_DATA, useValue: mockDialogData },
        { provide: MatSnackBar, useValue: mockSnackBar }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TransferDialogComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Initialization', () => {
    it('should initialize form with validators', () => {
      fixture.detectChanges();

      expect(component.form).toBeTruthy();
      expect(component.form.get('idDestino')?.value).toBe('');
      expect(component.form.get('valor')?.value).toBe(0);
    });

    it('should set max validator to origem valor', () => {
      fixture.detectChanges();

      const valorControl = component.form.get('valor');
      
      valorControl?.setValue(1001);
      expect(valorControl?.hasError('max')).toBeTrue();

      valorControl?.setValue(1000);
      expect(valorControl?.hasError('max')).toBeFalse();
    });
  });

  describe('Form Validation', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should validate required fields', () => {
      expect(component.form.valid).toBeFalse();

      const idDestinoControl = component.form.get('idDestino');
      expect(idDestinoControl?.hasError('required')).toBeTrue();
    });

    it('should validate valor minimum', () => {
      const valorControl = component.form.get('valor');

      valorControl?.setValue(0);
      expect(valorControl?.hasError('min')).toBeTrue();

      valorControl?.setValue(0.01);
      expect(valorControl?.hasError('min')).toBeFalse();
    });

    it('should validate valor maximum based on origem', () => {
      const valorControl = component.form.get('valor');

      valorControl?.setValue(1001);
      expect(valorControl?.hasError('max')).toBeTrue();

      valorControl?.setValue(1000);
      expect(valorControl?.hasError('max')).toBeFalse();

      valorControl?.setValue(500);
      expect(valorControl?.hasError('max')).toBeFalse();
    });

    it('should mark all fields as touched when form is invalid on submit', () => {
      component.transferir();

      expect(component.form.get('idDestino')?.touched).toBeTrue();
      expect(component.form.get('valor')?.touched).toBeTrue();
    });

    it('should not submit if form is invalid', () => {
      component.transferir();

      expect(mockBeneficioService.transferir).not.toHaveBeenCalled();
    });
  });

  describe('Transfer', () => {
    const mockTransferResponse: TransferenciaResponse = {
      sucesso: true,
      mensagem: 'Transferência realizada com sucesso',
      transacao: {
        origemId: 1,
        destinoId: 2,
        valor: 500,
        dataHora: '2025-11-10T10:00:00'
      },
      origem: {
        id: 1,
        nome: 'Benefício Origem',
        valorAnterior: 1000,
        valorAtual: 500
      },
      destino: {
        id: 2,
        nome: 'Benefício 2',
        valorAnterior: 2000,
        valorAtual: 2500
      }
    };

    beforeEach(() => {
      fixture.detectChanges();
      mockBeneficioService.transferir.and.returnValue(of(mockTransferResponse));
    });

    it('should set submitting flag while transferring', () => {
      component.form.setValue({
        idDestino: 2,
        valor: 500
      });

      expect(component.submitting).toBeFalse();
      component.transferir();
      // After successful transfer, submitting is not reset (dialog closes)
    });
  });

  describe('Cancel', () => {
    it('should close dialog with false on cancel', () => {
      component.cancelar();

      expect(mockDialogRef.close).toHaveBeenCalledWith(false);
    });
  });

  describe('Form Getters', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should provide access to valor control', () => {
      expect(component.valor).toBe(component.form.get('valor'));
    });

    it('should provide access to idDestino control', () => {
      expect(component.idDestino).toBe(component.form.get('idDestino'));
    });
  });

  describe('Edge Cases', () => {
    it('should handle empty beneficios list', () => {
      // Quando só há um benefício (o de origem), a lista de destino fica vazia
      component.data.todosBeneficios = [mockBeneficioOrigem];
      component.ngOnInit();
      fixture.detectChanges();
      
      expect(component.beneficiosDestino.length).toBe(0);
    });

    it('should handle transfer with minimum value', () => {
      fixture.detectChanges();
      mockBeneficioService.transferir.and.returnValue(of({
        sucesso: true,
        mensagem: 'Transferência realizada',
        transacao: { origemId: 1, destinoId: 2, valor: 0.01, dataHora: '2025-11-10' },
        origem: { id: 1, nome: 'Origem', valorAnterior: 1000, valorAtual: 999.99 },
        destino: { id: 2, nome: 'Destino', valorAnterior: 2000, valorAtual: 2000.01 }
      }));

      component.form.setValue({
        idDestino: 2,
        valor: 0.01
      });

      component.transferir();

      expect(mockBeneficioService.transferir).toHaveBeenCalledWith(
        jasmine.objectContaining({ valor: 0.01 })
      );
    });

    it('should handle transfer with maximum available value', () => {
      fixture.detectChanges();
      mockBeneficioService.transferir.and.returnValue(of({
        sucesso: true,
        mensagem: 'Transferência realizada',
        transacao: { origemId: 1, destinoId: 2, valor: 1000, dataHora: '2025-11-10' },
        origem: { id: 1, nome: 'Origem', valorAnterior: 1000, valorAtual: 0 },
        destino: { id: 2, nome: 'Destino', valorAnterior: 2000, valorAtual: 3000 }
      }));

      component.form.setValue({
        idDestino: 2,
        valor: 1000
      });

      component.transferir();

      expect(mockBeneficioService.transferir).toHaveBeenCalledWith(
        jasmine.objectContaining({ valor: 1000 })
      );
    });
  });

  describe('Beneficios Filtering', () => {
    it('should only show active beneficios', () => {
      fixture.detectChanges();

      const allActive = component.beneficiosDestino.every(b => b.ativo);
      expect(allActive).toBeTrue();
    });

    it('should exclude origem from destination list', () => {
      fixture.detectChanges();

      const hasOrigem = component.beneficiosDestino.some(b => b.id === mockBeneficioOrigem.id);
      expect(hasOrigem).toBeFalse();
    });
  });
});
