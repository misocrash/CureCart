import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Medicine {
  id: string; name: string; category: string; stock: number; price: number; minStock: number;
}

@Component({
  selector: 'app-manage-medicines',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './manage-medicines.component.html',
  styleUrls: ['./manage-medicines.component.css']
})
export class ManageMedicinesComponent {
  medicines: Medicine[] = [
    { id: '1', name: 'Paracetamol 500mg', category: 'Pain Relief', stock: 450, price: 12.99, minStock: 100 },
    { id: '2', name: 'Amoxicillin 250mg', category: 'Antibiotics', stock: 89, price: 24.50, minStock: 50 },
  ];
  
  isDialogOpen = false;
  editingMedicine: Medicine | null = null;
  formData: any = {};

  openAddDialog() {
    this.editingMedicine = null;
    this.formData = { name: '', category: '', stock: 0, price: 0, minStock: 0 };
    this.isDialogOpen = true;
  }

  openEditDialog(medicine: Medicine) {
    this.editingMedicine = medicine;
    this.formData = { ...medicine };
    this.isDialogOpen = true;
  }

  closeDialog() {
    this.isDialogOpen = false;
  }

  onFormSubmit() {
    if (this.editingMedicine) {
      // Update logic here
      const index = this.medicines.findIndex(m => m.id === this.editingMedicine!.id);
      if (index > -1) this.medicines[index] = { ...this.formData, id: this.editingMedicine.id };
    } else {
      // Add logic here
      this.medicines.push({ ...this.formData, id: Date.now().toString() });
    }
    this.closeDialog();
  }
}