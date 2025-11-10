import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { BeneficioService } from '../../services/beneficio.service';
import { Beneficio } from '../../models/beneficio.model';
import { TransferDialogComponent } from '../transfer-dialog/transfer-dialog.component';

@Component({
  selector: 'app-beneficio-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatDialogModule,
    MatSnackBarModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './beneficio-list.component.html',
  styleUrls: ['./beneficio-list.component.scss']
})
export class BeneficioListComponent implements OnInit {
  beneficios: Beneficio[] = [];
  displayedColumns: string[] = ['id', 'nome', 'descricao', 'valor', 'ativo', 'acoes'];
  loading = false;

  constructor(
    private beneficioService: BeneficioService,
    private router: Router,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.carregarBeneficios();
  }

  carregarBeneficios(): void {
    this.loading = true;
    this.beneficioService.listarBeneficios().subscribe({
      next: (data) => {
        this.beneficios = data;
        this.loading = false;
      },
      error: (error) => {
        this.showError(error.message);
        this.loading = false;
      }
    });
  }

  novoBeneficio(): void {
    this.router.navigate(['/beneficios/novo']);
  }

  editar(id: number): void {
    this.router.navigate(['/beneficios/editar', id]);
  }

  deletar(id: number): void {
    if (confirm('Tem certeza que deseja deletar este benefício?')) {
      this.beneficioService.deletar(id).subscribe({
        next: () => {
          this.showSuccess('Benefício deletado com sucesso!');
          this.carregarBeneficios();
        },
        error: (error) => this.showError(error.message)
      });
    }
  }

  abrirDialogTransferencia(beneficio: Beneficio): void {
    const dialogRef = this.dialog.open(TransferDialogComponent, {
      width: '500px',
      panelClass: 'transfer-dialog',
      data: { 
        beneficioOrigem: beneficio,
        todosBeneficios: this.beneficios 
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.carregarBeneficios();
      }
    });
  }

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
