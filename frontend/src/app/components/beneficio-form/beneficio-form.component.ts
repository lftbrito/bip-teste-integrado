import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { BeneficioService } from '../../services/beneficio.service';
import { Beneficio } from '../../models/beneficio.model';

@Component({
  selector: 'app-beneficio-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCheckboxModule,
    MatSnackBarModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './beneficio-form.component.html',
  styleUrls: ['./beneficio-form.component.scss']
})
export class BeneficioFormComponent implements OnInit {
  form!: FormGroup;
  isEditMode = false;
  beneficioId?: number;
  loading = false;
  submitting = false;

  constructor(
    private fb: FormBuilder,
    private beneficioService: BeneficioService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.checkEditMode();
  }

  private initForm(): void {
    this.form = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      descricao: ['', [Validators.maxLength(500)]],
      valor: [0, [Validators.required, Validators.min(0)]],
      ativo: [true]
    });
  }

  private checkEditMode(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.beneficioId = +id;
      this.carregarBeneficio();
    }
  }

  private carregarBeneficio(): void {
    this.loading = true;
    this.beneficioService.buscarPorId(this.beneficioId!).subscribe({
      next: (beneficio) => {
        this.form.patchValue(beneficio);
        this.loading = false;
      },
      error: (error) => {
        this.showError(error.message);
        this.loading = false;
        this.voltar();
      }
    });
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting = true;
    const beneficio: Beneficio = this.form.value;

    const operation = this.isEditMode
      ? this.beneficioService.atualizar(this.beneficioId!, beneficio)
      : this.beneficioService.criar(beneficio);

    operation.subscribe({
      next: () => {
        this.showSuccess(
          this.isEditMode ? 'Benefício atualizado com sucesso!' : 'Benefício criado com sucesso!'
        );
        this.router.navigate(['/beneficios']);
      },
      error: (error) => {
        this.showError(error.message);
        this.submitting = false;
      }
    });
  }

  voltar(): void {
    this.router.navigate(['/beneficios']);
  }

  // Getters para validação dos campos
  get nome() { return this.form.get('nome'); }
  get descricao() { return this.form.get('descricao'); }
  get valor() { return this.form.get('valor'); }

  private showSuccess(message: string): void {
    this.snackBar.open(message, 'Fechar', {
      duration: 3000,
      panelClass: ['success-snackbar']
    });
  }

  private showError(message: string): void {
    this.snackBar.open(message, 'Fechar', {
      duration: 5000,
      panelClass: ['error-snackbar']
    });
  }
}
