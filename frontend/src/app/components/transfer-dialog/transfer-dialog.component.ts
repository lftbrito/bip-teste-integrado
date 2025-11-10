import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { BeneficioService } from '../../services/beneficio.service';
import { Beneficio, TransferenciaRequest } from '../../models/beneficio.model';

interface DialogData {
  beneficioOrigem: Beneficio;
  todosBeneficios: Beneficio[];
}

@Component({
  selector: 'app-transfer-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatSnackBarModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './transfer-dialog.component.html',
  styleUrls: ['./transfer-dialog.component.scss']
})
export class TransferDialogComponent implements OnInit {
  form!: FormGroup;
  beneficiosDestino: Beneficio[] = [];
  submitting = false;

  constructor(
    private fb: FormBuilder,
    private beneficioService: BeneficioService,
    private dialogRef: MatDialogRef<TransferDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.filterBeneficiosDestino();
  }

  private initForm(): void {
    this.form = this.fb.group({
      idDestino: ['', Validators.required],
      valor: [0, [Validators.required, Validators.min(0.01), Validators.max(this.data.beneficioOrigem.valor)]]
    });
  }

  private filterBeneficiosDestino(): void {
    this.beneficiosDestino = this.data.todosBeneficios.filter(
      b => b.id !== this.data.beneficioOrigem.id && b.ativo
    );
  }

  transferir(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting = true;
    
    const request: TransferenciaRequest = {
      beneficioOrigemId: this.data.beneficioOrigem.id!,
      beneficioDestinoId: this.form.value.idDestino,
      valor: this.form.value.valor
    };

    this.beneficioService.transferir(request).subscribe({
      next: (response) => {
        this.showSuccess(response.mensagem);
        this.dialogRef.close(true);
      },
      error: (error) => {
        this.showError(error.message);
        this.submitting = false;
      }
    });
  }

  cancelar(): void {
    this.dialogRef.close(false);
  }

  get valor() { return this.form.get('valor'); }
  get idDestino() { return this.form.get('idDestino'); }

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
