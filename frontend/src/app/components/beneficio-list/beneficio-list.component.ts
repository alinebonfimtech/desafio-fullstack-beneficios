import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Beneficio, TransferRequest } from '../../models/beneficio.model';
import { BeneficioService } from '../../services/beneficio.service';

@Component({
  selector: 'app-beneficio-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './beneficio-list.component.html',
  styleUrls: ['./beneficio-list.component.css']
})
export class BeneficioListComponent implements OnInit {
  beneficios: Beneficio[] = [];
  filteredBeneficios: Beneficio[] = [];
  loading = false;
  error: string | null = null;
  success: string | null = null;
  
  showCreateModal = false;
  showEditModal = false;
  showTransferModal = false;
  
  searchTerm = '';
  activeOnly = false;
  
  currentBeneficio: Beneficio = this.getEmptyBeneficio();
  editingBeneficio: Beneficio = this.getEmptyBeneficio();
  
  transferRequest: TransferRequest = {
    fromId: 0,
    toId: 0,
    amount: 0
  };

  constructor(private beneficioService: BeneficioService) {}

  ngOnInit(): void {
    this.loadBeneficios();
  }

  loadBeneficios(): void {
    this.loading = true;
    this.error = null;
    
    this.beneficioService.findAll(this.activeOnly).subscribe({
      next: (data) => {
        this.beneficios = data;
        this.filteredBeneficios = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Erro ao carregar benefícios';
        this.loading = false;
      }
    });
  }

  filterBeneficios(): void {
    if (!this.searchTerm.trim()) {
      this.filteredBeneficios = this.beneficios;
      return;
    }
    
    this.beneficioService.searchByNome(this.searchTerm).subscribe({
      next: (data) => {
        this.filteredBeneficios = data;
      },
      error: (err) => {
        this.error = err.error?.message || 'Erro ao buscar benefícios';
      }
    });
  }

  toggleActiveFilter(): void {
    this.activeOnly = !this.activeOnly;
    this.loadBeneficios();
  }

  openCreateModal(): void {
    this.currentBeneficio = this.getEmptyBeneficio();
    this.showCreateModal = true;
    this.clearMessages();
  }

  openEditModal(beneficio: Beneficio): void {
    this.editingBeneficio = { ...beneficio };
    this.showEditModal = true;
    this.clearMessages();
  }

  openTransferModal(): void {
    this.transferRequest = { fromId: 0, toId: 0, amount: 0 };
    this.showTransferModal = true;
    this.clearMessages();
  }

  closeModals(): void {
    this.showCreateModal = false;
    this.showEditModal = false;
    this.showTransferModal = false;
  }

  createBeneficio(): void {
    this.loading = true;
    this.clearMessages();
    
    this.beneficioService.create(this.currentBeneficio).subscribe({
      next: () => {
        this.success = 'Benefício criado com sucesso!';
        this.closeModals();
        this.loadBeneficios();
      },
      error: (err) => {
        this.error = err.error?.message || 'Erro ao criar benefício';
        this.loading = false;
      }
    });
  }

  updateBeneficio(): void {
    if (!this.editingBeneficio.id) return;
    
    this.loading = true;
    this.clearMessages();
    
    this.beneficioService.update(this.editingBeneficio.id, this.editingBeneficio).subscribe({
      next: () => {
        this.success = 'Benefício atualizado com sucesso!';
        this.closeModals();
        this.loadBeneficios();
      },
      error: (err) => {
        this.error = err.error?.message || 'Erro ao atualizar benefício';
        this.loading = false;
      }
    });
  }

  deleteBeneficio(id: number): void {
    if (!confirm('Tem certeza que deseja deletar este benefício?')) return;
    
    this.loading = true;
    this.clearMessages();
    
    this.beneficioService.delete(id).subscribe({
      next: () => {
        this.success = 'Benefício deletado com sucesso!';
        this.loadBeneficios();
      },
      error: (err) => {
        this.error = err.error?.message || 'Erro ao deletar benefício';
        this.loading = false;
      }
    });
  }

  deactivateBeneficio(id: number): void {
    if (!confirm('Tem certeza que deseja desativar este benefício?')) return;
    
    this.loading = true;
    this.clearMessages();
    
    this.beneficioService.deactivate(id).subscribe({
      next: () => {
        this.success = 'Benefício desativado com sucesso!';
        this.loadBeneficios();
      },
      error: (err) => {
        this.error = err.error?.message || 'Erro ao desativar benefício';
        this.loading = false;
      }
    });
  }

  executeTransfer(): void {
    this.loading = true;
    this.clearMessages();
    
    this.beneficioService.transfer(this.transferRequest).subscribe({
      next: () => {
        this.success = 'Transferência realizada com sucesso!';
        this.closeModals();
        this.loadBeneficios();
      },
      error: (err) => {
        this.error = err.error?.message || 'Erro ao realizar transferência';
        this.loading = false;
      }
    });
  }

  clearMessages(): void {
    this.error = null;
    this.success = null;
  }

  getEmptyBeneficio(): Beneficio {
    return {
      nome: '',
      descricao: '',
      valor: 0,
      ativo: true
    };
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  }
}
